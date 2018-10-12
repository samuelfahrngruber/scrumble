using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class Sprint
    {
        public int Id { get; private set; }
        public int Number { get; private set; }
        public DateTime Start { get; set; }
        public DateTime Deadline { get; set; }
        
        public Project Project { get; set; }
    }
}
