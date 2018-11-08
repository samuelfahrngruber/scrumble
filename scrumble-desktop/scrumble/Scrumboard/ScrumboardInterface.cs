using CefSharp.Wpf;
using ScrumbleLib;
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

        public void changeTaskState(int taskId, string taskState, int position)
        {
            try
            {
                Scrumble.WrapperFactory.CreateTaskWrapper(taskId).WrappedValue.Position = position;
                Scrumble.WrapperFactory.CreateTaskWrapper(taskId).State = taskState;
            }
            catch(Exception ex)
            {
                Scrumble.Log(ex.Message + "\n" + ex.StackTrace, "#FF0000");
            }
        }
    }
}
