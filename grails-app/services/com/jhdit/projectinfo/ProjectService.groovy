package com.jhdit.projectinfo

import org.apache.commons.logging.LogFactory;

import grails.transaction.Transactional

@Transactional
class ProjectService {
	private static final log = LogFactory.getLog(this)

	def saveProject(Project project) {
		assert project
		
		project.save(failOnError: true, flush:true)
		reorganisePriorities(project)		
	}
	
	def updateProject(Project project) {
		assert project
		
		def currentPriority = project.priority
		def persistedPriority = project.getPersistentValue('priority')
		if (persistedPriority == currentPriority)	{
			persistedPriority = getPersistedPriority(project)
		}
		if (persistedPriority == currentPriority)	{
			persistedPriority = findGap(currentPriority)
		}

		project.validate()
		project.save(failOnError: true, flush:true)
		
		if (persistedPriority == currentPriority)	{
			return
		}
		
		boolean isIncreasedPriority =  persistedPriority > project.priority
		log.info "Update ${project.code} updatedPriority: ${project.priority} persistedPriority: ${persistedPriority}"

		def matches = Project.list()
		
		if (matches && matches.size() > 1)	{
			// Re-organise projects which have the same or greater priorities
			int x = 0
			for (Project existingProject: matches)	{
				x++
				if (existingProject == project) {
					continue
				}
	
				if (existingProject.priority != x)	{
					existingProject.priority = x
					existingProject.save()
				} else if (existingProject.priority == project.priority)	{
					if (isIncreasedPriority)	{
						existingProject.priority++
					} else {
						existingProject.priority--
					}
					existingProject.save()
				}
				
			}
		}
	}
	
	def findGap(def defaultValue)	{
		for (i in 1..Project.count())	{
			if (!Project.findByPriority(i))	{
				return i
			}
		}
		return defaultValue
	}
	
	/*
	def updateProject(Project project) {
		assert project
		
		project.validate()
		if (project.hasErrors())	{
			return
		}
		
		def persistedPriority = project.getPersistentValue('priority')
		if (persistedPriority == project.priority)	{
			persistedPriority = getPersistedPriority(project)
		}	
		
		if (persistedPriority == project.priority)	{
			throw new SystemException("Cannot determine change in priority: Update ${project.code} updatedPriority: ${project.priority} persistedPriority: ${persistedPriority}")
		}
		
		boolean isIncreasedPriority =  persistedPriority > project.priority
		log.info "Update ${project.code} updatedPriority: ${project.priority} persistedPriority: ${persistedPriority}"

		
		
		def matches = Project.list()
		
		project.save()

		if (matches && matches.size() > 1)	{
			// Re-organise projects which have the same or greater priorities
			int x = 0
			for (Project existingProject: matches)	{
				x++
				if (existingProject == project) {
					continue
				}
	
				if (existingProject.priority != x)	{
					existingProject.priority = x
					existingProject.save()
				} else if (existingProject.priority == project.priority)	{
					if (isIncreasedPriority)	{
						existingProject.priority++
					} else {
						existingProject.priority--
					}
					existingProject.save()
				}
				
			}
		}
	}
	*/

/*
	def updateProject(Project project) {
		assert project
		
		def updatedPriority = project.priority
 		def persistedPriority = project.getPersistentValue('priority')
		// def persistedPriority = getPersistedPriority(project)
		System.out.println "Update ${project.code} ${project.priority} persistedPriority: ${persistedPriority}"

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
*/	
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
	def deleteProject(final Project project) {
		assert project
		
		def matches = Project.findAllByPriorityGreaterThanEquals(project.priority, [sort: "priority", order: "desc"])
		
		project.delete(failOnError: true, flush:true)
		
		if (!matches)	{
			return
		}

		// Adjust project priorities
		for (Project existingProject: matches)	{
			if (existingProject == project) {
				continue
			}
			existingProject.priority--
			existingProject.save(failOnError: true, flush:true)	
		}
	}
	
	
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
			
			existingProject.priority++
			log.info "Incrementing existingProject: ${existingProject.code} priority to ${existingProject.priority}"
			existingProject.save(failOnError: true, flush:true)	
		}
	}	
}
