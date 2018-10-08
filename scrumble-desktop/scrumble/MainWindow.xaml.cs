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

        }

        private void testInitBrowser()
        {
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
            textBlock_statusBarIndicator.Text = "";
            progressBar_statusBar.Visibility = Visibility.Collapsed;
        }

        private void chromiumWebBrowser_scrumBoard_FrameLoadEnd(object sender, FrameLoadEndEventArgs e)
        {
            Dispatcher.Invoke(stopProgress);
        }
    }
}
