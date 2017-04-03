<g:form class="well" controller="extraction" action="createTokenSplitParser" params="[targetUri: (request.forwardURI - request.contextPath)]">
    <div id="parser">
        <div class="form-group">
            <g:render template="/extraction/parser"/>
            <g:textField class="form-control" name="token" placeholder="Separation token" />
        </div>
        <div class="form-group" id="linesToIgnore">
            <g:textField class="form-control" name="lineToIgnore0" placeholder="substring to ignore" />
        </div>
        <div class="form-group">
            <button class="btn btn-warning" type="button" id="addLineToIgnore" >add another substring to ignore</button>
        </div>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    %{--<div id="routineContainer">--}%
        %{--<g:render template="/transformation/selectRoutines"/>--}%
    %{--</div>--}%

    <g:actionSubmit name="submitTokenSplitParser" id="submitTokenSplitParser" value="save parser" action="createTokenSplitParser" onclick="return verifyForm()"/>
</g:form>

<g:javascript>
    appendSplitTokenEntry(0);

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        appendSplitTokenEntry(entryCounter);
    });

    function appendSplitTokenEntry(index){
        $("#entry" + index).append('<div class="checkbox"><label for="multiple' + index + '" style="margin-right: 5px;"><g:checkBox name="multiple' + index + '" /> Multiple</label></div>');
        $("#entry" + index).append('<input class="form-control" name="splitIndizes' + index + '" placeholder="Indizes (comma seperated)" value="" id="splitIndizes' + index + '" type="text"> ');

        if(index == 0)
            $("#entry0").append('<label>(Starts from 0, eg. "0", "8", "1,2,3" )</label>');
        $("#entry" + index).append('<hr>');
    }

    var lineToIgnoreCounter = 0;

    $("#parser").on('click', '#addLineToIgnore', function(){
        lineToIgnoreCounter++;
        $("#linesToIgnore").append('<input class="form-group" name="lineToIgnore' + lineToIgnoreCounter + '" placeholder="substring to ignore" value="" id="lineToIgnore' + lineToIgnoreCounter + '" type="text"> ');
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
                alert("Please fill out all 'Indizes (comma seperated)'-fields.");
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