using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace scrumble.DailyScrumTable
{
    public class TableColumn
    {
        public string Header { get; set; }
        public List<IDailyScrumCell> Entries { get; private set; }

        public TableColumn()
        {
            Entries = new List<IDailyScrumCell>();
        }
    }
}
