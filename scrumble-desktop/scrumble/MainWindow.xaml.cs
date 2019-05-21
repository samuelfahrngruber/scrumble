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
using scrumble.Views;
using ScrumbleLib.Utils;

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

        private int sprintEditor_sprintId;

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

            Scrumble.forceInit();

            initializeInformation();

            initializeMenu();
            initializeProjectOverview();

            initializeMyTasks();
            initializeSelectedTask();

            initCurrentProject();

            System.Windows.Threading.DispatcherTimer dispatcherTimer = new System.Windows.Threading.DispatcherTimer();
            dispatcherTimer.Tick += new EventHandler(dispatcherTimer_Tick);
            dispatcherTimer.Interval = new TimeSpan(0, 0, 2);
            dispatcherTimer.Start();
        }

        private void dispatcherTimer_Tick(object sender, EventArgs e)
        {
            Scrumble.GetChanges();
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

            if (!Cef.IsInitialized)
            {
                CefSettings settings = new CefSettings();
                settings.RegisterScheme(new CefCustomScheme()
                {
                    SchemeName = "scrumboard",
                    SchemeHandlerFactory = new CefSharp.SchemeHandler.FolderSchemeHandlerFactory("./Scrumboard/html/")
                });
                Cef.Initialize(settings);
            }
        }

        private void initializeMenu()
        {
            menu_menuBar.Visibility = Visibility.Visible;
        }

        private void initializeProjectOverview()
        {
            section_ProjectOverview.Visibility = Visibility.Visible;
        }

        private void initializeInformation()
        {
            Scrumble.Logger = this;
            //initDevConsole();

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
            // section_SelectedTask.Visibility = Visibility.Visible;

            taskColors = new List<string>();

            taskColors.Add("#fdddff");
            taskColors.Add("#ffc1f5");
            taskColors.Add("#c7beff");
            taskColors.Add("#a5d1ff");
            taskColors.Add("#c2fff5");
            taskColors.Add("#bfffe6");
            taskColors.Add("#c7ffa9");
            taskColors.Add("#fff5ab");
            taskColors.Add("#ffcdab");
            taskColors.Add("#ffa0a0");
            taskColors.Add("#ffd5d9");
            taskColors.Add("#FFFFFF");

            listBox_brushes.DataContext = taskColors;
        }


        public void setSelectedTask(int id)
        {
            setSelectedTask(Scrumble.WrapperFactory.CreateTaskWrapper(id));
        }

        private void setSelectedTask(TaskWrapper tw)
        {
            if (!section_SelectedTask.IsVisible)
                Dispatcher.Invoke(() => { section_SelectedTask.Visibility = Visibility.Visible; });
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
            Scrumble.GetProductBacklog().CollectionChanged += refreshProductBacklog;
            Scrumble.GetSprints().CollectionChanged += refreshSprints;
        }

        private async void refreshProductBacklog(object sender, NotifyCollectionChangedEventArgs e)
        {
            await Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, (Action)(() =>
            {
                listBox_productBacklog.Items.Refresh();
            }));
        }

        private void refreshSprints(object sender, NotifyCollectionChangedEventArgs e)
        {
            refreshsprints();
        }

        private async void refreshsprints()
        {
            await Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, (Action)(() => {
                ObservableCollectionEx<SprintWrapper> sprints = Scrumble.GetSprints();
                listBox_sprints.ItemsSource = sprints.OrderBy(item => item.Number);
                listBox_sprints.Items.Refresh();
            }));
        }

        private async void refreshSelectedTask(object sender, PropertyChangedEventArgs e)
        {
            await Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, (Action)(() =>
           {
               textBox_selectedTask_name.Text = selectedTask.Name;
               textBox_selectedTask_description.Text = selectedTask.Info;

               //textBlock_selectedTask_responsible.Text = selectedTask.WrappedValue.ResponsibleUser == null ? "-" : selectedTask.WrappedValue.ResponsibleUser.Username.ToString();
               comboBox_selectedTask_responsible.ItemsSource = currentProject.Team.Values;
               comboBox_selectedTask_responsible.SelectedItem = selectedTask.WrappedValue.ResponsibleUser;

               //textBlock_selectedTask_verify.Text = selectedTask.WrappedValue.VerifyingUser == null ? "-" : selectedTask.WrappedValue.VerifyingUser.Username.ToString();
               comboBox_selectedTask_verify.ItemsSource = currentProject.Team.Values;
               comboBox_selectedTask_verify.SelectedItem = selectedTask.WrappedValue.VerifyingUser;

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


                if (currentProject.CurrentSprint != null)
                {
                    textBlock_projectOverview_currentSprint.Text = "#" + currentProject.WrappedValue.CurrentSprint.Number;
                    textBlock_projectOverview_currentSprintDeadline.Text = currentProject.WrappedValue.CurrentSprint.Deadline.ToString("dddd, dd.MM.yyyy");

                    toolBarTray_scrumBoard.Visibility = Visibility.Visible;
                    chromiumWebBrowser_scrumBoard.Visibility = Visibility.Visible;
                    textBlock_selectSprintFirst.Visibility = Visibility.Collapsed;
                }
                else
                {
                    textBlock_projectOverview_currentSprint.Text = "-";
                    textBlock_projectOverview_currentSprintDeadline.Text = "-";

                    toolBarTray_scrumBoard.Visibility = Visibility.Collapsed;
                    chromiumWebBrowser_scrumBoard.Visibility = Visibility.Collapsed;
                    textBlock_selectSprintFirst.Visibility = Visibility.Visible;
                }

                listBox_teamMembers.ItemsSource = currentProject.Team.Values;
                listBox_teamMembers.Items.Refresh();

                comboBox_productOwner.ItemsSource = currentProject.Team.Values;
                comboBox_productOwner.SelectedItem = currentProject.WrappedValue.ProductOwner;

                refreshsprints();

                comboBox_addTask_responsible.ItemsSource = currentProject.WrappedValue.Team.Values;
                comboBox_addTask_verify.ItemsSource = currentProject.WrappedValue.Team.Values;

                listBox_productBacklog.ItemsSource = Scrumble.GetProductBacklog();
                listBox_productBacklog.Items.Refresh();
            }));
        }

        private void setMyTasks()
        {
            Dispatcher.BeginInvoke((Action)(() =>
            {
                treeViewItem_myTasks_sprintBacklog.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.SPRINT_BACKLOG);
                treeViewItem_myTasks_inProgress.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.IN_PROGRESS);
                treeViewItem_myTasks_inTest.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.TO_VERIFY);
                treeViewItem_myTasks_done.ItemsSource = Scrumble.GetMyTasks().Where(task => task.WrappedValue.State == TaskState.DONE);
            }));
        }

        private async void addTaskToScrumboard(TaskWrapper wrapper)
        {
            await Dispatcher.BeginInvoke(System.Windows.Threading.DispatcherPriority.Normal, (Action)(async () =>
            {
                if (chromiumWebBrowser_scrumBoard.IsInitialized
                    && chromiumWebBrowser_scrumBoard.WebBrowser != null
                    && chromiumWebBrowser_scrumBoard.CanExecuteJavascriptInMainFrame)
                {
                    JavascriptResponse response = await chromiumWebBrowser_scrumBoard.EvaluateScriptAsync("setTask(" + wrapper.ToJson() + ");");
                }
            }));
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
            //Dispatcher.Invoke(() => { setSelectedTask(6); });
            Scrumble.GetScrumboard().CollectionChanged += new NotifyCollectionChangedEventHandler(ScrumboardChanged);
            Dispatcher.Invoke(() => { setScrumboardContent(); });
        }

        private void ScrumboardChanged(object sender, NotifyCollectionChangedEventArgs e)
        {

            setScrumboardContent();
        }

        private void MyTasksChanged(object sender, NotifyCollectionChangedEventArgs e)
        {
            //different kind of changes that may have occurred in collection
            setMyTasks();
        }

        private void setScrumboardContent(bool force)
        {
            foreach (TaskWrapper task in Scrumble.GetScrumboard(force))
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
                if (textBlock_devConsole.Inlines.Count > 10)
                {
                    textBlock_devConsole.Inlines.Remove(textBlock_devConsole.Inlines.First());
                }
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

        private void toolBarButton_refreshBoard2_Click(object sender, RoutedEventArgs e)
        {
            Scrumble.GetChanges();
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
            if (tw != null)
            {
                setSelectedTask(tw);
            }
        }

        private void listBox_productBacklog_SelectionChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
        {
            TaskWrapper tw = listBox_productBacklog.SelectedItem as TaskWrapper;
            if (tw != null)
            {
                setSelectedTask(tw);
            }
        }

        private void contextMenuItem_removeTeamMember_Click(object sender, RoutedEventArgs e)
        {
            User u = listBox_teamMembers.SelectedItem as User;
            if (u != null)
            {
                Scrumble.GetCurrentProject().RemoveMember(u.Id);
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

        private void menuItem_project_switch_Click(object sender, RoutedEventArgs e)
        {
            Properties.Settings.Default["CurrentProjectId"] = -1;
            Properties.Settings.Default.Save();
            LoginWindow w = new LoginWindow();
            w.Show();
            Close();
        }

        private void menuItem_settings_logout_Click(object sender, RoutedEventArgs e)
        {
            Properties.Settings.Default["LoginUsername"] = null;
            Properties.Settings.Default["LoginPassword"] = null;
            Properties.Settings.Default["CurrentProjectId"] = -1;
            Properties.Settings.Default.Save();
            LoginWindow w = new LoginWindow();
            w.Show();
            Close();
        }

        private void button_plusRejection_Click(object sender, RoutedEventArgs e)
        {
            selectedTask.Rejections = selectedTask.Rejections + 1;
        }

        private void button_minusRejection_Click(object sender, RoutedEventArgs e)
        {
            if (selectedTask.Rejections > 0)
                selectedTask.Rejections = selectedTask.Rejections - 1;
        }

        private void textBox_selectedTask_description_LostFocus(object sender, RoutedEventArgs e)
        {
            selectedTask.Info = textBox_selectedTask_description.Text;
        }

        private void textBox_selectedTask_name_LostFocus(object sender, RoutedEventArgs e)
        {
            selectedTask.Name = textBox_selectedTask_name.Text;
        }

        private void comboBox_selectedTask_verify_SelectionChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
        {
            User u = (comboBox_selectedTask_verify.SelectedItem as User);
            selectedTask.VerifyingUser = u == null ? (int?)null : u.Id;
        }

        private void comboBox_selectedTask_responsible_SelectionChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
        {
            User u = (comboBox_selectedTask_responsible.SelectedItem as User);
            selectedTask.ResponsibleUser = u == null ? (int?)null : u.Id;
        }

        private void button_selectedTask_removeVerify_Click(object sender, RoutedEventArgs e)
        {
            comboBox_selectedTask_verify.SelectedValue = null;
        }

        private void button_selectedTask_removeResponsible_Click(object sender, RoutedEventArgs e)
        {
            comboBox_selectedTask_responsible.SelectedValue = null;
        }

        private void contextMenuItem_moveTaskToScrumboard_Click(object sender, RoutedEventArgs e)
        {
            TaskWrapper t = listBox_productBacklog.SelectedItem as TaskWrapper;
            if (t != null && Scrumble.GetCurrentProject().CurrentSprint != null)
            {
                t.WrappedValue.State = TaskState.SPRINT_BACKLOG;
                t.Sprint = Scrumble.GetCurrentProject().CurrentSprint;
                t.OnStateChanged();
            }
            else
            {
                MessageBox.Show("Either no task selected or no current sprint");
            }
        }

        private void button_addTeamMember_Click(object sender, RoutedEventArgs e)
        {
            attremptAddTeamMember();
        }

        private void Window_Closed(object sender, EventArgs e)
        {

        }

        private void textBox_addTeamMemberUsername_KeyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return)
            {
                attremptAddTeamMember();
            }
        }

        private void attremptAddTeamMember()
        {
            string username = textBox_addTeamMemberUsername.Text;
            if (username != "")
            {
                UserWrapper uw = Scrumble.GetUserByName(username);
                if (uw != null)
                {
                    currentProject.AddMember(uw);
                    textBox_addTeamMemberUsername.Text = "";
                }
                else
                {
                    MessageBox.Show("User not found!");
                }
            }
        }

        private void contextMenuItem_editTask_Click(object sender, RoutedEventArgs e)
        {
            TaskWrapper tw = listBox_productBacklog.SelectedItem as TaskWrapper;
            if (tw != null)
            {
                setSelectedTask(tw);
            }
        }

        private void contextMenuItem_setCurrentSprint_Click(object sender, RoutedEventArgs e)
        {
            SprintWrapper sw = listBox_sprints.SelectedItem as SprintWrapper;
            if (sw != null)
            {
                currentProject.CurrentSprint = sw.Id;
            }
        }

        private void button_addSprint_Click(object sender, RoutedEventArgs e)
        {
            showSprintEditor();
        }

        private void button_sprintEditor_close_Click(object sender, RoutedEventArgs e)
        {
            grid_sprintEditor.Visibility = Visibility.Collapsed;
        }

        private void showSprintEditor(int sprintid = -1)
        {
            this.sprintEditor_sprintId = sprintid;
            if (sprintid >= 0)
            {
                SprintWrapper sw = Scrumble.WrapperFactory.CreateSprintWrapper(sprintid);
                datePicker_sprintEditor_deadline.SelectedDate = sw.Deadline;
                datePicker_sprintEditor_start.SelectedDate = sw.Start;
                textBox_sprintEditor_number.Text = "" + sw.Number;
            }
            grid_sprintEditor.Visibility = Visibility.Visible;
        }

        private void button_sprintEditor_confirm_Click(object sender, RoutedEventArgs e)
        {

            int sprintNumber = 0;
            DateTime? start = datePicker_sprintEditor_start.SelectedDate;
            DateTime? deadline = datePicker_sprintEditor_deadline.SelectedDate;
            if (int.TryParse(textBox_sprintEditor_number.Text, out sprintNumber) == false
                || sprintNumber <= 0
                || start == null
                || deadline == null
                || deadline <= start)
            {
                MessageBox.Show("Wrong Input Data!");
                return;
            }

            if (false)
            {
                MessageBox.Show("Failed to save changes!\nThe sprint data interferes with other sprints!");
                return;
            }

            if (this.sprintEditor_sprintId < 1)
            {
                // create a sprint
                Sprint s = new Sprint(-1, null, sprintNumber, (DateTime)start, (DateTime)deadline);
                Scrumble.AddSprint(s);
            }
            else
            {
                // update a sprint
                SprintWrapper sw = Scrumble.WrapperFactory.CreateSprintWrapper(this.sprintEditor_sprintId);
                sw.WrappedValue.Deadline = (DateTime)deadline; // wrappedvalue, to not make a db call yet
                sw.WrappedValue.Start = (DateTime)start; // --,,--
                sw.Number = sprintNumber; // db call
            }
        }

        private void contextMenuItem_editSprint_Click(object sender, RoutedEventArgs e)
        {
            SprintWrapper sw = listBox_sprints.SelectedItem as SprintWrapper;
            if (sw != null)
            {
                showSprintEditor(sw.Id);
            }
        }

        private void comboBox_productOwner_SelectionChanged(object sender, System.Windows.Controls.SelectionChangedEventArgs e)
        {
            User u = comboBox_productOwner.SelectedItem as User;
            if(u != null && u != currentProject.WrappedValue.ProductOwner)
            {
                currentProject.ProductOwner = u.Id;
            }
        }
    }
}
