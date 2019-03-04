using Newtonsoft.Json.Linq;
using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Change
{
    class ScrumbleChange
    {
        public DateTime Timestamp { get; set; }
        public int Project { get; set; }
        public string Object { get; set; }
        public string Method { get; set; }
        public JObject Change { get; set; }

        public ScrumbleChange()
        {
            
        }

        public void ApplyJson(string json)
        {
            ApplyJson(JObject.Parse(json));
        }

        public void ApplyJson(JObject jo)
        {
            this.Timestamp = (DateTime)jo["timestamp"];
            this.Project = (int)jo["project"];
            this.Object = (string)jo["object"];
            this.Method = (string)jo["method"];
            this.Change = (JObject)jo["change"];
        }

        public static ScrumbleChange FromJson(string json)
        {
            return FromJson(JObject.Parse(json));
        }

        public static ScrumbleChange FromJson(JObject json)
        {
            ScrumbleChange c = new ScrumbleChange();
            c.ApplyJson(json);
            return c;
        }

        public void Apply()
        {
            switch (Method)
            {
                case "PUT":
                    ApplyPut();
                    break;
                case "POST":
                    ApplyPost();
                    break;
                case "DELETE":
                    ApplyDelete();
                    break;
            }
        }
        
        private void ApplyPost()
        {
            // maybe improve lol
            ApplyPut();
        }

        private void ApplyPut()
        {
            switch (Object)
            {
                case "TASK":
                    TaskWrapper tw = TaskWrapper.GetInstance((int)Change["id"]);
                    tw.ApplyJson(Change);
                    break;
                case "PROJECT":
                    ProjectWrapper pw = ProjectWrapper.GetInstance((int)Change["id"]);
                    pw.ApplyJson(Change);
                    break;
                case "SPRINT":
                    SprintWrapper sw = SprintWrapper.GetInstance((int)Change["id"]);
                    sw.ApplyJson(Change);
                    break;
                case "DAILYSCRUM":
                    //DailyScrumEntryWrapper dsew = DailyScrumEntryWrapper.GetInstance(new DailyScrumEntry());
                    //dsew.ApplyJson(Change);
                    //Scrumble.DailyScrumEntries.
                    break;
            }
        }

        private void ApplyDelete()
        {
            switch (Object)
            {
                case "TASK":                  
                    Scrumble.DeleteTask((int)Change["id"]);
                    break;
                case "USER":
                    Scrumble.OnMemberRemoved((int)Change["id"]);
                    break;
            }
        }
    }
}
