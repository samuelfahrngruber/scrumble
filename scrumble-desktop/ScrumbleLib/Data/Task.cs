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
        public Sprint Sprint { get; set; }

        public Task(int id, string name = default(string), string info = default(string), int rejections = default(int), User responsibleUser = default(User), User verifyingUser = default(User), Sprint sprint = default(Sprint))
        {
            this.Id = id;
            this.Name = name;
            this.Info = info;
            this.Rejections = rejections;
            this.VerifyingUser = verifyingUser;
            this.ResponsibleUser = responsibleUser;
            this.Sprint = sprint;
        }

        public override string ToString()
        {
            return "[#" + Id + "] " + Name;
        }
    }
}
