<g:form name="formDynamicParser">
    <div id="divDynamicParser">
        <div>
            <g:render template="/extraction/parser"/>
        </div>
        <div id="linesToIgnore">
            <g:textField name="lineToIgnore0" placeholder="substring to ignore" />
        </div>
        <div>
            <button type="button" id="addLineToIgnore" >add substring to ignore</button>
        </div>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    <g:submitToRemote id="submitColumnWidthParser" name="submitColumnWidthParser" value="save parser" controller="extraction"
                      action="createColumnWidthParser" onFailure="alert(XMLHttpRequest.responseText)"
                      onSuccess="alert('Parser was successfully saved!'); location.reload();"/>
    <div style="clear: both;"></div>
</g:form>

<script>
    appendColumnWidthEntry(0);

    // Nevermind optional will be used actually. Basically it's possible for number fields to be empty number and the program will try to parse them. With optional enabled no error is thrown.
    // // Remove the "optional" checkboxes. Those will not be used for the columnWidthParser, because of the nature of the files.
    // $("#optional0").parent().parent().remove();

    // $('#entryContainer').on('click', '#addEntryBtn', function(){
    //     $("#optional" + entryCounter).parent().remove()
    // });

    $('#formDynamicParser').on('mousedown', '#submitColumnWidthParser', verifyForm);

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        // appendColumnWidthEntry(entryCounter);

        var CWPEntries = $("#entry" + entryCounter);

        CWPEntries.find("#columnStart0").val("");
        CWPEntries.find("#columnStart0").attr("name", "columnStart" + entryCounter);
        CWPEntries.find("#columnStart0").attr("id", "columnStart" + entryCounter);

        CWPEntries.find("#columnEnd0").val("");
        CWPEntries.find("#columnEnd0").attr("name", "columnEnd" + entryCounter);
        CWPEntries.find("#columnEnd0").attr("id", "columnEnd" + entryCounter);

        CWPEntries.find("#CWPEntries0").attr("id", "CWPEntries" + entryCounter);

        CWPEntries.find("> label").remove();

    });

    function appendColumnWidthEntry(index){
        $("#entry" + index).append('<div style="width: 100%" id="CWPEntries' + index + '">' +
        '<input type="number" style="width: 50%" name="columnStart' + index + '" placeholder="Column start" value="" id="columnStart' + index + '"/> ' +
        '<input type="number" style="width: 50%" name="columnEnd' + index + '" placeholder="Column end" value="" id="columnEnd' + index + '"/>' +
        '</div>');

        if(index === 0)
            $("#entry" + index).append('<label>(Eg. 1-5, 5-12, 12-25)</label>');

        $("#entry" + index).append('<hr>');
    }

    var lineToIgnoreCounter = 0;
    $("#divDynamicParser").on('click', '#addLineToIgnore', function(){
        lineToIgnoreCounter++;

        var new_element = $("#lineToIgnore0").clone(true);
        new_element.val("");
        new_element.attr("id", "lineToIgnore" + lineToIgnoreCounter);
        new_element.attr("name", "lineToIgnore" + lineToIgnoreCounter);

        $("#linesToIgnore").append(new_element);
    });

    function verifyForm(){
        if($("#name").val() === ""){
            alert("Please fill out the 'Parsername'-field.");
            return false;
        }

        if($("#selectorName").val() === ""){
            alert("Please fill out the 'Filename substring'-field.");
            return false;
        }

        var i;
        var previousColumnEndIndex = 0;

        for(i = 0; i <= entryCounter; i++){
            if($("#field" + i).val() === ""){
                alert("Please fill out all 'property'-fields.");
                return false;
            }

            if($("#columnStart" + i).val() === ""){
                alert("Please fill out all 'Column start'-fields.");
                return false;
            }

            if($("#columnEnd" + i).val() === ""){
                alert("Please fill out all 'Column end'-fields.");
                return false;
            }

            if(parseInt($("#columnEnd" + i).val(), 10) <= 1){
                alert("'Column end' can not be negative, zero or one!");
                return false;
            }

            if(parseInt($("#columnStart" + i).val(), 10) < 1){
                alert("'Column start' can not be negative or zero!");
                return false;
            }

            if(isNaN(parseInt($("#columnStart" + i).val(), 10))){
                alert("'Column start' is not a number!");
                return false;
            }

            if(isNaN(parseInt($("#columnEnd" + i).val(), 10))){
                alert("'Column end' is not a number!");
                return false;
            }

            if(parseInt($("#columnStart" + i).val(), 10) > parseInt($("#columnEnd" + i).val(), 10)){
                alert("'Column start' can not be greater that the 'Column end'!");
                return false;
            }

            if(previousColumnEndIndex > parseInt($("#columnStart" + i).val(), 10)){
                alert("'Column end' of a previous entry can not be bigger than the 'Column start' of a later entry!");
                return false;
            }

            previousColumnEndIndex = parseInt($("#columnEnd" + i).val(), 10)
        }

        return true;
    }
</script>