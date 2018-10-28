using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Data
{
    public enum ScrumBoardColumn
    {
        SPRINTBACKLOG,
        INPROGRESS,
        INTEST,
        DONE
    }

    public static class ScrumBoardColumnParser
    {
        public static ScrumBoardColumn Parse(string str)
        {
            return (ScrumBoardColumn)Enum.Parse(typeof(ScrumBoardColumn), str);
        }
    }
}
