<%@ page import="transformation.TransformationRoutine; transformation.MethodInfo" %>

<link rel="stylesheet" href="${resource(dir: 'css', file: 'styling.css')}" type="text/css">

<g:form class="well">
    <div class="form-group">
        <label for="routine">Routine: </label>
        <g:select class="form-control" from="${TransformationRoutine.getAll()}" noSelection="[null: 'Please select a routine...']" optionKey="id" id="routine" name="routine" required="required"/>
    </div>
    <div class="form-group">
        <label for="transformation_method">Methodname: </label>
        <g:select class="form-control" from="${MethodInfo.values()}" noSelection="[null : 'Please select a method...']" id="transformation_method" name="transformation_method" required="required"/>
    </div>

    <g:remoteLink class="btn btn-warning" action="setProcedureProperties" controller="Transformation" update="procedureContainer" params="{routine: \$('#routine').val(), method: \$('#transformation_method').val()}">Submit selected</g:remoteLink>

    <div id="procedureContainer">
    </div>

</g:form>
