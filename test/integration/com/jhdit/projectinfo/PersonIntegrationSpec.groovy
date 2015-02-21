package com.jhdit.projectinfo

import com.jhdit.projectinfo.Person;

import grails.test.spock.IntegrationSpec
import grails.validation.ValidationException;

/**
 * Integration test for a Person domain entity. 
 */

class PersonIntegrationSpec extends IntegrationSpec {
	Person testPerson // Default test double
	
		def setup() {
			 testPerson = new Person(firstname: "John", lastname: "Smith")
		}

    void "Creation a person entity & check persisted"() {
		when:
			Person person = new Person(firstname: "John", lastname: "Smith")
			person.save()
			
		then:
			Person.get(person.id) == person
		
    }
	
	void "Delete an existing person entity & then ensure non-existent"() {
		given:
		Person person = new Person(firstname: "John", lastname: "Smith")
		person.save()

		when:
		person.delete()
		
		then:
			!person.exists()
			!Person.get(person.id)		
	}
	
	void "Update an existing person entity & then ensure current"() {
		given:
		Person person = new Person(firstname: "John", lastname: "Smith")
		person.save()

		when:
		person.firstname = "Jane"
		person.lastname = "Jones"
		person.save()
		
		then:
			def foundPerson = Person.findByFirstname("Jane")
			foundPerson.lastname == "Jones"
	}

	
	void "List existing people entities"() {
		given:
		Person person1 = new Person(firstname: "John", lastname: "Smith")
		Person person2 = new Person(firstname: "Jane", lastname: "Jones")
		Person person3 = new Person(firstname: "Eve", lastname: "Adams")

		when:
		person1.save()
		person2.save()
		person3.save()

		then:
		def existingPeopleList = Person.list()
		existingPeopleList.size() == 3
	}
	
	void "Fullname unique, on save"() {
		when:
			Person personWithSameFullname = new Person(firstname: testPerson.firstname, lastname: testPerson.lastname)
			testPerson.save()
			personWithSameFullname.save(failOnError: true, flush: true)
			
		then:
			def e = thrown(ValidationException)
			personWithSameFullname.errors.errorCount == 1
	}
	
	void "Fullname unique, on update"() {
		given:
			Person anotherPerson = new Person(firstname: 'Jane', lastname: testPerson.lastname)
			testPerson.save()
			anotherPerson.save(failOnError: true, flush: true)
		
		when:
			anotherPerson.firstname = testPerson.firstname
			anotherPerson.save(failOnError: true, flush: true)
			
		then:
			def e = thrown(ValidationException)
			anotherPerson.errors.errorCount == 1
	}

}
