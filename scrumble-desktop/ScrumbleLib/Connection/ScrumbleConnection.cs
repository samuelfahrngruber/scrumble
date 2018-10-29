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
        private static string getUrlForType(Type t)
        {
            if (t == typeof(Project)) return webserviceUrl + "scrumble/project/";
            if (t == typeof(Sprint)) return webserviceUrl + "scrumble/sprint/";
            if (t == typeof(Data.Task)) return webserviceUrl + "scrumble/task/";
            if (t == typeof(User)) return webserviceUrl + "scrumble/user/";
            //throw new Exception("invalid type for ScrumbleConnection.Update<T>(IDataWrapper<T>)");
            return null;
        }

        private static string getUrlForWrapper<T>(IDataWrapper<T> wrapper)
        {
            return getUrlForType(typeof(T)) + wrapper.Id;
        }

        public static void Update<T>(IDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapper(wrapper);
            string json = wrapper.ToJson();
            Scrumble.Log("PUT: " + url, "#00FFFF");
            Scrumble.Log(json, "#00FFFF");
        }

        public static void Add<T>(IDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapper(wrapper);
            string json = wrapper.ToJson();
            Scrumble.Log("POST: " + url, "#FFFF00");
            Scrumble.Log(json, "#FFFF00");
        }

        public static Project GetProject(Project project)
        {
            ProjectWrapper wrapper = ProjectWrapper.GetInstance(project);
            int projectId = wrapper.Id;
            //json = "{" +
            //    "\"id\":" + projectId + "," +
            //    "\"name\":\"SCRUMBLE" + projectId + "\"," +
            //    "\"currentsprint\":" + 666 + "," +
            //    "\"productowner\":" + projectId * 2 + "" +
            //    "}";
            string url = getUrlForWrapper(wrapper);
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + wrapper.Id);
            Scrumble.Log("GET - Project:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return project;
        }

        public static User GetUser(User user)
        {
            UserWrapper wrapper = UserWrapper.GetInstance(user);
            int userId = wrapper.Id;
            //string json = "{" +
            //    "\"id\":" + userId + "," +
            //    "\"username\":\"USER" + userId + "\"" +
            //    "}";
            string url = getUrlForWrapper(wrapper);
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + wrapper.Id);
            Scrumble.Log("GET - User:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return user;
        }

        public static Data.Task GetTask(Data.Task task)
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
            string url = getUrlForWrapper(wrapper);
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + wrapper.Id);
            Scrumble.Log("GET - Task:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return task;
        }

        public static Sprint GetSprint(Sprint sprint)
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
            string url = getUrlForWrapper(wrapper);
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + wrapper.Id);
            Scrumble.Log("GET - Sprint:", "#00FF00");
            Scrumble.Log(json, "#00FF00");
            wrapper.ApplyJson(json);
            return sprint;
        }
    }
}
