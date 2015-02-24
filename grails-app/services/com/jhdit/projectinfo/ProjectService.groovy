package com.jhdit.projectinfo

import org.apache.commons.logging.LogFactory;

import grails.transaction.Transactional

/**
 * Service to handle CRUD operations for Project entities
 * Provides logic to automatically re-organise other project priorities where necessary
 * 
 * TODO: JHD: Concurrent access issues? (Synchronize?)  
 */

@Transactional
class ProjectService {
	private static final log = LogFactory.getLog(this)
	
	private static final FIELD_PROJECT_PRIORITY = 'priority'

	def saveProject(Project project) {
		assert project
		
		project.save(failOnError: true, flush:true)
		this.shiftPriorities(project.priority, project.code, false)
	}
	
	def updateProject(final Project project) {
		assert project
		
		// Handle an update with an amended priority
		if (project.isDirty(FIELD_PROJECT_PRIORITY))	{
			this.updateProjectWithChangedPriority(project)
			return	
		}
		
		project.validate()
		project.save(failOnError: true, flush:true)
	}	
	
	def deleteProject(final Project project) {
		assert project
		
		def deletePriority = project.priority
		def deleteCode = project.code
		
		project.delete(failOnError: true, flush:true)
		shiftPriorities(deletePriority, deleteCode, true)
	}
	
	private def updateProjectWithChangedPriority(final Project project) {
		def amendedPriority = project.priority
		def previousPriority = findPreviousPriority(project, amendedPriority)
		log.info "Project update: code: ${project.code} amendedPriority: ${amendedPriority} previousPriority: ${previousPriority}"

		project.validate()
		project.save(failOnError: true, flush:true)
		
		if (previousPriority == amendedPriority)	{
			log.error "Project update with dirty priority (code: ${project.code}). However, amendedPriority: ${amendedPriority} equals previousPriority: ${previousPriority}"
			return
		}
		
		final boolean isIncreasedPriority =  previousPriority > project.priority
		this.shiftOtherProjectPriorities(project, isIncreasedPriority)
	}
	
	/**
	 * Re-organise all other project priorities around the supplied project 
	 */
	
	private def shiftOtherProjectPriorities(final Project project, final boolean isIncrementOthers)	{
		def matches = Project.list()
		if (!matches || matches.size() <= 1) {
			return  // Nothing to do
		}

		// Re-organise projects which have the same or greater priorities
		int index = 0
		for (Project existingProject: matches)	{
			index++
			if (existingProject == project) {
				continue
			}

			if (existingProject.priority != index)	{
				existingProject.priority = index
				existingProject.save()
			} else if (existingProject.priority == project.priority)	{
				if (isIncrementOthers)	{
					existingProject.priority++
				} else {
					existingProject.priority--
				}
				existingProject.save()
			}
		}
	}

	/**
	 * Obtain the previous project priority, when dirty, via various methods (since some are seemingly unreliable)
	 * @param project Target project
	 * @param currentPriority 
	 * @return Previous priority
	 */
	
	private def findPreviousPriority(final Project project, def currentPriority)	{
		def previousPriority = project.getPersistentValue(FIELD_PROJECT_PRIORITY)
		if (previousPriority == currentPriority)	{
			log.info "Attempting to get previous priority via persisted entity for project code: ${project.code}"
			previousPriority = getPersistedPriority(project)
		}
		if (previousPriority == currentPriority)	{
			log.info "Attempting to get previous priority via gap for project code: ${project.code}"
			previousPriority = findGap(currentPriority)
		}
		return previousPriority
	}
	
	private def getPersistedPriority(final Project project)	{
		Project persistedProject = Project.findByCode(project.code)
		return persistedProject.priority
	}
	
	private def findGap(def defaultValue)	{
		for (i in 1..Project.count())	{
			if (!Project.findByPriority(i))	{
				return i
			}
		}
		return defaultValue
	}	
	
	/**
	 * Shift any matching priorities (from a target) i.e. towards 1 (the highest priority)
	 * @param targetPriority  Starting priority to match
	 * @param targetProjectCode Code of project to be excluded
	 * @param isDecrement Shift towards 1 (the highest priority) if true, otherwise away
	 */
	
	private void shiftPriorities(def targetPriority, def targetProjectCode, final boolean isDecrement)	{
		def matches = Project.findAllByPriorityGreaterThanEquals(targetPriority, [sort: FIELD_PROJECT_PRIORITY, order: "desc"])

		if (!matches)	{
			return // Nothing to do!
		}

		// Adjust project priorities
		for (Project existingProject: matches)	{
			if (existingProject.code == targetProjectCode) {
				continue // Ignore target
			}
			if (isDecrement)	{
				existingProject.priority--
			} else	{
				existingProject.priority++
			}
			log.info "Changed existingProject code: ${existingProject.code} priority to ${existingProject.priority}"
			existingProject.save(failOnError: true, flush:true)
		}
	}
}
