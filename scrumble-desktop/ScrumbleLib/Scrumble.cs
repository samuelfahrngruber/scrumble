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
using ScrumbleLib.Utils;
using System.Collections.Specialized;

namespace ScrumbleLib
{
    public static class Scrumble
    {
        internal static ObservableCollectionEx<TaskWrapper> MyTasks { get; private set; } = null;
        internal static ObservableCollectionEx<TaskWrapper> Scrumboard { get; private set; } = null;
        internal static ObservableCollectionEx<TaskWrapper> ProductBacklog { get; private set; } = null;

        internal static ProjectWrapper currentProject { get; set; } = null;

        public static IDevLogger Logger { get; set; }

        public static WrapperFactory WrapperFactory { get; } = new WrapperFactory();

        public static ObservableCollectionEx<TaskWrapper> GetScrumboard(bool forceLoad = false)
        {
            if (Scrumboard == null) {
                Scrumboard = new ObservableCollectionEx<TaskWrapper>();
                Scrumboard.CollectionChanged += taskPropertyChanged;
            }

            if (forceLoad == true || Scrumboard == null)
            {
                //ObservableCollectionEx<TaskWrapper> oldscrumboard;
                Scrumboard.Clear();
                //Scrumboard = new ObservableCollectionEx<TaskWrapper>();
                ScrumbleController.GetProjectTasks();
            }
            return Scrumboard;
        }

        private static void taskPropertyChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            if(e.Action == NotifyCollectionChangedAction.Reset)
            {
                RefreshObservableTaskCollections();
            }
        }

        private static void RefreshObservableTaskCollections()
        {
            foreach(Data.Task t in ScrumbleController.Tasks.Values)
            {

            }
        }

        public static ObservableCollectionEx<TaskWrapper> GetMyTasks(bool forceLoad = false)
        {
            if (forceLoad == true || MyTasks == null)
            {
                MyTasks = new ObservableCollectionEx<TaskWrapper>();
                ScrumbleController.GetProjectTasks();
            }
            return MyTasks;
        }

        public static ObservableCollectionEx<TaskWrapper> GetProductBacklog(bool forceLoad = false)
        {
            if (forceLoad == true || ProductBacklog == null)
            {
                ProductBacklog = new ObservableCollectionEx<TaskWrapper>();
                ScrumbleController.GetProjectTasks();
            }
            return ProductBacklog;
        }

        public static ProjectWrapper GetCurrentProject(bool forceLoad = false)
        {
            if (forceLoad == true || currentProject == null)
            {
                currentProject = ProjectWrapper.GetInstance(ScrumbleController.currentProject.Id);
            }
            return currentProject;
        }

        public static bool Login(string username, string password)
        {
            return ScrumbleController.Login(username, password);
        }

        public static void SetProject(int projectId)
        {
            ScrumbleController.SetCurrentProject(projectId);
        }

        public static void SetProject(Project project)
        {
            ScrumbleController.SetCurrentProject(project);
        }

        public static void SetProject(ProjectWrapper project)
        {
            ScrumbleController.SetCurrentProject(project.WrappedValue);
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
            if (task.Sprint != null
                && (task.ResponsibleUser.Id == ScrumbleController.currentUser.Id || task.VerifyingUser.Id == ScrumbleController.currentUser.Id)
                && ScrumbleController.currentProject.CurrentSprint.Id == task.Sprint.Id)
            {
                TaskWrapper tw = TaskWrapper.GetInstance(task.Id);
                int index = GetMyTasks().IndexOf(tw);
                if (index != -1)
                    GetMyTasks()[index] = tw;
                else
                    GetMyTasks().Add(TaskWrapper.GetInstance(task.Id));
            }
            if (task.Project != null
                && task.Project.Id == ScrumbleController.currentProject.Id
                && task.Sprint != null 
                && task.Sprint.Id == task.Project.CurrentSprint.Id
                && task.State != TaskState.PRODUCT_BACKLOG)
            {
                TaskWrapper tw = TaskWrapper.GetInstance(task.Id);
                int index = GetScrumboard().IndexOf(tw);
                if (index != -1)
                    GetScrumboard()[index] = tw;
                else
                    GetScrumboard().Add(TaskWrapper.GetInstance(task.Id));
            }
            if (task.Project != null
                && task.Project.Id == ScrumbleController.currentProject.Id
                && task.State == TaskState.PRODUCT_BACKLOG)
            {
                TaskWrapper tw = TaskWrapper.GetInstance(task.Id);
                int index = GetProductBacklog().IndexOf(tw);
                if (index != -1)
                    GetProductBacklog()[index] = tw;
                else
                    GetProductBacklog().Add(TaskWrapper.GetInstance(task.Id));
            }
        }

        public static void OnTaskRemoved(Data.Task wrappedValue)
        {
            GetMyTasks().Remove(TaskWrapper.GetInstance(wrappedValue));
            GetScrumboard().Remove(TaskWrapper.GetInstance(wrappedValue));
            GetProductBacklog().Remove(TaskWrapper.GetInstance(wrappedValue));
        }

        internal static void OnSprintAdded(Sprint sprint)
        {
            // todo implement
        }

        internal static void TasksFromJson(string json)
        {
            TasksFromJson(JArray.Parse(json));
        }

        internal static void TasksFromJson(JArray scrumboard)
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

        public static void AddTask(Data.Task task)
        {
            ScrumbleController.AddTask(task);
        }

        public static void DeleteTask(int taskId)
        {
            ScrumbleController.DeleteTask(taskId);
        }
    }
}
