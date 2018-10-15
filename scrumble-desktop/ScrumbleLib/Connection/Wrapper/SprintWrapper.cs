using Newtonsoft.Json.Linq;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class SprintWrapper : DataWrapper<Sprint>
    {
        // Wrapping Methods and Constructors
        public static SprintWrapper Wrap(Sprint sprint)
        {
            return new SprintWrapper(sprint);
        }

        public SprintWrapper(Sprint wrappedValue) : base(wrappedValue)
        {

        }

        // Unwrapping Methods and Constructors
        public static SprintWrapper Unwrap(int id, int project, int number, DateTime start, DateTime deadline)
        {
            return new SprintWrapper(id, project, number, start, deadline);
        }

        public SprintWrapper(int id, int project, int number, DateTime start, DateTime deadline)
            : base(new Sprint(id, ScrumbleController.GetProject(project), number, start, deadline))
        {

        }

        public static SprintWrapper FromJson(JObject jsonObject)
        {
            return new Wrapper.SprintWrapper(jsonObject);
        }
        public static SprintWrapper FromJson(string json)
        {
            return FromJson(JObject.Parse(json));
        }

        public SprintWrapper(JObject jsonObject) : this(
            (int)jsonObject["id"],
            (int)jsonObject["project"],
            (int)jsonObject["number"],
            DateTime.Parse((string)jsonObject["start"]),
            DateTime.Parse((string)jsonObject["deadline"]))
        {

        }

        public int Id
        {
            get
            {
                return WrappedValue.Id;
            }
            private set
            {
                throw new Exception("Use WrappedValue.Id.set instead!");
            }
        }

        public int Project
        {
            get
            {
                return WrappedValue.Project == null ? -1 : WrappedValue.Project.Id;
                //return WrappedValue.Project.Id;
            }
            set
            {
                WrappedValue.Project = ScrumbleController.GetProject(value);
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
                ScrumbleConnection.Update(this);
            }
        }
    }
}
