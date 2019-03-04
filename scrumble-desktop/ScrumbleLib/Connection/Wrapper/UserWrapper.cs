using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ScrumbleLib.Connection.Json;
using ScrumbleLib.Data;
using ScrumbleLib.Utils;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class UserWrapper : IIndexableDataWrapper<User>
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public User WrappedValue { get; private set; }

        private static IndexSet<UserWrapper> instances = new IndexSet<UserWrapper>();

        internal static UserWrapper GetInstance(User wrappedValue)
        {
            UserWrapper instance;
            if (instances.Contains(wrappedValue.Id))
            {
                instance = instances[wrappedValue.Id];
                instance.WrappedValue = wrappedValue;
                instance.OnPropertyChanged(null);
            }
            else
            {
                ScrumbleController.Users.Add(wrappedValue);
                instance = new UserWrapper(wrappedValue);
                instances.Add(instance);
            }
            return instance;
        }

        internal static UserWrapper GetNonIndexedInstance(User u)
        {
            return new UserWrapper(u);
        }

        private UserWrapper(User wrappedValue)
        {
            WrappedValue = wrappedValue;
        }

        internal static UserWrapper GetInstance(int userId)
        {
            UserWrapper instance;
            if (instances.Contains(userId))
            {
                instance = instances[userId];
            }
            else
            {
                instance = new UserWrapper(userId);
                instances.Add(instance);
            }
            return instance;
        }

        private UserWrapper(int id)
        {
            WrappedValue = ScrumbleController.GetUser(id);
        }

        protected void OnPropertyChanged(string propertyName)
        {
            var handler = PropertyChanged;
            if (PropertyChanged != null)
            {
                handler(this, new PropertyChangedEventArgs(propertyName));
            }
        }

        public string ToJson()
        {
            JsonSerializerSettings settings = new JsonSerializerSettings();
            settings.ContractResolver = new LowercaseContractResolver();
            string json = JsonConvert.SerializeObject(this, Formatting.Indented, settings);
            return json;
        }
        
        public void ApplyJson(JObject jsonObject)
        {
            if (jsonObject.ContainsKey("id")) WrappedValue.Id = (int)jsonObject["id"];
            if (jsonObject.ContainsKey("name")) WrappedValue.Username = (string)jsonObject["name"]; // username
            OnPropertyChanged("ALL");
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
            protected set
            {
                // todo inspect
                WrappedValue.Id = value;
            }
        }

        [JsonProperty("name")]
        public string Username
        {
            get
            {
                return WrappedValue.Username;
            }

            set
            {
                WrappedValue.Username = value;
                OnPropertyChanged("Name");
                ScrumbleConnection.Update(this);
            }
        }
    }
}
