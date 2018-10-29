using ScrumbleLib;
using System.Collections.Generic;
using System.Windows;
using CefSharp;
using CefSharp.Wpf;
using ScrumbleLib.Data;
using ScrumbleLib.Connection.Wrapper;
using scrumble.Scrumboard;
using System.Linq;
using System.Collections.Specialized;
using System;
using System.Windows.Documents;
using System.Windows.Media;
using System.Windows.Input;
using System.ComponentModel;

namespace scrumble
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window, IDevLogger
    {
        private ScrumboardInterface scrumboardInterface;
        private DeveloperConsole console;

        private TaskWrapper selectedTask = null;

        public MainWindow()
        {
            Scrumble.Logger = this;

            testInitBrowser();
            InitializeComponent();
            chromiumWebBrowser_scrumBoard.RegisterJsObject("scrumble_scrumboardInterface", scrumboardInterface);

            //initDevConsole();

            testInit();
            Scrumble.Login("user", "pw");
            TaskWrapper.GetInstance(18);
            setSelectedTask(18);
            setMyTasks();
            console = new DeveloperConsole(this);

            Scrumble.MyTasks.CollectionChanged += new NotifyCollectionChangedEventHandler(MyTasksChanged);
        }

        private void testInit()
        {
            List<ScrumbleLib.Data.Task> toDo = new List<ScrumbleLib.Data.Task>();
            //toDo.Add(new ScrumbleLib.Data.Task("hello"));
            //toDo.Add(new ScrumbleLib.Data.Task("world"));
            treeViewItem_myTasks_sprintBacklog.ItemsSource = toDo;

            List<ScrumbleLib.Data.Task> productBacklog = new List<ScrumbleLib.Data.Task>();
            //productBacklog.Add(new ScrumbleLib.Data.Task("hello_pbl"));
            //productBacklog.Add(new ScrumbleLib.Data.Task("world_pbl"));
            treeViewItem_productBacklog.ItemsSource = productBacklog;

            List<User> teamMembers = new List<User>();
            teamMembers.Add(new User(10, "pauli"));
            teamMembers.Add(new User(11, "simsi"));
            teamMembers.Add(new User(12, "webi"));
            treeViewItem_teamMembers.ItemsSource = teamMembers;

            string projectLog = "" +
                "[2018-10-04 19:06] Added Project Log in Gui\n" +
                "[2018-10-04 19:10] Successfully Tested\n" +
                "[2018-10-04 19:30] Added Daily Scrum Table in Gui";
            textBox_projectLog.Text = projectLog;

        }

        private void setSelectedTask(int id)
        {
            if (selectedTask != null)
                selectedTask.PropertyChanged -= refreshSelectedTask;
            selectedTask = TaskWrapper.GetInstance(id);
            refreshSelectedTask(null, null);
            selectedTask.PropertyChanged += refreshSelectedTask;
        }

        private async void refreshSelectedTask(object sender, PropertyChangedEventArgs e)
        {
            await Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, (Action) (() =>
            {
                textBlock_selectedTask_name.Text = selectedTask.Name;
                textBlock_selectedTask_description.Text = selectedTask.Info;
                textBlock_selectedTask_responsible.Text = selectedTask.WrappedValue.ResponsibleUser.Username.ToString();
                textBlock_selectedTask_verify.Text = selectedTask.WrappedValue.VerifyingUser.Username.ToString();
                textBlock_selectedTask_rejections.Text = selectedTask.Rejections.ToString();
            }));
        }

        private void setMyTasks()
        {
            Dispatcher.Invoke(() => { 
                treeViewItem_myTasks_sprintBacklog.ItemsSource = Scrumble.MyTasks.Where(task => task.WrappedValue.State == ScrumBoardColumn.SPRINTBACKLOG);
                treeViewItem_myTasks_inProgress.ItemsSource = Scrumble.MyTasks.Where(task => task.WrappedValue.State == ScrumBoardColumn.INPROGRESS);
                treeViewItem_myTasks_inTest.ItemsSource = Scrumble.MyTasks.Where(task => task.WrappedValue.State == ScrumBoardColumn.INTEST);
                treeViewItem_myTasks_done.ItemsSource = Scrumble.MyTasks.Where(task => task.WrappedValue.State == ScrumBoardColumn.DONE);
            });
        }

        private async void addTaskToScrumboard(TaskWrapper wrapper)
        {
            if (chromiumWebBrowser_scrumBoard.CanExecuteJavascriptInMainFrame)
            {
                JavascriptResponse response = await chromiumWebBrowser_scrumBoard.EvaluateScriptAsync("addTask(" + wrapper.ToJson() + ");");
            }
        }

        private void testInitBrowser()
        {
            CefSharpSettings.LegacyJavascriptBindingEnabled = true;
            scrumboardInterface = ScrumboardInterface.Create(this);

            CefSettings settings = new CefSettings();
            settings.RegisterScheme(new CefCustomScheme()
            {
                SchemeName = "scrumboard",
                SchemeHandlerFactory = new CefSharp.SchemeHandler.FolderSchemeHandlerFactory("./Scrumboard/html/")
            });
            Cef.Initialize(settings);
        }

        private void chromiumWebBrowser_scrumBoard_IsBrowserInitializedChanged(object sender, DependencyPropertyChangedEventArgs e)
        {
            if (chromiumWebBrowser_scrumBoard.IsBrowserInitialized)
            {

            }
        }

        private void startProgress(string indicator)
        {
            textBlock_statusBarIndicator.Text = indicator;
            progressBar_statusBar.Visibility = Visibility.Visible;
        }

        private void stopProgress()
        {
            separator_statusBarSeparator.Visibility = Visibility.Collapsed;
            textBlock_statusBarIndicator.Text = "";
            textBlock_statusBarIndicator.Visibility = Visibility.Collapsed;
            progressBar_statusBar.Visibility = Visibility.Collapsed;
        }

        private void chromiumWebBrowser_scrumBoard_FrameLoadEnd(object sender, FrameLoadEndEventArgs e)
        {
            Dispatcher.Invoke(stopProgress);
            Dispatcher.Invoke(() => { setSelectedTask(4); });
            Scrumble.Scrumboard.CollectionChanged += new NotifyCollectionChangedEventHandler(ScrumboardChanged);
            Dispatcher.Invoke(setScrumboardContent);
        }

        private void ScrumboardChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            //different kind of changes that may have occurred in collection
            if (e.Action == NotifyCollectionChangedAction.Add)
            {
                foreach (TaskWrapper task in e.NewItems)
                {
                    addTaskToScrumboard(task);
                }
            }
            if (e.Action == NotifyCollectionChangedAction.Replace)
            {
                foreach (TaskWrapper task in e.NewItems)
                {
                    addTaskToScrumboard(task);
                }
            }
            if (e.Action == NotifyCollectionChangedAction.Remove)
            {
                foreach (TaskWrapper task in e.NewItems)
                {
                    //removeTaskFromScrumboard(task);
                }
            }
            if (e.Action == NotifyCollectionChangedAction.Move)
            {
                //your code
            }
            //setScrumboardContent();
        }

        private void MyTasksChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            //different kind of changes that may have occurred in collection
            setMyTasks();
        }

        private void setScrumboardContent()
        {
            foreach(TaskWrapper task in Scrumble.Scrumboard)
            {
                addTaskToScrumboard(task);
            }
        }

        private void addTaskToScrumboardTMP()
        {
            addTaskToScrumboard(TaskWrapper.GetInstance(3));
        }

        public void LogDev(string text, string hexcolor)
        {
            Dispatcher.Invoke(() =>
            {
                Run r = new Run();
                r.Text = text + "\n";
                r.Foreground = (SolidColorBrush)(new BrushConverter().ConvertFrom(hexcolor));
                textBlock_devConsole.Inlines.Add(r);
                scrollViewer_devConsole.ScrollToEnd();
            });
        }

        private void initDevConsole()
        {
            bool isenabled = (bool)Properties.Settings.Default["EnableDeveloperTools"];
            if (!isenabled)
            {
                tabItem_devConsole.Visibility = Visibility.Collapsed;
            }
        }

        private void textBox_devConsole_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return)
            {
                string command = textBox_devConsole.Text;
                LogDev(command, "#FFFFFF");
                textBox_devConsole.Text = "";
                console.Execute(command);
            }
        }

        
    }
}
