package com.jhdit.projectinfo

import groovy.transform.ToString;

import java.util.Date;

import org.apache.log4j.spi.LoggerFactory;
import org.slf4j.Logger;

import com.jhdit.projectinfo.ProjectStatus;

/**
 * Domain entity capturing project information. 
 * 
 * Assumptions:
 * 
 * Optional properties (i.e. nullable):
 * 
 * techLead - assume this can be added during a running project, or omitted for a non-technical project (unlike projectManager)
 * dueDate  - assume this is date only (not-time)
 */

@ToString(includeNames=true, includeFields=true) // Display for dev. usage
class Project {
	// private static Logger log = LoggerFactory.getLogger(Project.class)

	Date dateCreated // Auto-populated audit field
	Date lastUpdated // Auto-populated audit field
		
	String name
	String code
	Person projectManager
	Person techLead
	Integer priority
	ProjectStatus currentStatus
	Date dueDate

	static constraints = {
		name blank: false, size:3..100, unique: false
		code blank: false, size:3..20, unique: true
		priority unique: true
		dueDate nullable: true
		projectManager nullable: false
		techLead nullable: true	// Assumption: Tech. Lead. can be assigned after project has begun
		priority ( validator: { value ->
			return value > 0 // Ensure priority is greater than zero			
		})
		dueDate ( validator: { value ->
			// Ensure 'dueDate' is in the future (if non-NULL)
			return value ? value > new Date() : true 
		})
	}
	
	static mapping = {
		id column: 'project_id' // Override the default identifier property
		table 'projects' // Customized table name
		// autoTimestamp true 
		sort priority: "asc" // default sort
		// sort: 'priority'
	}
}