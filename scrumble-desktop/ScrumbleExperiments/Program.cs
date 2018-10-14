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
            Project scrumble = new Project(22, "scrumble");
            ProjectWrapper scrumbleW = new ProjectWrapper(scrumble);
            Sprint s = new Sprint(0, scrumble);
            SprintWrapper sw = new SprintWrapper(s);
            string json = sw.ToJson();

            Console.WriteLine(json);

            Console.WriteLine();

            SprintWrapper newSW = DataWrapper<Sprint>.FromJson<SprintWrapper>(json);
            Console.WriteLine(newSW.ToJson());
        }
    }
}
