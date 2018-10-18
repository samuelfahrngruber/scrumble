using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class User : IIndexable
    {
        public int Id { get; set; }
        public string Username { get; set; }

        public User(int id, string name = default(string))
        {
            Id = id;
            Username = name;
        }

        public override string ToString()
        {
            return Username;
        }

        public override bool Equals(object obj)
        {
            return obj is User ? ((User)obj).Id == Id : false;
        }

        public override int GetHashCode()
        {
            return base.GetHashCode();
        }
    }
}
