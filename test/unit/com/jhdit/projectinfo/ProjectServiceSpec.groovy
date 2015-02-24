
package com.jhdit.projectinfo

import grails.test.mixin.TestFor
import grails.validation.ValidationException;
import spock.lang.Specification
import static com.jhdit.projectinfo.ProjectStatus.*

/**
 * Unit tests for ProjectService
 * 
 * NB: See integration tests (see @ProjectServiceIntegrationSpec) for more extensive set of tests (which exercise real entities).
 */
@TestFor(ProjectService)
@Mock([Person, Project])
class ProjectServiceSpec extends Specification {
	Person testPerson
	Project a, b, c, d, e

	def setup() {
		testPerson = new Person(firstname: "John", lastname: "Smith").save()
		a = new Project(name: "Project A", code: "PR_A", priority: 1, currentStatus: BRIEFING, projectManager: testPerson).save()
		b = new Project(name: "Project B", code: "PR_B", priority: 2, currentStatus: SCOPING, projectManager: testPerson).save()
		c = new Project(name: "Project C", code: "PR_C", priority: 3, currentStatus: DEVELOPMENT, projectManager: testPerson).save()
		d = new Project(name: "Project D", code: "PR_D", priority: 4, currentStatus: QA, projectManager: testPerson).save()
		e = new Project(name: "Project E", code: "PR_E", priority: 5, currentStatus: RELEASE, projectManager: testPerson).save(flush: true)
	}

	void "Check service injection" () {
		expect "auto-wiring OK":
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
}
