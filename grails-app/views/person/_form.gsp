<%@ page import="com.jhdit.projectinfo.Person" %>



<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'projects', 'error')} ">
	<label for="projects">
		<g:message code="person.projects.label" default="Projects" />
		
	</label>
	
<ul class="one-to-many">
<g:each in="${personInstance?.projects?}" var="p">
    <li><g:link controller="project" action="show" id="${p.id}">${p?.encodeAsHTML()}</g:link></li>
</g:each>
<li class="add">
<g:link controller="project" action="create" params="['person.id': personInstance?.id]">${message(code: 'default.add.label', args: [message(code: 'project.label', default: 'Project')])}</g:link>
</li>
</ul>


</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'firstname', 'error')} required">
	<label for="firstname">
		<g:message code="person.firstname.label" default="Firstname" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="firstname" required="" value="${personInstance?.firstname}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: personInstance, field: 'lastname', 'error')} required">
	<label for="lastname">
		<g:message code="person.lastname.label" default="Lastname" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="lastname" required="" value="${personInstance?.lastname}"/>

</div>

