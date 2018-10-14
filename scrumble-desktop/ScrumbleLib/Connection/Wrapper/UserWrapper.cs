using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class UserWrapper : DataWrapper<User>
    {
        public UserWrapper(User wrappedValue) : base(wrappedValue)
        {

        }

        public string Username
        {
            get
            {
                return WrappedValue.Username;
            }

            set
            {
                WrappedValue.Username = value;
                ScrumbleConnection.Update(this);
            }
        }

        public override User Unwrap()
        {
            throw new NotImplementedException();
        }
    }
}
