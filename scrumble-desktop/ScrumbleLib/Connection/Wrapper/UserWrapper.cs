using Newtonsoft.Json.Linq;
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
        // wrapping methods and constructors
        public static UserWrapper Wrap(User user)
        {
            return new UserWrapper(user);
        }
        public UserWrapper(User wrappedValue) : base(wrappedValue)
        {

        }
        // unwrapping methods and constructors
        public static UserWrapper FromJson(JObject jsonObject)
        {
            return new UserWrapper(jsonObject);
        }
        public static UserWrapper FromJson(string json)
        {
            return FromJson(JObject.Parse(json));
        }

        public UserWrapper(int id, string username)
            : base(new User(id, username, null))
        {

        }

        public UserWrapper(JObject jsonObject) : this(
            (int)jsonObject["id"],
            (string)jsonObject["username"])
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
    }
}
