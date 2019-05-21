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
using ScrumbleLib.Connection.Change;

namespace ScrumbleLib
{
    public static class Scrumble
    {
        internal static ObservableCollectionEx<TaskWrapper> MyTasks { get; private set; } = null;
        internal static ObservableCollectionEx<TaskWrapper> Scrumboard { get; private set; } = null;
        internal static ObservableCollectionEx<TaskWrapper> ProductBacklog { get; private set; } = null;
        internal static ObservableCollectionEx<ProjectWrapper> MyProjects { get; private set; } = null;
        internal static ObservableCollectionEx<SprintWrapper> Sprints { get; private set; } = null;
        internal static ObservableCollectionEx<DailyScrumEntryWrapper> DailyScrumEntries { get; private set; } = null;

        internal static ProjectWrapper currentProject { get; set; } = null;

        public static IDevLogger Logger { get; set; }

        public static WrapperFactory WrapperFactory { get; } = new WrapperFactory();

        public static DateTime LastChangeRefresh { get; private set; } = DateTime.Now;

        public static ObservableCollectionEx<TaskWrapper> GetScrumboard(bool forceLoad = false)
        {
            if (Scrumboard == null)
            {
                Scrumboard = new ObservableCollectionEx<TaskWrapper>();
                Scrumboard.CollectionChanged += taskPropertyChanged;
                forceLoad = true;
            }

            if (forceLoad == true)
            {
                //ObservableCollectionEx<TaskWrapper> oldscrumboard;
                Scrumboard.Clear();
                //Scrumboard = new ObservableCollectionEx<TaskWrapper>();
                ScrumbleController.GetProjectTasks();
            }
            return Scrumboard;
        }

        public static ObservableCollectionEx<ProjectWrapper> GetMyProjects(bool forceLoad = false)
        {
            if (MyProjects == null)
            {
                MyProjects = new ObservableCollectionEx<ProjectWrapper>();
                MyProjects.CollectionChanged += projectPropertyChanged;
                forceLoad = true;
            }

            if (forceLoad == true)
            {
                MyProjects.Clear();
                ScrumbleController.GetMyProjects();
            }
            return MyProjects;
        }

        public static ObservableCollectionEx<SprintWrapper> GetSprints(bool forceLoad = false)
        {
            if (Sprints == null)
            {
                Sprints = new ObservableCollectionEx<SprintWrapper>();
                Sprints.CollectionChanged += sprintPropertyChanged;
                forceLoad = true;
            }
            if (forceLoad == true)
            {
                Sprints.Clear();
                ScrumbleController.GetSprints();
            }
            return Sprints;
        }

        private static void taskPropertyChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            if (e.Action == NotifyCollectionChangedAction.Reset)
            {
                RefreshObservableTaskCollections();
            }
        }

