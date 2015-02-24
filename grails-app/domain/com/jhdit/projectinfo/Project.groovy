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
		final def currentPriority = this.priority
		final def maxPriority = getMaxPriority()

		if (currentPriority > maxPriority)	{

			//	Message parameter info. (from 'Grails in Action, 2nd ed.' P.95)
			//
			// {0}—The name of the domain class property.
			// {1}—The name of the domain class.
			// {2}—The invalid value.
			// {3}—The limiting value in the constraint, such as a maximum value or a matching pattern. Applies to match, max, min, maxSize, minSize, inList, and equals constraints.
			// {4}—The upper bound for a constraint ({3} is the lower bound). Applies to range and size constraints.

			this.errors.rejectValue("priority", "default.invalid.max.size.message", [
				'priority',
				'class Project',
				currentPriority,
				maxPriority] as Object[], "Priority exceeds maximum value!")
		}
	}

	private def getMaxPriority()	{
		return Project.count() + (this.isPersisted() ? 0 : 1)
	}
}
