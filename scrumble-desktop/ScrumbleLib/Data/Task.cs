using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class Task : IIndexable
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public string Info { get; set; }
        public int Rejections { get; set; }
        public User ResponsibleUser { get; set; }
        public User VerifyingUser { get; set; }

        public Task(string name)
        {
            Name = name;
        }

        public override string ToString()
        {
            return Name;
        }
    }
}
