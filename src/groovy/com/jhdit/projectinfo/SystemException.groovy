package com.jhdit.projectinfo

/**
 * General unchecked exception for system errors.
 */

class SystemException extends RuntimeException {
	public SystemException() {
		super()
	}

	public SystemException(final String message) {
		super(message)
	}

	public SystemException(final String message, final Throwable cause) {
		super(message, cause)
	}

	public SystemException(final Throwable cause) {
		super(cause)
	}
}

