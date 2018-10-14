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
        private string Password { get; set; }

        public User(int id, string name, string password)
        {
            Id = id;
            Username = name;
            Password = password;
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
