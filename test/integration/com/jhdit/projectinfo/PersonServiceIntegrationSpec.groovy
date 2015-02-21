package com.jhdit.projectinfo

import grails.test.spock.IntegrationSpec

class PersonServiceIntegrationSpec extends IntegrationSpec {
	def personService // Auto-wired / injected

    def setup() {
    }

    def cleanup() {
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
			personService.deletePerson(person.id)
			
		then: "Person entity is deleted"
			!Person.findById(person.id)				
	}
	
}
