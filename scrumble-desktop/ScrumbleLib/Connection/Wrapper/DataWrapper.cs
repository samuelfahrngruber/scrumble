using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ScrumbleLib.Connection.Json;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Web.Script.Serialization;

namespace ScrumbleLib.Connection.Wrapper
{
    public abstract class DataWrapper<DataType>
    {
        [JsonIgnore]
        public DataType WrappedValue { get; set; }

        public string ToJson()
        {
            JsonSerializerSettings settings = new JsonSerializerSettings();
            settings.ContractResolver = new LowercaseContractResolver();
            string json = JsonConvert.SerializeObject(this, Formatting.Indented, settings);
            return json;
        }

        public DataWrapper(DataType wrappedValue)
        {
            WrappedValue = wrappedValue;
        }

        public override string ToString()
        {
            return WrappedValue.ToString();
        }
    }
}
