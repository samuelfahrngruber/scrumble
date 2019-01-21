using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ScrumbleLib.Utils;
using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ScrumbleLib.Connection.Wrapper
{
    public interface IIndexableDataWrapper<T> : IDataWrapper<T>, IIndexable
    {

    }
}
