using Newtonsoft.Json.Linq;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class ProjectWrapper : DataWrapper<Project>, IIndexable
    {
        public ProjectWrapper(Project project)
            : base(project)
        {

        }

        public ProjectWrapper(int id)
            : base(ScrumbleController.GetProject(id))
        {

        }

        public void ApplyFields(int id, string name, int productOwner, int currentSprint)
        {
            WrappedValue.Id = id;
            WrappedValue.Name = name;
            WrappedValue.ProductOwner = ScrumbleController.GetUser(productOwner);
            WrappedValue.CurrentSprint = ScrumbleController.GetSprint(currentSprint);
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
            set
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
                WrappedValue.ProductOwner = ScrumbleController.GetUser(value);
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
                WrappedValue.CurrentSprint = ScrumbleController.GetSprint(value);
                ScrumbleConnection.Update(this);
            }
        }

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
