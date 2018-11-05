using ScrumbleLib.Connection;
using ScrumbleLib.Connection.Wrapper;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ScrumbleLib.Data;
using Newtonsoft.Json.Linq;

namespace ScrumbleLib
{
    public static class Scrumble
    {
        public static ObservableCollectionEx<TaskWrapper> MyTasks { get; private set; } = new ObservableCollectionEx<TaskWrapper>();
        internal static ObservableCollectionEx<TaskWrapper> Scrumboard { get; private set; } = new ObservableCollectionEx<TaskWrapper>();
        public static ObservableCollectionEx<TaskWrapper> ProductBacklog { get; private set; } = new ObservableCollectionEx<TaskWrapper>();

        public static ObservableCollectionEx<TaskWrapper> GetScrumboard(bool forceLoad = false)
        {
            if(forceLoad == true || Scrumboard == null)
            {
                Scrumboard = new ObservableCollectionEx<TaskWrapper>();
                ScrumbleController.GetScrumboard();
            }
            return Scrumboard;
        }

        public static int CurrentProject
        {
            get
            {
                return ScrumbleController.currentProject;
            }
        }

        public static IDevLogger Logger { get; set; }

        public static WrapperFactory WrapperFactory { get; } = new WrapperFactory();

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
            if (task.Project != null
                    && task.Project.Id == ScrumbleController.currentProject 
                    && task.Sprint != null 
                    && task.Sprint.Id == task.Project.CurrentSprint.Id
                    && task.State != TaskState.PRODUCT_BACKLOG)
                GetScrumboard().Add(TaskWrapper.GetInstance(task.Id));
            if (task.Project != null && task.Project.Id == ScrumbleController.currentProject && task.State == TaskState.PRODUCT_BACKLOG)
                ProductBacklog.Add(TaskWrapper.GetInstance(task.Id));
        }

        internal static void OnSprintAdded(Sprint sprint)
        {
            // todo implement
        }

        internal static void ScrumboardFromJson(string json)
        {
            ScrumboardFromJson(JArray.Parse(json));
        }

        internal static void ScrumboardFromJson(JArray scrumboard)
        {
            foreach (JObject task in scrumboard)
            {
                Data.Task t = new Data.Task((int)task["id"]);
                TaskWrapper tw = TaskWrapper.GetInstance(t);
                tw.ApplyJson(task);
                OnTaskAdded(t);
            }
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
