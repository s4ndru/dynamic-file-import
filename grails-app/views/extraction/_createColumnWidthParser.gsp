<g:form class="well">
    <div id="parser">
        <div class="form-group">
            <g:render template="/extraction/parser"/>
        </div>
        <div class="form-group" id="linesToIgnore">
            <g:textField class="form-control" name="lineToIgnore0" placeholder="substring to ignore" />
        </div>
        <div class="form-group">
            <button class="btn btn-warning" type="button" id="addLineToIgnore" >add substring to ignore</button>
        </div>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    <g:submitToRemote class="btn btn-primary pull-right" name="submitColumnWidthParser" value="save parser"
                      action="createColumnWidthParser" onclick="return verifyForm()" onFailure="alert(XMLHttpRequest.responseText)"
                      onSuccess="alert('Parser was successfully saved!'); location.reload();"/>
    <div style="clear: both;"></div>
</g:form>

<g:javascript>
    appendColumnWidthEntry(0);

    // Remove the "optional" checkboxes. Those will not be used for the columnWidthParser, because of the nature of XML.
    $("#optional0").parent().remove();

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        $("#optional" + entryCounter).parent().remove()
    });

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        appendColumnWidthEntry(entryCounter);
    });

    function appendColumnWidthEntry(index){
        $("#entry" + index).append('<div class="form-group" style="width: 100%"><input type="number" class="form-control" style="width: 48%" name="columnStart' + index + '" placeholder="Column start" value="" id="columnStart' + index + '" type="text"/> ' +
                '<input type="number" class="form-control" style="width: 48%" name="columnEnd' + index + '" placeholder="Column end" value="" id="columnEnd' + index + '" type="text"/></div>');
        //$("#entry" + index).append();

        if(index == 0)
            $("#entry" + index).append('<label>(Eg. 1-5, 5-12, 12-25)</label>');

        $("#entry" + index).append('<hr>');
    }

    var lineToIgnoreCounter = 0;

    $("#parser").on('click', '#addLineToIgnore', function(){
        lineToIgnoreCounter++;
        $("#linesToIgnore").append('<input class="form-control" name="lineToIgnore' + lineToIgnoreCounter + '" placeholder="substring to ignore" value="" id="lineToIgnore' + lineToIgnoreCounter + '" type="text"> ');
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
</g:javascript>