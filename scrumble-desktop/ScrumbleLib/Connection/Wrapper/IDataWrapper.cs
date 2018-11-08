using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ScrumbleLib.Data;
using ScrumbleLib.Utils;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public interface IDataWrapper<T> : IIndexable, INotifyPropertyChanged
    {
        [JsonIgnore]
        T WrappedValue { get; }

        string ToJson();

        void ApplyJson(JObject jsonObject);

        void ApplyJson(string json);
    }
}
