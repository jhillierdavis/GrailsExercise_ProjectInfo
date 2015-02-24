
package com.jhdit.projectinfo

import com.jhdit.projectinfo.Person;

import grails.test.mixin.TestFor
import grails.validation.ValidationException;
import spock.lang.Specification

/**
 * Unit tests for (mocked) Person domain entity.
 */
@TestFor(Person)
@Mock(Person)
class PersonSpec extends Specification {
	Person testPerson // Default test double

	def setup() {
		testPerson = new Person(firstname: "John", lastname: "Smith")
	}

	void "Creation a mock Person entity & check 'persisted'"() {
		when:
		testPerson.save()

		then:
		Person.get(testPerson.id) == testPerson

	}

	void "Create, save & list mock people entities"() {
		when:
		def people = [
			new Person(firstname: "Kathy", lastname: "Sierra"),
			new Person(firstname: "Bert", lastname: "Bates"),
			new Person(firstname: "Martin", lastname: "Fowler"),
			new Person(firstname: "Josh", lastname: "Bloch"),
			new Person(firstname: "Gayle", lastname: "McDowell"),
			testPerson
		]
		people.eachWithIndex()	{ person, i ->
			person.save(failOnError: true, flush: (i == 1 + people.size() ? true: false))
		}

		then:
		!Person.list().empty
		Person.list().size() == people.size()
		Person.last() == testPerson
	}


	void "getFullname() check"()	{
		when "A person has a full name":
		testPerson = new Person(firstname: "John", lastname: "Smith")

		then "Their fullname is the concatonation":
		testPerson.getFullname() == "John Smith"
	}

	void "Cannot creation Person instance without mandatory properties"() {
		when:
		Person invalidPerson = new Person()
		invalidPerson.save(failOnError: true)

		then:
		def e = thrown(ValidationException)
		invalidPerson.errors.errorCount == 2
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
