using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    internal static class ScrumbleController
    {
        public static int currentUser { get; private set; } = -1;
        public static int currentProject { get; private set; } = -1;

        public static IndexSet<Project> Projects { get; private set; } = new IndexSet<Project>();
        public static IndexSet<Sprint> Sprints { get; private set; } = new IndexSet<Sprint>();
        public static IndexSet<Data.Task> Tasks { get; private set; } = new IndexSet<Data.Task>();

        internal static void SetCurrentProject(int projectId)
        {
            currentProject = projectId;
        }

        public static IndexSet<User> Users { get; private set; } = new IndexSet<User>();

        public static Project GetProject(int id)
        {
            if (Projects.Contains(id))
            {
                return Projects[id];
            }
            Project project = new Project(id);
            Projects.Add(project);
            project = ScrumbleConnection.GetProject(project);
            project = ScrumbleConnection.GetTeam(project);
            Scrumble.OnProjectAdded(project);
            return project;
        }

        public static User GetUser(int id)
        {
            if (Users.Contains(id))
            {
                return Users[id];
            }
            User user = new User(id);
            Users.Add(user);
            user = ScrumbleConnection.GetUser(user);
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
            task = ScrumbleConnection.GetTask(task);
            Scrumble.OnTaskAdded(task);
            return task;
        }

        public static Sprint GetSprint(int id)
        {
            if (Sprints.Contains(id))
            {
                return Sprints[id];
            }
            Sprint sprint = new Sprint(id);
            Sprints.Add(sprint);
            sprint = ScrumbleConnection.GetSprint(sprint);
            Scrumble.OnSprintAdded(sprint);
            return sprint;
        }


        public static bool Login(string username, string password)
        {
            currentUser = 2;
            currentProject = 4;
            return true;
        }


        private static bool isLoggedIn()
        {
            return currentUser >= 0;
        }
        private static bool isProjectSet()
        {
            return currentProject >= 0;
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
