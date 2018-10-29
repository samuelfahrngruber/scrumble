using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ScrumbleLib.Connection.Json;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class SprintWrapper : IDataWrapper<Sprint>
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public Sprint WrappedValue { get; private set; }

        private static IndexSet<SprintWrapper> instances = new IndexSet<SprintWrapper>();

        public static SprintWrapper GetInstance(Sprint wrappedValue)
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
                instance = new SprintWrapper(wrappedValue);
                instances.Add(instance);
            }
            return instance;
        }

        private SprintWrapper(Sprint wrappedValue)
        {
            this.WrappedValue = wrappedValue;
        }

        public static SprintWrapper GetInstance(int sprintId)
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
            this.WrappedValue = ScrumbleController.GetSprint(id).Result;
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

        public async void ApplyFields(int id, int project, int number, DateTime start, DateTime deadline)
        {
            WrappedValue.Id = id;
            WrappedValue.Project = await ScrumbleController.GetProject(project);
            WrappedValue.Number = number;
            WrappedValue.Start = start;
            WrappedValue.Deadline = deadline;
        }

        public void ApplyJson(JObject jsonObject)
        {
            ApplyFields(
                (int)jsonObject["id"],
                (int)jsonObject["project"],
                (int)jsonObject["number"],
                DateTime.Parse((string)jsonObject["start"]),
                DateTime.Parse((string)jsonObject["deadline"]));
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

        public int Project
        {
            get
            {
                return WrappedValue.Project == null ? -1 : WrappedValue.Project.Id;
            }
            set
            {
                WrappedValue.Project = ScrumbleController.GetProject(value).Result;
                OnPropertyChanged("Project");
                ScrumbleConnection.Update(this);
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
    }
}
