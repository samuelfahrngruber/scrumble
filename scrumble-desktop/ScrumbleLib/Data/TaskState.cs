using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public enum TaskState
    {
        PRODUCT_BACKLOG,
        SPRINTBACKLOG,
        IN_PROGRESS,
        TO_VERIFY,
        DONE
    }

    public static class TaskStateParser
    {
        public static TaskState Parse(string str)
        {
            return (TaskState)Enum.Parse(typeof(TaskState), str);
        }
    }
}
