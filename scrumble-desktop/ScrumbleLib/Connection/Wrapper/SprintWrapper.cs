using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class SprintWrapper
    {
        public Sprint Value { get; set; }
        public ScrumbleConnection Connection { get; private set; }
        public ScrumbleController Controller { get; private set; }

        public int Project
        {
            get
            {
                return Value.Project.Id;
            }

            set
            {
                Value.Project = Controller.Projects[value];
                Connection.Update(this);
            }
        }
    }
}
