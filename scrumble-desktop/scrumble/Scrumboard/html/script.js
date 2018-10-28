$(function () {
    refreshClasses();
});

var tasks = [];

var state2column = {
    "SPRINTBACKLOG": "column-sprintbacklog",
    "INPROGRESS": "column-inprogress",
    "INTEST": "column-intest",
    "DONE": "column-done"
}

var column2state = {};

for(var key in state2column) {
    column2state[state2column[key]] = key;
}

function refreshClasses() {
    $(".column").sortable({
        connectWith: ".column",
        handle: ".portlet-header",
        cancel: ".portlet-toggle",
        placeholder: "portlet-placeholder ui-corner-all",
        stop: (event, ui) => {
            var index = ui.item.index();
            var column = ui.item.parent();
            console.log("new position of " + ui.item.attr("id") + ": (column: " + column.attr("id") + ", row: " + index + ")");
            changeTaskState(Number(ui.item.attr("id")), column2state[column.attr("id")]);
        }
    });

    $(".portlet")
      .addClass("ui-widget ui-widget-content ui-helper-clearfix ui-corner-all")
      .find(".portlet-header")
      .addClass("ui-widget-header ui-corner-all")

    $(".portlet-toggle").click(function () {
        var icon = $(this);
        icon.toggleClass("ui-icon-minusthick ui-icon-plusthick");
        icon.closest(".portlet").find(".portlet-content").toggle();
    });
}

function addTask(taskWrapper) {
    var column = $("#" + state2column[taskWrapper.state.toUpperCase()]);

    var taskdiv = $('<div class="portlet" id="' + taskWrapper.id + '"></div>');
    var header = $('<div class="portlet-header">' + taskWrapper.name + '<div>');
    var content = $('<div class="portlet-content">' + taskWrapper.info + '</div>');
    taskdiv.append(header);
    taskdiv.append(content);
    $("#" + taskWrapper.id).remove();
    column.append(taskdiv);
    refreshClasses();
}

function changeTaskState(taskid, newTaskState) {
    scrumble_scrumboardInterface.changeTaskState(taskid, newTaskState);
}

window.onerror = function (errorMessage, url, lineNumber) {
    alert(errorMessage);
}
window.onc

