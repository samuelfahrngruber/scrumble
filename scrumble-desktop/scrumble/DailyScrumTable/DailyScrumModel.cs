using ScrumbleLib;
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
        private IDictionary<Tuple<User, DateTime>, DailyScrumEntry> model;
        public ISet<User> Users { get; private set; }
        public ISet<DateTime> Dates { get; private set; }
        
        public DailyScrumModel()
        {
            model = new Dictionary<Tuple<User, DateTime>, DailyScrumEntry>();
            Users = new HashSet<User>();
            Dates = new HashSet<DateTime>();
        }

        public DailyScrumEntry Get(User user, DateTime day)
        {
            day = day.Date;
            Tuple<User, DateTime> key = new Tuple<User, DateTime>(user, day);
            return model.ContainsKey(key) ? model[key] : new DailyScrumEntry();
        }

        public void Set(User user, DateTime day, DailyScrumEntry entry)
        {
            day = day.Date;
            Dates.Add(day);
            model[Tuple.Create(user, day)] = entry;
        }
    }
}
