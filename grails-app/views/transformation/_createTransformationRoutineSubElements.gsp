<div>
    <label for="to_update">Update existing:</label>
    <g:checkBox name="to_update" value="true"/>
</div>

<div>
    <g:field name="order_id" type="number" required="required" onchange="" min="0" placeholder="Sequence number"/>
</div>

<div>
    <label for="property_container">
        Properties of object to update:
        <div id="property_container">
            <div id="update_properties">
                <g:render template="transformationRoutineProperties" params="${params}" />
            </div>
            <g:submitToRemote id="addPropertyBtn" type="button" url="[action: 'addPropertyPair']" update="update_properties" value="add propertypair"/>
            %{--<button type="button" id="addPropertyBtn" >add propertypair</button>--}%
        </div>
    </label>
</div>

<g:actionSubmit name="submitTransformationRoutine" id="submitTransformationRoutine" value="save TransformationRoutine" action="createTransformationRoutine" onclick="return verifyForm()" style="float: right;"/>


<g:javascript>

    %{--$('#property_container').on('click', '#addPropertyBtn', function(){--}%
        %{--updatePropertyCounter++--}%

        %{--var update_key_select = '${select(name: "update_key", from: params.entries, optionKey: "field", noSelection: [null : "Please select a entry..."])}';--}%
        %{--var update_value_select = '${select(name: "update_value", from: params.properties, noSelection: [null : "Please select a property..."])}';--}%
        %{--// update_key_select = update_key_select.substring(0, update_key_select.indexOf('name="update_key')) + updatePropertyCounter + update_key_select.substring(update_key_select.indexOf('name="update_key'));--}%
        %{--// update_key_select = update_key_select.substring(0, update_key_select.indexOf('id="update_key')) + updatePropertyCounter + update_key_select.substring(update_key_select.indexOf('id="update_key'));--}%
        %{--// update_value_select = update_value_select.substring(0, update_value_select.indexOf('name="update_value')) + updatePropertyCounter + update_key_select.substring(update_key_select.indexOf('name="update_value'));--}%
        %{--// update_value_select = update_value_select.substring(0, update_value_select.indexOf('id="update_value')) + updatePropertyCounter + update_key_select.substring(update_key_select.indexOf('id="update_value'));--}%

        %{--var div = document.createElement('div');--}%
        %{--div.id = 'update_property' + updatePropertyCounter;--}%
        %{--div.innerHTML = update_key_select + update_value_select;--}%
        %{--// div.innerHTML = '<input name="update_key' + updatePropertyCounter + '" placeholder="propertykey" id="update_key' + updatePropertyCounter + '" type="text" required="required"> ' +--}%
        %{--//         '<input name="update_value' + updatePropertyCounter + '" placeholder="propertyvalue" id="update_value' + updatePropertyCounter + '" type="text" required="required">';--}%

        %{--$('#update_properties').append(div);--}%
    %{--});--}%
</g:javascript>