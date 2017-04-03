<div id="entries">
    <div id="entry0" class="form-inline">
        <g:textField class="form-control" name="field0" placeholder="Property"/>
        <g:select class="form-control" name="dataType0" from="${extraction.EntryDatatype.values()}" />
        <div class="checkbox">
            <label for="optional0" style="margin-right: 5px;"><g:checkBox name="optional0" /> Optional</label>
            <label for="trim0" style="margin-right: 5px;"><g:checkBox name="trim0" /> Trim</label>
        </div>
    </div>
</div>
<button class="btn btn-warning" type="button" id="addEntryBtn" >add another entry</button>

<g:javascript>
    var entryCounter = 0;

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        entryCounter++

        var div = document.createElement('div');
        div.id = 'entry' + entryCounter;
        div.setAttribute('class', 'form-inline');
        div.innerHTML = '<input class="form-control" name="field' + entryCounter + '" placeholder="Property" value="" id="field' + entryCounter + '" type="text"> ' +
                        '<select class="form-control" name="dataType' + entryCounter + '" id="dataType' + entryCounter + '" >' +
                        '<option value="String" >String</option>' +
                        '<option value="Integer" >Integer</option>' +
                        '<option value="Float" >Float</option>' +
                        '</select> ' +
                        '<div class="checkbox"><label for="optional' + entryCounter + '" style="margin-right: 5px;"><g:checkBox name="optional' + entryCounter + '" /> Optional    </label>' +
                        '<label for="trim' + entryCounter + '" style="margin-right: 5px;"><g:checkBox name="trim' + entryCounter + '" /> Trim    </label></div>';

        $('#entries').append(div);
});
</g:javascript>


