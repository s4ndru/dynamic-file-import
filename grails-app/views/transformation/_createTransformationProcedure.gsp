<%@ page import="transformation.TransformationRoutine; transformation.MethodInfo" %>

<link rel="stylesheet" href="${resource(dir: 'css', file: 'styling.css')}" type="text/css">

<g:form class="well">
    <div class="form-group">
        <label for="routine">Routine: </label>
        <g:select class="form-control" from="${TransformationRoutine.getAll()}" noSelection="[null: 'Please select a routine...']" optionKey="id" id="routine" name="routine" required="required"/>
    </div>
    <div class="form-group">
        <label for="transformation_method">Methodname: </label>
        <g:select class="form-control" from="${MethodInfo.values().propertyName.sort()}" noSelection="[null : 'Please select a method...']" id="transformation_method" name="transformation_method" required="required"/>
    </div>

    %{-- using attribute onmousedown because onclick gets replaced with the grails ajax server request --}%
    %{-- onFailure was included just in case we do some serverside validation --}%
    <g:remoteLink class="btn btn-warning" action="setProcedureProperties" controller="Transformation" update="procedureContainer" onmousedown="return verifyForm()"
                  onFailure="alert(XMLHttpRequest.responseText)" params="{routine: \$('#routine').val(), method: \$('#transformation_method').val()}">Submit selected</g:remoteLink>

    <div id="procedureContainer">
    </div>

</g:form>

<g:javascript>
    function verifyForm(){

        if($("#routine").val() == "null"){
            alert("Please select a routine to which the procedure will belong to.");
            return false;
        }

        if($("#transformation_method").val() == "null"){
            alert("Please select a transformation method which will be applied by the procedure.");
            return false;
        }

        if($("#to_update").is(":checked") == true){
            for(var i = 0; i <= updatePropertyCounter; i++){
                if($("#update_key" + i).val() == "" || $("#update_value" + i).val() == ''){
                    alert("When having the update checkbox checked, all 'propertykey' and 'propertyvalue' fields have to be specified.");
                    return false;
                }
            }
        }
        return true;
    }
</g:javascript>