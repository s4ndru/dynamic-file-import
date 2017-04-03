<%@ page import="transformation.TransformationRoutine; transformation.MethodInfo" %>

<g:form class="well">
    <div class="form-group">
        <label for="routine">Routine: </label>
        <g:select class="form-control" from="${TransformationRoutine.getAll()}" noSelection="[null: 'Please select a routine...']" optionKey="id" id="routine" name="routine" required="required"/>
    </div>
    <div class="form-group">
        <label for="transformation_method">Methodname: </label>
        <g:select class="form-control" from="${MethodInfo.values()}" noSelection="[null : 'Please select a method...']" id="transformation_method" name="transformation_method" required="required"/>
    </div>

    <g:remoteLink class="btn btn-warning" action="setProcedureProperties" update="procedureContainer" params="{routine: \$('#routine').val(), method: \$('#transformation_method').val()}">Submit selected</g:remoteLink>

    <div id="procedureContainer">
    </div>

</g:form>

<g:javascript>
    var notable_object_index = 0;
    $('#notable_objects_container').on('click', '#addObjectBtn', function(){
        notable_object_index++;

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
            param_index++;

            var div = document.createElement('div');
            div.id = 'parameter_property' + param_index;
            div.innerHTML = '<input name="parameter_key' + param_index + '" placeholder="parameterkey" id="parameter_key' + param_index + '|' + wrapper_index +  '" type="text" required="required"> ' +
                    '<input name="parameter_value' + param_index + '" placeholder="parametervalue" id="parameter_value' + param_index + '" type="text" required="required">';

            $('#parameter_properties' + wrapper_index).append(div);
        });
    });

    $('#parameter_container').on('click', '#addParamBtn0', function(){
        param_index++;

        var div = document.createElement('div');
        div.id = 'parameter_property' + param_index;
        div.innerHTML = '<input name="parameter_key' + param_index + '" placeholder="parameterkey" id="parameter_key' + param_index + '|' + wrapper_index +  '" type="text" required="required"> ' +
                '<input name="parameter_value' + param_index + '" placeholder="parametervalue" id="parameter_value' + param_index + '" type="text" required="required">';

        $('#parameter_properties0').append(div);
    });


</g:javascript>