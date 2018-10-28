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
        public SprintWrapper(Sprint wrappedValue) : base(wrappedValue)
        {

        }

        public SprintWrapper(int id)
            : base(ScrumbleController.GetSprint(id).Result)
        {

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

        public override int Id
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
