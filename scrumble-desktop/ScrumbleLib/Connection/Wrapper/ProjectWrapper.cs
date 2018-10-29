﻿using Newtonsoft.Json;
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
    public class ProjectWrapper : IDataWrapper<Project>
    {
        public event PropertyChangedEventHandler PropertyChanged;

        public Project WrappedValue { get; private set; }

        private static IndexSet<ProjectWrapper> instances = new IndexSet<ProjectWrapper>();

        public static ProjectWrapper GetInstance(Project wrappedValue)
        {
            ProjectWrapper instance;
            if (instances.Contains(wrappedValue.Id))
            {
                instance = instances[wrappedValue.Id];
                instance.WrappedValue = wrappedValue;
                instance.OnPropertyChanged(null);
            }
            else
            {
                instance = new ProjectWrapper(wrappedValue);
                instances.Add(instance);
            }
            return instance;
        }

        private ProjectWrapper(Project project)
        {
            WrappedValue = project;
        }

        public static ProjectWrapper GetInstance(int projectId)
        {
            ProjectWrapper instance;
            if (instances.Contains(projectId))
            {
                instance = instances[projectId];
            }
            else
            {
                instance = new ProjectWrapper(projectId);
                instances.Add(instance);
            }
            return instance;
        }

        private ProjectWrapper(int id)
        {
            WrappedValue = ScrumbleController.GetProject(id).Result;
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

        public void ApplyFields(int id, string name, int productOwner, int currentSprint)
        {
            WrappedValue.Id = id;
            WrappedValue.Name = name;
            WrappedValue.ProductOwner = ScrumbleController.GetUser(productOwner).Result;
            WrappedValue.CurrentSprint = ScrumbleController.GetSprint(currentSprint).Result;
        }

        public void ApplyJson(JObject jsonObject)
        {
            ApplyFields(
                (int)jsonObject["id"],
                (string)jsonObject["project"],
                (int)jsonObject["productowner"],
                (int)jsonObject["currentsprint"]);
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

        public int ProductOwner
        {
            get
            {
                return WrappedValue.ProductOwner == null ? -1 : WrappedValue.ProductOwner.Id;
            }
            set
            {
                WrappedValue.ProductOwner = ScrumbleController.GetUser(value).Result;
                OnPropertyChanged("ProductOwner");
                ScrumbleConnection.Update(this);
            }
        }

        public int CurrentSprint
        {
            get
            {
                return WrappedValue.CurrentSprint == null ? -1 : WrappedValue.CurrentSprint.Id;
            }
            set
            {
                WrappedValue.CurrentSprint = ScrumbleController.GetSprint(value).Result;
                OnPropertyChanged("CurrentSprint");
                ScrumbleConnection.Update(this);
            }
        }

        // todo
        public HashSet<User> Team
        {
            get
            {
                return WrappedValue.Team;
            }
            set
            {
                WrappedValue.Team = value;
                ScrumbleConnection.Update(this);
            }
        }
    }
}