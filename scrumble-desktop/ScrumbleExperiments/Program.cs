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
            ////Project scrumble = new Project(22, "scrumble");
            ////ProjectWrapper scrumbleW = new ProjectWrapper(scrumble);
            ////Sprint s = new Sprint(0, scrumble);
            ////SprintWrapper sw = new SprintWrapper(s);
            ////string json = sw.ToJson();

            ////Console.WriteLine(json);

            ////sw.Project = 100;

            ////Console.WriteLine();

            ////SprintWrapper newSW = SprintWrapper.FromJson(json);
            ////Console.WriteLine(newSW.ToJson());

            TaskWrapper tw = ScrumbleConnection.GetTask(3);

        }
    }
}
