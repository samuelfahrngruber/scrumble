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
            Project p = ScrumbleConnection.GetProject(id).WrappedValue;
            if(p != null) Projects.Add(p);
            return p;
        }

        public static User GetUser(int id)
        {
            if (Users.Contains(id))
            {
                return Users[id];
            }
            User u = ScrumbleConnection.GetUser(id).WrappedValue;
            Users.Add(u);
            return u;
        }

        public static Data.Task GetTask(int id)
        {
            if (Tasks.Contains(id))
            {
                return Tasks[id];
            }
            Data.Task t = ScrumbleConnection.GetTask(id).WrappedValue;
            Tasks.Add(t);
            return t;
        }

        public static Sprint GetSprint(int id)
        {
            if (Sprints.Contains(id))
            {
                return Sprints[id];
            }
            Sprint s = ScrumbleConnection.GetSprint(id).WrappedValue;
            Sprints.Add(s);
            return s;
        }
    }
}
