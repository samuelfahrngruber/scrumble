using Newtonsoft.Json.Linq;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ScrumbleLib.Connection.Wrapper
{
    public class TaskWrapper : DataWrapper<Task>
    {
        // Wrapping Methods and Constructors
        public static TaskWrapper Wrap(Task task)
        {
            return new TaskWrapper(task);
        }

        public TaskWrapper(Task wrappedValue) : base(wrappedValue)
        {

        }

        // Unwrapping Methods and Constructors
        public static TaskWrapper Unwrap(int id, string name, string info, int rejections, int responsibleUser, int verifyingUser, int sprint)
        {
            return new TaskWrapper(id, name, info, rejections, responsibleUser, verifyingUser, sprint);
        }

        public TaskWrapper(int id, string name, string info, int rejections, int responsibleUser, int verifyingUser, int sprint)
            : base(new Task(id, name, info, rejections, ScrumbleController.GetUser(responsibleUser), ScrumbleController.GetUser(verifyingUser), ScrumbleController.GetSprint(sprint)))
        {

        }

        public static TaskWrapper FromJson(JObject jsonObject)
        {
            return new TaskWrapper(jsonObject);
        }
        public static TaskWrapper FromJson(string json)
        {
            return FromJson(JObject.Parse(json));
        }

        public TaskWrapper(JObject jsonObject) : this(
            (int)jsonObject["id"],
            (string)jsonObject["name"],
            (string)jsonObject["info"],
            (int)jsonObject["rejections"],
            (int)jsonObject["responsibleuser"],
            (int)jsonObject["verifyinguser"],
            (int)jsonObject["sprint"])
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

        public string Info
        {
            get
            {
                return WrappedValue.Info;
            }
            set
            {
                WrappedValue.Info = value;
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
                ScrumbleConnection.Update(this);
            }
        }

        public int ResponsibleUser
        {
            get
            {
                return WrappedValue.ResponsibleUser == null ? -1 : WrappedValue.ResponsibleUser.Id;
            }
            set
            {
                WrappedValue.ResponsibleUser = ScrumbleController.GetUser(value);
                ScrumbleConnection.Update(this);
            }
        }

        public int VerifyingUser
        {
            get
            {
                return WrappedValue.VerifyingUser == null ? -1 : WrappedValue.VerifyingUser.Id;
            }
            set
            {
                WrappedValue.VerifyingUser = ScrumbleController.GetUser(value);
                ScrumbleConnection.Update(this);
            }
        }
    }
}
