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
        public static Dictionary<int, Project> Projects { get; set; } = new Dictionary<int, Project>();

        public static Project GetProject(int id)
        {
            if (Projects.ContainsKey(id))
            {
                return Projects[id];
            }
            return null;
            ScrumbleConnection;
        }
    }
}
