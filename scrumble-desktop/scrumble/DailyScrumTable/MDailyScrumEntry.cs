using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ScrumbleLib.Connection.Wrapper;

namespace scrumble.DailyScrumTable
{
    public class MDailyScrumEntry : IDailyScrumCell
    {
        public DailyScrumEntryWrapper WrappedDSEntry { get; private set; }
        public string TaskId {
            get
            {
                return WrappedDSEntry.Task == null || WrappedDSEntry.Task.WrappedValue == null ? "-" : "" + WrappedDSEntry.Task.Id;
            }
            set
            {
                //WrappedDSEntry.Task.Id = int.Parse(value);
            }
        }
        public string Text {
            get
            {
                return WrappedDSEntry.Description;
            }
            set
            {
                WrappedDSEntry.Description = value;
            }
        }

        private MDailyScrumEntry(DailyScrumEntryWrapper dsWrapper)
        {
            this.WrappedDSEntry = dsWrapper;
        }

        public static MDailyScrumEntry CreateFromDailyScrumWrapper(DailyScrumEntryWrapper wrapper)
        {
            return new MDailyScrumEntry(wrapper);
        }
    }
}
