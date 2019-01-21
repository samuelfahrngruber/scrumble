using ScrumbleLib;
using ScrumbleLib.Connection.Wrapper;
using ScrumbleLib.Data;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Imaging;
using System.Windows.Navigation;
using System.Windows.Shapes;

namespace scrumble.DailyScrumTable
{
    /// <summary>
    /// Interaction logic for DailyScrumTable.xaml
    /// </summary>
    public partial class DailyScrumTable : UserControl
    {
        private DailyScrumModel model;
        public DailyScrumModel Model
        {
            get { return model; }
            set
            {
                model = value;
                onModelChanged();
            }
        }

        public DailyScrumTable()
        {
            InitializeComponent();
            model = new DailyScrumModel();
        }

        private void initDailyScrumTable2()
        {
            List<TableColumn> table;

            string sam = "sam";
            string webi = "webi";
            string pauli = "pauli";
            string simon = "simon";

            model.Users.Add(sam);
            model.Users.Add(webi);
            model.Users.Add(pauli);
            model.Users.Add(simon);

            //model.Set(sam, DateTime.Parse("2018-10-01"), new MDailyScrumEntry() {  Text = "STARTUP" });
            //model.Set(webi, DateTime.Parse("2018-10-01"), new MDailyScrumEntry() { Text = "STARTUP" });
            //model.Set(pauli, DateTime.Parse("2018-10-01"), new MDailyScrumEntry() { Text = "STARTUP" });
            //model.Set(simon, DateTime.Parse("2018-10-01"), new MDailyScrumEntry() { Text = "STARTUP" });

            //model.Set(sam, DateTime.Parse("2018-10-02"), new MDailyScrumEntry() { Text = "create c# project", TaskId="#20" });
            //model.Set(webi, DateTime.Parse("2018-10-02"), new MDailyScrumEntry() { Text = "create database" });
            //model.Set(pauli, DateTime.Parse("2018-10-02"), new MDailyScrumEntry() { Text = "create android project" });
            //model.Set(simon, DateTime.Parse("2018-10-02"), new MDailyScrumEntry() { Text = "create database" });

            //model.Set(pauli, DateTime.Parse("2018-10-04"), new MDailyScrumEntry() { Text = "added scrumboard to app", TaskId = "#20" });

            //model.Set(sam, DateTime.Parse("2018-10-05"), new MDailyScrumEntry() { Text = "design wpf", TaskId = "#20" });
            //model.Set(pauli, DateTime.Parse("2018-10-05"), new MDailyScrumEntry() { Text = "add task, view task" });
            //model.Set(simon, DateTime.Parse("2018-10-05"), new MDailyScrumEntry() { Text = "webservice description", TaskId = "#20" });

            //model.Set(sam, DateTime.Parse("2018-10-08"), new MDailyScrumEntry() { Text = "wpf: scrumboard, dailyscrum, improvements" });
            //model.Set(webi, DateTime.Parse("2018-10-08"), new MDailyScrumEntry() { Text = "webservice init", TaskId = "#20" });
            //model.Set(pauli, DateTime.Parse("2018-10-08"), new MDailyScrumEntry() { Text = "MISSING" });
            //model.Set(simon, DateTime.Parse("2018-10-08"), new MDailyScrumEntry() { Text = "webservice init" });

            table = convertFromModel();
            itemsControl_DailyScrumDays.ItemsSource = table;
        }

        private void onModelChanged()
        {
            List<TableColumn> table = convertFromModel();
            itemsControl_DailyScrumDays.ItemsSource = table;
        }

        private List<TableColumn> convertFromModel()
        {
            List<TableColumn> table = new List<TableColumn>();

            TableColumn dayCol = new TableColumn();
            dayCol.Header = "";
            foreach (string user in model.Users)
            {
                dayCol.Entries.Add(new DailyScrumRowHeader() { Text = user });
            }
            table.Add(dayCol);
            foreach (DateTime date in model.Dates.OrderByDescending(date => date))
            {
                dayCol = new TableColumn();
                dayCol.Header = date.Date.ToString("ddd, dd.MM.yyyy");
                foreach (string user in model.Users)
                {
                    MDailyScrumEntry entry = model.Get(user, date);
                    dayCol.Entries.Add(entry);
                }
                table.Add(dayCol);
            }

            return table;
        }
    }
}
