<%@ page import="transformation.MethodInfo" %>
<div id="parameter_property${params.index}_0">
    <g:if test="${MethodInfo.getSecondClassPropertiesPosition((String)params.method) == 0}">
        <g:select name="parameter_key${params.index}_0" id="parameter_key${params.index}_0" from="${params.secondPropertiesList}" style="width: 48%"
                  noSelection="[null : 'Please select a property..']" placeholder="parameterkey" />
    </g:if>
    <g:elseif test="${MethodInfo.isObjectOnWrapperAndTuplePosition((String)params.method, params.index, 0)}">
        <g:select name="parameter_key${params.index}_0" id="parameter_key${params.index}_0" from="${params.objectFields}" style="width: 48%"
                  noSelection="[null : 'Please select a property..']" placeholder="parameterkey" />
    </g:elseif>
    <g:elseif test="${MethodInfo.isDatumOnWrapperAndTuplePosition((String)params.method, params.index, 0)}">
        <g:select name="parameter_key${params.index}_0" id="parameter_key${params.index}_0" from="${params.entries}" style="width: 48%"
                  noSelection="[null : 'Please select a datum...']" placeholder="parameterkey" />
    </g:elseif>
    <g:else>
        <g:textField name="parameter_key${params.index}_0" id="parameter_key${params.index}_0" placeholder="parameterkey" style="width: 48%" />
    </g:else>

    <g:if test="${MethodInfo.getSecondClassPropertiesPosition((String)params.method) == 1}">
        <g:select name="parameter_key${params.index}_0" id="parameter_value${params.index}_0" from="${params.secondPropertiesList}" style="width: 48%"
                  noSelection="[null : 'Please select a property..']" placeholder="parameterkey" />
    </g:if>
    <g:elseif test="${MethodInfo.isObjectOnWrapperAndTuplePosition((String)params.method, params.index, 1)}">
        <g:select name="parameter_value${params.index}_0" id="parameter_value${params.index}_0" from="${params.objectFields}" style="width: 48%"
                  noSelection="[null : 'Please select a property..']"/>
    </g:elseif>
    <g:elseif test="${MethodInfo.isDatumOnWrapperAndTuplePosition((String)params.method, params.index, 1)}">
        <g:select name="parameter_value${params.index}_0" id="parameter_value${params.index}_0" from="${params.entries}" style="width: 48%"
                  noSelection="[null : 'Please select a datum...']" placeholder="parametervalue" />
    </g:elseif>
    <g:else>
        <g:textField name="parameter_value${params.index}_0" id="parameter_value${params.index}_0" placeholder="parametervalue" style="width: 48%" />
    </g:else>
</div>