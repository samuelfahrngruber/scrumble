using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ScrumbleLib.Connection.Json;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web.Script.Serialization;

namespace ScrumbleLib.Connection.Wrapper
{
    public abstract class DataWrapper<DataType>: INotifyPropertyChanged, IIndexable
    {
        [JsonIgnore]
        public DataType WrappedValue { get; set; }

        public abstract int Id { get; protected set; }

        protected static Dictionary<int, List<DataWrapper<DataType>>> Instances = new Dictionary<int, List<DataWrapper<DataType>>>();

        public event PropertyChangedEventHandler PropertyChanged;

        public string ToJson()
        {
            JsonSerializerSettings settings = new JsonSerializerSettings();
            settings.ContractResolver = new LowercaseContractResolver();
            string json = JsonConvert.SerializeObject(this, Formatting.Indented, settings);
            return json;
        }

        protected DataWrapper(DataType wrappedValue)
        {
            WrappedValue = wrappedValue;
            if (Instances.ContainsKey(Id))
                Instances[Id].Add(this);
            else
                Instances[Id] = new List<DataWrapper<DataType>>();
        }

        public override string ToString()
        {
            return WrappedValue.ToString();
        }

        private void notify(string propertyName)
        {
            var handler = PropertyChanged;
            if (PropertyChanged != null)
            {
                handler(this, new PropertyChangedEventArgs(propertyName));
            }
        }

        protected void OnPropertyChanged(string propertyName)
        {
            notify(propertyName);
            foreach(DataWrapper<DataType> wrapper in Instances[Id])
            {
                wrapper.notify(propertyName);
            }
        }
    }
}
