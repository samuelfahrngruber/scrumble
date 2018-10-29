using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    internal static class ScrumbleConnection
    {
        private const string webserviceUrl = "https://scrumble-api.herokuapp.com/";
        private static HttpClient client = new HttpClient();
        private static string getUrlForWrapper<T>(IDataWrapper<T> w)
        {
            if (typeof(T) == typeof(Project)) return "scrumble/project/";
            if (typeof(T) == typeof(Sprint)) return "scrumble/sprint/";
            if (typeof(T) == typeof(Data.Task)) return "scrumble/task/";
            if (typeof(T) == typeof(User)) return "scrumble/user/";
            //throw new Exception("invalid type for ScrumbleConnection.Update<T>(IDataWrapper<T>)");
            return null;
        }

        public static async void Update<T>(IDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapper(wrapper);
            string json = wrapper.ToJson();
            Scrumble.Log("PUT: " + url, "#00FFFF");
            Scrumble.Log(json, "#00FFFF");
        }

        public static async void Add<T>(IDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapper(wrapper);
            string json = wrapper.ToJson();
            Scrumble.Log("POST: " + url, "#FFFF00");
            Scrumble.Log(json, "#FFFF00");
        }

        public static async Task<Project> GetProject(Project project)
        {
            ProjectWrapper wrapper = ProjectWrapper.GetInstance(project);
            int projectId = wrapper.Id;
            //json = "{" +
            //    "\"id\":" + projectId + "," +
            //    "\"name\":\"SCRUMBLE" + projectId + "\"," +
            //    "\"currentsprint\":" + 666 + "," +
            //    "\"productowner\":" + projectId * 2 + "" +
            //    "}";
            string url = webserviceUrl + getUrlForWrapper(wrapper) + project.Id;
            HttpResponseMessage response = await client.GetAsync(url);
            if (response.StatusCode != System.Net.HttpStatusCode.OK)
                return null;
            string json = await response.Content.ReadAsStringAsync();
            Scrumble.Log("GET - Project:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return project;
        }

        public static async Task<User> GetUser(User user)
        {
            UserWrapper wrapper = UserWrapper.GetInstance(user);
            int userId = wrapper.Id;
            //string json = "{" +
            //    "\"id\":" + userId + "," +
            //    "\"username\":\"USER" + userId + "\"" +
            //    "}";
            string url = webserviceUrl + getUrlForWrapper(wrapper) + "/" + user.Id;
            HttpResponseMessage response = await client.GetAsync(url);
            if (response.StatusCode != System.Net.HttpStatusCode.OK)
                return null;
            string json = await response.Content.ReadAsStringAsync();

            Scrumble.Log("GET - User:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return user;
        }

        public static async Task<Data.Task> GetTask(Data.Task task)
        {
            TaskWrapper wrapper = TaskWrapper.GetInstance(task);
            int taskId = task.Id;
            //string json = "{" +
            //    "\"id\":" + taskId + "," +
            //    "\"name\":\"Task" + taskId + "\"," +
            //    "\"info\":\"TASKINFO" + taskId + taskId + taskId + taskId + "\"," +
            //    "\"rejections\":" + 2 + "," +
            //    "\"responsibleuser\":" + taskId % 3 + "," +
            //    "\"verifyinguser\":" + taskId * 100 + "," +
            //    "\"sprint\":" + taskId + "," +
            //    "\"state\":\"" + "INTEST" + "\"" +
            //    "}";
            string url = webserviceUrl + getUrlForWrapper(wrapper) + task.Id;

            //string json = client.GetStringAsync("https://scrumble-api.herokuapp.com/scrumble/task/18").Result;
            string json = "";
            HttpResponseMessage response = await client.GetAsync("https://scrumble-api.herokuapp.com/scrumble/task/18");
            if (response.IsSuccessStatusCode)
                json = await response.Content.ReadAsStringAsync();

            Scrumble.Log("GET - Task:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return task;
        }

        public static async Task<Sprint> GetSprint(Sprint sprint)
        {
            SprintWrapper wrapper = SprintWrapper.GetInstance(sprint);
            int sprintId = wrapper.Id;
            //string json = "{" +
            //    "\"id\":" + sprintId + "," +
            //    "\"project\":" + sprintId + "," +
            //    "\"number\":" + 0 + "," +
            //    "\"start\":\"" + new DateTime() + "\"," +
            //    "\"deadline\":\"" + new DateTime() + "\"" +
            //    "}";
            string url = webserviceUrl + getUrlForWrapper(wrapper) + "/" + sprint.Id;
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.StatusCode != System.Net.HttpStatusCode.OK)
                return null;
            string json = await response.Content.ReadAsStringAsync();

            Scrumble.Log("GET - Sprint:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return sprint;
        }
    }
}
