<%@ page import="transformation.TransformationController; transformation.MethodInfo" %>
<div>
    <g:field name="order_id" type="number" min="0" required="required" placeholder="Sequence number"/>
</div>
<div>
    <label for="is_repetitive">
        <g:checkBox name="is_repetitive" value="${params.isCrossMethod == "true" ? "" : "true"}" /> Repetitive procedure
    </label>
</div>

<div>
    <label for="notable_objects_container">
        Notable parsing objects:
        <div id="notable_objects_container">
            <div id="notable_objects">
                <div id="notable_object0">
                    <g:select name="object_key0" from="${params.entries}" noSelection="[null : 'Please select an entry...']" style="width: 50%"/>
                    <g:textField name="object_value0" id="object_value0" placeholder="property value" style="width: 50%"/>
                </div>
            </div>
            <button type="button" id="addObjectBtn" onclick="addNotableObject('');">add objectpair</button>
        </div>
    </label>
</div>

<g:if test="${params.wrapperCount != 0}">
    <div id="parameter_container_container">
        <label id="parameter_container_label" for="parameter_container">
            Method parameters:
            <div id="parameter_container">
                <g:if test="${params.isCrossMethod == "true"}">
                    <g:hiddenField name="isCrossMethod" value="${params.isCrossMethod}"/>
                    <div>
                        <label for="cross_is_repetitive">
                            <g:checkBox name="cross_is_repetitive" value="true" /> Repetitive cross-procedure
                        </label>
                    </div>

                    <div>
                        <label for="cross_notable_objects_container">
                            Notable parsing objects for cross-procedure:
                            <div id="cross_notable_objects_container">
                                <div id="cross_notable_objects">
                                    <div id="cross_notable_object0">
                                        <g:select name="cross_object_key0" from="${params.entries}" noSelection="[null : 'Please select an entry...']" style="width: 50%"/>
                                        <g:textField name="cross_object_value0" placeholder="property value" style="width: 50%"/>
                                    </div>
                                </div>
                                <button type="button" id="cross_addObjectBtn" onclick="addNotableObject('cross_');">add objectpair for cross-procedure</button>
                            </div>
                        </label>
                    </div>
                </g:if>

                <g:each in="${0..params.wrapperCount - 1}" var="i">
                    <div id="parameter_properties${i}">
                        <div id="parameter_property${i}_0">
                            <g:if test="${MethodInfo.isObjectOnWrapperAndTuplePosition((String)params.method, i, 0)}">
                                <g:select name="parameter_key${i}_0" id="parameter_key${i}_0" from="${params.objectFields}" style="width: 48%"
                                          noSelection="[null : 'Please select a property..']" placeholder="parameterkey" />
                            </g:if>
                            <g:elseif test="${MethodInfo.isDatumOnWrapperAndTuplePosition((String)params.method, i, 0)}">
                                <g:select name="parameter_key${i}_0" id="parameter_key${i}_0" from="${params.entries}" style="width: 48%"
                                          noSelection="[null : 'Please select a datum...']" placeholder="parameterkey"
                                          onchange="if(this.value == '${TransformationController.arithmeticPlaceholder}'){
                                          var belongs_to = ${params.belongs_to}; var right_value = \$('#parameter_value1_0').val(); ${remoteFunction(action: 'setArithmeticParameters', update: "parameter_properties1",
                                          params: '\'right_value=\' + right_value + \'&left_value=\' + this.value + \'&belongs_to=\' + belongs_to')}}"/>
                            </g:elseif>
                            <g:else>
                                <g:textField name="parameter_key${i}_0" id="parameter_key${i}_0" placeholder="parameterkey" style="width: 48%" />
                            </g:else>

                            %{-- Following two ifs are special cases --}%
                            <g:if test="${params.method == "unaryArithmeticOperation" && i == 0}">
                                <g:select name="parameter_value${i}_0" id="parameter_value${i}_0" from="${MethodInfo.getUnaryArithmeticOperators()}" style="width: 48%"
                                          noSelection="[null : 'Please select a operation..']" />
                            </g:if>
                            <g:elseif test="${params.method == "arithmeticOperation" && i == 0}">
                                <g:select name="parameter_value${i}_0" id="parameter_value${i}_0" from="${MethodInfo.getArithmeticOperators()}" style="width: 48%"
                                          noSelection="[null : 'Please select a operation..']" />
                            </g:elseif>
                            %{-- If a method has a parameter with a class name (which is implied if the function doesn't return null), then it can be assumed that is on the first wrapper and the right value --}%
                            <g:elseif test="${MethodInfo.getSecondClassPropertiesPosition(params.method) != null && i == 0}">
                                <g:select name="parameter_value${i}_0" id="parameter_value${i}_0" from="${params.domainList}" style="width: 48%"
                                          noSelection="[null : 'Please select a class..']" onchange="var method = '${params.method}';var index = ${i + 1}; var belongs_to = ${params.belongs_to};
                                          ${remoteFunction(action: 'setSecondClassProperties', update: "parameter_properties${i + 1}",
                                          params: '\'selectedClass=\' + this.value + \'&method=\' + method + \'&index=\' + index + \'&belongs_to=\' + belongs_to')}"/>
                            </g:elseif>
                            <g:elseif test="${MethodInfo.isObjectOnWrapperAndTuplePosition((String)params.method, i, 1)}">
                                <g:select name="parameter_value${i}_0" id="parameter_value${i}_0" from="${params.objectFields}" style="width: 48%"
                                          noSelection="[null : 'Please select a property..']"/>
                            </g:elseif>
                            <g:elseif test="${MethodInfo.isDatumOnWrapperAndTuplePosition((String)params.method, i, 1)}">
                                <g:select name="parameter_value${i}_0" id="parameter_value${i}_0" from="${params.entries}" style="width: 48%"
                                          noSelection="[null : 'Please select a datum...']" placeholder="parametervalue"
                                          onchange="if(this.value == '${TransformationController.arithmeticPlaceholder}'){
                                          var belongs_to = ${params.belongs_to}; var left_value = \$('#parameter_key1_0').val(); ${remoteFunction(action: 'setArithmeticParameters', update: "parameter_properties1",
                                          params: '\'right_value=\' + this.value + \'&left_value=\' + left_value + \'&belongs_to=\' + belongs_to')}}"/>
                            </g:elseif>
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
</g:if>
<g:submitToRemote name="submitTransformationProcedure" value="save TransformationProcedure"
                  action="createTransformationProcedure" onclick="return verifyForm()" onFailure="alert(XMLHttpRequest.responseText)"
                  onSuccess="alert('Procedure was successfully saved!'); location.reload();"/>

<div style="clear: both;"></div>

<g:javascript>

debugger;
    if("${params.isCrossMethod}" === "true")
        $("#is_repetitive").prop('disabled', true);

    if("${MethodInfo.isRightSideNotUsed(params.method)}" === "true")
        $("#parameter_value0_0").prop('disabled', true);

    var notable_object_counter = 0;

    // If this function is used for cross procedures, then 'cross' is prepended to all selectors
    function addNotableObject(prepend){
        notable_object_counter++;

        var new_element = $("#" + prepend + "notable_object0").clone(true);
        new_element.attr("id", prepend + "notable_object" + notable_object_counter);

        new_element.children("#" + prepend + "object_key0").attr("name", prepend + "object_key" + notable_object_counter);
        new_element.children("#" + prepend + "object_key0").attr("id", prepend + "object_key" + notable_object_counter);

        new_element.children("#" + prepend + "object_value0").val("");
        new_element.children("#" + prepend + "object_value0").attr("name", prepend + "object_value" + notable_object_counter);
        new_element.children("#" + prepend + "object_value0").attr("id", prepend + "object_value" + notable_object_counter);


        $("#" + prepend + "notable_objects").append(new_element);
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

        if($("#parameter_key" + wrapperIndex + "_0").is("select"))
            new_element.children("#parameter_key" + wrapperIndex + "_0").val("null");
        else
            new_element.children("#parameter_key" + wrapperIndex + "_0").val("");
        if($("#parameter_value" + wrapperIndex + "_0").is("select"))
            new_element.children("#parameter_value" + wrapperIndex + "_0").val("null");
        else
            new_element.children("#parameter_value" + wrapperIndex + "_0").val("");

        new_element.children("#parameter_key" + wrapperIndex + "_0").attr("name", "parameter_key" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);
        new_element.children("#parameter_value" + wrapperIndex + "_0").attr("name", "parameter_value" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);
        new_element.children("#parameter_key" + wrapperIndex + "_0").attr("id", "parameter_key" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);
        new_element.children("#parameter_value" + wrapperIndex + "_0").attr("id", "parameter_value" + wrapperIndex+ "_" + wrapper_index_array[wrapperIndex]);

        $("#parameter_properties" + wrapperIndex).append(new_element);
    }
</g:javascript>
