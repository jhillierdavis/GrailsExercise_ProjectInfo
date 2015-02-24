package com.jhdit.projectinfo

import groovy.transform.ToString
import java.util.Date

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
class Project extends BaseEntity {		
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
		// priority unique: true // Enforce at service layer
		dueDate nullable: true
		projectManager nullable: false
		techLead nullable: true	// Assumption: Tech. Lead. can be assigned after project has begun
		priority ( validator: { value ->			
			return value > 0 // Ensure priority is greater than zero
			// NB: Unfortunately cannot seem to apply Projects.count() here for (dynamic) max value			
		})
		dueDate ( validator: { value ->
			// Ensure 'dueDate' is in the future (if non-NULL)
			return value ? value > new Date() : true 
		})
	}
	
	static mapping = {
		id column: 'project_id' // Override the default identifier property
		table 'projects' // Customized table name
		sort priority: "asc" // default sort
	}
	
	
	def beforeValidate()	{
		if (this.isPriorityExcessive())	{
			// TODO: JHD: Handle args in this validation message
			this.errors.rejectValue("priority", "default.invalid.max.size.message", "Too large!")
		}
	}	
	
	boolean isPriorityExcessive()	{
		def maxPriority = Project.count() + (this.isPersisted() ? 0 : 1)
		return this.priority > maxPriority
	}	
}
