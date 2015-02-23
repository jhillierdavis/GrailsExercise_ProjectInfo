package com.jhdit.projectinfo

import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataIntegrityViolationException;

import grails.transaction.Transactional

/**
 * Service layer encapsulating additional logic to handle operations on Person entities 
 */

@Transactional
class PersonService {
	private static final log = LogFactory.getLog(this)
	
	final static String NO_MATCH_BY_ID = "No Person found with ID: "
	final static String PERSON_HAS_PROJECTS = "Cannot delete a person with associated projects! Person: "

    def deletePerson(final long personId) {
		/*
		if (!personId)	{
			throw new IllegalArgumentException("NULL personId")
		}
		*/
		assert personId
		
		// Find the associated person
		Person person = Person.findById(personId)
		if (!person)	{
			throw new SystemException(NO_MATCH_BY_ID + personId)
		}

		this.deletePerson(person)		
    }
	
	def deletePerson(final Person person)	{		
		assert person
		
		if (person.projects || Project.findAllByProjectManager(person) || Project.findAllByTechLead(person))	{
			throw new SystemException(PERSON_HAS_PROJECTS + person)
		}
		
		try	{
			person.delete(failOnError: true, flush: true)
		} catch (DataIntegrityViolationException e)	{
			log.debug("DEBUG: Failed to delete person: ${person} with projects: ${person.projects}")
			throw new SystemException(PERSON_HAS_PROJECTS + person, e)
		}
	}
}
