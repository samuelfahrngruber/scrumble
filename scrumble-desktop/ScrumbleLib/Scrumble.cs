﻿using ScrumbleLib.Connection;
using ScrumbleLib.Connection.Wrapper;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ScrumbleLib.Data;

namespace ScrumbleLib
{
    public static class Scrumble
    {
        public static ObservableCollectionEx<TaskWrapper> MyTasks { get; private set; } = new ObservableCollectionEx<TaskWrapper>();
        public static ObservableCollectionEx<TaskWrapper> Scrumboard { get; private set; } = new ObservableCollectionEx<TaskWrapper>();

        public static IDevLogger Logger { get; set; }

        public static bool Login(string username, string password)
        {
            return ScrumbleController.Login(username, password);
        }

        public static void SetProject(int projectId)
        {
            ScrumbleController.SetCurrentProject(projectId);
        }

        internal static void OnProjectAdded(Project p)
        {
            // todo implement
        }

        internal static void OnUserAdded(User user)
        {
            // todo implement
        }

        internal static void OnTaskAdded(Data.Task task)
        {
            if(task.ResponsibleUser.Id == ScrumbleController.currentUser) // todo also check for current project
                MyTasks.Add(TaskWrapper.GetInstance(task.Id));
            if (true)//(ScrumbleController.GetProject(ScrumbleController.currentProject).Result.CurrentSprint.Id == task.Sprint.Id)
                Scrumboard.Add(TaskWrapper.GetInstance(task.Id));
        }

        internal static void OnSprintAdded(Sprint sprint)
        {
            // todo implement
        }

        public static void Log(string str, string col)
        {
            if(Logger != null)
            {
                Logger.LogDev(str, col);
            }
        }
    }
}