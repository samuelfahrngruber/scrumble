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
                //chromiumWebBrowser_scrumBoard.LoadHtml(File.ReadAllText("./Scrumboard/sbMarkup.html"));
            }
        }
    }
}
