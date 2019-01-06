<g:form name="formDynamicParser">
    <div id="divDynamicParser">
        <div>
            <g:render template="/extraction/parser"/>
            <g:textField name="superTag" placeholder="Supertag" />
            <g:textField name="excelTag" placeholder="Exceltag (for SpreadsheetML)" />
            <g:field type="number" name="startBuffer" placeholder="# of ignored top supertags" />
            <g:field type="number" name="endBuffer" placeholder="# of ignored bottom supertags" />
        </div>
    </div>

    <div id="entryContainer">
        <g:render template="/extraction/entries"/>
    </div>

    <g:submitToRemote id="submitSimpleXMLParser" name="submitSimpleXMLParser" value="save parser"
                      action="createSimpleXMLParser" onFailure="alert(XMLHttpRequest.responseText)" controller="extraction"
                      onSuccess="alert('Parser was successfully saved!'); location.reload();"/>
    <div style="clear: both;"></div>
</g:form>

<g:javascript>

    // Remove the "optional" checkboxes. Those will not be used for the xmlParser, because of the nature of XML.
    $("#optional0").parent().parent().remove();

    $('#formDynamicParser').on('mousedown', '#submitSimpleXMLParser', verifyForm);

    // $('#entryContainer').on('click', '#addEntryBtn', function(){
    //     $("#optional" + entryCounter).parent().remove()
    // });

    function verifyForm(){

        if($("#name").val() === ""){
            alert("Please fill out the 'Parsername'-field.");
            return false;
        }

        if($("#selectorName").val() === ""){
            alert("Please fill out the 'Filename substring'-field.");
            return false;
        }

        if($("#superTag").val() === ""){
            alert("Please fill out the 'Supertag'-field.");
            return false;
        }

        if(($("#excelTag").val() !== "" && ($("#startBuffer").val() === "" || $("#endBuffer").val() === "")) || ($("#excelTag").val() === "" && $("#startBuffer").val() !== "" && $("#endBuffer").val() !== "")){
            alert("'Exceltag' and the 'ignored top supertags' or 'ignored bottom supertags' fields can not be set without each other. Either set 'Exceltag' + 'ignored top supertags' + ('ignored bottom supertags') or none!");
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
        }

        return true;
    }
</g:javascript>