﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class Project : IIndexable
    {
        public int Id { get; set; }
        public string Name { get; set; }
        public User ProductOwner { get; set; }
        public HashSet<User> Team { get; set; }

        public Project(int id, string name = default(string), User productOwner = default(User))
        {
            this.Id = id;
            this.Name = name;
            this.ProductOwner = productOwner;

            this.Team = new HashSet<User>();
        }

        public bool AddMember(User member)
        {
            return Team.Add(member);
        }

        public bool RemoveMember(User member)
        {
            return Team.Remove(member);
        }
    }
}
