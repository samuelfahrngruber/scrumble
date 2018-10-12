using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class Project
    {
        public int Id { get; private set; }
        public string Name { get; set; }
        public User ProductOwner { get; set; }
        public List<User> Team { get; private set; }

        
    }
}
