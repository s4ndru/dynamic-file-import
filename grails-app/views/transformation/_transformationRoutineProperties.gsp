<g:hiddenField name="propertyCounter" value="${params.propertyCounter}"/>
<g:each var="i" in="${(0..< params.propertyCounter)}">
    <div id="update_property${i}">
        <g:select class="form-control col-lg-6" style="width: 50%" name="update_key${i}" from="${params.entries}" value="${params["update_key" + i] ? params["update_key" + i] : null}" id="update_key${i}" noSelection="[null : 'Please select a entry...']" />
        <g:select class="form-control col-lg-6" style="width: 50%" name="update_value${i}" from="${params.properties}" value="${params["update_value" + i] ? params["update_value" + i] : null}" id="update_value${i}" noSelection="[null : 'Please select a property...']" />
    </div>
    <div style="clear: both;"></div>
</g:each>