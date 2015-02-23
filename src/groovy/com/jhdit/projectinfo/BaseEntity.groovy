package com.jhdit.projectinfo

import java.util.Date;

/*
 * Abstract (non-persisted) base class for domain classes
 */

abstract class BaseEntity {
	Date dateCreated // Auto-populated audit field
	Date lastUpdated // Auto-populated audit field
	
	/**
	 * Indicates whether the associated entity has been persisted to the database (based on the presence of an assigned ID)
	 * @return True if persisted
	 */
	
	boolean isPersisted()	{
		return this.id // Note: Groovy Truth - non-null object references are coerced to true.
	}
}
