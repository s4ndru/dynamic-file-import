<g:form controller="extraction" action="createColumnWidthParser">
    <div id="parser">
        <div>
            <g:render template="/extraction/parser"/>
        </div>
        <div id="linesToIgnore">
            <g:textField name="lineToIgnore0" placeholder="substring to ignore" />
        </div>
        <button type="button" id="addLineToIgnore" >add substring to ignore</button>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    <g:actionSubmit name="submitColumnWidthParser" id="submitColumnWidthParser" value="save parser" action="createColumnWidthParser" onclick="return verifyForm()" style="float: right;"/>
</g:form>

%{-- TODO some more checks columnStart and columnEnd--}%
%{-- TODO mention in document that different "creation" pages should not be mixed because of similar JS names and stuff --}%
<g:javascript>
    appendColumnWidthEntry(0);
    $("#entry0").append('<label>(Eg. 1-5, 5-12, 12-25)</label>')

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        appendColumnWidthEntry(entryCounter);
    });

    function appendColumnWidthEntry(index){
        $("#entry" + index).append('<input name="columnStart' + index + '" placeholder="Column start" value="" id="columnStart' + index + '" type="text"> ');
        $("#entry" + index).append('<input name="columnEnd' + index + '" placeholder="Column end" value="" id="columnEnd' + index + '" type="text"> ');
    }

    var lineToIgnoreCounter = 0;

    $("#parser").on('click', '#addLineToIgnore', function(){
        lineToIgnoreCounter++;
        $("#linesToIgnore").append('<input name="lineToIgnore' + lineToIgnoreCounter + '" placeholder="substring to ignore" value="" id="lineToIgnore' + lineToIgnoreCounter + '" type="text"> ');
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