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
        public TaskWrapper(Task wrappedValue) : base(wrappedValue)
        {

        }

        public TaskWrapper(int id)
            : base(ScrumbleController.GetTask(id))
        {

        }
        public void ApplyJson(string json)
        {
            ApplyJson(JObject.Parse(json));
        }

        public void ApplyJson(JObject jsonObject)
        {
            ApplyFields(
               (int)jsonObject["id"],
               (string)jsonObject["name"],
               (string)jsonObject["info"],
               (int)jsonObject["rejections"],
               (int)jsonObject["responsibleuser"],
               (int)jsonObject["verifyinguser"],
               (int)jsonObject["sprint"]);
        }

        public void ApplyFields(int id, string name, string info, int rejections, int responsibleUser, int verifyingUser, int sprint)
        {
            WrappedValue.Id = id;
            WrappedValue.Name = name;
            WrappedValue.Info = info;
            WrappedValue.Rejections = rejections;
            WrappedValue.ResponsibleUser = ScrumbleController.GetUser(responsibleUser);
            WrappedValue.VerifyingUser = ScrumbleController.GetUser(verifyingUser);
            WrappedValue.Sprint = ScrumbleController.GetSprint(sprint);
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
