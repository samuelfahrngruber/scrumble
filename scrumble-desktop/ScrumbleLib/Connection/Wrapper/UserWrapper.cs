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
        public UserWrapper(User wrappedValue) : base(wrappedValue)
        {

        }

        public UserWrapper(int id)
            : base(ScrumbleController.GetUser(id))
        {

        }

        public void ApplyFields(int id, string username)
        {
            WrappedValue.Id = id;
            WrappedValue.Username = username;
        }

        public void ApplyJson(JObject jsonObject)
        {
            ApplyFields(
                (int)jsonObject["id"],
                (string)jsonObject["username"]);
        }

        public void ApplyJson(string json)
        {
            ApplyJson(JObject.Parse(json));
        }

        public int Id
        {
            get
            {
                return WrappedValue.Id;
            }
            set
            {
                // todo inspect
                WrappedValue.Id = value;
            }
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
