package com.jhdit.projectinfo


import spock.lang.Ignore;
import grails.test.spock.IntegrationSpec
import grails.validation.ValidationException;
import static com.jhdit.projectinfo.ProjectStatus.*

/**
 * Integration tests for ProjectService.
 *
 * E.g. run via:
 *
 * grails test-app integration: ProjectServiceIntegrationSpec
 */

class ProjectServiceIntegrationSpec extends IntegrationSpec {
	def projectService // Auto-wired / injected
	def service // Alias (for convenience)
	Person testPerson
	Project a, b, c, d, e


	def setup() {
		service = projectService
		
		testPerson = new Person(firstname: "John", lastname: "Smith").save()
		a = new Project(name: "Project A", code: "PR_A", priority: 1, currentStatus: BRIEFING, projectManager: testPerson).save()
		b = new Project(name: "Project B", code: "PR_B", priority: 2, currentStatus: SCOPING, projectManager: testPerson).save()
		c = new Project(name: "Project C", code: "PR_C", priority: 3, currentStatus: DEVELOPMENT, projectManager: testPerson).save()
		d = new Project(name: "Project D", code: "PR_D", priority: 4, currentStatus: QA, projectManager: testPerson).save()
		e = new Project(name: "Project E", code: "PR_E", priority: 5, currentStatus: RELEASE, projectManager: testPerson).save(flush: true)
	}

	def cleanup() {
	}

	void "Check service injection" () {
		expect:
		service
	}

	void "Creating a new project re-organises project priorities when necessary"() {

		given: "New (unsaved) project"
		Project x = new Project(name: "Project X", code: "PR_X", priority: 2, currentStatus: DEVELOPMENT, projectManager: testPerson)

		when: "A new project is created"
		x.priority == 2
		service.saveProject(x)

		then: "The priorities of the project are re-organised"
		a.priority == 1
		x.priority == 2
		b.priority == 3
		c.priority == 4
		d.priority == 5
		e.priority == 6
	}

	void "Creating a new project without need for re-organisation"() {

		given: "New (unsaved) project"
		Project x = new Project(name: "Project X", code: "PR_X", priority: 6, currentStatus: DEVELOPMENT, projectManager: testPerson)

		when: "A new project is created"
		x.priority == 6
		service.saveProject(x)

		then: "The priorities of the project are re-organised"
		a.priority == 1
		b.priority == 2
		c.priority == 3
		d.priority == 4
		e.priority == 5
		x.priority == 6
	}

	void "Cannot creating a new project priority above number of projects"() {
		given: "New (unsaved) project"
		Project x = new Project(name: "Project X", code: "PR_X", priority: 7, currentStatus: DEVELOPMENT, projectManager: testPerson)

		expect:
		x.priority == 7

		when: "A new project is created"
		service.saveProject(x)

		then: "Fails validation"
		def ex = thrown(ValidationException)
		ex.errors.hasFieldErrors('priority')
	}

	void "Cannot update a new project priority above number of projects"() {
		given: "New (unsaved) project"
		Project x = new Project(name: "Project X", code: "PR_X", priority: 6, currentStatus: DEVELOPMENT, projectManager: testPerson).save()

		expect:
		x.priority == 6

		when: "A new project is created"
		x.priority = 7  
		service.saveProject(x) // Update

		then: "Fails validation"
		def ex = thrown(ValidationException)
		ex.errors.hasFieldErrors('priority')
	}
	
	void "Updated project with increased priority has reorganised existing project priorities"() {
		expect: "the following initial setup"
		a.priority == 1
		b.priority == 2
		c.priority == 3
		d.priority == 4 
		e.priority == 5
		
		when: "An existing project is edited & the priority (only) is increased"
		d.priority = 2
		service.updateProject(d)

		then: "The priorities of the other project are re-organised accordingly"
		a.priority == 1
		d.priority == 2 // Updated
		b.priority == 3
		c.priority == 4
		e.priority == 5
	}

	void "Updated project with decreased priority has reorganised existing project priorities"() {
		expect: "the following initial setup"
		a.priority == 1
		b.priority == 2
		c.priority == 3
		d.priority == 4 
		e.priority == 5
		
		when: "An existing project is edited & the priority (only) is decreased"
		b.priority = 4
		service.updateProject(b)

		then: "The priorities of the other project are re-organised accordingly"
		a.priority == 1
		c.priority == 2
		d.priority == 3
		b.priority == 4 // Updated
		e.priority == 5
	}

	void "Deleted project has reorganised existing project priorities"() {
		expect: "the following initial setup"
		a.priority == 1
		b.priority == 2
		c.priority == 3
		d.priority == 4 
		e.priority == 5
		
		when: "An existing project is deleted"
		service.deleteProject(b)

		then: "The priorities of the remaining project are re-organised"
		!Project.findById(b.id)
		a.priority == 1
		c.priority == 2
		d.priority == 3
		e.priority == 4
	}

}
