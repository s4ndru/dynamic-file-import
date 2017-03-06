<div id="entries">
    <div id="entry0">
        <g:textField name="field0" placeholder="Property"/>
        <g:select name="dataType0" from="${extraction.EntryDatatype.values()}" />
        <label for="optional0">Optional</label><g:checkBox name="optional0" />
        <label for="trim0">Trim</label><g:checkBox name="trim0" />
    </div>
</div>
<button type="button" id="addEntryBtn" >add another entry</button>

<g:javascript>
    var entryCounter = 0;

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        entryCounter++

        var div = document.createElement('div');
        div.id = 'entry' + entryCounter;
        div.innerHTML = '<input name="field' + entryCounter + '" placeholder="Property" value="" id="field' + entryCounter + '" type="text"> ' +
                        '<select name="dataType' + entryCounter + '" id="dataType' + entryCounter + '" >' +
                        '<option value="String" >String</option>' +
                        '<option value="Integer" >Integer</option>' +
                        '<option value="Float" >Float</option>' +
                        '</select> ' +
                        '<label for="optional' + entryCounter + '">Optional</label><g:checkBox name="optional' + entryCounter + '" /> ' +
                        '<label for="trim' + entryCounter + '">Trim</label><g:checkBox name="trim' + entryCounter + '" /> ';

        $('#entries').append(div);
});
</g:javascript>


