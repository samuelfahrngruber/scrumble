using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using ScrumbleLib.Connection.Wrapper;

namespace ScrumbleLib.Utils
{
    public class IndexSet<T> where T : IIndexable
    {
        private System.Collections.Generic.Dictionary<int, T> data;
        public Dictionary<int, T>.ValueCollection Values
        {
            get
            {
                return data.Values;
            }
        }
        public IndexSet()
        {
            data = new Dictionary<int, T>();
        }

        public T this[int key]
        {
            get
            {
                return data[key];
            }
            private set
            {
                data[key] = value;
            }
        }

        public bool Add(T value)
        {
            if (Contains(value.Id))
                return false;
            data.Add(value.Id, value);
            return true;
        }

        public bool Contains(int key)
        {
            return data.ContainsKey(key);
        }

        public bool Contains(T item)
        {
            return data.ContainsKey(item.Id);
        }
    }
}
