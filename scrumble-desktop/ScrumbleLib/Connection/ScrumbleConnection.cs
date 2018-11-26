using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Net.Http;
using System.Net.Http.Headers;
using System.Text;
using System.Threading.Tasks;
using System.Web.Script.Serialization;

namespace ScrumbleLib.Connection
{
    internal static class ScrumbleConnection
    {
        //private const string webserviceUrl = "http://ssmagic:8080/";
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

        private static string getUrlForWrapperType<T>(IDataWrapper<T> wrapper)
        {
            return getUrlForType(typeof(T));
        }

        public static IDataWrapper<T> Update<T>(IDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapper(wrapper);
            string json = wrapper.ToJson();

            HttpContent content = new StringContent(json, Encoding.UTF8, "application/json");

            Scrumble.Log("PUT: " + url, "#00FFFF");
            Scrumble.Log(json, "#00FFFF");

            HttpResponseMessage response = client.PutAsync(url, content).Result;

            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid PUT at " + url + "for id " + wrapper.Id);

            Scrumble.Log("Result:", "#00FFFF");
            Scrumble.Log(json, "#00FFFF");

            //wrapper.ApplyJson(json);
            return wrapper;
        }

        public static IDataWrapper<T> Add<T>(IDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapperType(wrapper);
            string json = wrapper.ToJson();

            HttpContent content = new StringContent(json, Encoding.UTF8, "application/json");

            Scrumble.Log("POST: " + url, "#FFFF00");
            Scrumble.Log(json, "#FFFF00");

            HttpResponseMessage response = client.PostAsync(url, content).Result;

            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid POST at " + url + "for id " + wrapper.Id);

            Scrumble.Log("Result:", "#FFFF00");
            Scrumble.Log(json, "#FFFF00");

            wrapper.ApplyJson(json);
            return wrapper;
        }

        public static IDataWrapper<T> Delete<T>(IDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapper(wrapper);

            Scrumble.Log("DELETE: " + url, "#fc622a");

            HttpResponseMessage response = client.DeleteAsync(url).Result;

            if (!response.IsSuccessStatusCode)
                throw new Exception("invalid DELETE at " + url + "for id " + wrapper.Id);

            Scrumble.Log("Successfully deleted", "#fc622a");
            return wrapper;
        }

        public static IDataWrapper<T> Get<T>(IDataWrapper<T> wrapper)
        {
            int id = wrapper.Id;
            string url = getUrlForWrapper(wrapper);
            string json = "";

            Scrumble.Log("GET - " + wrapper.WrappedValue.GetType().Name + ":", "#00FF00");

            HttpResponseMessage response = client.GetAsync(url).Result;

            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid GET at " + url + "for id " + wrapper.Id);

            Scrumble.Log("Result:", "#00FF00");
            Scrumble.Log(json, "#00FF00");

            wrapper.ApplyJson(json);
            return wrapper;
        }

        public static ProjectWrapper GetTeam(ProjectWrapper wrapper)
        {
            int projectId = wrapper.Id;

            string url = getUrlForWrapper(wrapper) + "/user";
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + wrapper.Id);
            Scrumble.Log("GET - Project-Team:", "#00FF00");
            Scrumble.Log(json, "#00FF00");

            wrapper.ApplyTeamJson(json);

            return wrapper;
        }

        public static ProjectWrapper GetProjectTasks(ProjectWrapper wrapper)
        {
            List<Data.Task> scrumboard = new List<Data.Task>();

            string url = getUrlForWrapper(wrapper) + "/task";
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + wrapper.Id);
            Scrumble.Log("GET - Project-Tasks:", "#00FF00");
            Scrumble.Log(json, "#00FF00");

            Scrumble.TasksFromJson(json);

            return wrapper;
        }
    }
}
