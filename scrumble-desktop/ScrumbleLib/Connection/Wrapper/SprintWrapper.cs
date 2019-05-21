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
    public class SprintWrapper : IIndexableDataWrapper<Sprint>
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public Sprint WrappedValue { get; private set; }

        private static IndexSet<SprintWrapper> instances = new IndexSet<SprintWrapper>();

        internal static SprintWrapper GetInstance(Sprint wrappedValue)
        {
            SprintWrapper instance;
            if (instances.Contains(wrappedValue.Id))
            {
                instance = instances[wrappedValue.Id];
                instance.WrappedValue = wrappedValue;
                instance.OnPropertyChanged(null);
            }
            else
            {
                ScrumbleController.Sprints.Add(wrappedValue);
                instance = new SprintWrapper(wrappedValue);
                instances.Add(instance);
            }
            return instance;
        }

        private SprintWrapper(Sprint wrappedValue)
        {
            this.WrappedValue = wrappedValue;
        }

        internal static SprintWrapper GetInstance(int sprintId)
        {
            SprintWrapper instance;
            if (instances.Contains(sprintId))
            {
                instance = instances[sprintId];
            }
            else
            {
                instance = new SprintWrapper(sprintId);
                instances.Add(instance);
            }
            return instance;
        }

        private SprintWrapper(int id)
        {
            this.WrappedValue = ScrumbleController.GetSprint(id);
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

        //public void ApplyFields(int id, int project, int number, DateTime start, DateTime deadline)
        //{
        //    WrappedValue.Id = id;
        //    WrappedValue.Project = ScrumbleController.GetProject(project);
        //    WrappedValue.Number = number;
        //    WrappedValue.Start = start;
        //    WrappedValue.Deadline = deadline;
        //}

        public void ApplyJson(JObject jsonObject)
        {
            if (jsonObject.ContainsKey("id")) WrappedValue.Id = (int)jsonObject["id"];
            if (jsonObject.ContainsKey("project")) WrappedValue.Project = ScrumbleController.GetProject((int)jsonObject["project"]);
            if (jsonObject.ContainsKey("number")) WrappedValue.Number = (int)jsonObject["number"];
            if (jsonObject.ContainsKey("startdate")) WrappedValue.Start = (DateTime)jsonObject["startdate"];
            if (jsonObject.ContainsKey("deadline")) WrappedValue.Deadline = (DateTime)jsonObject["deadline"];
            //ApplyFields(
            //    (int)jsonObject["id"],
            //    (int)jsonObject["project"],
            //    (int)jsonObject["number"],
            //    DateTime.Parse((string)jsonObject["startdate"]),
            //    DateTime.Parse((string)jsonObject["deadline"]));
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
                throw new Exception("Use WrappedValue.Id.set instead!");
            }
        }

        public int? Project
        {
            get
            {
                return WrappedValue.Project == null ? null : (int?)WrappedValue.Project.Id;
            }
            set
            {
                WrappedValue.Project = value == null ? null : ScrumbleController.GetProject((int)value);
                OnPropertyChanged("Project");
                ScrumbleConnection.Update(this);
            }
        }

        [JsonIgnore]
        public bool IsCurrentSprint
        {
            get
            {
                return WrappedValue.Project != null && Scrumble.currentProject.CurrentSprint != null 
                    ? Scrumble.currentProject.CurrentSprint == Id
                    : false;
            }
        }

        public int Number
        {
            get
            {
                return WrappedValue.Number;
            }
            set
            {
                WrappedValue.Number = value;
                OnPropertyChanged("Number");
                ScrumbleConnection.Update(this);
            }
        }

        [JsonProperty("startdate")]
        public DateTime Start
        {
            get
            {
                return WrappedValue.Start;
            }
            set
            {
                WrappedValue.Start = value;
                OnPropertyChanged("Start");
                ScrumbleConnection.Update(this);
            }
        }

        public DateTime Deadline
        {
            get
            {
                return WrappedValue.Deadline;
            }
            set
            {
                WrappedValue.Deadline = value;
                OnPropertyChanged("Deadline");
                ScrumbleConnection.Update(this);
            }
        }

        public override string ToString()
        {
            return WrappedValue.ToString();
        }
    }
}
