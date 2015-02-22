package com.jhdit.projectinfo

import static org.springframework.http.HttpStatus.*
import grails.transaction.Transactional

/**
 * Generated PersonController via static scaffolding:
 * 
 *  grails generate-all com.jhdit.projectinfo.Person
 *
 */

@Transactional(readOnly = false)
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

/*	
    @Transactional
    def delete(Person personInstance) {

        if (personInstance == null) {
            notFound()
            return
        }

        personInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Person.label', default: 'Person'), personInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }
*/
	def delete(Person personInstance) {
		if (personInstance == null) {
			notFound()
			return
		}

		assert personService // Check service injected
		
		def messageCode = 'default.deleted.message'
		def personId = personInstance.id		
		try {
			personService.deletePerson(personId)
		}
		catch (SystemException e) {
//			System.out.println "DEBUG: exception=${e}"
//			flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'person.label', default: 'Person'), personId])
			messageCode = 'default.not.deleted.message'
		}
		
		// redirect action: "index"
		request.withFormat {
			form multipartForm {
				flash.message = message(code: messageCode, args: [message(code: 'Person.label', default: 'Person'), personId])
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