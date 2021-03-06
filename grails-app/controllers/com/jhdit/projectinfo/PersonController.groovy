package com.jhdit.projectinfo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * Front controller: PersonController 
 * 
 * Uses an injected service (see @PersonService) for any non-trivial CRUD operations (to encapsulate application logic in service layer). 
 * 
 * Initial version generated via Grails static scaffolding:
 * 
 *  grails generate-all com.jhdit.projectinfo.Person
 */

@Transactional(readOnly = true) // Default (overridden in udpate & delete)
class PersonController {
	def personService // Injected

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Person.list(params), model:[personInstanceCount: Person.count()]
    }

    def show(Person personInstance) {
        respond personInstance
    }

    def create() {
        respond new Person(params)
    }

    @Transactional
    def save(Person personInstance) {
        if (personInstance == null) {
            notFound()
            return
        }

        if (personInstance.hasErrors()) {
            respond personInstance.errors, view:'create'
            return
        }

        personInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'person.label', default: 'Person'), personInstance.id])
                redirect personInstance
            }
            '*' { respond personInstance, [status: CREATED] }
        }
    }

    def edit(Person personInstance) {
        respond personInstance
    }

    @Transactional
    def update(Person personInstance) {
        if (personInstance == null) {
            notFound()
            return
        }

        if (personInstance.hasErrors()) {
            respond personInstance.errors, view:'edit'
            return
        }

        personInstance.save flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Person.label', default: 'Person'), personInstance.id])
                redirect personInstance
            }
            '*'{ respond personInstance, [status: OK] }
        }
    }

	@Transactional
	def delete(Person personInstance) {
		if (personInstance == null) {
			notFound()
			return
		}

		def messageCode = 'default.deleted.message'
		assert personService // Check service injected
		try {
			personService.deletePerson(personInstance)
		}
		catch (SystemException e) {
			messageCode = 'default.not.deleted.message'
		}
		
		// redirect action: "index"
		request.withFormat {
			form multipartForm {
				flash.message = message(code: messageCode, args: [message(code: 'Person.label', default: 'Person'), personInstance.id])
				redirect action:"index", method:"GET"
			}
			'*'{ render status: NO_CONTENT }
		}
	}
	

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'person.label', default: 'Person'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
