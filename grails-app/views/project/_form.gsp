<%@ page import="com.jhdit.projectinfo.Project" %>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'name', 'error')} required">
	<label for="name">
		<g:message code="project.name.label" default="Name" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="name" maxlength="100" required="" value="${projectInstance?.name}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'code', 'error')} required">
	<label for="code">
		<g:message code="project.code.label" default="Code" />
		<span class="required-indicator">*</span>
	</label>
	<g:textField name="code" maxlength="20" required="" value="${projectInstance?.code}"/>

</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'priority', 'error')} required">
	<label for="priority">
		<g:message code="project.priority.label" default="Priority" />
		<span class="required-indicator">*</span>
	</label>
 	
	<g:field name="priority" type="number" value="${projectInstance.priority}" required="" min="1" max="${Project.count() + (projectInstance.id ? 0 : 1)}" />
</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'dueDate', 'error')} ">
	<label for="dueDate">
		<g:message code="project.dueDate.label" default="Due Date" />
		
	</label>
	<g:datePicker name="dueDate" precision="day"  value="${projectInstance?.dueDate}" default="none" noSelection="['': '']" relativeYears="[0..5]" />

</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'projectManager', 'error')} required">
	<label for="projectManager">
		<g:message code="project.projectManager.label" default="Project Manager" />
		<span class="required-indicator">*</span>
	</label>
	<g:select id="projectManager" name="projectManager.id" from="${com.jhdit.projectinfo.Person.list()}" optionKey="id" optionValue="fullname"  required="" value="${projectInstance?.projectManager?.id}" class="many-to-one"/>

</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'techLead', 'error')} ">
	<label for="techLead">
		<g:message code="project.techLead.label" default="Tech Lead" />
		
	</label>
	<g:select id="techLead" name="techLead.id" from="${com.jhdit.projectinfo.Person.list()}" optionKey="id" optionValue="fullname" value="${projectInstance?.techLead?.id}" class="many-to-one" noSelection="['null': '']"/>

</div>

<div class="fieldcontain ${hasErrors(bean: projectInstance, field: 'currentStatus', 'error')} required">
	<label for="currentStatus">
		<g:message code="project.currentStatus.label" default="Current Status" />
		<span class="required-indicator">*</span>
	</label>
	<g:select name="currentStatus" from="${com.jhdit.projectinfo.ProjectStatus?.values()}" keys="${com.jhdit.projectinfo.ProjectStatus.values()*.name()}" required="" value="${projectInstance?.currentStatus?.name()}" />

</div>

