﻿$(function () {
    refreshClasses();
});

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

var tasks = [];

function addTasks() {
    alert(JSON.stringify(scrumble_scrumboardInterface));
    var tmptasks = scrumble_scrumboardInterface.getTasks();
    for(var t of tmptasks){
        addTaskWrapper(t);
    }
}

function addTask(taskWrapper) {
    alert(JSON.stringify(taskWrapper));
    var taskdiv = $('<div class="portlet"></div>');
    var header = $('<div class="portlet-header">' + taskWrapper.name + '<div>');
    var content = $('<div class="portlet-content">' + taskWrapper.info + '</div>');
    taskdiv.append(header);
    taskdiv.append(content);
    $("#column-sprintbacklog").append(taskdiv);
    refreshClasses();
}

window.onerror = function (errorMessage, url, lineNumber) {
    alert(errorMessage);
}