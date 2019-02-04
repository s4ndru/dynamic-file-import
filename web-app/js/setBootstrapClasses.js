function setBootstrapClasses() {

    // DynamicParser
    $("form#formDynamicParser").attr("class", "well");
    $("form#formDynamicParser > input").attr("class", "btn btn-primary pull-right");
    $("div#divDynamicParser > div ").attr("class", "form-group");
    $("div#divDynamicParser > div > input").attr("class", "form-control");
    $("div#divDynamicParser > div > select ").attr("class", "form-control");

    // Entries
    //$("#entryContainer > div ").attr("class", "form-inline");
    $("#entries > div").attr("class", "form-group");
    $("#entries > div").attr("style", "width: 100%");

    $("#field0").attr("class", "form-control");
    $("#field0").attr("style", "width: 50%; float: left");
    $("#dataType0").attr("class", "form-control");
    $("#dataType0").attr("style", "width: 50%;");
    $("#entryContainer > button ").attr("class", "btn btn-warning");

    // CWP
    $("#linesToIgnore > input").attr("class", "form-control");
    $("div#divDynamicParser > div > button").attr("class", "btn btn-warning");
    $("#CWPEntries0").attr("class", "input-group");
    $("#CWPEntries0 > input").attr("class", "form-control");

    // STP
    $("#STPEntries0").attr("class", "input-group");
    $("#STPEntries0 > input").attr("class", "form-control");
    $("#STPEntries0 > input:nth-child(-n + 2)").attr("style", "width: 50%");

    // SXP

    //TSP
    $("#TSPEntries0 > input").attr("class", "form-control");
    $("#TSPEntries0 > input").attr("style", "");


    //Transformation stuff
    $("form#divTransformationForm").attr("class", "well");
    $("#order_id").attr("class", "form-control");

    //TransformationProcedure
    $("#divTransformationForm > div").attr("class", "form-group");
    $("#divTransformationForm > div > select").attr("class", "form-control");
    $("#divTransformationForm > div > a").attr("class", "btn btn-warning");
    $("#parameter_container_container").attr("class", "form-group");
    $("#procedureContainer > div").attr("class", "form-group");
    $("#procedureContainer > input").attr("class", "btn btn-primary pull-right");

    $("#is_repetitive").parent().parent().attr("class", "checkbox");
    $("#notable_objects_container").parent().attr("style", "width: 100%");
    $("#notable_objects > div").attr("class", "form-group");
    $("#notable_objects > div > select").attr("class", "form-control");
    $("#notable_objects > div > select").attr("style", "width: 50%; float: left;");
    $("#notable_objects > div > input").attr("class", "form-control");
    $("#notable_objects > div > input").attr("style", "width: 50%;");
    $("#addObjectBtn").attr("class", "btn btn-warning");

    $("#cross_is_repetitive").parent().parent().attr("class", "checkbox");
    $("#cross_notable_objects_container").attr("class", "form-group");
    $("#cross_notable_objects_container").parent().attr("style", "width: 100%");
    $("#cross_notable_objects > div").attr("class", "form-group");
    $("#cross_notable_objects > div > select").attr("class", "form-control");
    $("#cross_notable_objects > div > select").attr("style", "width: 50%; float: left;");
    $("#cross_notable_objects > div > input").attr("class", "form-control");
    $("#cross_addObjectBtn").attr("class", "btn btn-warning");

    $("#parameter_container_label").attr("style", "width: 100%");
    $("#parameter_container > div > div").attr("class", "form-group");
    $("#parameter_container > div > div > select").attr("class", "form-control");
    $("#parameter_container > div > div > input").attr("class", "form-control");
    $("#parameter_container > div > div > select").attr("style", "width: 50%;");
    $("#parameter_container > div > div > input").attr("style", "width: 50%;");
    $("#parameter_container > div > div > select, #parameter_container > div > div > input").attr("style", "width: 50%;");
    $("#parameter_container > div > div > select:nth-child(2n - 1), #parameter_container > div > div > input:nth-child(2n - 1)").attr("style", "width: 50%; float: left;");
    $("#parameter_container > button").attr("class", "btn btn-warning");

    //TransformationRoutine
    $("#to_update").parent().parent().attr("class", "checkbox");
    $("#order_id").parent().attr("class", "form-group");
    $("#update_properties").attr("class", "form-group");
    $("#update_properties > div > select").attr("class", "form-control col-lg-6");
    $("#update_properties > div > select").attr("style", "width: 50%;");
    $("#update_properties > div > select:nth-child(2n - 1)").attr("style", "width: 50%; float: left;");
    $("#property_container > input").attr("class", "btn btn-warning");
    $("#routineContainer > input").attr("class", "btn btn-primary pull-right");

    //Fileparsing folder-input
    $("#dfiParsingForm").attr("class", "form-group");
    $("#dfiParsingForm > input:nth-child(1)").attr("class", "form-control");
    $("#dfiParsingForm > input:nth-child(1)").attr("style", "width: 70%; float: left;");
    $("#dfiParsingForm > input:nth-child(2)").attr("class", "btn btn-warning");
    $("#dfiParsingForm > input:nth-child(2)").attr("style", "width: 30%;");
}

// If this file is added at the end of a html page, then call the function to set all the styling classes
setBootstrapClasses();

// Ajax used to generate sub elements for transformation-routines/procedures.
// The ajaxComplete function-call ensures that the added sub elements are also properly styled.
$(document).ajaxComplete(setBootstrapClasses);