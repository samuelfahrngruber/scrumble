using ScrumbleLib.Connection;
using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleExperiments
{
    class Program
    {
        static void Main(string[] args)
        {
            string input = " ";
            int id;
            while (input != null && input != "")
            {
                Console.WriteLine(@"Create new wrapper for:
task,
project,
sprint,
user

");
                Console.Write("Your choice: ");
                input = Console.ReadLine();
                Console.Write("ID of " + input + ": ");
                id = int.Parse(Console.ReadLine());
                switch (input)
                {
                    case "project":
                        ProjectWrapper pw = new ProjectWrapper(id);
                        break;
                    case "sprint":
                        SprintWrapper sw = new SprintWrapper(id);
                        break;
                }
            }
        }
    }
}
