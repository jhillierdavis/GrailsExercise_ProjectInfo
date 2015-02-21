package com.jhdit.projectinfo

// import org.apache.log4j.spi.LoggerFactory;
// import org.slf4j.Logger;

import static com.jhdit.projectinfo.ProjectStatus.*

import com.jhdit.projectinfo.Project;

import grails.validation.ValidationException
import grails.test.spock.IntegrationSpec
// import spock.lang.Specification

/**
 * Integration tests for Project domain class 
 */

class ProjectIntegrationSpec extends IntegrationSpec {
	// private static Logger log = LoggerFactory.getLogger(ProjectIntegrationSpec.class)
	private Project testProject
	private static def BLANK_FIELD = "    "
	
	
	def setup()	{
		// Set-up a default valid project entity
		Person testPerson = new Person(firstname: "John", lastname: "Smith").save()
		testProject = new Project(name: "Test Project", code: "TEST_01", priority: 1, currentStatus: SCOPING, projectManager: testPerson)
	}

	void "Creating a project persists it to the database"() {
		when: "the project is saved"
			testProject.save(failOnError: true, flush: true)

		then: "it is saved successfully & can be found in the database"
			testProject.errors.errorCount == 0
			testProject.id != null
			Project p = Project.get(testProject.id)
			p.name == testProject.name
			// System.out.println "DEBUG: Project p=${p}"
	}

	def "Updating a saved project changes its properties"() {
		given: "An existing project"
			testProject.save(failOnError: true, flush: true)

		when: "A property is changed"
			def foundProject = Project.get(testProject.id)
			foundProject.name = 'Updated Test Project'
			foundProject.save(failOnError: true)

		then: "The change is reflected in the database"
			Project.get(testProject.id).name == 'Updated Test Project'
	}
	
	def "Deleting an existing project removes it from the database"() {
		given: "An existing project"		
			testProject.save(failOnError: true, flush: true)

		when: "The project is deleted"
			def foundProject = Project.get(testProject.id)
			foundProject.delete(flush: true)

		then: "The project is removed from the database"
			!Project.exists(foundProject.id) 
	}
	
	def "Listing existing projects retrieves them all"() {
		given: "Some existing projects"
			def projects = this.setupPersistedTestProjects()
		
		when: "Project are retrieved"
			def foundProjects = Project.list()		

		then: "Exisiting projects are returned from the database"
			foundProjects.size() == projects.size()
			foundProjects.contains(projects.first())
			foundProjects.contains(projects.last())
	}
	
	void "'projectManager' is mandatory (cannot be null)"()	{
		given: "An existing project"
			testProject.projectManager = null

		when: "A property is changed"
			testProject.save(failOnError: true)

		then:
		def ex = thrown(ValidationException)
		ex.errors.hasFieldErrors('projectManager')
	}
	
	void "'priority' property is unique amongst Project entities"()	{
		when: "All projects have the same priority"
			def projects = this.setupUnpersistedTestProjects()
			projects.each() { project ->
				project.priority = 1
				project.save(failOnError: true, flush: true)
			}
		
		then: "Associated validation error thrown on save()"
			def ex = thrown(ValidationException)
			ex.errors.hasFieldErrors('priority')
			ex.errors.errorCount == 1
	}
	
	void "Project code cannot be blank"()	{
		given:
		testProject.code = BLANK_FIELD
		
		when:			
			testProject.save(failOnError: true, flush: true)
		
		then:
			def ex = thrown(ValidationException)			
			ex.errors.hasFieldErrors('code')
			ex.errors.errorCount == 1
	}
	
	void "Project name cannot be blank"()	{
		given:
		testProject.name = BLANK_FIELD
		
		when:
			testProject.save(failOnError: true, flush: true)
		
		then:
			def ex = thrown(ValidationException)
			ex.errors.hasFieldErrors('name')
			testProject.errors.errorCount == 1
	}
	
	void "Invalid negative priority value"()	{
		given: "Negative priority"
			testProject.priority = -1
			
		when:
			testProject.save(failOnError: true, flush: true)
			
		then:
			def ex = thrown(ValidationException)
			ex.errors.hasFieldErrors('priority')
			testProject.errors.errorCount == 1
	}
	
	void "Priority listing is default (Assumption: ascending i.e.q highest first)"()	{
		when:
		def projects = this.setupPersistedTestProjects()
		projects << testProject
		projects.eachWithIndex()	{ project, i ->
			project.save(failOnError: true, flush: (i == 1 + projects.size() ? true: false))
		}
		
	then:
		def list = Project.list()
		! list.empty
		list.size() == projects.size()
		list.first().priority == 1
		list.last().priority == projects.size()
	}
	
	private def setupUnpersistedTestProjects()	{
		Person pm1  = new Person(firstname: 'Alice', lastname: 'Agile').save()
		Person pm2  = new Person(firstname: 'Wally', lastname: 'Wasterfall').save()
		
		def existingProject1 = new Project(name: "Medium Priority Project", code: "PT_02", priority: 2, currentStatus: QA, projectManager: pm1)
		def existingProject2 = new Project(name: "Low Priority Project", code: "PT_03", priority: 3, currentStatus: BRIEFING, projectManager: pm2)
		
		def projects = []
		projects << existingProject1
		projects << existingProject2
		return projects
	}

	
	private def setupPersistedTestProjects()	{
		def projects = setupUnpersistedTestProjects()
		projects.eachWithIndex { project, i ->
			project.save(failOnError: true, flush: i == 1 + projects.size() ? true: false)
		}
		return projects
	}
		
	private void debugValidationErrors(ValidationException ex)	{
		for (error in ex.errors)	{
			System.out.println("DEBUG: validation error: ${error}")
		}
	}
}
