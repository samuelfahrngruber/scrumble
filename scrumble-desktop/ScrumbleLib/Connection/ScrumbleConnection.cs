using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    public static class ScrumbleConnection
    {
        public static void Update<WrappedType>(DataWrapper<WrappedType> wrapper)
        {
            string json = wrapper.ToJson();
        }

        public static ProjectWrapper GetProject(int projectId)
        {
            string json = "{" +
                "\"Id\":" + projectId + "," +
                "\"Name\":\"SCRUMBLE" + projectId + "\"" +
                "}";
            return ProjectWrapper.FromJson<ProjectWrapper>(json);
        }

        public static ProjectWrapper GetUser(int projectId)
        {
            string json = "{" +
                "\"Id\":" + projectId + "," +
                "\"Name\":\"SCRUMBLE" + projectId + "\"" +
                "}";
            return ProjectWrapper.FromJson<ProjectWrapper>(json);
        }
    }
}
