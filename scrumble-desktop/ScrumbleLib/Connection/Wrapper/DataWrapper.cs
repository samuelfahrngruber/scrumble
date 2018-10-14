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
        [ScriptIgnore]
        public DataType WrappedValue { get; private set; }

        public string ToJson()
        {
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            return serializer.Serialize(this);
        }

        public static WrapperType FromJson<WrapperType>(string json) where WrapperType : DataWrapper<DataType>
        {
            JavaScriptSerializer serializer = new JavaScriptSerializer();
            WrapperType wrapper = serializer.Deserialize<WrapperType>(json);
            wrapper.WrappedValue = wrapper.Unwrap();
            return wrapper;
        }

        public DataWrapper(DataType wrappedValue)
        {
            WrappedValue = wrappedValue;
        }

        public abstract DataType Unwrap();
    }
}
