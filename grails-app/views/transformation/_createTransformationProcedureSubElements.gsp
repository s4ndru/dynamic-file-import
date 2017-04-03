<div class="form-group">
    <g:field class="form-control" name="order_id" type="number" min="0" required="required" placeholder="Sequence number"/>
</div>
<div class="checkbox">
    <label for="is_repetitive">
        <g:checkBox name="is_repetitive" value="true"/> Repetitive procedure
    </label>
</div>

<div class="form-group">
    <label for="notable_objects_container">
        Notable parsing objects:
        <div id="notable_objects_container">
            <div id="notable_objects">
                <div id="notable_object0">
                    <g:select name="object_key${i}" from="${params.entries}" value="${params["object_key" + i] ? params["object_key" + i] : null}" id="object_key${i}" noSelection="[null : 'Please select a entry...']"/>
                    <g:textField name="object_value0" id="object_value0" placeholder="propertyvalue" />
                </div>
            </div>
            <button type="button" id="addObjectBtn" >add objectpair</button>
        </div>
    </label>
</div>

<div class="form-group" id="parameter_container_container">
    <label id="parameter_container_label" for="parameter_container">
        Method parameters:
        <div id="parameter_container">
            <div id="parameter_properties0">
                <div id="parameter_property0|0">
                    <g:textField name="parameter_key0|0" id="parameter_key0|0" placeholder="parameterkey" />
                    <g:textField name="parameter_value0|0" id="parameter_value0|0" placeholder="parametervalue" />
                </div>
            </div>
            <button type="button" id="addParamBtn0">add parameterpair</button>
            <hr>
        </div>
    </label>
    <button type="button" id="addWrapperBtn" >add wrapper</button>
</div>

<g:submitToRemote class="btn btn-primary pull-right" name="submitTransformationProcedure" id="submitTransformationProcedure" value="save TransformationProcedure"
                  action="createTransformationProcedure" onclick="return verifyForm()" onFailure="alert(XMLHttpRequest.responseText)"
                  onSuccess="alert('Routine was successfully saved!'); location.reload();"/>

<div style="clear: both;"></div>