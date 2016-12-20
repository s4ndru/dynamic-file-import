<g:form controller="transformation" action="createTransformationRoutine">
    <div>
        <label for="belongs_to">Parser:</label>
        <g:select name="belongs_to" from="${extraction.DynamicParser.getAll()}" noSelection="[null : 'please select a parser...']" optionKey="id" required="required"/>
    </div>

    <div>
        <g:textField name="target_object" placeholder="Name of targetobject" required="required"/>
    </div>

    <div>
        <label for="to_update">Update existing:</label>
        <g:checkBox name="to_update" value="true"/>
    </div>

    <div>
        <g:field name="order_id" type="number" required="required" onchange="" min="0" placeholder="Order number"/>
    </div>

    <div>
        <label for="property_container">
            Properties of object to update:
            <div id="property_container">
                <div id="update_properties">
                    <div id="update_property0">
                        <g:textField name="update_key0" id="update_key0" placeholder="propertykey" />
                        <g:textField name="update_value0" id="update_value0" placeholder="propertyvalue" />
                    </div>
                </div>
                <button type="button" id="addPropertyBtn" >add propertypair</button>
            </div>
        </label>
    </div>

    <g:actionSubmit name="submitTransformationRoutine" id="submitTransformationRoutine" value="save TransformationRoutine" action="createTransformationRoutine" onclick="return verifyForm()" style="float: right;"/>
</g:form>

<g:javascript>

    var updatePropertyCounter = 0;

    function verifyForm(){
        if($("#belongs_to").val() == "null"){
            alert("Please select a parser.");
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

    $('#property_container').on('click', '#addPropertyBtn', function(){
        updatePropertyCounter++

        var div = document.createElement('div');
        div.id = 'update_property' + updatePropertyCounter;
        div.innerHTML = '<input name="update_key' + updatePropertyCounter + '" placeholder="propertykey" id="update_key' + updatePropertyCounter + '" type="text" required="required"> ' +
        '<input name="update_value' + updatePropertyCounter + '" placeholder="propertyvalue" id="update_value' + updatePropertyCounter + '" type="text" required="required">';

        $('#update_properties').append(div);
    });
</g:javascript>


