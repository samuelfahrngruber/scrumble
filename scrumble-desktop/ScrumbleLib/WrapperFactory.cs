using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace ScrumbleLib
{
    public class WrapperFactory
    {
        internal WrapperFactory()
        {

        }

        public TaskWrapper CreateTaskWrapper(int id)
        {
            return TaskWrapper.GetInstance(id);
        }

        public TaskWrapper CreateTaskWrapper(Task value)
        {
            return TaskWrapper.GetInstance(value);
        }

        public UserWrapper CreateUserWrapper(int id)
        {
            return UserWrapper.GetInstance(id);
        }

        public UserWrapper CreateUserWrapper(User value)
        {
            return UserWrapper.GetInstance(value);
        }

        public ProjectWrapper CreateProjectWrapper(int id)
        {
            return ProjectWrapper.GetInstance(id);
        }

        public ProjectWrapper CreateProjectWrapper(Project value)
        {
            return ProjectWrapper.GetInstance(value);
        }

        public SprintWrapper CreateSprintWrapper(int id)
        {
            return SprintWrapper.GetInstance(id);
        }

        public SprintWrapper CreateSprintWrapper(Sprint value)
        {
            return SprintWrapper.GetInstance(value);
        }
    }
}
