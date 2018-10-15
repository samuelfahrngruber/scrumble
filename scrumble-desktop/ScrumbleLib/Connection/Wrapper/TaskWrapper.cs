using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public class TaskWrapper : DataWrapper<Task>
    {
        public TaskWrapper(Task wrappedValue) : base(wrappedValue)
        {

        }
    }
}
