package com.jhdit.projectinfo

class PersonControllerOld {
    static scaffold = true
	def  personService // injected
	
	
    def delete() {
		System.out.println "DEBUG: param=${params}"
		
		System.out.println "DEBUG: id=${params.id}"
		assert personService // Check injected
		
		def personId = params.id
        
        try {
			personService.deletePerson(personId)
        }
        catch (SystemException e) {
			System.out.println "DEBUG: exception=${e}"
            flash.message = message(code: 'default.not.deleted.message', args: [message(code: 'person.label', default: 'Person'), personId])
        }
        
		redirect action: "index"
    }
    
}
