package com.jhdit.projectinfo

import static com.jhdit.projectinfo.ProjectStatus.*
import grails.test.mixin.TestFor
import groovy.mock.interceptor.MockFor;
import spock.lang.Specification

/**
 * See the API for {@link grails.test.mixin.services.ServiceUnitTestMixin} for usage instructions
 */
@TestFor(PersonService)
@Mock([Person, Project])
class PersonServiceSpec extends Specification {
	
	void "Deletion using an invalid Person ID throws exception"()	{
		when:
			service.deletePerson(null)
			
		then:
			// def ex = thrown(IllegalArgumentException)
			def ex = thrown(AssertionError)
			
	}

	void "Deletion using a non existent/persisted Person throws exception"()	{
		when:
			int bogusId = 123
			!Person.exists(bogusId)
			service.deletePerson(bogusId)
		
		then:
			def ex = thrown(SystemException)
			ex.message.startsWith(PersonService.NO_MATCH + bogusId)
			// System.out.println "DEBUG: Expected exception: ${ex}"
	}

	
	void "Deletion prohibited for a person entity without associated projects"()	{
		given: 'A person entity without projects'
			Person person = new Person(firstname: "John", lastname: "Doe")
		
		when: "Saved"
			person.save(flush:true)
		
		then: "Exists"
			Person.exists(person.id)
			Person.findById(person.id) == person
			
		when: "Deleted"
			service.deletePerson(person.id)
			
		then: "Person entity is deleted"
			!Person.exists(person.id)
			!Person.findById(person.id)				
	}
	
	
	void "Deletion permitted for a person entity with associated projects"()	{
		given: 'A persisted person entity'
			Person person = new Person(firstname: "Billy", lastname: "Busy")
			person.save(flush: true)
		
		when: "Associated with a project & persist"
			Project project = new Project(name: "Test Project", code: "PT_01", priority: 1, currentStatus: RELEASE, projectManager: person)
			project.save(flush: true)
			
		
		then: "Exists"
			Person.findById(person.id) == person
			Person.exists(person.id)
			
		when: "Deleted"
			service.deletePerson(person.id)
			
		then: "Person entity is deleted"
			def ex = thrown(SystemException)	
			ex.message.startsWith(PersonService.PERSON_HAS_PROJECTS)
			Person.exists(person.id)
			Person.findById(person.id).getFullname() == person.getFullname() // Still exists!	
	}

}
