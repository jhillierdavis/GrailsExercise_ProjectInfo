package com.jhdit.projectinfo

import org.apache.commons.logging.LogFactory;

import grails.transaction.Transactional

@Transactional
class ProjectService {
	private static final log = LogFactory.getLog(this)

	def saveProject(Project project) {
		assert project
		
		reorganisePriorities(project)
		project.save failOnError: true, flush:true
	}
	
	def updateProject(Project project) {
		assert project
		
		if (this.isPriorityTooLarge(project))	{
			throw new SystemException("Project prioriy is too high. Priority: ${project.priority}")
		}		
		


		

		def updatedPriority = project.priority
 		def persistedPriority = project.getPersistentValue('priority')
		// def persistedPriority = getPersistedPriority(project)
		System.out.println "Update ${project.code} ${project.priority} persistedPriority =${persistedPriority}"

		System.out.println "Changed priority: updated:${updatedPriority} persisted: ${persistedPriority} ${project.code}"
		// assert project.isDirty('priority') // Ensure dealing with an update
		// assert project.isDirty()
		
		if (updatedPriority == persistedPriority)	{
			System.out.println "Doesn't appear to be an update, priorities match!"
			project.save(failOnError: true, flush: true)
			return
		}
		
		if (persistedPriority < updatedPriority)	{	
			// Priority decreased	
			def matches = Project.findAllByPriorityLessThanEquals(updatedPriority, [sort: "priority", order: "asc"])
	
			if (!matches)	{
				return
			}
			
			// Re-organise projects which have the same or greater priorities
			for (Project existingProject: matches)	{
				if (existingProject == project) {
					continue
				}
				
				if (existingProject.priority > persistedPriority)	{
					existingProject.priority--
					existingProject.save()
				}				
			}
		} else	{
			// 	Priority increased
			def matches = Project.findAllByPriorityGreaterThanEquals(updatedPriority, [sort: "priority", order: "desc"])
			if (!matches)	{
				return
			}
			
			// Re-organise projects which have the same or greater priorities
			for (Project existingProject: matches)	{
				if (existingProject == project) {
					continue
				}
				
				if (existingProject.priority <= persistedPriority)	{
					existingProject.priority++
					existingProject.save()
				}				
			}

		}
		
		// Update to intended value
		project.priority = updatedPriority
		project.save(flush: true)	
	}
	
	private def getPersistedPriority(final Project project)	{
		// NB: project.getPersistentValue('priority') doesn't appear to work unfortunately in integration tests!
		
		for (i in 0 ..Project.count())	{
			Project existing = Project.findByPriority(i)
			if (existing && existing.code == project.code)	{
				return i
			}
		}
		return project.priority
	}
	
	
	
	boolean isPriorityTooLarge(final Project project) {
		assert project
		return project.priority > 1 + Project.count()
	}

/*
	private void updatePriorities(Project project)	{
		assert project // Ensure not NULL

		
		
		def tempPriority = 0
		def updatedPriority = project.priority
		def persistedPriority = project.getPersistentValue('priority')
		System.out.println "Changed priority: updated:${updatedPriority} peristed: ${persistedPriority} ${project}"
		// assert project.isDirty('priority') // Ensure dealing with an update
		assert project.isDirty()
		
		project.priority = tempPriority
		project.save(flush: true)
		

		if (persistedPriority < updatedPriority)	{	
			// Priority decreased	
			def matches = Project.findAllByPriorityLessThanEquals(updatedPriority, [sort: "priority", order: "asc"])
	
			if (!matches)	{
				return
			}
			
			// Re-organise projects which have the same or greater priorities
			for (Project existingProject: matches)	{
				if (existingProject == project) {
					continue
				}
				
				if (existingProject.priority > persistedPriority)	{
					existingProject.priority--
					existingProject.save()
				}				
			}
		} else	{
			// 	Priority increased
			def matches = Project.findAllByPriorityGreaterThanEquals(updatedPriority, [sort: "priority", order: "desc"])
			if (!matches)	{
				return
			}
			
			// Re-organise projects which have the same or greater priorities
			for (Project existingProject: matches)	{
				if (existingProject == project) {
					continue
				}
				
				if (existingProject.priority <= persistedPriority)	{
					existingProject.priority++
					existingProject.save()
				}				
			}

		}
		
		project.priority = updatedPriority
		project.save(flush:true)
		
	}
*/
	private def reorganisePriorities(Project project)	{
		assert project
		
		def matches = Project.findAllByPriorityGreaterThanEquals(project.priority, [sort: "priority", order: "desc"])

		if (!matches)	{
			return
		}
		
		// Re-organise projects which have the same or greater priorities
		for (Project existingProject: matches)	{
			

			if (existingProject == project) {
//				System.out.println "currentProject: ${project}"
//				System.out.println "existingProject: ${existingProject}"
				continue
			}
			System.out.println "existingProject.priority: ${existingProject.priority}"
			log.info "existingProject: ${existingProject}"
			existingProject.priority++
			existingProject.save flush:true			
		}
	}
	

	
	boolean hasSuppressedError(final Project project)	{
		if (project.hasErrors())	{
			if (project.errors.hasFieldErrors('priority') && project.errors.errorCount == 1)	{
				return true
			}
		}
		return false
	}
}
