package com.jhdit.projectinfo

import grails.test.spock.IntegrationSpec
import static com.jhdit.projectinfo.ProjectStatus.*

/**
 * Integration tests for PersonService.
 * 
 * E.g. run via:
 * 
 * grails test-app integration: PersonServiceIntegrationSpec
 */

class PersonServiceIntegrationSpec extends IntegrationSpec {
	def personService // Auto-wired / injected
	def service // Alias (for convenience)

	def setup() {
		service = personService
	}

	void "Check service injection" () {
		expect:
		this.service
	}

	void "A person entity without associated projects can be deleted"()	{
		given: 'A person entity without projects'
		Person person = new Person(firstname: "John", lastname: "Doe")

		when: "Saved"
		person.save(flush:true)

		then: "Exists"
		// person.exists()
		Person.findById(person.id) == person

		when: "Deleted"
		service.deletePerson(person.id)

		then: "Person entity is deleted"
		!Person.findById(person.id)
	}

	void "Deletion permitted for a person entity without associated projects"()	{
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


	void "Deletion prohibited for a person who is a PM on projects"()	{
		given: 'A persisted person entity'
		Person pm = new Person(firstname: "Billy", lastname: "Busy")
		pm.save(failOnError: true, flush: true)

		when: "Associated with a project & persist"
		Project project = new Project(name: "Test Project", code: "PT_01", priority: 1, currentStatus: RELEASE, projectManager: pm)
		project.save(failOnError: true, flush: true)

		then: "Exists"
		Person.findById(pm.id) == pm
		Person.exists(pm.id)

		when: "Deletion attempted"
		service.deletePerson(pm.id)

		then: "Exception is thrown"
		def ex = thrown(SystemException)
		ex.message.startsWith(PersonService.PERSON_HAS_PROJECTS)
		Person.exists(pm.id)
		Person.findById(pm.id).getFullname() == pm.getFullname() // Still exists!
	}

	void "Deletion prohibited for a person who is a Tech. Lead on projects"()	{
		given: 'A persisted person entity'
		Person pm = new Person(firstname: "Alice", lastname: "Agile")
		Person techLead = new Person(firstname: "Josh", lastname: "Java")
		pm.save()
		techLead.save(flush: true)

		when: "Associated with a project & persist"
		Project project = new Project(name: "Test Project", code: "PT_01", priority: 1, currentStatus: RELEASE, projectManager: pm, techLead: techLead)
		project.save(flush: true)

		then: "Exists"
		Person.findById(techLead.id) == techLead
		Person.exists(techLead.id)
		Person.exists(pm.id)

		when: "Deletion attempted"
		service.deletePerson(techLead.id)

		then: "Exception is thrown"
		def ex = thrown(SystemException)
		ex.message.startsWith(PersonService.PERSON_HAS_PROJECTS)
		Person.exists(techLead.id)
		Person.exists(pm.id)
		Person.findById(techLead.id).getFullname() == techLead.getFullname() // Still exists!
	}


}
