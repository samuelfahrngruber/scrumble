using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class UserWrapper
    {
        public User Value { get; set; }
        public ScrumbleConnection Connection { get; private set; }

        public string Username
        {
            get
            {
                return Value.Username;
            }

            set
            {
                Value.Username = value;
                Connection.Update(Value);
            }
        }
    }
}
