using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    public static class ScrumbleConnection
    {
        public static void Update<WrappedType>(DataWrapper<WrappedType> wrapper)
        {
            string json = wrapper.ToJson();
            ConsoleColor color = Console.ForegroundColor;
            Console.ForegroundColor = ConsoleColor.Cyan;
            Console.WriteLine("PUT:");
            Console.WriteLine(json);
            Console.ForegroundColor = color;
        }

        public static ProjectWrapper GetProject(int projectId)
        {
            string json = "{" +
                "\"id\":" + projectId + "," +
                "\"name\":\"SCRUMBLE" + projectId + "\"," +
                "\"currentsprint\":" + 666 + "," +
                "\"productowner\":" + projectId * 2 + "" +
                "}";
            ConsoleColor color = Console.ForegroundColor;
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("GET - Project:");
            Console.WriteLine(json);
            Console.ForegroundColor = color;
            return ProjectWrapper.FromJson(json);
        }

        public static UserWrapper GetUser(int userId)
        {
            string json = "{" +
                "\"id\":" + userId + "," +
                "\"username\":\"USER" + userId + "\"" +
                "}";
            ConsoleColor color = Console.ForegroundColor;
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("GET - User:");
            Console.WriteLine(json);
            Console.ForegroundColor = color;
            return UserWrapper.FromJson(json);
        }

        public static TaskWrapper GetTask(int taskId)
        {
            string json = "{" +
                "\"id\":" + taskId + "," +
                "\"name\":\"Task" + taskId + "\"," +
                "\"info\":\"TASKINFO" + taskId + taskId + taskId + taskId + "\"," +
                "\"rejections\":" + 2 + "," +
                "\"responsibleuser\":" + taskId * 10 + "," +
                "\"verifyinguser\":" + taskId * 100 + "," +
                "\"sprint\":" + taskId + "" +
                "}";
            ConsoleColor color = Console.ForegroundColor;
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("GET - Task:");
            Console.WriteLine(json);
            Console.ForegroundColor = color;
            return TaskWrapper.FromJson(json);
        }

        public static SprintWrapper GetSprint(int sprintId)
        {
            string json = "{" +
                "\"id\":" + sprintId + "," +
                "\"project\":" + sprintId + "," +
                "\"number\":" + 0 + "," +
                "\"start\":\"" + new DateTime() + "\"," +
                "\"deadline\":\"" + new DateTime() + "\"" +
                "}";
            ConsoleColor color = Console.ForegroundColor;
            Console.ForegroundColor = ConsoleColor.Green;
            Console.WriteLine("GET - Sprint:");
            Console.WriteLine(json);
            Console.ForegroundColor = color;
            return SprintWrapper.FromJson(json);
        }
    }
}
