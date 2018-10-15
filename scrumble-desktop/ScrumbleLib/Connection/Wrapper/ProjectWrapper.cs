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
        // wrapping methods and constructors
        public static ProjectWrapper Wrap(Project project)
        {
            return new ProjectWrapper(project);
        }

        public ProjectWrapper(Project wrappedValue) : base(wrappedValue)
        {

        }

        // Unwrapping Methods and Constructors
        public static ProjectWrapper Unwrap(int id, string name, int productOwner, int currentSprint)
        {
            return new ProjectWrapper(id, name, productOwner, currentSprint);
        }

        public static ProjectWrapper FromJson(JObject jsonObject)
        {
            return new ProjectWrapper(jsonObject);
        }
        public static ProjectWrapper FromJson(string json)
        {
            return FromJson(JObject.Parse(json));
        }

        public ProjectWrapper(int id, string name, int productOwner, int currentSprint)
            : base(new Project(id, name, ScrumbleController.GetUser(productOwner), ScrumbleController.GetSprint(currentSprint)))
        {

        }

        public ProjectWrapper(JObject jsonObject) : this(
            (int)jsonObject["id"],
            (string)jsonObject["project"],
            (int)jsonObject["productowner"],
            (int)jsonObject["currentsprint"])
        {

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
                //return WrappedValue.Project.Id;
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
                //return WrappedValue.Project.Id;
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
