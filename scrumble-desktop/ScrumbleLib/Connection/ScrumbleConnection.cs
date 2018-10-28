using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    internal static class ScrumbleConnection
    {
        // todo remove all console.writelines
        public static async void Update<WrappedType>(DataWrapper<WrappedType> wrapper)
        {
            string json = wrapper.ToJson();
            Scrumble.Log("PUT:", "#00FFFF");
            Scrumble.Log(json, "#00FFFF");
        }

        public static async void Update<WrappedType>(IDataWrapper<WrappedType> wrapper)
        {
            string json = wrapper.ToJson();
            Scrumble.Log("PUT:", "#00FFFF");
            Scrumble.Log(json, "#00FFFF");
        }

        public static async Task<Project> GetProject(Project project)
        {
            ProjectWrapper wrapper = new ProjectWrapper(project);
            int projectId = wrapper.Id;
            string json = "{" +
                "\"id\":" + projectId + "," +
                "\"name\":\"SCRUMBLE" + projectId + "\"," +
                "\"currentsprint\":" + 666 + "," +
                "\"productowner\":" + projectId * 2 + "" +
                "}";
            Scrumble.Log("GET - Project:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return project;
        }

        public static async Task<User> GetUser(User user)
        {
            UserWrapper wrapper = new UserWrapper(user);
            int userId = wrapper.Id;
            string json = "{" +
                "\"id\":" + userId + "," +
                "\"username\":\"USER" + userId + "\"" +
                "}";
            Scrumble.Log("GET - User:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return user;
        }

        public static async Task<Data.Task> GetTask(Data.Task task)
        {
            TaskWrapper wrapper = TaskWrapper.GetInstance(task);
            int taskId = task.Id;
            string json = "{" +
                "\"id\":" + taskId + "," +
                "\"name\":\"Task" + taskId + "\"," +
                "\"info\":\"TASKINFO" + taskId + taskId + taskId + taskId + "\"," +
                "\"rejections\":" + 2 + "," +
                "\"responsibleuser\":" + taskId % 3 + "," +
                "\"verifyinguser\":" + taskId * 100 + "," +
                "\"sprint\":" + taskId + "," +
                "\"state\":\"" + "INTEST" + "\"" +
                "}";
            Scrumble.Log("GET - Task:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return task;
        }

        public static async Task<Sprint> GetSprint(Sprint sprint)
        {
            SprintWrapper wrapper = new SprintWrapper(sprint);
            int sprintId = wrapper.Id;
            string json = "{" +
                "\"id\":" + sprintId + "," +
                "\"project\":" + sprintId + "," +
                "\"number\":" + 0 + "," +
                "\"start\":\"" + new DateTime() + "\"," +
                "\"deadline\":\"" + new DateTime() + "\"" +
                "}";
            Scrumble.Log("GET - Sprint:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return sprint;
        }
    }
}