        private static void projectPropertyChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            if (e.Action == NotifyCollectionChangedAction.Reset)
            {
                RefreshObservableProjectCollections();
            }
        }

        private static void sprintPropertyChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            if (e.Action == NotifyCollectionChangedAction.Reset)
            {
                RefreshObservableSprintCollections();
            }
        }

        private static void RefreshObservableTaskCollections()
        {
            foreach (Data.Task t in ScrumbleController.Tasks.Values)
            {

            }
        }

        private static void RefreshObservableProjectCollections()
        {
            foreach (Project p in ScrumbleController.Projects.Values)
            {

            }
        }

        private static void RefreshObservableSprintCollections()
        {
            foreach (Project p in ScrumbleController.Projects.Values)
            {

            }
        }

        public static ObservableCollectionEx<TaskWrapper> GetMyTasks(bool forceLoad = false)
        {
            if (MyTasks == null)
            {
                MyTasks = new ObservableCollectionEx<TaskWrapper>();
                MyTasks.CollectionChanged += taskPropertyChanged;
                forceLoad = true;
            }
            if (forceLoad)
            {
                MyTasks.Clear();
                ScrumbleController.GetProjectTasks();
            }
            return MyTasks;
        }

        public static ObservableCollectionEx<DailyScrumEntryWrapper> GetDailyScrumEntries(bool forceLoad = false)
        {
            if (DailyScrumEntries == null)
            {
                DailyScrumEntries = new ObservableCollectionEx<DailyScrumEntryWrapper>();
                //DailyScrumEntries.CollectionChanged += dailyScrumPropertyChanged;
                forceLoad = true;
            }
            if (forceLoad == true)
            {
                DailyScrumEntries.Clear();
                ScrumbleController.GetDailyScrumEntries();
            }
            return DailyScrumEntries;
        }

        public static ObservableCollectionEx<TaskWrapper> GetProductBacklog(bool forceLoad = false)
        {
            if (ProductBacklog == null)
            {
                ProductBacklog = new ObservableCollectionEx<TaskWrapper>();
                ProductBacklog.CollectionChanged += taskPropertyChanged;
                forceLoad = true;
            }
            if (forceLoad)
            {
                ProductBacklog.Clear();
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

        public static User GetCurrentUser()
        {
            return ScrumbleController.currentUser;
        }

        internal static UserWrapper UserFromJson(string json)
        {
            return UserFromJson(JArray.Parse(json));
        }

        internal static UserWrapper UserFromJson(JArray jarr)
        {
            JObject u = jarr[0] as JObject;
            return UserFromJson(u);
        }

        internal static UserWrapper UserFromJson(JObject jo)
        {
            User user = new User((int)jo["id"]);
            ScrumbleController.Users.Add(user);
            UserWrapper uw = UserWrapper.GetInstance(user);
            uw.ApplyJson(jo);
            OnUserAdded(user);
            return uw;
        }

        public static bool Login(string username, string password)
        {
            return ScrumbleController.Login(username, password);
        }

        public static bool Register(string username, string password)
        {
            return ScrumbleController.Register(username, password);
        }

        public static void SetProject(int projectId)
        {
            ScrumbleController.SetCurrentProject(projectId);
            currentProject = ProjectWrapper.GetInstance(projectId);
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
            ProjectWrapper pw = ProjectWrapper.GetInstance(p.Id);
            OnProjectAdded(pw);
        }

        internal static void OnProjectAdded(ProjectWrapper pw)
        {
            int index = GetMyProjects().IndexOf(pw);
            if (index != -1)
                GetMyProjects()[index] = pw;
            else
                GetMyProjects().Add(pw);
        }

        internal static void OnUserAdded(User user)
        {
            // todo implement
        }

        internal static void OnTaskAdded(Data.Task task)
        {
            if (task.Sprint != null
                && ScrumbleController.currentProject.CurrentSprint != null
                && ((task.ResponsibleUser != null
                && task.ResponsibleUser.Id == ScrumbleController.currentUser.Id) || (task.VerifyingUser != null && task.VerifyingUser.Id == ScrumbleController.currentUser.Id))
                && ScrumbleController.currentProject.CurrentSprint.Id == task.Sprint.Id
                && !GetMyTasks().Any(item => item.Id == task.Id))
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
                && task.Project.CurrentSprint != null
                && task.Sprint.Id == task.Project.CurrentSprint.Id
                && task.State != TaskState.PRODUCT_BACKLOG
                && !GetScrumboard().Any(item => item.Id == task.Id))
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
                && task.State == TaskState.PRODUCT_BACKLOG
                && !GetProductBacklog().Any(item => item.Id == task.Id))
            {
                TaskWrapper tw = TaskWrapper.GetInstance(task.Id);
                int index = GetProductBacklog().IndexOf(tw);
                if (index != -1)
                    GetProductBacklog()[index] = tw;
                else
                    GetProductBacklog().Add(TaskWrapper.GetInstance(task.Id));
            }
        }

        internal static void OnTaskAdded(TaskWrapper task)
        {
            if (currentProject == null || task == null) return;
            if (task.Sprint != null
                && ((task.ResponsibleUser != null && task.ResponsibleUser == ScrumbleController.currentUser.Id) || (task.VerifyingUser != null && task.VerifyingUser == ScrumbleController.currentUser.Id))
                && ScrumbleController.currentProject.CurrentSprint.Id == task.Sprint
                && !GetMyTasks().Any(item => item.Id == task.Id))
            {
                TaskWrapper tw = TaskWrapper.GetInstance(task.Id);
                int index = GetMyTasks().IndexOf(tw);
                if (index != -1)
                    GetMyTasks()[index] = tw;
                else
                    GetMyTasks().Add(TaskWrapper.GetInstance(task.Id));
            }
            if (task.Project != null
                && task.Project == currentProject.Id
                && task.Sprint != null
                && task.Sprint == currentProject.CurrentSprint
                && task.WrappedValue.State != TaskState.PRODUCT_BACKLOG
                && !GetScrumboard().Any(item => item.Id == task.Id))
            {
                TaskWrapper tw = TaskWrapper.GetInstance(task.Id);
                int index = GetScrumboard().IndexOf(tw);
                if (index != -1)
                    GetScrumboard()[index] = tw;
                else
                    GetScrumboard().Add(TaskWrapper.GetInstance(task.Id));
            }
            if (task.Project != null
                && task.Project == currentProject.Id
                && task.WrappedValue.State == TaskState.PRODUCT_BACKLOG
                && !GetProductBacklog().Any(item => item.Id == task.Id))
            {
                TaskWrapper tw = TaskWrapper.GetInstance(task.Id);
                int index = GetProductBacklog().IndexOf(tw);
                if (index != -1)
                    GetProductBacklog()[index] = tw;
                else
                    GetProductBacklog().Add(TaskWrapper.GetInstance(task.Id));
            }
        }

        internal static void OnTaskStateChanged(TaskWrapper t)
        {
            OnTaskRemoved(t);
            OnTaskAdded(t);
        }

        public static void OnTaskRemoved(int taskId)
        {
            OnTaskRemoved(TaskWrapper.GetInstance(taskId));
        }

        public static void OnTaskRemoved(TaskWrapper tw)
        {
            ScrumbleController.Tasks.Remove(tw.WrappedValue);
            GetMyTasks().Remove(tw);
            GetScrumboard().Remove(tw);
            GetProductBacklog().Remove(tw);
        }

        public static void OnTaskRemoved(Data.Task wrappedValue)
        {
            OnTaskRemoved(TaskWrapper.GetInstance(wrappedValue));
            //ScrumbleController.Tasks.Remove(wrappedValue);
            //GetMyTasks().Remove(TaskWrapper.GetInstance(wrappedValue));
            //GetScrumboard().Remove(TaskWrapper.GetInstance(wrappedValue));
            //GetProductBacklog().Remove(TaskWrapper.GetInstance(wrappedValue));
        }

        internal static void OnSprintAdded(Sprint sprint)
        {
            OnSprintAdded(SprintWrapper.GetInstance(sprint));
        }

        internal static void OnSprintAdded(SprintWrapper sw)
        {
            if (currentProject != null && sw.Project == currentProject.Id
                && !GetSprints().Any(item => item.Id == sw.Id))
                GetSprints().Add(sw);
        }

        internal static void OnDailyScrumEntryAdded(DailyScrumEntryWrapper entry)
        {
            DailyScrumEntries.Add(entry);
        }

        internal static void TasksFromJson(string json)
        {
            if (json != null && json != "")
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

        internal static void SprintsFromJson(string json)
        {
            if (json != null && json != "")
                SprintsFromJson(JArray.Parse(json));
        }

        internal static void SprintsFromJson(JArray sprints)
        {
            foreach (JObject sprint in sprints)
            {
                Sprint s = new Sprint((int)sprint["id"]);
                SprintWrapper sw = SprintWrapper.GetInstance(s);
                sw.ApplyJson(sprint);
                OnSprintAdded(s);
            }
        }


        internal static void DailyScrumEntriesFromJson(string json)
        {
            if (json != null && json != "")
                DailyScrumEntriesFromJson(JArray.Parse(json));
        }

        internal static void DailyScrumEntriesFromJson(JArray dailyScrumEntries)
        {
            foreach (JObject jdsentry in dailyScrumEntries)
            {
                DailyScrumEntry e = new DailyScrumEntry();
                DailyScrumEntryWrapper ew = DailyScrumEntryWrapper.GetInstance(e);
                ew.ApplyJson(jdsentry);
                OnDailyScrumEntryAdded(ew);
            }
        }
        internal static void ProjectsFromJson(string json)
        {
            if (json != null && json != "")
                ProjectsFromJson(JArray.Parse(json));
        }
        internal static void ProjectsFromJson(JArray projects)
        {
            foreach (JObject proj in projects)
            {
                Project p = new Project((int)proj["id"]);
                ProjectWrapper pw = ProjectWrapper.GetInstance(p);
                pw.ApplyJson(proj);
                OnProjectAdded(p);
            }
        }

        public static void Log(string str, string col)
        {
            if (Logger != null)
            {
                Logger.LogDev(str, col);
            }
        }

        public static void AddTask(Data.Task task)
        {
            ScrumbleController.AddTask(task);
        }

        public static int AddProject(Project project, User user = null)
        {
            if (user == null)
                user = GetCurrentUser();
            project.ProductOwner = user;
            ScrumbleController.AddProject(project);
            ProjectWrapper pw = ProjectWrapper.GetInstance(project.Id);
            pw.AddMember(UserWrapper.GetInstance(user.Id));
            //ProjectWrapper pw = ProjectWrapper.GetInstance(project);   
            return pw.Id;
        }


        public static void AddSprint(Sprint s)
        {
            s.Project = ScrumbleController.currentProject;
            ScrumbleController.AddSprint(s);
        }

        public static void DeleteTask(int taskId)
        {
            ScrumbleController.DeleteTask(taskId);
        }

        public static void OnMemberRemoved(User wrappedValue)
        {

        }

        public static void OnMemberRemoved(int userid)
        {
            currentProject.Team.Remove(userid);
            currentProject.FireTeamChanged();
        }


        public static void GetChanges()
        {
            ScrumbleController.GetChanges(LastChangeRefresh);
            LastChangeRefresh = DateTime.Now;
        }

        internal static void ChangesFromJson(string jsonchanges)
        {
            if (jsonchanges != null && jsonchanges != "")
                ChangesFromJson(JArray.Parse(jsonchanges));
        }

        internal static void ChangesFromJson(JArray changes)
        {
            foreach (JObject change in changes)
            {
                ScrumbleChange c = ScrumbleChange.FromJson(change);
                c.Apply();
            }
        }

        public static UserWrapper GetUserByName(string name)
        {
            return ScrumbleController.GetUserByName(name);
        }

        public static void Clear()
        {
            if(MyTasks != null)
                MyTasks.Clear();
            if (Scrumboard != null)
                Scrumboard.Clear();
            if(ProductBacklog != null)
                ProductBacklog.Clear();
            if (MyProjects != null)
                MyProjects.Clear();
            if(Sprints != null)
                Sprints.Clear();
            if(DailyScrumEntries != null)
                DailyScrumEntries.Clear();

            Logger = null;

            LastChangeRefresh = DateTime.Now;

            ScrumbleController.Clear();
        }

        public static void forceInit()
        {
            GetMyProjects(true);
            GetMyTasks(true);
            GetDailyScrumEntries(true);
            GetScrumboard(true);
            GetSprints(true);
            GetProductBacklog(true);
            ScrumbleController.GetProject(currentProject.Id, true);
        }
    }
}
