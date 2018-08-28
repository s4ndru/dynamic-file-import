<%@ page import="transformation.MethodInfo" %>
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
                    <g:select name="object_key0" from="${params.entries}" noSelection="[null : 'Please select a entry...']" style="width: 48%"/>
                    <g:textField name="object_value0" id="object_value0" placeholder="propertyvalue" style="width: 48%"/>
                </div>
            </div>
            <button type="button" id="addObjectBtn" onclick="addNotableObject();">add objectpair</button>
        </div>
    </label>
</div>

<div id="parameter_container_container" class="form-group">
    <label id="parameter_container_label" for="parameter_container">
        Method parameters:
        <div id="parameter_container">
            <g:each in="${0..params.wrapperCount - 1}" var="i">
                <div id="parameter_properties${i}">
                    <div id="parameter_property${i}_0">
                        <g:if test="${MethodInfo.isFromObject((String)params.method, i)}">
                            <g:select name="parameter_key${i}_0" id="parameter_key${i}_0" from="${params.objectFields}" style="width: 48%"
                                      noSelection="[null : 'Please select a entry..']" placeholder="parameterkey" />
                        </g:if>
                        <g:elseif test="${MethodInfo.isDataWrapper((String)params.method, i) &&
                                (MethodInfo.getDatumSetPosition((String)params.method) == null ? true : MethodInfo.getDatumSetPosition((String)params.method).contains(0))}">
                            <g:select name="parameter_key${i}_0" id="parameter_key${i}_0" from="${params.entries}" style="width: 48%"
                                      noSelection="[null : 'Please select a datum...']" placeholder="parameterkey" />
                        </g:elseif>
                        <g:else>
                            <g:textField name="parameter_key${i}_0" id="parameter_key${i}_0" placeholder="parameterkey" style="width: 48%" />
                        </g:else>

                        <g:if test="${MethodInfo.isDataWrapper((String)params.method, i) &&
                                (MethodInfo.getDatumSetPosition((String)params.method) == null ? false : MethodInfo.getDatumSetPosition((String)params.method).contains(1))}">
                            <g:select name="parameter_value${i}_0" id="parameter_value${i}_0" from="${params.entries}" style="width: 48%"
                                      noSelection="[null : 'Please select a datum...']" placeholder="parametervalue" />
                        </g:if>
                        <g:else>
                            <g:textField name="parameter_value${i}_0" id="parameter_value${i}_0" placeholder="parametervalue" style="width: 48%" />
                        </g:else>

                    </div>
                </div>
                <g:if test="${MethodInfo.isRepeatable((String)params.method, i)}">
                    <button type="button" id="addParamBtn${i}" onclick="addParameterToWrapper(${i});">add parameterpair</button>
                </g:if>
                <hr/>
            </g:each>
        </div>
    </label>
</div>

<g:submitToRemote class="btn btn-primary pull-right" name="submitTransformationProcedure" value="save TransformationProcedure"
                  action="createTransformationProcedure" onclick="return verifyForm()" onFailure="alert(XMLHttpRequest.responseText)"
                  onSuccess="alert('Routine was successfully saved!'); location.reload();"/>

<div style="clear: both;"></div>

<g:javascript>
    var notable_object_counter = 0;

    function addNotableObject(){
        notable_object_counter++;

        var new_element = $('#notable_object0').clone(true);
        new_element.attr("id", "notable_object" + notable_object_counter);
        new_element.children("#object_key0").attr("name", "object_key" + notable_object_counter);
        new_element.children("#object_value0").attr("name", "object_value" + notable_object_counter);

        $("#notable_objects").append(new_element);
    }

    var wrapper_count = ${params.wrapperCount};
    var wrapper_index_array = [];

    for(var i = 0; i < wrapper_count; i++)
        wrapper_index_array.push(0)

    function addParameterToWrapper(wrapperIndex){
        wrapper_index_array[wrapperIndex]++;
        debugger;

        var new_element = $('#parameter_property' + wrapperIndex + "_0").clone(true);

        new_element.attr("id", "parameter_property" + wrapperIndex + "_" + wrapper_index_array[wrapperIndex]);
        new_element.attr("name", "parameter_property" + wrapperIndex + "_" + wrapper_index_array[wrapperIndex]);

        new_element.children("#parameter_key" + wrapperIndex + "_0").attr("name", "parameter_key" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);
        new_element.children("#parameter_value" + wrapperIndex + "_0").attr("name", "parameter_value" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);

        new_element.children("#parameter_key" + wrapperIndex + "_0").val("null");
        new_element.children("#parameter_value" + wrapperIndex + "_0").val("");

        new_element.children("#parameter_key" + wrapperIndex + "_0").attr("id", "parameter_key" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);
        new_element.children("#parameter_value" + wrapperIndex + "_0").attr("id", "parameter_value" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);

        $("#parameter_properties" + wrapperIndex).append(new_element);
    }
</g:javascript>
