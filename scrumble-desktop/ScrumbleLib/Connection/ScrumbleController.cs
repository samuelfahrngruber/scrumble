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
        public static IndexSet<User> Users { get; private set; } = new IndexSet<User>();

        public static Project GetProject(int id)
        {
            if (Projects.Contains(id))
            {
                return Projects[id];
            }
            Project p = ScrumbleConnection.GetProject(id).WrappedValue;
            return p;
        }

        public static User GetUser(int id)
        {
            if (Users.Contains(id))
            {
                return Users[id];
            }
            return null;
        }
    }
}
