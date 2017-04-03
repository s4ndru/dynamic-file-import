<g:form class="well" controller="extraction" action="createColumnWidthParser">
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

    <g:actionSubmit class="btn btn-primary" name="submitColumnWidthParser" id="submitColumnWidthParser" value="save parser" action="createColumnWidthParser" onclick="return verifyForm()" style="float: right;"/>
    <div style="clear: both;"></div>
</g:form>

%{-- TODO some more checks columnStart and columnEnd--}%
%{-- TODO mention in document that different "creation" pages should not be mixed because of similar JS names and stuff --}%
<g:javascript>
    appendColumnWidthEntry(0);

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        appendColumnWidthEntry(entryCounter);
    });

    function appendColumnWidthEntry(index){
        $("#entry" + index).append('<input class="form-control" name="columnStart' + index + '" placeholder="Column start" value="" id="columnStart' + index + '" type="text"> ');
        $("#entry" + index).append('<input class="form-control" name="columnEnd' + index + '" placeholder="Column end" value="" id="columnEnd' + index + '" type="text"> ');

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

        if($("#name").val() == ""){
            alert("Please fill out the 'Parsername'-field.");
            return false;
        }

        if($("#selectorName").val() == ""){
            alert("Please fill out the 'Filename substring'-field.");
            return false;
        }

        var i;

        for(i = 0; i <= entryCounter; i++){
            if($("#field" + i).val() == ""){
                alert("Please fill out all 'property'-fields.");
                return false;
            }

            if($("#columnStart" + i).val() == ""){
                alert("Please fill out all 'Column start'-fields.");
                return false;
            }

            if($("#columnEnd" + i).val() == ""){
                alert("Please fill out all 'Column end'-fields.");
                return false;
            }

            /*if($("#multiple" + i).prop("checked") == false && $("#splitIndizes" + i).val().split(",").length > 1){
                alert("There are multiple split indizes but 'multiple' is not checked, please review.");
                return false;
            }*/
        }

        return true;
    }
</g:javascript>