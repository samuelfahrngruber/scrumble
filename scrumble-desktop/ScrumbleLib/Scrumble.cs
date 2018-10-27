using ScrumbleLib.Connection;
using ScrumbleLib.Connection.Wrapper;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ScrumbleLib.Data;

namespace ScrumbleLib
{
    public static class Scrumble
    {
        public static ObservableCollection<TaskWrapper> MyTasks { get; private set; } = new ObservableCollection<TaskWrapper>();
        public static bool Login(string username, string password)
        {
            return ScrumbleController.Login(username, password);
        }

        public static void SetProject(int projectId)
        {
            ScrumbleController.SetCurrentProject(projectId);
        }

        internal static void OnProjectAdded(Project p)
        {
            // todo implement
        }

        internal static void OnUserAdded(User user)
        {
            // todo implement
        }

        internal static void OnTaskAdded(Data.Task task)
        {
            if(task.ResponsibleUser.Id == ScrumbleController.currentUser)
                MyTasks.Add(new TaskWrapper(task));
        }

        internal static void OnSprintAdded(Sprint sprint)
        {
            // todo implement
        }
    }
}
