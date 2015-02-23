package com.jhdit.projectinfo

import static org.springframework.http.HttpStatus.*

/**
 * Generated PersonController via static scaffolding:
 *
 *  grails generate-all com.jhdit.projectinfo.Project
 */


import grails.transaction.Transactional

@Transactional(readOnly = false)
class ProjectController {
	def projectService // Injected

    static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

    def index(Integer max) {
        params.max = Math.min(max ?: 10, 100)
        respond Project.list(params), model:[projectInstanceCount: Project.count()]
    }

    def show(Project projectInstance) {
		System.out.println("DEBUG: ProjectController.show(${projectInstance?.code}) ... ")
        respond projectInstance
    }

    def create() {
		System.out.println("DEBUG: ProjectController.create() ... ")
        respond new Project(params)
    }

    @Transactional
    def save(Project projectInstance) {
		System.out.println("DEBUG: ProjectController.save(${projectInstance?.code}) ... ")
        if (projectInstance == null) {
            notFound()
            return
        }
/*
        if (projectInstance.hasErrors()) {
            respond projectInstance.errors, view:'create'
            return
        }
*/
		if (projectInstance.hasErrors() && !this.projectService.hasSuppressedError(projectInstance)) {
				respond projectInstance.errors, view:'create'
				return
		}
		
		assert this.projectService
		
		// Check priority <= max
		if (this.projectService.isPriorityTooLarge(projectInstance))	{
			flash.message = "Priority is too large!"
			respond projectInstance.errors, view:'create'
			return

		}
		this.projectService.saveProject projectInstance
		

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.created.message', args: [message(code: 'project.label', default: 'Project'), projectInstance.id])
                redirect projectInstance
            }
            '*' { respond projectInstance, [status: CREATED] }
        }
    }

    def edit(Project projectInstance) {
		System.out.println("DEBUG: ProjectController.edit(${projectInstance?.code}) ... ")
        respond projectInstance
    }

    // @Transactional
    def update(Project projectInstance) {
		System.out.println("DEBUG: ProjectController.update(${projectInstance?.code}) ... ")
		
        if (projectInstance == null) {
            notFound()
            return
        }

/*		
        if (projectInstance.hasErrors()) {
            respond projectInstance.errors, view:'edit'
            return
        }

        projectInstance.save flush:true
*/
		
		// if (projectInstance.hasErrors() && !this.projectService.hasSuppressedError(projectInstance)) {
		if (projectInstance.hasErrors()) {
			respond projectInstance.errors, view:'edit'
			return
		}
		
		assert this.projectService
		
		// Check priority <= max
		if (this.projectService.isPriorityTooLarge(projectInstance))	{
			flash.message = "Priority is too large!"
			respond projectInstance.errors, view:'edit'
			return
	
		}
		// this.projectService.updateProject projectInstance
		// projectInstance.save flush:true
		System.out.println("DEBUG: Discard updates ... ")
		Project p = Project.get(projectInstance.id)
		p.discard()
		
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.updated.message', args: [message(code: 'Project.label', default: 'Project'), projectInstance.id])
                redirect projectInstance
            }
            '*'{ respond projectInstance, [status: OK] }
        }
    }

    @Transactional
    def delete(Project projectInstance) {

        if (projectInstance == null) {
            notFound()
            return
        }

        projectInstance.delete flush:true

        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.deleted.message', args: [message(code: 'Project.label', default: 'Project'), projectInstance.id])
                redirect action:"index", method:"GET"
            }
            '*'{ render status: NO_CONTENT }
        }
    }

    protected void notFound() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.not.found.message', args: [message(code: 'project.label', default: 'Project'), params.id])
                redirect action: "index", method: "GET"
            }
            '*'{ render status: NOT_FOUND }
        }
    }
}
