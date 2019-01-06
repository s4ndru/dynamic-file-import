<div id="entries">
    <div id="entry0">
        <g:textField name="field0" placeholder="Field"/>
        <g:select name="dataType0" from="${extraction.EntryDataType.values()}" />
        <div class="checkbox">
            <label for="optional0"><g:checkBox name="optional0" /> Optional</label>
        </div>
    </div>
</div>
<button type="button" id="addEntryBtn" >add another entry</button>

<g:javascript>
    var entryCounter = 0;

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        entryCounter++;

        %{--var div = document.createElement('div');
        div.id = 'entry' + entryCounter;
        div.setAttribute('class', 'form-inline');
        div.innerHTML = '<input name="field' + entryCounter + '" placeholder="Field" value="" id="field' + entryCounter + '" type="text"> ' +
                        '<select name="dataType' + entryCounter + '" id="dataType' + entryCounter + '" >' +
                        '<option value="String" >String</option>' +
                        '<option value="Integer" >Integer</option>' +
                        '<option value="Float" >Float</option>' +
                        '<option value="Boolean" >Boolean</option>' +
                        '<option value="Long" >Long</option>' +
                        '</select> ' +
                        '<div class="checkbox"><label for="optional' + entryCounter + '" style="margin-right: 5px;"><g:checkBox name="optional' + entryCounter + '" /> Optional    </label>';

        --}%

        var clonedElement = $("#entry0").clone(true);
        clonedElement.attr("id", "entry" + entryCounter);

        clonedElement.find("#field0").val("");
        clonedElement.find("#field0").attr("name", "field" + entryCounter);
        clonedElement.find("#field0").attr("id", "field" + entryCounter);

        clonedElement.find("#dataType0").val("String");
        clonedElement.find("#dataType0").attr("name", "dataType" + entryCounter);
        clonedElement.find("#dataType0").attr("id", "dataType" + entryCounter);

        clonedElement.find("#optional0").parent().attr("for", "optional" + entryCounter);
        clonedElement.find("#optional0").prev().attr("name", "_optional" + entryCounter);
        clonedElement.find("#optional0").prop("checked", false);
        clonedElement.find("#optional0").attr("name", "optional" + entryCounter);
        clonedElement.find("#optional0").attr("id", "optional" + entryCounter);

        $('#entries').append(clonedElement);

    });
</g:javascript>
