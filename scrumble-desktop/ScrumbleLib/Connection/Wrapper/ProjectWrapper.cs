using Newtonsoft.Json.Linq;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class ProjectWrapper : DataWrapper<Project>
    {
        public static ProjectWrapper Wrap(Project project)
        {
            return new ProjectWrapper(project);
        }

        public ProjectWrapper(Project wrappedValue) : base(wrappedValue)
        {

        }

        // Unwrapping Methods and Constructors
        public static ProjectWrapper Unwrap(int id, string name, int productOwner)
        {
            return new ProjectWrapper(id, name, productOwner);
        }

        public static ProjectWrapper FromJson(JObject jsonObject)
        {
            return new ProjectWrapper(jsonObject);
        }
        public static ProjectWrapper FromJson(string json)
        {
            return FromJson(JObject.Parse(json));
        }

        public ProjectWrapper(int id, string name, int productOwner)
            : base(new Project(id, name, ScrumbleController.GetUser(productOwner)))
        {

        }

        public ProjectWrapper(JObject jsonObject) : this(
            (int)jsonObject["id"],
            (string)jsonObject["project"],
            (int)jsonObject["productowner"])
        {

        }

        public int Id { get; set; }
        public string Name { get; set; }
        public User ProductOwner { get; set; }
        public HashSet<User> Team { get; set; }
    }
}
