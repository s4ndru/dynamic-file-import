%{--<g:form class="well" controller="extraction" action="createTokenSplitParser" params="[targetUri: (request.forwardURI - request.contextPath)]">--}%
<g:form name="formDynamicParser">
    <div id="divDynamicParser">
        <div>
            <g:render template="/extraction/parser"/>
            <g:textField name="token" placeholder="Separation token" />
        </div>
        <div id="linesToIgnore">
            <g:textField name="lineToIgnore0" placeholder="substring to ignore" />
        </div>
        <div >
            <button type="button" id="addLineToIgnore" >add another substring to ignore</button>
        </div>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    %{--<div id="routineContainer">--}%
        %{--<g:render template="/transformation/selectRoutines"/>--}%
    %{--</div>--}%

    <g:submitToRemote id="submitTokenSplitParser" name="submitTokenSplitParser" value="save parser" controller="extraction"
                      action="createTokenSplitParser" onFailure="alert(XMLHttpRequest.responseText)"
                      onSuccess="alert('Parser was successfully saved!'); location.reload();"/>
    <div style="clear: both;"></div>
</g:form>

<g:javascript>
    appendSplitTokenEntry(0);

    $('#formDynamicParser').on('mousedown', '#submitTokenSplitParser', verifyForm);

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        // appendSplitTokenEntry(entryCounter);

        var TSPEntries = $("#entry" + entryCounter);

        TSPEntries.find("#splitIndex0").val("");
        TSPEntries.find("#splitIndex0").attr("name", "splitIndex" + entryCounter);
        TSPEntries.find("#splitIndex0").attr("id", "splitIndex" + entryCounter);

        TSPEntries.find("#TSPEntries0").attr("id", "TSPEntries" + entryCounter);

        $("#entry" + entryCounter + " > label").remove();

    });

    function appendSplitTokenEntry(index){
        $("#entry" + index).append('<div id="TSPEntries' + index + '">' +
             '<input name="splitIndex' + index + '" placeholder="Field-index" value="" id="splitIndex' + index + '" type="number">' +
             '</div>');

        if(index == 0)
            $("#entry0").append('<label>Starts from 0</label>');
        $("#entry" + index).append('<hr>');
    }

    var lineToIgnoreCounter = 0;

    $("#divDynamicParser").on('click', '#addLineToIgnore', function(){
        lineToIgnoreCounter++;

        var clonedLineToIgnore = $("#lineToIgnore0").clone(true);

        clonedLineToIgnore.val("");
        clonedLineToIgnore.attr("name", "lineToIgnore" + lineToIgnoreCounter);
        clonedLineToIgnore.attr("id", "lineToIgnore" + lineToIgnoreCounter);

        $("#linesToIgnore").append(clonedLineToIgnore);
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

        if($("#token").val() === ""){
            alert("Please fill out the 'Separation token'-field.");
            return false;
        }

        var i;

        /*for(i = 0; i <= lineToIgnoreCounter; i++){
            //alert("lineCounter: " + lineToIgnoreCounter + ", entryCounter: " + entryCounter + ", value input: " + $("#lineToIgnore" + i).val())

            if($("#lineToIgnore" + i).val() == ""){
                alert("Please fill out all 'substring to ignore'-fields.");
                return false;
            }
        }*/

        for(i = 0; i <= entryCounter; i++){
            if($("#field" + i).val() === ""){
                alert("Please fill out all 'property'-fields.");
                return false;
            }

            /*if($("#splitIndizes" + i).val() == ""){
                alert("Please fill out all 'Indizes (comma seperated)'-fields.");
                return false;
            }*/

            /*if($("#multiple" + i).prop("checked") && $("#splitIndex" + i).val().split(",").length <= 1){
                alert("'Multiple' cannot be checked with only one or less split indizes, please review.");
                return false;
            }*/

            /*if($("#multiple" + i).prop("checked") == false && $("#splitIndizes" + i).val().split(",").length > 1){
                alert("There are multiple split indizes but 'multiple' is not checked, please review.");
                return false;
            }*/

            if($("#splitIndex" + i).val() === ""){
                alert("Split index is not set for an entry!");
                return false;
            }
        }

        return true;
    }
</g:javascript>