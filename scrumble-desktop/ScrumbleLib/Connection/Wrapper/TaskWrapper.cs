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

namespace ScrumbleLib.Connection.Wrapper
{
    public class TaskWrapper : IDataWrapper<Task>
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public Task WrappedValue  { get; private set; }

        private static IndexSet<TaskWrapper> instances = new IndexSet<TaskWrapper>();

        internal static TaskWrapper GetInstance(Task wrappedValue)
        {
            TaskWrapper instance;
            if (instances.Contains(wrappedValue.Id))
            {
                instance = instances[wrappedValue.Id];
                instance.WrappedValue = wrappedValue;
                instance.OnPropertyChanged(null);
            }
            else {
                ScrumbleController.Tasks.Add(wrappedValue);
                instance = new TaskWrapper(wrappedValue);
                instances.Add(instance);
            }
            return instance;
        }

        private TaskWrapper(Task wrappedValue)
        {
            WrappedValue = wrappedValue;
        }

        internal static TaskWrapper GetInstance(int taskId)
        {
            TaskWrapper instance;
            if (instances.Contains(taskId))
            {
                instance = instances[taskId];
            }
            else {
                instance = new TaskWrapper(taskId);
                instances.Add(instance);
            }
            return instance;
        } 

        private TaskWrapper(int id)
        {
            WrappedValue = ScrumbleController.GetTask(id);
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

        public void ApplyJson(string json)
        {
            ApplyJson(JObject.Parse(json));
        }

        public void ApplyJson(JObject jsonObject)
        {
            //ScrumBoardColumn state = (ScrumBoardColumn)Enum.Parse(typeof(ScrumBoardColumn), (string)jsonObject["state"]);
            ApplyFields(
               (int)jsonObject["id"],
               (string)jsonObject["name"],
               (string)jsonObject["info"],
               (int)jsonObject["rejections"],
               (int)jsonObject["responsible"], //responsibleUser
               (int)jsonObject["verify"], //verifyingUser
               (int?)jsonObject["sprint"],
               (int)jsonObject["project"],
               (string)jsonObject["state"],
               (int)jsonObject["position"]);
        }

        public void ApplyFields(int id, string name, string info, int rejections, int responsibleUser, int verifyingUser, int? sprint, int project, string state, int position)
        {
            WrappedValue.Id = id;
            WrappedValue.Name = name;
            WrappedValue.Info = info;
            WrappedValue.Rejections = rejections;
            WrappedValue.ResponsibleUser = ScrumbleController.GetUser(responsibleUser);
            WrappedValue.VerifyingUser = ScrumbleController.GetUser(verifyingUser);
            WrappedValue.Sprint = sprint == null ? null : ScrumbleController.GetSprint((int)sprint);
            WrappedValue.Project = ScrumbleController.GetProject(project);
            WrappedValue.State = TaskStateParser.Parse(state);
            WrappedValue.Position = position;
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

        public string Name
        {
            get
            {
                return WrappedValue.Name;
            }
            set
            {
                WrappedValue.Name = value;
                OnPropertyChanged("Name");
                ScrumbleConnection.Update(this);
            }
        }

        public string Info
        {
            get
            {
                return WrappedValue.Info;
            }
            set
            {
                WrappedValue.Info = value;
                OnPropertyChanged("Info");
                ScrumbleConnection.Update(this);
            }
        }


        public int Rejections
        {
            get
            {
                return WrappedValue.Rejections;
            }
            set
            {
                WrappedValue.Rejections = value;
                OnPropertyChanged("Rejections");
                ScrumbleConnection.Update(this);
            }
        }

        [JsonProperty("responsible")]
        public int ResponsibleUser
        {
            get
            {
                return WrappedValue.ResponsibleUser == null ? -1 : WrappedValue.ResponsibleUser.Id;
            }
            set
            {
                WrappedValue.ResponsibleUser = ScrumbleController.GetUser(value);
                OnPropertyChanged("ResponsibleUser");
                ScrumbleConnection.Update(this);
            }
        }

        [JsonProperty("verify")]
        public int VerifyingUser
        {
            get
            {
                return WrappedValue.VerifyingUser == null ? -1 : WrappedValue.VerifyingUser.Id;
            }
            set
            {
                WrappedValue.VerifyingUser = ScrumbleController.GetUser(value);
                OnPropertyChanged("VerifyingUser");
                ScrumbleConnection.Update(this);
            }
        }

        public int Sprint
        {
            get
            {
                return WrappedValue.Sprint == null ? -1 : WrappedValue.Sprint.Id;
            }
            set
            {
                WrappedValue.Sprint = ScrumbleController.GetSprint(value);
                OnPropertyChanged("Sprint");
                ScrumbleConnection.Update(this);
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
                WrappedValue.Project = ScrumbleController.GetProject(value);
                OnPropertyChanged("Project");
                ScrumbleConnection.Update(this);
            }
        }

        public string State
        {
            get
            {
                return WrappedValue.State.ToString();
            }
            set
            {
                WrappedValue.State = TaskStateParser.Parse(value);
                OnPropertyChanged("State");
                ScrumbleConnection.Update(this);
            }
        }

        public int Position
        {
            get
            {
                return WrappedValue.Position;
            }
            set
            {
                WrappedValue.Position = value;
                OnPropertyChanged("Position");
                ScrumbleConnection.Update(this);
            }
        }

        public override string ToString()
        {
            return WrappedValue.ToString();
        }
    }
}
