
package com.jhdit.spock;

import spock.lang.Specification;
import spock.lang.Unroll;

/**
 * Simple UnitTest to confirm Spock test framework is functioning correctly (&amp; for quick reference).
 *
 * See https://code.google.com/p/spock/wiki/HelloSpock
 * and https://code.google.com/p/spock/wiki/SpockBasics
 *
 * Spock blocks:
 *
 * given: preconditions, data fixtures
 * setup: alias for given (JUnit syntax)
 * when: actions that trigger some outcome
 * then: make assertions about the outcome
 * expect: shorthand for when & then
 * where: applies varied inputs
 * and: subdivides other blocks
 * cleanup: post-conditions, housekeeping
 *
 */

class SpockCheckSpec extends Specification {

	@Unroll // Run each data row as a separate test
	def "length of Spock's and his friends' names"() {
		expect: "matching name length"
			name.size() == length
		
		where: "character name data"
			name | length
			"Spock" | 5
			"Kirk" | 4
			"Scotty" | 6
	}
}
