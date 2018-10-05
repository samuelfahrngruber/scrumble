using ScrumbleLib;
using System;
using System.Collections.Generic;
using System.Data;
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

namespace scrumble
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            InitializeComponent();

            testInit();
        }

        private void testInit()
        {
            List<UserStory> toDo = new List<UserStory>();
            toDo.Add(new UserStory("hello"));
            toDo.Add(new UserStory("world"));
            treeViewItem_sprintBacklog.ItemsSource = toDo;

            List<UserStory> productBacklog = new List<UserStory>();
            productBacklog.Add(new UserStory("hello_pbl"));
            productBacklog.Add(new UserStory("world_pbl"));
            treeViewItem_productBacklog.ItemsSource = productBacklog;

            List<User> teamMembers = new List<User>();
            teamMembers.Add(new User("pauli"));
            teamMembers.Add(new User("simsi"));
            teamMembers.Add(new User("webi"));
            teamMembers.Add(new User("sami"));
            treeViewItem_teamMembers.ItemsSource = teamMembers;

            string projectLog = "" +
                "[2018-10-04 19:06] Added Project Log in Gui\n" +
                "[2018-10-04 19:10] Successfully Tested\n" +
                "[2018-10-04 19:30] Added Daily Scrum Table in Gui";
            textBox_projectLog.Text = projectLog;

            testInitDailyScrumTable();
        }

        private void testInitDailyScrumTable()
        {
            double[,] _dataArray;
            DataView _dataView;

            _dataArray = new double[2, 2] { { 10, 15 }, { 20, 25 } };

            var array = _dataArray;
            var rows = array.GetLength(0);
            var rowHeaders = new string[rows];
            for(int i = 0; i < rows; i++)
            {
                rowHeaders[i] = "row" + i;
            }
            var columns = array.GetLength(1);
            var t = new DataTable();
            // Add columns with name "0", "1", "2", ...
            t.Columns.Add(new DataColumn("."));

            for (var c = 0; c < columns; c++)
            {
                t.Columns.Add(new DataColumn("column" + c.ToString()));
            }
            // Add data to DataTable
            for (var r = 0; r < rows; r++)
            {
                var newRow = t.NewRow();
                for (var c = 1; c < columns; c++)
                {
                    newRow[0] = rowHeaders[r];
                    newRow[c] = array[r, c];
                }
                t.Rows.Add(newRow);
            }
            _dataView = t.DefaultView;
            dataGrid_DailyScrumTable.ItemsSource = _dataView;
        }
    }
}
