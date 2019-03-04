$(function () {
    refreshClasses();
});

var tasks = [];

var state2column = {
    "SPRINT_BACKLOG": "column-sprintbacklog",
    "IN_PROGRESS": "column-inprogress",
    "TO_VERIFY": "column-intest",
    "DONE": "column-done"
}

var column2state = {};

for(var key in state2column) {
    column2state[state2column[key]] = key;
}

function refreshClasses() {
    $(".column").sortable({
        connectWith: ".column",
        handle: ".portlet-text",
        cancel: ".portlet-toggle",
        placeholder: "portlet-placeholder",
        stop: (event, ui) => {
            var index = ui.item.index();
            var column = ui.item.parent();
            console.log("new position of " + ui.item.attr("id") + ": (column: " + column.attr("id") + ", row: " + index + ")");
            changeTaskState(Number(ui.item.attr("id")), column2state[column.attr("id")], index - 1);
        }
    });

    $(".portlet")
      .addClass("ui-widget ui-widget-content ui-helper-clearfix")

    $(".portlet-toggle").click(function () {
        var icon = $(this);
        icon.toggleClass("ui-icon-minusthick ui-icon-plusthick");
        icon.closest(".portlet").find(".portlet-content").toggle();
    });
}

function setTask(taskWrapper) {
    var column = $("#" + state2column[taskWrapper.state.toUpperCase()]);

    var taskdiv = $('<div class="portlet borderless" id="' + taskWrapper.id + '"></div>');

    var text = $('<div class="portlet-text borderless">' + taskWrapper.name + '</div>');

    var header_btn_del = $('<div class="portlet-button-delete borderless" onclick="deleteTask(' + taskWrapper.id + ')"><i class="fas fa-times-circle"></i></div>');
    var header_btn_info = $('<div class="portlet-button-info borderless" onclick="showInfo(' + taskWrapper.id + ')"><i class="fas fa-info-circle"></i></div>');

    text.css('background-color', taskWrapper.color);

    taskdiv.append(text);
    taskdiv.append(header_btn_info);
    taskdiv.append(header_btn_del);

    //    <div class="portlet borderless">
    //        <div class="portlet-text borderless">documentation of something</div>
    //        <div class="portlet-button-delete borderless"><i class="fas fa-times-circle"></i></div>
    //        <div class="portlet-button-info borderless"><i class="fas fa-info-circle"></i></div>
    //    </div>

    $("#" + taskWrapper.id).remove();
    column.append(taskdiv);

    //alert();
    refreshClasses();
}

function removeTask(taskWrapper) {
    $("#" + taskWrapper.id).remove();
}

function changeTaskState(taskid, newTaskState, position) {
    scrumble_scrumboardInterface.changeTaskState(taskid, newTaskState, position);
}

function showInfo(taskid) {
    scrumble_scrumboardInterface.showInfo(taskid);
}

function deleteTask(taskid) {
    scrumble_scrumboardInterface.deleteTask(taskid);
}

window.onerror = function (errorMessage, url, lineNumber) {
    alert(errorMessage);
}

