using ScrumbleLib;
using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace scrumble.DailyScrumTable
{
    public class DailyScrumModel
    {
        private IDictionary<Tuple<string, DateTime>, MDailyScrumEntry> model;
        public ISet<string> Users { get; private set; }
        public ISet<DateTime> Dates { get; private set; }

        public static DailyScrumModel CreateFromDSEntries(IEnumerable<MDailyScrumEntry> mdsEntries)
        {
            DailyScrumModel model = new DailyScrumModel();
            foreach(MDailyScrumEntry e in mdsEntries)
            {
                model.Set(e.WrappedDSEntry.User.Username, e.WrappedDSEntry.Date, e);
            }
            return model;
        }

        public static DailyScrumModel CreateFromDSEntries(IEnumerable<DailyScrumEntryWrapper> mdsEntries)
        {
            return CreateFromDSEntries(mdsEntries.Select(w => MDailyScrumEntry.CreateFromDailyScrumWrapper(w)));
        }

        public DailyScrumModel()
        {
            model = new Dictionary<Tuple<string, DateTime>, MDailyScrumEntry>();
            Users = new HashSet<string>();
            Dates = new HashSet<DateTime>();
        }

        public MDailyScrumEntry Get(string user, DateTime day)
        {
            day = day.Date;
            Tuple<string, DateTime> key = new Tuple<string, DateTime>(user, day);
            return model.ContainsKey(key) ? model[key] : null;
        }

        public void Set(string user, DateTime day, MDailyScrumEntry entry)
        {
            if (!Users.Contains(user))
                Users.Add(user);
            day = day.Date;
            Dates.Add(day);
            model[Tuple.Create(user, day)] = entry;
        }
    }
}
