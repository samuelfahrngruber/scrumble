using CefSharp.Wpf;
using ScrumbleLib.Connection.Wrapper;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace scrumble.Scrumboard
{
    public class ScrumboardInterface
    {
        private MainWindow csContext;
        public ScrumboardInterface(MainWindow csContext)
        {
            this.csContext = csContext;
        }

        public static ScrumboardInterface Create(MainWindow csContext)
        {
            return new ScrumboardInterface(csContext);
        }

        public void changeTaskState(int taskId, string taskState)
        {
            TaskWrapper.GetInstance(taskId).State = taskState;
        }
    }
}
