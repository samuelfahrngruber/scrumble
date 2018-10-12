using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class Task
    {
        public string Name { get; set; }
        public Task(String name)
        {
            Name = name;
        }

        public override string ToString()
        {
            return Name;
        }
    }
}
