using ScrumbleLib;
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
            initDailyScrumTable2();
        }

        private void initDailyScrumTable()
        {
            List<TableColumn> table = new List<TableColumn>();

            TableColumn dayCol = new TableColumn();
            dayCol.Header = "";

            dayCol.Entries.Add(new DailyScrumRowHeader() { Text = "pauli" });
            dayCol.Entries.Add(new DailyScrumRowHeader() { Text = "simsi" });
            dayCol.Entries.Add(new DailyScrumRowHeader() { Text = "webi" });

            table.Add(dayCol);

            dayCol = new TableColumn();
            dayCol.Header = "Today";

            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#00", Text = "Say Hello" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#01", Text = "Say World" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#02", Text = "Say Something" });

            table.Add(dayCol);

            dayCol = new TableColumn();
            dayCol.Header = "Yesterday";

            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#10", Text = "Say Hello2" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#11", Text = "Say World2" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#12", Text = "Say Something2" });

            table.Add(dayCol);

            dayCol = new TableColumn();
            dayCol.Header = "2 Days Ago";

            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#20", Text = "Say Hello3" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#21", Text = "Say World3" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#22", Text = "Say Something3" });

            table.Add(dayCol);

            dayCol = new TableColumn();
            dayCol.Header = "3 Days Ago";

            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#30", Text = "Say Hello4" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#31", Text = "Say World4" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#32", Text = "Say Something4" });

            table.Add(dayCol);

            dayCol = new TableColumn();
            dayCol.Header = "4 Days Ago";

            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#40", Text = "Say Hello5" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#41", Text = "Say World5" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#42", Text = "Say Something5" });

            table.Add(dayCol);

            dayCol = new TableColumn();
            dayCol.Header = "5 Days Ago";

            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#50", Text = "Say Hello6" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#51", Text = "Say World6" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#52", Text = "Say Something6" });

            table.Add(dayCol);

            dayCol = new TableColumn();
            dayCol.Header = "6 Days Ago";

            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#60", Text = "Say Hello3" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#61", Text = "Say World3" });
            dayCol.Entries.Add(new DailyScrumEntry() { TaskId = "#62", Text = "Say Something3" });

            table.Add(dayCol);

            itemsControl_DailyScrumDays.ItemsSource = table;
        }

        private void initDailyScrumTable2()
        {
            List<TableColumn> table;

            User sam = new User("sam");
            User webi = new User("webi");
            User pauli = new User("pauli");
            User simon = new User("simon");

            model.Users.Add(sam);
            model.Users.Add(webi);
            model.Users.Add(pauli);
            model.Users.Add(simon);

            model.Set(sam, DateTime.Parse("2018-10-01"), new DailyScrumEntry() {  Text = "STARTUP" });
            model.Set(webi, DateTime.Parse("2018-10-01"), new DailyScrumEntry() { Text = "STARTUP" });
            model.Set(pauli, DateTime.Parse("2018-10-01"), new DailyScrumEntry() { Text = "STARTUP" });
            model.Set(simon, DateTime.Parse("2018-10-01"), new DailyScrumEntry() { Text = "STARTUP" });

            model.Set(sam, DateTime.Parse("2018-10-02"), new DailyScrumEntry() { Text = "create c# project", TaskId="#20" });
            model.Set(webi, DateTime.Parse("2018-10-02"), new DailyScrumEntry() { Text = "create database" });
            model.Set(pauli, DateTime.Parse("2018-10-02"), new DailyScrumEntry() { Text = "create android project" });
            model.Set(simon, DateTime.Parse("2018-10-02"), new DailyScrumEntry() { Text = "create database" });

            model.Set(pauli, DateTime.Parse("2018-10-04"), new DailyScrumEntry() { Text = "added scrumboard to app", TaskId = "#20" });

            model.Set(sam, DateTime.Parse("2018-10-05"), new DailyScrumEntry() { Text = "design wpf", TaskId = "#20" });
            model.Set(pauli, DateTime.Parse("2018-10-05"), new DailyScrumEntry() { Text = "add task, view task" });
            model.Set(simon, DateTime.Parse("2018-10-05"), new DailyScrumEntry() { Text = "webservice description", TaskId = "#20" });

            model.Set(sam, DateTime.Parse("2018-10-08"), new DailyScrumEntry() { Text = "wpf: scrumboard, dailyscrum, improvements" });
            model.Set(webi, DateTime.Parse("2018-10-08"), new DailyScrumEntry() { Text = "webservice init", TaskId = "#20" });
            model.Set(pauli, DateTime.Parse("2018-10-08"), new DailyScrumEntry() { Text = "MISSING" });
            model.Set(simon, DateTime.Parse("2018-10-08"), new DailyScrumEntry() { Text = "webservice init" });

            table = convertFromModel();
            itemsControl_DailyScrumDays.ItemsSource = table;
        }

        private void onModelChanged()
        {

        }

        private List<TableColumn> convertFromModel()
        {
            List<TableColumn> table = new List<TableColumn>();

            TableColumn dayCol = new TableColumn();
            dayCol.Header = "";
            foreach (User user in model.Users)
            {
                dayCol.Entries.Add(new DailyScrumRowHeader() { Text = user.Name });
            }
            table.Add(dayCol);
            foreach (DateTime date in model.Dates.OrderByDescending(date => date))
            {
                dayCol = new TableColumn();
                dayCol.Header = date.Date.ToString("ddd, dd.MM.yyyy");
                foreach (User user in model.Users)
                {
                    DailyScrumEntry entry = model.Get(user, date);
                    dayCol.Entries.Add(entry);
                }
                table.Add(dayCol);
            }

            return table;
        }
    }
}
