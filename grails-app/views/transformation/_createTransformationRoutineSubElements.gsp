<div class="checkbox">
    <label for="to_update">
        <g:checkBox name="to_update" value="true"/> Update existing
    </label>
</div>

<div class="form-group">
    <g:field name="order_id" class="form-control" type="number" required="required" min="0" placeholder="Sequence number"/>
</div>

<div>
    <label for="property_container">
        Properties of object to update:
        <div id="property_container">
            <div id="update_properties" class="form-group">
                <g:render template="transformationRoutineProperties" params="${params}" />
            </div>
            <g:submitToRemote class="btn btn-warning" style="margin: 5px 0 5px 0" id="addPropertyBtn" type="button" url="[action: 'addPropertyPair']" update="update_properties" value="add propertypair"/>
            %{--<button type="button" id="addPropertyBtn" >add propertypair</button>--}%
        </div>
    </label>
</div>

<g:submitToRemote class="btn btn-primary pull-right" name="submitTransformationRoutine" id="submitTransformationRoutine" value="save TransformationRoutine"
                  action="createTransformationRoutine" onclick="return verifyForm()" onFailure="alert(XMLHttpRequest.responseText)"
                  onSuccess="alert('Routine was successfully saved!'); location.reload();"/>
<div style="clear: both;"></div>


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