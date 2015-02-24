package com.jhdit.projectinfo

import java.util.Date;

import groovy.transform.ToString;

/**
 * Domain entity representing a person.
 * 
 * NB: Design assumptions:
 * 
 * - A person can play either a PM or Tech Lead role (or both) - no role info. is currently captured in the domain model
 * - A person's full name must be unique in the system.
 */

@ToString(includeNames=true, includeFields=true) // Display for dev. usage
class Person extends BaseEntity {
	String firstname
	String lastname
	
	// One-to-many association (with 'projectManager')
	static hasMany = [projects: Project]
	static mappedBy = [projects: 'projectManager']

    static constraints = {
		projects nullable: true
		firstname unique: 'lastname' // Ensure full-name is unique (assumes people with same full name use some form of variant)
    }
	
	static mapping = {
		id column: 'person_id' // Override the default identifier property
		table 'people' // Customized table name 
		
		// Use an intermediate join table for projects, rather than embedding it
		// TODO: JHD: Check required
		projects joinTable: [name: 'project_manager_projects',
							  key: 'person_id',
							  column: 'project_id']							  
	}		
	
	public String getFullname()	{
		return this.firstname + " " + this.lastname;
	}
}
