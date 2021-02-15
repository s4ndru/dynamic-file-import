<%@ page import="extraction.DynamicParser" %>

<g:form name="divTransformationForm" controller="transformation" action="createTransformationRoutine">
    <div>
        <label for="belongs_to">Parser:</label>
        <g:select name="belongs_to" from="${DynamicParser.getAll()}" noSelection="[null: 'Please select a parser...']" optionKey="id" required="required" />
                  %{-- onchange="${remoteFunction(action: 'setRoutineProperties', update: "routineContainer", params: '\'parser=\' + this.value')};" />--}%
    </div>
    <div>
        <label for="target_object">Object:</label>
        <g:select name="target_object" from="${params.domainList}" noSelection="[null : 'Please select a target object...']" required="required"/>
    </div>
    <div>
        %{-- using attribute onmousedown because onclick gets replaced with the grails ajax server request --}%
        %{-- onFailure was included just in case we do some serverside validation --}%
        <g:remoteLink action="setRoutineProperties" controller="Transformation" update="routineContainer" onmousedown="return verifyForm()"
                      onFailure="alert(XMLHttpRequest.responseText)" params="{parser: \$('#belongs_to').val(), targetObject: \$('#target_object').val()}">Submit selected</g:remoteLink>
    </div>
    <div id="routineContainer">
    </div>
</g:form>

<script>

    var updatePropertyCounter = 0;

    function verifyForm(){

        if($("#belongs_to").val() == "null"){
            alert("Please select a parser to which the routine will belong to.");
            return false;
        }

        if($("#target_object").val() == "null"){
            alert("Please select a target object for the routine.");
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

</script>


