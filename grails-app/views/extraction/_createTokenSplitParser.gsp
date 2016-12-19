<g:form controller="parsing" action="createTokenSplitParser" params="[targetUri: (request.forwardURI - request.contextPath)]">
    <div id="parser">
        <div>
            <g:render template="/extraction/parser"/>
            <g:textField name="token" placeholder="Separation token" />
        </div>
        <div id="linesToIgnore">

            <g:textField name="lineToIgnore0" placeholder="substring to ignore" />
        </div>
        <button type="button" id="addLineToIgnore" >add substring to ignore</button>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    <g:actionSubmit name="submitTokenSplitParser" id="submitTokenSplitParser" value="submit parsing" action="createTokenSplitParser" onclick="return verifyForm()"/>
</g:form>

<g:javascript>
    appendSplitTokenEntry(0);
    $("#entry0").append('<label>(Starts from 0, eg. "0", "8", "1,2,3" )</label>')

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        appendSplitTokenEntry(entryCounter);
    });

    function appendSplitTokenEntry(index){
        $("#entry" + index).append('<label for="multiple' + index + '">Multiple</label><g:checkBox name="multiple' + index + '" /> ');
        $("#entry" + index).append('<input name="splitIndizes' + index + '" placeholder="Comma separated indizes" value="" id="splitIndizes' + index + '" type="text"> ');
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

        if($("#token").val() == ""){
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
            if($("#field" + i).val() == ""){
                alert("Please fill out all 'property'-fields.");
                return false;
            }

            if($("#splitIndizes" + i).val() == ""){
                alert("Please fill out all 'Comma separated indizes'-fields.");
                return false;
            }

            if($("#multiple" + i).prop("checked") && $("#splitIndizes" + i).val().split(",").length <= 1){
                alert("'Multiple' cannot be checked with only one or less split index, please review.");
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