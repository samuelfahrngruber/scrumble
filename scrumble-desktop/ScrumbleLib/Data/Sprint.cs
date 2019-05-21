using ScrumbleLib.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public class Sprint : IIndexable
    {
        public int Id { get; set; }
        public int Number { get; set; }
        public DateTime Start { get; set; }
        public DateTime Deadline { get; set; }
        public Project Project { get; set; }

        public Sprint(int id, Project project = default(Project), int number = -1, DateTime start = default(DateTime), DateTime deadline = default(DateTime))
        {
            this.Id = id;
            this.Project = project;
            this.Number = number;
            this.Start = start;
            this.Deadline = deadline;
        }

        public override string ToString()
        {
            return "Sprint #" + Number + " (" + Start.ToShortDateString() + " - " + Deadline.ToShortDateString() + ")";
        }
    }
}
