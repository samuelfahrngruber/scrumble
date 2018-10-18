using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    public static class ScrumbleController
    {
        public static IndexSet<Project> Projects { get; private set; } = new IndexSet<Project>();
        public static IndexSet<Sprint> Sprints { get; private set; } = new IndexSet<Sprint>();
        public static IndexSet<Data.Task> Tasks { get; private set; } = new IndexSet<Data.Task>();
        public static IndexSet<User> Users { get; private set; } = new IndexSet<User>();

        public static Project GetProject(int id)
        {
            if (Projects.Contains(id))
            {
                return Projects[id];
            }
            Project project = new Project(id);
            Projects.Add(project);
            System.Threading.Tasks.Task.Run(() => ScrumbleConnection.GetProject(project));
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
            System.Threading.Tasks.Task.Run(() => ScrumbleConnection.GetUser(user));
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
            System.Threading.Tasks.Task.Run(() => ScrumbleConnection.GetTask(task));
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
            System.Threading.Tasks.Task.Run(() => ScrumbleConnection.GetSprint(sprint));
            return sprint;
        }
    }
}
