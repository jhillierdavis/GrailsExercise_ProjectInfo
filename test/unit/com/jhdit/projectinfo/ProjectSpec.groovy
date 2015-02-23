package com.jhdit.projectinfo

import static com.jhdit.projectinfo.ProjectStatus.*
import grails.test.mixin.TestFor
import grails.validation.ValidationException;
import groovy.mock.interceptor.MockFor
import spock.lang.Specification
import spock.lang.Unroll;

/**
 * See the API for {@link grails.test.mixin.domain.DomainClassUnitTestMixin} for usage instructions
 */

@TestFor(Project)
@Mock(Person)
class ProjectSpec extends Specification {
	Person testPerson = new Person(firstname: "John", lastname: "Smith")
	Project testProject
	
	def setup()	{		
		testProject = new Project(name: "Test Project", code: "PT_01", priority: 1, currentStatus: RELEASE, projectManager: testPerson)
	}

    void "Mock creation of a valid Project entity"() {
		when:			
			testProject.save(failOnError: true)
			
		then:
			Project.get(testProject.id) == testProject		
    }
	
	void "Test 'beforeValidate' with valid creation priority"() {
		given:
			testProject.priority = 1
			
		when:
			testProject.beforeValidate()
			
		then:
			!testProject.hasErrors()
	}
	
	void "Test 'beforeValidate' with invalid creation priority"() {
		given:
			testProject.priority = 2
			
		when:
			testProject.beforeValidate()
			
		then:
			testProject.hasErrors()
			testProject.errors.hasFieldErrors('priority')
//			for (error in testProject.errors)	{
//				System.out.println "Validation error: ${error}"
//			}
	}

	void "Test 'beforeValidate' with valid update priority"() {
		given:
			testProject.save()
			testProject.priority = 1
			
		when:
			testProject.beforeValidate()
			
		then:
			!testProject.hasErrors()
	}
	
	void "Test 'beforeValidate' with invalid update priority"() {
		given:
			testProject.save()
			testProject.priority = 2
			
		when:
			testProject.beforeValidate()
			
		then:
			testProject.hasErrors()
			testProject.errors.hasFieldErrors('priority')
//			for (error in testProject.errors)	{
//				System.out.println "Validation error: ${error}"
//			}
	}

			
	void "Listing can be sorted (by priority descending)"()	{
		when:
		// NB: Must be saved in priority order
		def projects = [
			testProject,
			new Project(name: "Medium Priority Project", code: "PT_02", priority: 2, currentStatus: QA, projectManager: testPerson),
			new Project(name: "Low Priority Project", code: "PT_03", priority: 3, currentStatus: BRIEFING, projectManager: testPerson),
			]
		projects.eachWithIndex()	{ project, i ->
			project.save(failOnError: true, flush: (i == 1 + projects.size() ? true: false))
		}
		
	then:
		// NB: Explicit sort order, as unfortunately default doesn't appear to work for mocks
		def list = Project.list(sort: 'priority', order: "desc") 
		! list.empty
		list.size() == projects.size()
		list.first().priority == projects.size() 
		list.last().priority == 1
	}
	
	void "Listing can be sorted (by priority ascending)"()	{
		when:
		// NB: Must be saved in priority order
		def projects = [
			testProject,
			new Project(name: "Medium Priority Project", code: "PT_02", priority: 2, currentStatus: QA, projectManager: testPerson),
			new Project(name: "Low Priority Project", code: "PT_03", priority: 3, currentStatus: BRIEFING, projectManager: testPerson)
			]
		projects.eachWithIndex()	{ project, i ->
			project.save(failOnError: true, flush: (i == 1 + projects.size() ? true: false))
		}
		
	then:
		// NB: Explicit sort order, as unfortunately default doesn't appear to work for mocks
		def list = Project.list(sort: 'priority', order: "asc") // Be explicit as default sort unfortunately doesn't work when mocking
		! list.empty
		list.size() == projects.size()
		list.first().priority == 1
		list.last().priority == projects.size()
	}

	
	void "Invalid negative priority value"()	{
		given: "Negative priority"
			testProject.priority = -1
			
		when:
			testProject.save(failOnError: true, flush: true)
			
		then:
			def e = thrown(ValidationException)
			testProject.errors.errorCount == 1
	}

	// @Unroll // Run each data row as a separate test (useful for failures)
	def "Invalid priority values"(int x) {
		when:
			testProject.priority = 	x
			testProject.save(failOnError: true, flush: true)
			
		then:
			def e = thrown(ValidationException)
			testProject.errors.errorCount == 1
		
		where: "invalid 'priority' data"
			x << [0, -1]
	}
		
	// @Unroll // Run each data row as a separate test (useful for failures)
	def "Invalid due date values"(Date d) {
		when:
			testProject.dueDate = 	d
			testProject.save(failOnError: true, flush: true)
			
		then:
			def e = thrown(ValidationException)
			testProject.errors.errorCount == 1
		
		where: "invalid 'dueDate' data"
			d << [new Date().previous(), new Date() - 1000]
	}

	void "List Projects by PM"()	{
		given:
			Person pm1  = new Person(firstname: 'Alice', lastname: 'Agile').save()
			Person pm2  = new Person(firstname: 'Wally', lastname: 'Wasterfall').save()
			Person pm3  = new Person(firstname: 'Nigel', lastname: 'None').save()

			// NB: Must be saved in priority order
			Project projectB = new Project(name: "Project B", code: "P_B", priority: 1, currentStatus: BRIEFING, projectManager: pm1).save()
			Project projectC = new Project(name: "Project C", code: "P_C", priority: 2, currentStatus: DEVELOPMENT, projectManager: pm2).save()
			Project projectA = new Project(name: "Project A", code: "P_A", priority: 3, currentStatus: QA, projectManager: pm1).save()								
	
		expect:
			pm1.projects.size() == 2
			pm1.projects.contains(projectA)
			pm1.projects.contains(projectB)
			pm2.projects.size() == 1
			pm2.projects.contains(projectC)
			!pm3.projects // No associated projects
	}

	void "List Projects by Tech Lead"()	{
		given:
			Person pm1  = new Person(firstname: 'Alice', lastname: 'Agile').save()
			Person pm2  = new Person(firstname: 'Wally', lastname: 'Wasterfall').save()
			Person techLead1 = new Person(firstname: 'Gail', lastname: 'Grails').save()
			Person techLead2 = new Person(firstname: 'Sandra', lastname: 'Swift').save()
			
			// NB: Must be saved in priority order
			Project projectB = new Project(name: "Project B", code: "P_B", priority: 1, currentStatus: BRIEFING, projectManager: pm1).save()
			Project projectC = new Project(name: "Project C", code: "P_C", priority: 2, currentStatus: DEVELOPMENT, projectManager: pm2, techLead: techLead1).save()
			Project projectA = new Project(name: "Project A", code: "P_A", priority: 3, currentStatus: QA, projectManager: pm1, techLead: techLead1).save()
	
		expect:
			techLead1.projects.size() == 2
			techLead1.projects.contains(projectA)
			techLead1.projects.contains(projectC)
			!techLead2.projects // No associated projects
	}

}
