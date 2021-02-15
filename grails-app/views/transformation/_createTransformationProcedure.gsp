<%@ page import="transformation.TransformationRoutine; transformation.MethodInfo" %>

<g:form name="divTransformationForm">
    <div>
        <label for="belongs_to">Routine: </label>
        <g:select from="${TransformationRoutine.getAll()}" noSelection="[null: 'Please select a routine...']" optionKey="id" id="belongs_to" name="belongs_to" required="required"/>
    </div>
    <div>
        <label for="transformation_method">Methodname: </label>
        <g:select from="${MethodInfo.getAllProceduresExceptCrossProcedures()}" noSelection="[null: 'Please select a method...']" id="transformation_method" name="transformation_method" required="required"/>
    </div>

    <div id="divCrossMethod">
        <label for="cross_method">Crossmethod: </label>
        <g:select from="${MethodInfo.getCrossProceduresMap()}" noSelection="[null: 'Please select a cross method...']" id="cross_method" name="cross_method" required="required"/>
    </div>

    %{-- using attribute onmousedown because onclick gets replaced with the grails ajax server request --}%
    %{-- onFailure was included just in case we do some server side validation --}%
    <div>
        <g:remoteLink action="setProcedureProperties" controller="Transformation" update="procedureContainer" onmousedown="return verifyForm()"
                  onFailure="alert(XMLHttpRequest.responseText)" params="{belongs_to: \$('#belongs_to').val(), method: \$('#transformation_method').val(), crossMethod: \$('#cross_method').val()}">Submit selected</g:remoteLink>
    </div>
    <div id="procedureContainer">
    </div>

</g:form>

<script>
    $("#divCrossMethod").toggle(false);

    $("#transformation_method").change(function(){
        if(this.value === "cacheInfoForCrossProcedure"){
            $("#divCrossMethod").toggle(true);
        }
        else{
            $("#divCrossMethod").toggle(false);
            $("#cross_method").val("null");
        }
    });

    function verifyForm(){

        if($("#belongs_to").val() === "null"){
            alert("Please select a routine to which the procedure will belong to.");
            return false;
        }

        if($("#transformation_method").val() === "null"){
            alert("Please select a transformation method which will be applied by the procedure.");
            return false;
        }

        /*if($("#to_update").is(":checked") == true){
            for(var i = 0; i <= updatePropertyCounter; i++){
                if($("#update_key" + i).val() === "" || $("#update_value" + i).val() === ''){
                    alert("When having the update checkbox checked, all 'propertykey' and 'propertyvalue' fields have to be specified.");
                    return false;
                }
            }
        }*/

        if($("#transformation_method").val() === "cacheInfoForCrossProcedure" && $("#cross_method").val() === "null"){
            alert("Please select a cross method which will be used in the method 'cacheInforForCrossProcedure'.");
            return false;
        }

        return true;
    }
</script>