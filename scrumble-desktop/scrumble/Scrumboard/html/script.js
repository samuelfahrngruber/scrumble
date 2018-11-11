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
        handle: ".portlet-header",
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

function addTask(taskWrapper) {
    var column = $("#" + state2column[taskWrapper.state.toUpperCase()]);

    var taskdiv = $('<div class="portlet" id="' + taskWrapper.id + '"></div>');

    var header = $('<div class="portlet-header"></div>');

    var header_text = $('<div class="portlet-header-text">' + taskWrapper.name + '</div>');
    var header_btn_del = $('<div class="portlet-button-delete" onclick="deleteTask(' + taskWrapper.id + ')"><i class="fas fa-times-circle"></i></div>');
    var header_btn_info = $('<div class="portlet-button-info" onclick="showInfo(' + taskWrapper.id + ')"><i class="fas fa-info-circle"></i></div>');
    header.append(header_text);
    header.append(header_btn_info);
    header.append(header_btn_del);

    //<div class="portlet">
    //   <div class="portlet-header">
    //        <div class="portlet-header-text">documentation</div>
    //        <div class="portlet-button-delete"><i class="fas fa-times-circle"></i></div>
    //        <div class="portlet-button-info"><i class="fas fa-info-circle"></i></div>
    //   </div>
    //   <div class="portlet-content">Lorem ipsum dolor sit amet, consectetuer adipiscing elit</div>
    //</div>
    var content = $('<div class="portlet-content">' + taskWrapper.info + '</div>');
    taskdiv.append(header);
    taskdiv.append(content);
    $("#" + taskWrapper.id).remove();
    column.append(taskdiv);
    refreshClasses();
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

