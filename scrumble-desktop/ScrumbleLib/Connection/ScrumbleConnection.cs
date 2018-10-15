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
                "\"id\":" + projectId + "," +
                "\"name\":\"SCRUMBLE" + projectId + "\"" +
                "\"productowner\":" + projectId * 2 + "," +
                "}";
            return ProjectWrapper.FromJson(json);
        }

        public static UserWrapper GetUser(int userId)
        {
            string json = "{" +
                "\"id\":" + userId + "," +
                "\"name\":\"USER" + userId + "\"" +
                "}";
            return UserWrapper.FromJson(json);
        }
    }
}
