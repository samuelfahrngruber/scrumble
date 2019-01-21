using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using ScrumbleLib.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    internal static class ScrumbleController
    {
        public static User currentUser { get; private set; } = null;
        public static Project currentProject { get; private set; } = null;

        public static IndexSet<Project> Projects { get; private set; } = new IndexSet<Project>();
        public static IndexSet<Sprint> Sprints { get; private set; } = new IndexSet<Sprint>();
        public static IndexSet<Data.Task> Tasks { get; private set; } = new IndexSet<Data.Task>();
        public static IndexSet<User> Users { get; private set; } = new IndexSet<User>();

        public static void SetCurrentProject(Project project)
        {
            currentProject = project;
        }

        public static void SetCurrentProject(int projectId)
        {
            currentProject = GetProject(projectId, true);
        }

        public static Project GetProject(int id, bool forceLoad = false)
        {
            if (Projects.Contains(id) && forceLoad == false)
            {
                return Projects[id];
            }
            Project project = new Project(id);
            Projects.Add(project);
            ProjectWrapper pw = ProjectWrapper.GetInstance(project);
            ScrumbleConnection.Get(pw);
            ScrumbleConnection.GetTeam(pw);
            Scrumble.OnProjectAdded(project);
            return project;
        }

        public static void GetProjectTasks()
        {
            ProjectWrapper pw = ProjectWrapper.GetInstance(currentProject.Id);
            ScrumbleConnection.GetProjectTasks(pw);
        }

        public static void GetMyProjects()
        {
            UserWrapper uw = UserWrapper.GetInstance(currentUser.Id);
            ScrumbleConnection.GetUsersProjects(uw);
        }

        public static User GetUser(int id)
        {
            if (Users.Contains(id))
            {
                return Users[id];
            }
            User user = new User(id);
            Users.Add(user);
            UserWrapper uw = UserWrapper.GetInstance(user);
            ScrumbleConnection.Get(uw);
            Scrumble.OnUserAdded(user);
            return user;
        }

        public static Data.Task GetTask(int id)
        {
            if (Tasks.Contains(id))
            {
                return Tasks[id];
            }
            Data.Task task = new Data.Task(id);
            Tasks.Add(task);
            TaskWrapper tw = TaskWrapper.GetInstance(task);
            ScrumbleConnection.Get(tw);
            Scrumble.OnTaskAdded(task);
            return task;
        }

        public static Data.Task AddTask(Data.Task task)
        {
            TaskWrapper tw = TaskWrapper.GetInstance(task);
            ScrumbleConnection.Add(tw);
            Tasks.Add(tw.WrappedValue);
            Scrumble.OnTaskAdded(tw.WrappedValue);
            return tw.WrappedValue;
        }

        public static Data.Task DeleteTask(int taskId)
        {
            TaskWrapper tw = TaskWrapper.GetInstance(taskId);
            ScrumbleConnection.Delete(tw);
            Tasks.Remove(taskId);
            Scrumble.OnTaskRemoved(tw.WrappedValue);
            return tw.WrappedValue;
        }

        public static Sprint GetSprint(int id)
        {
            if (Sprints.Contains(id))
            {
                return Sprints[id];
            }
            Sprint sprint = new Sprint(id);
            Sprints.Add(sprint);
            SprintWrapper sw = SprintWrapper.GetInstance(sprint);
            ScrumbleConnection.Get(sw);
            Scrumble.OnSprintAdded(sprint);
            return sprint;
        }


        public static bool Login(string username, string password)
        {
            int uid = ScrumbleConnection.Login(username, password);
            if (uid <= 0)
                return false;
            currentUser = GetUser(uid);
            return true;
        }

        public static void GetDailyScrumEntries()
        {
            ProjectWrapper pw = ProjectWrapper.GetInstance(currentProject.Id);
            ScrumbleConnection.GetDailyScrumEntries(pw);
        }

        private static bool isLoggedIn()
        {
            return currentUser != null;
        }
        private static bool isProjectSet()
        {
            return currentProject != null;
        }
        private static void assertLoggedIn()
        {
            if (!isLoggedIn())
                throw new Exception("not logged in");
        }
        private static void assertProjectSet()
        {
            if (!isProjectSet())
                throw new Exception("project not set");
        }
    }
}
