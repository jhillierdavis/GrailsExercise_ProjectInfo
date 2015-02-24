import grails.util.Environment

import com.jhdit.projectinfo.Person
import com.jhdit.projectinfo.Project


import static com.jhdit.projectinfo.ProjectStatus.*

/**
 * Class to generate some sample data (e.g. for convenience during development, for UI testing) 
 */

class BootStrap {
	private boolean isBootstapDataWanted = true

    def init = { servletContext ->
		
		def currentEnv = Environment.current
		
		// Bootstrap only when required (e.g. for development env.)
		if (!isBootstapDataWanted || currentEnv != Environment.DEVELOPMENT)	{
			return // Bail out before loading data (if appropriate)
		}
		
		// Add some initial people & projects to play with during development

		// Project Managers (?)
		Person agile  = new Person(firstname: 'Alice', lastname: 'Agile').save()
		Person agile2  = new Person(firstname: 'Andrew', lastname: 'Agile').save()
		Person waterfall  = new Person(firstname: 'Walter', lastname: 'Waterfall').save()
		Person spiral  = new Person(firstname: 'Sally', lastname: 'Spiral').save()
		
		// Tech. Leads (?)
		Person java  = new Person(firstname: 'Gail', lastname: 'Grails').save()
		Person grails  = new Person(firstname: 'Josh', lastname: 'Java').save()
		Person rails  = new Person(firstname: 'Ruby', lastname: 'Rails').save()
		Person swift  = new Person(firstname: 'Sally', lastname: 'Swift').save()
		Person cobol  = new Person(firstname: 'Carl', lastname: 'Cobol').save()

		// NB: Take care with priorities (must match number of projects)
		def projectA1 = new Project(name: "Project A", code: "PR-A1", priority: 1, currentStatus: RELEASE, projectManager: agile, techLead: java, dueDate: new Date() + 50).save()
		def projectA2 = new Project(name: "Project A", code: "PR-A2", priority: 2, currentStatus: QA, projectManager: agile2, techLead: swift, dueDate: new Date() + 100).save()
		def projectB = new Project(name: "Project B", code: "PR-B", priority: 3, currentStatus: BRIEFING, projectManager: waterfall, dueDate: new Date() + 500).save()
		def projectC = new Project(name: "Project C", code: "PR-C", priority: 4, currentStatus: DEVELOPMENT, projectManager: spiral, techLead: rails, dueDate: new Date() + 15).save()
		def projectD = new Project(name: "Project D", code: "PR-D", priority: 5, currentStatus: INTERACTION, projectManager: agile, techLead: grails, dueDate: new Date() + 75).save()
		def projectE = new Project(name: "Project E", code: "PR-E", priority: 6, currentStatus: SCOPING, projectManager: waterfall, techLead: waterfall).save()

    }
	
    def destroy = {
    }
}
