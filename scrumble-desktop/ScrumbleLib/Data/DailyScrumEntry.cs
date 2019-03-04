using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class DailyScrumEntry
    {
        public string Id;
        public User User;
        public DateTime Date;
        public Task Task;
        public string Description;
        public int ProjectId;
        public int? SprintId;

        public DailyScrumEntry(string id = default(string), User user = default(User), DateTime date = default(DateTime), Task task = default(Task), string text = default(string), int projectid = default(int), int? sprintid = null)
        {
            Id = id;
            Date = date;
            Task = task;
            Description = text;
            User = user;
            ProjectId = projectid;
            SprintId = sprintid;
        }
    }
}
