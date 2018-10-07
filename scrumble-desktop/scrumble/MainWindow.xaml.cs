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
using CefSharp;
using System.IO;
using CefSharp.Wpf;
using CefSharp.SchemeHandler;
using scrumble.DailyScrumTable;

namespace scrumble
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        public MainWindow()
        {
            testInitBrowser();
            InitializeComponent();

            testInit();
        }

        private void testInit()
        {
            List<ScrumbleLib.Task> toDo = new List<ScrumbleLib.Task>();
            toDo.Add(new ScrumbleLib.Task("hello"));
            toDo.Add(new ScrumbleLib.Task("world"));
            treeViewItem_sprintBacklog.ItemsSource = toDo;

            List<ScrumbleLib.Task> productBacklog = new List<ScrumbleLib.Task>();
            productBacklog.Add(new ScrumbleLib.Task("hello_pbl"));
            productBacklog.Add(new ScrumbleLib.Task("world_pbl"));
            treeViewItem_productBacklog.ItemsSource = productBacklog;

            List<User> teamMembers = new List<User>();
            teamMembers.Add(new User("pauli"));
            teamMembers.Add(new User("simsi"));
            teamMembers.Add(new User("webi"));
            treeViewItem_teamMembers.ItemsSource = teamMembers;

            string projectLog = "" +
                "[2018-10-04 19:06] Added Project Log in Gui\n" +
                "[2018-10-04 19:10] Successfully Tested\n" +
                "[2018-10-04 19:30] Added Daily Scrum Table in Gui";
            textBox_projectLog.Text = projectLog;

            initDailyScrumTable();

        }

        private void testInitBrowser()
        {
            CefSettings settings = new CefSettings();
            settings.RegisterScheme(new CefCustomScheme()
            {
                SchemeName = "scrumboard",
                SchemeHandlerFactory = new CefSharp.SchemeHandler.FolderSchemeHandlerFactory("./Scrumboard/html")
            });
            //chromiumWebBrowser_scrumBoard.ResourceHandlerFactory = new ScrumBoardResourceHandlerFactory();
            Cef.Initialize(settings);
        }

        private void chromiumWebBrowser_scrumBoard_IsBrowserInitializedChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            if (chromiumWebBrowser_scrumBoard.IsBrowserInitialized)
            {
                //stopProgress();
            }
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

        //private class TableCell<T>
        //{
        //    public T Value { get; set; }
        //    public TableCell() : this(default(T))
        //    {

        //    }
        //    public TableCell(T val)
        //    {
        //        Value = val;
        //    }
        //}

        private class TableColumn
        {
            public string Header { get; set; }
            public List<IDailyScrumCell> Entries { get; private set; }

            public TableColumn()
            {
                Entries = new List<IDailyScrumCell>();
            }
        }

        private class Table
        {
            public List<string> RowHeaders { get; private set; }
            public List<TableColumn> TableColumns { get; private set; }

            public Table()
            {
                RowHeaders = new List<string>();
                TableColumns = new List<TableColumn>();
            }
        }

        private void startProgress(string indicator)
        {
            textBlock_statusBarIndicator.Text = indicator;
            progressBar_statusBar.Visibility = Visibility.Visible;
        }

        private void stopProgress()
        {
            textBlock_statusBarIndicator.Text = "";
            progressBar_statusBar.Visibility = Visibility.Collapsed;
        }

        private void chromiumWebBrowser_scrumBoard_FrameLoadEnd(object sender, FrameLoadEndEventArgs e)
        {
            Dispatcher.Invoke(stopProgress);
        }
    }
}
