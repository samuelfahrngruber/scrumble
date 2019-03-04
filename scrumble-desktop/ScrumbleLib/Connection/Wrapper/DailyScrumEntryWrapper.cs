using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Newtonsoft.Json.Linq;
using System.ComponentModel;
using Newtonsoft.Json;
using ScrumbleLib.Connection.Json;

namespace ScrumbleLib.Connection.Wrapper
{
    public class DailyScrumEntryWrapper : IDataWrapper<DailyScrumEntry>
    {
        public DailyScrumEntry WrappedValue { get; private set; }

        internal static DailyScrumEntryWrapper GetInstance(DailyScrumEntry wrappedValue)
        {
            return new DailyScrumEntryWrapper(wrappedValue);
        }

        private DailyScrumEntryWrapper(DailyScrumEntry wrappedValue)
        {
            WrappedValue = wrappedValue;
        }

        protected void OnPropertyChanged(string propertyName)
        {
            var handler = PropertyChanged;
            if (PropertyChanged != null)
            {
                handler(this, new PropertyChangedEventArgs(propertyName));
            }
        }

        public event PropertyChangedEventHandler PropertyChanged;

        public void ApplyJson(string json)
        {
            ApplyJson(JObject.Parse(json));
        }

        public void ApplyJson(JObject jsonObject)
        {
            JObject task = jsonObject["task"] as JObject;

            UserWrapper uw = UserWrapper.GetNonIndexedInstance(new User(-1));

            JObject jotask = jsonObject["task"] as JObject;
            JObject jouser = jsonObject["user"] as JObject;

            TaskWrapper tw = null;

            if (jotask != null)
            {
                tw = TaskWrapper.GetNonIndexedInstance(new Task(-1));
                tw.ApplyJson(jotask);
            }
            uw.ApplyJson(jouser);

            WrappedValue.Id = (string)jsonObject["_id"];
            WrappedValue.ProjectId = (int)jsonObject["project"];
            WrappedValue.SprintId = (int?)jsonObject["sprint"];
            WrappedValue.Task = tw == null ? null : tw.WrappedValue;
            WrappedValue.Description = (string)jsonObject["description"];
            WrappedValue.Date = (DateTime)jsonObject["date"];
            WrappedValue.User = uw.WrappedValue;
        }

        public string ToJson()
        {
            JsonSerializerSettings settings = new JsonSerializerSettings();
            settings.ContractResolver = new LowercaseContractResolver();
            string json = JsonConvert.SerializeObject(this, Formatting.Indented, settings);
            return json;
        }

        public UserWrapper User
        {
            get
            {
                return UserWrapper.GetNonIndexedInstance(WrappedValue.User);
            }
            set
            {
                WrappedValue.User = value.WrappedValue;
            }
        }

        [JsonProperty("_id")]
        public string Id
        {
            get
            {
                return WrappedValue.Id;
            }
            private set
            {

            }
        }

        public int Project
        {
            get
            {
                return WrappedValue.ProjectId;
            }
            private set
            {
                WrappedValue.ProjectId = value;
            }
        }

        public DateTime Date
        {
            get
            {
                return WrappedValue.Date;
            }
            set
            {
                WrappedValue.Date = value;
            }
        }
        public TaskWrapper Task
        {
            get
            {
                return WrappedValue.Task == null ? null : TaskWrapper.GetNonIndexedInstance(WrappedValue.Task);
            }
            set
            {
                WrappedValue.Task = value.WrappedValue;
                ScrumbleConnection.Update(this);
                OnPropertyChanged("Task");
            }
        }
        public string Description
        {
            get
            {
                return WrappedValue.Description;
            }
            set
            {
                WrappedValue.Description = value;
                ScrumbleConnection.Update(this);
                OnPropertyChanged("Text");
            }
        }

        public int? Sprint
        {
            get
            {
                return WrappedValue.SprintId;
            }
            private set
            {

                WrappedValue.SprintId = value;
            }
        }
    }
}
