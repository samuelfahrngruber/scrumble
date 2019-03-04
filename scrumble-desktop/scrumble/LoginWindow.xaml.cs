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
using System.Windows.Shapes;

namespace scrumble.Views
{
    /// <summary>
    /// Interaction logic for LoginWindow.xaml
    /// </summary>
    public partial class LoginWindow : Window
    {
        public LoginWindow()
        {
            InitializeComponent();
            string username = Properties.Settings.Default["LoginUsername"] as string;
            string password = Properties.Settings.Default["LoginPassword"] as string;

            if(username != null && password != null && username != "" && password != "")
                attemptLogin(username, password);

            int? projid = Properties.Settings.Default["CurrentProjectId"] as int?;
            if(projid != null && projid >= 0)
                attemptProjectSelection((int)projid);
        }

        private void button_close_Click(object sender, RoutedEventArgs e)
        {
            Close();
        }

        private void button_login_Click(object sender, RoutedEventArgs e)
        {
            attemptLogin();
        }

        private void attemptLogin()
        {

            string username = textBox_username.Text;
            string password = passwordBox_password.Password;

            attemptLogin(username, password);
        }

        private void attemptLogin(string username, string password)
        {
            bool saveCredentials = checkBox_autoLogin.IsChecked == true;
            setLoginEnabled(false);

            bool success = Scrumble.Login(username, password);

            if (success)
            {
                if (saveCredentials)
                {
                    Properties.Settings.Default["LoginUsername"] = username;
                    Properties.Settings.Default["LoginPassword"] = password;
                    Properties.Settings.Default.Save();
                }
                setMyProjects();
                ShowProjectSelector();
            }
            else
            {
                MessageBox.Show("invalid credentials");
                setLoginEnabled(true);
            }
        }

        private void setMyProjects()
        {
            listBox_projects.ItemsSource = Scrumble.GetMyProjects(true);
        }

        private void attemptProjectSelection()
        {
            ProjectWrapper m = listBox_projects.SelectedItem as ProjectWrapper;
            if (m != null)
                attemptProjectSelection(m.Id);
        }

        private void attemptProjectSelection(int projectId)
        {
            bool saveProj = checkBox_autoSelectProject.IsChecked == true;

            if (saveProj)
            {
                Properties.Settings.Default["CurrentProjectId"] = projectId;
                Properties.Settings.Default.Save();
            }

            Scrumble.SetProject(projectId);
            MainWindow mainwin = new MainWindow();
            mainwin.Show();
            Close();
        }

        public void ShowProjectSelector(bool visible = true)
        {
            if (visible)
            {
                grid_loginForm.Visibility = Visibility.Collapsed;
                grid_projectChooser.Visibility = Visibility.Visible;
            }
            else
            {
                grid_loginForm.Visibility = Visibility.Visible;
                grid_projectChooser.Visibility = Visibility.Collapsed;
            }
        }

        private void hyperlink_register_Click(object sender, RoutedEventArgs e)
        {
            grid_registerBox.Visibility = Visibility.Visible;
        }

        private void button_closeRegister_Click(object sender, RoutedEventArgs e)
        {
            grid_registerBox.Visibility = Visibility.Collapsed;
        }

        private void button_confirmRegistration_Click(object sender, RoutedEventArgs e)
        {

        }

        private void button_selectProject_Click(object sender, RoutedEventArgs e)
        {
            attemptProjectSelection();
        }

        private void setLoginEnabled(bool e)
        {
            checkBox_autoLogin.IsEnabled = e;
            button_login.IsEnabled = e;
            passwordBox_password.IsEnabled = e;
            textBox_username.IsEnabled = e;
        }

        private void login_keyDown(object sender, KeyEventArgs e)
        {
            if (e.Key == Key.Return)
            {
                attemptLogin();
            }
        }

        private void Window_MouseDown(object sender, MouseButtonEventArgs e)
        {
            if (e.ChangedButton == MouseButton.Left)
                this.DragMove();
        }
    }
}
