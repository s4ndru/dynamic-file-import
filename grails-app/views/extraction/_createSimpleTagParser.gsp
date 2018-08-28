<g:form class="well">
    <div id="parser">
        <div class="form-group">
            <g:render template="/extraction/parser"/>
            <g:textField class="form-control" name="domainStartTag" placeholder="Domain starttag" />
            <g:textField class="form-control" name="domainEndTag" placeholder="Domain endtag" />
            <g:field type="number" class="form-control" name="nestingLevel" placeholder="Nesting level"/>
        </div>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    <g:submitToRemote class="btn btn-primary pull-right" name="submitSimpleTagParser" value="save parser"
                      action="createSimpleTagParser" onclick="return verifyForm()" onFailure="alert(XMLHttpRequest.responseText)"
                      onSuccess="alert('Parser was successfully saved!'); location.reload();"/>
    <div style="clear: both;"></div>
</g:form>

<g:javascript>
    appendSimpleTagEntry(0);
    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        appendSimpleTagEntry(entryCounter);
    });

    function appendSimpleTagEntry(index){
        $("#entry" + index).append('<input class="form-control" name="startTag' + index + '" placeholder="Starttag" value="" id="startTag' + index + '" type="text"> ');
        $("#entry" + index).append('<input class="form-control" name="endTag' + index + '" placeholder="Endtag" value="" id="endTag' + index + '" type="text"> ');
        $("#entry" + index).append('<input class="form-control" name="arraySplitTag' + index + '" placeholder="Array splitting tag" value="" id="arraySplitTag' + index + '" type="text"> ');

        $("#entry" + index).append('<hr>');
    }

    function verifyForm(){

        if($("#name").val() === ""){
            alert("Please fill out the 'Parsername'-field.");
            return false;
        }

        if($("#selectorName").val() === ""){
            alert("Please fill out the 'Filename substring'-field.");
            return false;
        }

        if(($("#domainEndTag").val() !== "" && $("#domainStartTag").val() === "") || ($("#domainEndTag").val() === "" && $("#domainStartTag").val() !== "")){
            alert("Both, domain starttag and endtag, have to be set or not set!");
            return false;
        }

        if($("#nestingLevel").val() !== "" && ($("#domainStartTag").val() === "" || $("#domainEndTag").val() === "")){
            alert("'Nesting level' cannot be set without both 'domain start- and end-tag'!");
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

            if($("#arraySplitTag" + i).val() !== "" && ($("#endTag" + i).val() === "" || $("#startTag" + i).val() === "")){
                alert("'Array splitting tag' cannot be set without both 'start- and end-tag'!");
                return false;
            }

            if($("#startTag" + i).val() === ""){
                alert("'startTag' has to be set!");
                return false;
            }

        }

        return true;
    }
</g:javascript>