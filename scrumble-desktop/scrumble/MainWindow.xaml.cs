using ScrumbleLib;
using System.Collections.Generic;
using System.Windows;
using CefSharp;
using CefSharp.Wpf;
using ScrumbleLib.Data;
using ScrumbleLib.Connection.Wrapper;
using scrumble.Scrumboard;

namespace scrumble
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private ScrumboardInterface scrumboardInterface;
        public MainWindow()
        {
            testInitBrowser();
            InitializeComponent();
            chromiumWebBrowser_scrumBoard.RegisterJsObject("scrumble_scrumboardInterface", scrumboardInterface);


            testInit();
            Scrumble.Login("user", "pw");
            new TaskWrapper(1);
            new TaskWrapper(2);
            new TaskWrapper(3);
            new TaskWrapper(4);
            new TaskWrapper(5);
            new TaskWrapper(6);
            new TaskWrapper(7);
            new TaskWrapper(8);
            setSelectedTask();
            setMyTasks();
        }

        private void testInit()
        {
            List<ScrumbleLib.Data.Task> toDo = new List<ScrumbleLib.Data.Task>();
            //toDo.Add(new ScrumbleLib.Data.Task("hello"));
            //toDo.Add(new ScrumbleLib.Data.Task("world"));
            treeViewItem_sprintBacklog.ItemsSource = toDo;

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

        private void setSelectedTask()
        {
            int id = 4;
            TaskWrapper task = new TaskWrapper(id);
            textBlock_selectedTask_name.Text = task.Name;
            textBlock_selectedTask_description.Text = task.Info;
            textBlock_selectedTask_responsible.Text = task.WrappedValue.ResponsibleUser.Username.ToString();
            textBlock_selectedTask_verify.Text = task.WrappedValue.VerifyingUser.Username.ToString();
            textBlock_selectedTask_rejections.Text = task.Rejections.ToString();
        } 

        private void setMyTasks()
        {
            treeViewItem_sprintBacklog.ItemsSource = Scrumble.MyTasks;
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
                SchemeHandlerFactory = new CefSharp.SchemeHandler.FolderSchemeHandlerFactory("./Scrumboard/html")
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
            Dispatcher.Invoke(setSelectedTask);
            Dispatcher.Invoke(addTaskToScrumboardTMP);
        }

        private void addTaskToScrumboardTMP()
        {
            addTaskToScrumboard(new TaskWrapper(3));

        }
    }
}
