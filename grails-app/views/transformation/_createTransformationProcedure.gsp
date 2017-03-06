<%@ page import="transformation.TransformationRoutine; transformation.MethodInfo" %>

<g:form>
    <div>
        <label for="routine">Routine: </label>
        <g:select from="${TransformationRoutine.getAll()}" noSelection="[null: 'please select a routine...']" id="routine" name="routine" required="required"/>
    </div>
    <div>
        <label for="transformation_method">Methodname: </label>
        <g:select from="${MethodInfo.values()}" noSelection="[null : 'please select a method...']" id="transformation_method" name="transformation_method" required="required"/>
    </div>
    <div>
        <g:field name="order_id" type="number" min="0" required="required" placeholder="Sequence number"/>
    </div>
    <div>
        <label for="is_repetitive">Repetitive procedure</label>
        <g:checkBox name="is_repetitive" value="true"/>
    </div>

    <div>
        <label for="notable_objects_container">
            Notable parsing objects:
            <div id="notable_objects_container">
                <div id="notable_objects">
                    <div id="notable_object0">
                        <g:textField name="object_key0" id="object_key0" placeholder="propertykey" />
                        <g:textField name="object_value0" id="object_value0" placeholder="propertyvalue" />
                    </div>
                </div>
                <button type="button" id="addObjectBtn" >add objectpair</button>
            </div>
        </label>
    </div>

    <div id="parameter_container_container">
        <label id="parameter_container_label" for="parameter_container">
            Method parameters:
            <div id="parameter_container">
                <div id="parameter_properties0">
                    <div id="parameter_property0|0">
                        <g:textField name="parameter_key0|0" id="parameter_key0|0" placeholder="parameterkey" />
                        <g:textField name="parameter_value0|0" id="parameter_value0|0" placeholder="parametervalue" />
                    </div>
                </div>
                <button type="button" id="addParamBtn0" >add parameterpair</button>
                <hr>
            </div>
        </label>
        <button type="button" id="addWrapperBtn" >add wrapper</button>
    </div>

</g:form>

<g:javascript>
    var notable_object_index = 0;
    $('#notable_objects_container').on('click', '#addObjectBtn', function(){
        notable_object_index++

        var div = document.createElement('div');
        div.id = 'parameter_property' + notable_object_index;
        div.innerHTML = '<input name="object_key' + notable_object_index + '" placeholder="objectkey" id="object_key' + notable_object_index + '" type="text" required="required"> ' +
                '<input name="object_value' + notable_object_index + '" placeholder="objectvalue" id="object_value' + notable_object_index + '" type="text" required="required">';

        $('#notable_objects').append(div);
    });

    var wrapper_index = 0;
    var param_index = 0;

    //TODO finish
    $('#parameter_container_container').on('click', '#addWrapperBtn', function(){
        wrapper_index++

        var div = document.createElement('div');
        div.id = 'notable_object' + wrapper_index;
        div.innerHTML = '<div id="parameter_properties' + wrapper_index + '">' +
                        '<div id="parameter_property' + wrapper_index + '|0">' +
                        '<input name="parameter_key' + param_index + '" placeholder="parameterkey" id="parameter_key' + wrapper_index + '|0" type="text" required="required"> ' +
                        '<input name="parameter_value' + param_index + '" placeholder="parametervalue" id="parameter_value' + wrapper_index + '|0" type="text" required="required">' +
                        '</div>' +
                        '</div>' +
                        '<button type="button" id="addParamBtn' + wrapper_index + '" >add parameterpair</button>' +
                        '<hr>';

        $('#parameter_container').append(div);

        $('#parameter_container').on('click', '#addParamBtn' + wrapper_index, function(){
            param_index++

            var div = document.createElement('div');
            div.id = 'parameter_property' + param_index;
            div.innerHTML = '<input name="parameter_key' + param_index + '" placeholder="parameterkey" id="parameter_key' + param_index + '|' + wrapper_index +  '" type="text" required="required"> ' +
                    '<input name="parameter_value' + param_index + '" placeholder="parametervalue" id="parameter_value' + param_index + '" type="text" required="required">';

            $('#parameter_properties' + wrapper_index).append(div);
        });
    });

    $('#parameter_container').on('click', '#addParamBtn0', function(){
        param_index++

        var div = document.createElement('div');
        div.id = 'parameter_property' + param_index;
        div.innerHTML = '<input name="parameter_key' + param_index + '" placeholder="parameterkey" id="parameter_key' + param_index + '|' + wrapper_index +  '" type="text" required="required"> ' +
                '<input name="parameter_value' + param_index + '" placeholder="parametervalue" id="parameter_value' + param_index + '" type="text" required="required">';

        $('#parameter_properties0').append(div);
    });

</g:javascript>