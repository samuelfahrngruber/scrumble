﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib
{
    public class User
    {
        public string Name { get; set; }
        public User(String name)
        {
            Name = name;
        }

        public override string ToString()
        {
            return Name;
        }
    }
}
