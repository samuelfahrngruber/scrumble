using ScrumbleLib;
using System.Collections.Generic;
using System.Windows;
using CefSharp;
using CefSharp.Wpf;
using ScrumbleLib.Connection.Wrapper;
using scrumble.Scrumboard;
using System.Linq;
using System.Collections.Specialized;
using System;
using System.Windows.Documents;
using System.Windows.Media;
using System.Windows.Input;
using System.ComponentModel;
using System.Threading.Tasks;
using ScrumbleLib.Data;
using MahApps.Metro.Controls;
using System.IO;
using System.Reflection;
using scrumble.DailyScrumTable;

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
        private ProjectWrapper currentProject = null;

        private int? temp_removeTask = null;

        private List<string> taskColors;

        public MainWindow()
        {
            initializeCef();

            InitializeComponent();

            menu_menuBar.Visibility = Visibility.Collapsed;
            section_ProjectOverview.Visibility = Visibility.Collapsed;
            section_Information.Visibility = Visibility.Collapsed;
            section_MyTasks.Visibility = Visibility.Collapsed;
            section_SelectedTask.Visibility = Visibility.Collapsed;

            chromiumWebBrowser_scrumBoard.RegisterJsObject("scrumble_scrumboardInterface", scrumboardInterface);

            Window_Loaded(null, null);

            initializeInformation();

            initializeMenu();
            initializeProjectOverview();

            initializeMyTasks();
            initializeSelectedTask();

            initCurrentProject();
        }


        private async void Window_Loaded(object sender, RoutedEventArgs e)
        {
            //await Dispatcher.BeginInvoke((Action)(() => initializeInformation()));

            //await Dispatcher.BeginInvoke((Action)(() => initializeMenu()));
            //await Dispatcher.BeginInvoke((Action)(() => initializeProjectOverview()));

            //await Dispatcher.BeginInvoke((Action)(() => initializeMyTasks()));
            //await Dispatcher.BeginInvoke((Action)(() => initializeSelectedTask()));
        }

        private void initializeCef()
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

        private void initializeMenu()
        {
            menu_menuBar.Visibility = Visibility.Visible;
        }

        private void initializeProjectOverview()
        {

            Scrumble.WrapperFactory.CreateProjectWrapper(22);
            section_ProjectOverview.Visibility = Visibility.Visible;
        }
        
        private void initializeInformation()
        {
            Scrumble.Logger = this;
            //initDevConsole();
            string projectLog = "" +
                "[2018-10-04 19:06] Added Project Log in Gui\n" +
                "[2018-10-04 19:10] Successfully Tested\n" +
                "[2018-10-04 19:30] Added Daily Scrum Table in Gui";
            textBox_projectLog.Text = projectLog;
            console = new DeveloperConsole(this);
            section_Information.Visibility = Visibility.Visible;

            initializeDailyScrumTable();
        }

        private void initializeDailyScrumTable()
        {
            IEnumerable<DailyScrumEntryWrapper> dailyScrumEntries = Scrumble.GetDailyScrumEntries(true);
            DailyScrumModel model = DailyScrumModel.CreateFromDSEntries(dailyScrumEntries);
            dailyScrumTable_DSTable.Model = model;
        }

        private void initializeMyTasks()
        {
            setMyTasks();
            Scrumble.GetMyTasks().CollectionChanged += new NotifyCollectionChangedEventHandler(MyTasksChanged);
            section_MyTasks.Visibility = Visibility.Visible;
        }

        private void initializeSelectedTask()
        {
            Scrumble.WrapperFactory.CreateTaskWrapper(18);
            setSelectedTask(18);
            section_SelectedTask.Visibility = Visibility.Visible;

            taskColors = new List<string>();

            taskColors.Add("#FDD7FF");
            taskColors.Add("#FF8EEC");
            taskColors.Add("#8A79FF");
            taskColors.Add("#4FA5FF");
            taskColors.Add("#73FFE8");
            taskColors.Add("#39FFB0");
            taskColors.Add("#B5FF8D");
            taskColors.Add("#FFF082");
            taskColors.Add("#FFB841");
            taskColors.Add("#FF8080");
            taskColors.Add("#FF9EA6");
            taskColors.Add("#FFFFFF");

            listBox_brushes.DataContext = taskColors;
        }


        public void setSelectedTask(int id)
        {
            setSelectedTask(Scrumble.WrapperFactory.CreateTaskWrapper(id));
        }

        private void setSelectedTask(TaskWrapper tw)
        {
            if (selectedTask != null)
                selectedTask.PropertyChanged -= refreshSelectedTask;
            selectedTask = tw;
            refreshSelectedTask(null, null);
            selectedTask.PropertyChanged += refreshSelectedTask;
        }

        private void initCurrentProject()
        {
            currentProject = Scrumble.GetCurrentProject();
            refreshCurrentProject(null, null);
            currentProject.PropertyChanged += refreshCurrentProject;
            //Scrumble.GetProductBacklog().CollectionChanged += refreshProductBacklog;
        }

        private void refreshProductBacklog(object sender, NotifyCollectionChangedEventArgs e)
        {
            refreshCurrentProject(null, null);
        }

        private async void refreshSelectedTask(object sender, PropertyChangedEventArgs e)
        {
            await Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, (Action) (() =>
            {
                textBlock_selectedTask_name.Text = selectedTask.Name;
                textBlock_selectedTask_description.Text = selectedTask.Info;
                textBlock_selectedTask_responsible.Text = selectedTask.WrappedValue.ResponsibleUser == null ? "-" : selectedTask.WrappedValue.ResponsibleUser.Username.ToString();
                textBlock_selectedTask_verify.Text = selectedTask.WrappedValue.VerifyingUser == null ? "-" : selectedTask.WrappedValue.VerifyingUser.Username.ToString();
                textBlock_selectedTask_rejections.Text = selectedTask.Rejections.ToString();
                button_chooseTaskColor.Background = (SolidColorBrush)(new BrushConverter().ConvertFrom(selectedTask.Color));
            }));
        }

        private async void refreshCurrentProject(object sender, PropertyChangedEventArgs e)
        {
            await Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, (Action)(() =>
            {
                textBlock_projectOverview_name.Text = currentProject.Name;
                textBlock_projectOverview_productOwner.Text = currentProject.WrappedValue.ProductOwner.ToString();

                textBlock_projectOverview_currentSprint.Text = "#" + currentProject.WrappedValue.CurrentSprint.Number;
                textBlock_projectOverview_currentSprintDeadline.Text = currentProject.WrappedValue.CurrentSprint.Deadline.ToString("dddd, dd.MM.yyyy");

                treeViewItem_teamMembers.ItemsSource = currentProject.WrappedValue.Team;

                comboBox_addTask_responsible.ItemsSource = currentProject.WrappedValue.Team;
                comboBox_addTask_verify.ItemsSource = currentProject.WrappedValue.Team;

                treeViewItem_productBacklog.ItemsSource = Scrumble.GetProductBacklog();
            }));
        }

        private void setMyTasks()
        {
            Dispatcher.BeginInvoke((Action)(() => { 
                treeViewItem_myTasks_sprintBacklog.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.SPRINT_BACKLOG);
                treeViewItem_myTasks_inProgress.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.IN_PROGRESS);
                treeViewItem_myTasks_inTest.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.TO_VERIFY);
                treeViewItem_myTasks_done.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.DONE);
            }));
        }

        private async void addTaskToScrumboard(TaskWrapper wrapper)
        {
            if (chromiumWebBrowser_scrumBoard.CanExecuteJavascriptInMainFrame)
            {
                JavascriptResponse response = await chromiumWebBrowser_scrumBoard.EvaluateScriptAsync("addTask(" + wrapper.ToJson() + ");");
            }
        }

        private async void removeTaskFromScrumboard(TaskWrapper wrapper)
        {
            if (chromiumWebBrowser_scrumBoard.CanExecuteJavascriptInMainFrame)
            {
                JavascriptResponse response = await chromiumWebBrowser_scrumBoard.EvaluateScriptAsync("removeTask(" + wrapper.ToJson() + ");");
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
            textBlock_statusBarIndicator.Text = "";
            textBlock_statusBarIndicator.Visibility = Visibility.Collapsed;
            progressBar_statusBar.Visibility = Visibility.Collapsed;
        }

        private void chromiumWebBrowser_scrumBoard_FrameLoadEnd(object sender, FrameLoadEndEventArgs e)
        {
            Dispatcher.Invoke(stopProgress);
            Dispatcher.Invoke(() => { setSelectedTask(6); });
            Scrumble.GetScrumboard().CollectionChanged += new NotifyCollectionChangedEventHandler(ScrumboardChanged);
            Dispatcher.Invoke(() => { setScrumboardContent(); });
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
                foreach (TaskWrapper task in e.OldItems)
                {
                    //removeTaskFromScrumboard(task);
                    removeTaskFromScrumboard(task);
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

        private void setScrumboardContent(bool force)
        {
            foreach(TaskWrapper task in Scrumble.GetScrumboard(force))
            {
                addTaskToScrumboard(task);
            }
        }

        private void setScrumboardContent()
        {
            setScrumboardContent(false);
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

        private void toolBarButton_refreshBoard_Click(object sender, RoutedEventArgs e)
        {
            setScrumboardContent(true);
        }

        private void toolBarButton_addTask_Click(object sender, RoutedEventArgs e)
        {
            InputBox.Visibility = System.Windows.Visibility.Visible;
        }

        private void button_closeAddTask_Click(object sender, RoutedEventArgs e)
        {
            InputBox.Visibility = System.Windows.Visibility.Collapsed;
        }

        private void button_confirmAddTask_Click(object sender, RoutedEventArgs e)
        {
            string name = textBox_addTask_name.Text;
            string info = textBox_addTask_info.Text;
            User responsible = comboBox_addTask_responsible.SelectedItem as User;
            User verify = comboBox_addTask_verify.SelectedItem as User;

            ScrumbleLib.Data.Task t = new ScrumbleLib.Data.Task(-1, 
                name, 
                info, 
                0, 
                responsible, 
                verify, 
                Scrumble.GetCurrentProject().WrappedValue.CurrentSprint, 
                Scrumble.GetCurrentProject().WrappedValue, 
                TaskState.SPRINT_BACKLOG, 
                0,
                "#FFFFFF"); // todo colorpicker

            Scrumble.AddTask(t);

            InputBox.Visibility = Visibility.Collapsed;

            textBox_addTask_name.Text = "";
            textBox_addTask_info.Text = "";
            comboBox_addTask_verify.SelectedItem = null;
            comboBox_addTask_responsible.SelectedItem = null;
        }

        private void treeView_myTasks_SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            TaskWrapper tw = treeView_myTasks.SelectedItem as TaskWrapper;
            if(tw != null)
            {
                setSelectedTask(tw);
            }
        }

        private void treeView_projectOverview_SelectedItemChanged(object sender, RoutedPropertyChangedEventArgs<object> e)
        {
            TaskWrapper tw = treeView_projectOverview.SelectedItem as TaskWrapper;
            if(tw != null)
            {
                setSelectedTask(tw);
            }
        }

        public void showRemoveTaskPopup(int taskid)
        {
            temp_removeTask = taskid;
            Dispatcher.BeginInvoke((Action)(() => { grid_removeTaskDialog.Visibility = System.Windows.Visibility.Visible; }));
        }

        private void button_closeRemoveTask_Click(object sender, RoutedEventArgs e)
        {
            grid_removeTaskDialog.Visibility = System.Windows.Visibility.Collapsed;
        }

        private void button_removeTask_Click(object sender, RoutedEventArgs e)
        {
            if (radioButton_removeTask_pbl.IsChecked == true)
            {
                TaskWrapper tw = Scrumble.WrapperFactory.CreateTaskWrapper((int)temp_removeTask);
                tw.WrappedValue.Sprint = null;
                tw.State = TaskState.PRODUCT_BACKLOG.ToString();
                removeTaskFromScrumboard(tw);
            }
            else if (radioButton_removeTask_delete.IsChecked == true)
            {
                Scrumble.DeleteTask((int)temp_removeTask);
            }
            grid_removeTaskDialog.Visibility = System.Windows.Visibility.Collapsed;
        }

        private void button_closeChooseColor_Click(object sender, RoutedEventArgs e)
        {
            inputBox_colorSelector.Visibility = Visibility.Collapsed;
        }

        private void button_chooseTaskColor_Click(object sender, RoutedEventArgs e)
        {
            listBox_brushes.SelectedItem = selectedTask.Color;
            inputBox_colorSelector.Visibility = Visibility.Visible;
        }

        private void button_selectColor_Click(object sender, RoutedEventArgs e)
        {
            string col = listBox_brushes.SelectedItem as string;
            selectedTask.Color = col;
            inputBox_colorSelector.Visibility = Visibility.Collapsed;
        }
    }
}
