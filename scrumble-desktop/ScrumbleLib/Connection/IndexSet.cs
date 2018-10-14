using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection
{
    public class IndexSet<T> where T : IIndexable
    {
        private System.Collections.Generic.Dictionary<int, T> data;
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

        public void Add(T value)
        {
            // data[value.Id] = value;
            data.Add(value.Id, value);
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
