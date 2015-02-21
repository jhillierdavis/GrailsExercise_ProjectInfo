package com.jhdit.projectinfo

import grails.transaction.Transactional

/**
 * Service layer encapsulating additional logic to handle operations on Person entities 
 */

@Transactional
class PersonService {
	final static String NO_MATCH = "No Person found with ID: "
	final static String PERSON_HAS_PROJECTS = "Cannot delete a person with associated projects! Person: "

    def deletePerson(def personId) {
		/*
		if (!personId)	{
			throw new IllegalArgumentException("NULL personId")
		}
		*/
		assert personId
		
		// Find the associated person
		Person person = Person.findById(personId)
		
		// Handle not found or has associated projects
		if (!person)	{
			throw new SystemException(NO_MATCH + personId)
		} else if (person.projects)	{
			throw new SystemException(PERSON_HAS_PROJECTS + person)
		}
		
		person.delete(flush: true)
    }
}
