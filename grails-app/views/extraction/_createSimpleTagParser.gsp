<g:form name="formDynamicParser">
    <div id="divDynamicParser">
        <div>
            <g:render template="/extraction/parser"/>
            <g:textField name="domainStartTag" placeholder="Domain starttag" />
            <g:textField name="domainEndTag" placeholder="Domain endtag" />
            <g:field type="number" name="nestingLevel" placeholder="Nesting level"/>
        </div>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    <g:submitToRemote id="submitSimpleTagParser" name="submitSimpleTagParser" value="save parser" controller="extraction"
                      action="createSimpleTagParser" onFailure="alert(XMLHttpRequest.responseText)"
                      onSuccess="alert('Parser was successfully saved!'); location.reload();"/>
    <div style="clear: both;"></div>
</g:form>

<g:javascript>
    appendSimpleTagEntry(0);

    $('#formDynamicParser').on('mousedown', '#submitSimpleTagParser', verifyForm);

    $('#entryContainer').on('click', '#addEntryBtn', function(){
        // // "entryCounter" is a global variable used in the usual "addEntry"-javascript.
        // // We could just use a newly initialized counter too, because this "onclick" is always called right after the original onclick
        // appendSimpleTagEntry(entryCounter);

        var STPEntries = $("#entry" + entryCounter);

        STPEntries.find("#STPEntries0").attr("id", "STPEntries" + entryCounter);

        STPEntries.find("#startTag0").val("");
        STPEntries.find("#startTag0").attr("name", "startTag" + entryCounter);
        STPEntries.find("#startTag0").attr("id", "startTag" + entryCounter);

        STPEntries.find("#endTag0").val("");
        STPEntries.find("#endTag0").attr("name", "endTag" + entryCounter);
        STPEntries.find("#endTag0").attr("id", "endTag" + entryCounter);

        STPEntries.find("#arraySplitTag0").val("");
        STPEntries.find("#arraySplitTag0").attr("name", "arraySplitTag" + entryCounter);
        STPEntries.find("#arraySplitTag0").attr("id", "arraySplitTag" + entryCounter);

    });

    function appendSimpleTagEntry(index){
        $("#entry" + index).append('<div style="width: 100%" id="STPEntries' + index + '"> ' +
        '<input name="startTag' + index + '" placeholder="Starttag" value="" id="startTag' + index + '" type="text"> ' +
        '<input name="endTag' + index + '" placeholder="Endtag" value="" id="endTag' + index + '" type="text">' +
        '<input name="arraySplitTag' + index + '" placeholder="Array splitting tag" value="" id="arraySplitTag' + index + '" type="text"> ' +
        '</div>' +
        '<hr>');
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

            for(var j = i + 1; j <= entryCounter; j++) {
                if($("#startTag" + j).val().includes($("#startTag" + i).val())) {
                    alert("Simpler start-tag appeared in entry-list that will match tags which are matched by a later defined and more complex start-tag!");
                    return false;
                }
            }
        }

        return true;
    }
</g:javascript>