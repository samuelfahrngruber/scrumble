﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace scrumble.DailyScrumTable
{
    public class DailyScrumEntry : IDailyScrumCell
    {
        public string TaskId { get; set; }
        public string Text { get; set; }
    }
}
