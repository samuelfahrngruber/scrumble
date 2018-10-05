using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib
{
    public class UserStory
    {
        public string Name { get; set; }
        public UserStory(String name)
        {
            Name = name;
        }

        public override string ToString()
        {
            return Name;
        }
    }
}
