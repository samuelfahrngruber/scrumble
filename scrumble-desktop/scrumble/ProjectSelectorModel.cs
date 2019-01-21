using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace scrumble
{
    public class ProjectSelectorModel
    {
        private Project model;

        public ProjectSelectorModel(Project proj)
        {
            this.model = proj;
        }

        public string Name
        {
            get
            {
                return model.Name;
            }
        }
        
        public int Id
        {
            get
            {
                return model.Id;
            }
        }

        public string ProductOwnerName
        {
            get
            {
                return model.ProductOwner.Username;
            }
        }
        public int SprintNumber
        {
            get
            {
                return model.CurrentSprint.Number;
            }
        }
        public DateTime SprintDeadline
        {
            get
            {
                return model.CurrentSprint.Deadline;
            }
        }
    }
}
