<%@ page import="transformation.TransformationController" %>
<div id="parameter_property1_0">
    <g:if test="${!params.arbitrary_left_value}">
        <g:select name="parameter_key1_0" id="parameter_key1_0" from="${params.entries}" style="width: 48%"
                  noSelection="[null : 'Please select a datum...']" placeholder="parameterkey" value="${params.left_value}"
                  onchange="if(this.value == '${TransformationController.arithmeticPlaceholder}'){
                  var belongs_to = ${params.belongs_to}; var right_value = \$('#parameter_value1_0').val(); var arbitrary_right_value = ${params.arbitrary_right_value};
                  ${remoteFunction(action: 'setArithmeticParameters', update: "parameter_properties1",
                  params: '\'right_value=\' + right_value + \'&left_value=\' + this.value + \'&belongs_to=\' + belongs_to ' +
                          '+ \'&arbitrary_right_value=\' + arbitrary_right_value')}}"/>
    </g:if>
    <g:else>
        <g:field type="number" name="parameter_key1_0" id="parameter_key1_0" placeholder="parameterkey" style="width: 48%"
                     value="${params.left_value}"/>
    </g:else>

    <g:if test="${!params.arbitrary_right_value}">
        <g:select name="parameter_value1_0" id="parameter_value1_0" from="${params.entries}" style="width: 48%"
                  noSelection="[null : 'Please select a datum...']" placeholder="parametervalue" value="${params.right_value}"
                  onchange="if(this.value == '${TransformationController.arithmeticPlaceholder}'){
                  var belongs_to = ${params.belongs_to}; var left_value = \$('#parameter_key1_0').val(); var arbitrary_left_value = ${params.arbitrary_left_value};
                  ${remoteFunction(action: 'setArithmeticParameters', update: "parameter_properties1",
                  params: '\'right_value=\' + this.value + \'&left_value=\' + left_value + \'&belongs_to=\' + belongs_to ' +
                          '+ \'&arbitrary_left_value=\' + arbitrary_left_value')}}"/>
    </g:if>
    <g:else>
        <g:field type="number" name="parameter_value1_0" id="parameter_value1_0" placeholder="parametervalue" style="width: 48%"
                     value="${params.right_value}"/>
    </g:else>
</div>