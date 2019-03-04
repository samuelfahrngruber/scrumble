using Newtonsoft.Json.Linq;
using ScrumbleLib.Connection.Change;
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
        //private const string webserviceUrl = "http://192.168.193.59:8080/";
        private const string webserviceUrl = "https://scrumble-api.herokuapp.com/";
        private static HttpClient client = new HttpClient();
        private static string getUrlForType(Type t)
        {
            if (t == typeof(Project)) return webserviceUrl + "scrumble/project/";
            if (t == typeof(Sprint)) return webserviceUrl + "scrumble/sprint/";
            if (t == typeof(Data.Task)) return webserviceUrl + "scrumble/task/";
            if (t == typeof(User)) return webserviceUrl + "scrumble/user/";
            if (t == typeof(DailyScrumEntry)) return webserviceUrl + "scrumble/dailyscrum/";
            //throw new Exception("invalid type for ScrumbleConnection.Update<T>(IDataWrapper<T>)");
            return null;
        }

        private static string getUrlForWrapper<T>(IIndexableDataWrapper<T> wrapper)
        {
            return getUrlForType(typeof(T)) + wrapper.Id;
        }

        private static string getUrlForWrapperType<T>(IDataWrapper<T> wrapper)
        {
            return getUrlForType(typeof(T));
        }

        public static int Login(string username, string password)
        {
            string url = webserviceUrl + "scrumble/login";

            int result = -2;

            JObject jsono = new JObject();
            jsono.Add("name", username);
            jsono.Add("password", password);
            string json = jsono.ToString();

            HttpContent content = new StringContent(json, Encoding.UTF8, "application/json");

            Scrumble.Log("POST: " + url, "#FFFF00");
            Scrumble.Log(json, "#FFFF00");

            HttpResponseMessage response = client.PostAsync(url, content).Result;

            if (response.IsSuccessStatusCode)
            {
                json = response.Content.ReadAsStringAsync().Result;
                jsono = JObject.Parse(json);
                result = (int)jsono.GetValue("id");
            }
            else
                result = -1;

            Scrumble.Log("Result:", "#FFFF00");
            Scrumble.Log(json, "#FFFF00");

            return result;
        }

        public static IIndexableDataWrapper<T> Update<T>(IIndexableDataWrapper<T> wrapper)
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



        public static DailyScrumEntryWrapper Update(DailyScrumEntryWrapper wrapper)
        {
            string url = getUrlForType(typeof(DailyScrumEntry)) + wrapper.Id;
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

        public static IIndexableDataWrapper<T> Add<T>(IIndexableDataWrapper<T> wrapper)
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

        public static IIndexableDataWrapper<T> Delete<T>(IIndexableDataWrapper<T> wrapper)
        {
            string url = getUrlForWrapper(wrapper);

            Scrumble.Log("DELETE: " + url, "#fc622a");

            HttpResponseMessage response = client.DeleteAsync(url).Result;

            if (!response.IsSuccessStatusCode)
                throw new Exception("invalid DELETE at " + url + "for id " + wrapper.Id);

            Scrumble.Log("Successfully deleted", "#fc622a");
            return wrapper;
        }

        internal static ProjectWrapper GetDailyScrumEntries(ProjectWrapper pw)
        {
            List<DailyScrumEntry> scrumboard = new List<DailyScrumEntry>();

            string url = getUrlForWrapper(pw) + "/dailyscrum?sprint=" + pw.CurrentSprint;
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + pw.Id);
            Scrumble.Log("GET - Project-DailyScrumEntries:", "#00FF00");
            Scrumble.Log(json, "#00FF00");

            Scrumble.DailyScrumEntriesFromJson(json);

            return pw;
        }

        internal static ProjectWrapper GetChanges(ProjectWrapper pw, DateTime timestamp)
        {
            List<ScrumbleChange> scrumboard = new List<ScrumbleChange>();

            string url = getUrlForWrapper(pw) + "/changes?timestamp=" + timestamp.ToUniversalTime().ToString("O") ;
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + pw.Id);
            Scrumble.Log("GET - Changes:", "#00FF00");
            Scrumble.Log(json, "#00FF00");


            if(json != null && json != "")
                Scrumble.ChangesFromJson(json);

            return pw;
        }

        public static IIndexableDataWrapper<T> Get<T>(IIndexableDataWrapper<T> wrapper)
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

        public static UserWrapper GetUsersProjects(UserWrapper user)
        {
            List<Project> projects = new List<Project>();

            string url = getUrlForWrapper(user) + "/project";
            string json = "";
            HttpResponseMessage response = client.GetAsync(url).Result;
            if (response.IsSuccessStatusCode)
                json = response.Content.ReadAsStringAsync().Result;
            else
                throw new Exception("invalid get at " + url + "for id " + user.Id);
            Scrumble.Log("GET - User's Projects:", "#00FF00");
            Scrumble.Log(json, "#00FF00");

            Scrumble.ProjectsFromJson(json);

            return user;
        }

    }
}
