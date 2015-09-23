using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Documents;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Media.Animation;
using System.Windows.Navigation;
using System.Windows.Shapes;
using System.Threading.Tasks;
using InfoCell.Classes;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace InfoCELLW7
{
    public partial class MainPage : PhoneApplicationPage
    {
        // Constructor
        public MainPage()
        {
            InitializeComponent();
            LocalizeMenu();
            LoadData();
        }

        private void LocalizeMenu()
        {

            (ApplicationBar.MenuItems[0] as ApplicationBarMenuItem).Text = AppResources.str_unregmenu;
        }


        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            CheckRegistration();
            base.OnNavigatedTo(e);
        }

        private void CheckRegistration()
        {
            if (StaticDB.IsRegistered)
            {
                ContentPanel.Visibility = System.Windows.Visibility.Visible;
                enterPhoneGrid.Visibility = System.Windows.Visibility.Collapsed;
                enterPinGrid.Visibility = System.Windows.Visibility.Collapsed;

                return;
            }

            if (StaticDB.PhoneNumber == "")
            {
                ContentPanel.Visibility = System.Windows.Visibility.Collapsed;
                enterPhoneGrid.Visibility = System.Windows.Visibility.Visible;
                enterPinGrid.Visibility = System.Windows.Visibility.Collapsed;
            }
            else if (StaticDB.PhoneNumber != "")
            {
                ContentPanel.Visibility = System.Windows.Visibility.Collapsed;
                enterPhoneGrid.Visibility = System.Windows.Visibility.Collapsed;
                enterPinGrid.Visibility = System.Windows.Visibility.Visible;
            }
        }

        private void LoadData()
        {
            DataContext = StaticDB.mainMenu;
            //mainMenu.ItemsSource = StaticDB.mainMenu.Submenus;
        }

        private void unregClick(object sender, EventArgs e)
        {
            if (!StaticDB.IsRegistered) return;

            if (MessageBox.Show(AppResources.str_unregister, AppResources.str_confirmation, MessageBoxButton.OKCancel) != MessageBoxResult.OK)
            {
                return;
            }

            StaticDB.PhoneNumber = "";
            StaticDB.Pin = "";
            StaticDB.IsRegistered = false;

            StaticDB.SaveSettings();

            showPhoneInput();
        }

        private void TextBlock_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            InfocellMenuItem m = (DataContext as InfocellMenuItem);

            if (m.Parent != null)
            {
                DataContext = m.Parent;
            }
        }

        private void showInputPin()
        {
            enterPhoneGrid.Visibility = System.Windows.Visibility.Collapsed;
            enterPinGrid.Visibility = System.Windows.Visibility.Visible;
            ContentPanel.Visibility = System.Windows.Visibility.Collapsed;
        }


        private string generateNewPin()
        {
            string res = "";

            Random rnd = new Random(Environment.TickCount);

            res = rnd.Next(10000, 99999).ToString();

            return res;
        }

        private async void phoneRegisterClick(object sender, RoutedEventArgs e)
        {
            if (txtPhone.Text.Trim() == "" || txtPhone.Text.Trim().Length != 9)
            {
                MessageBox.Show(AppResources.str_wrongnumber);
                return;
            }

            btnRegister.IsEnabled = false;

            StaticDB.Pin = generateNewPin();

            StaticDB.PhoneNumber = "994" + txtPhone.Text.Trim();

            StaticDB.SaveSettings();

            string url = string.Format("msisdn={0}&pin={1}", StaticDB.PhoneNumber, StaticDB.Pin);

            string resp = await HttpHelper.DownloadString("http://tools.dsc.az/infoapp/pin.asp", url, null);

            if (resp != null)
            {
                string s = resp.Substring(0, 1);

                if (s == "0")
                {
                    showInputPin();
                }
                else if (s == "1")
                {
                    MessageBox.Show(AppResources.str_badrequest);
                }
                else if (s == "2")
                {
                    MessageBox.Show(AppResources.str_limitout);
                }
            }
            else
            {
                StaticDB.Pin = "";
                StaticDB.PhoneNumber = "";
                StaticDB.SaveSettings();

                MessageBox.Show(AppResources.str_noresponse);
            }

            btnRegister.IsEnabled = true;

        }

        private void retryClick(object sender, RoutedEventArgs e)
        {
            StaticDB.PhoneNumber = "";
            StaticDB.Pin = "";
            StaticDB.SaveSettings();
            txtPhone.Text = "";
            txtPin.Text = "";

            showPhoneInput();
        }

        private void showPhoneInput()
        {
            enterPhoneGrid.Visibility = System.Windows.Visibility.Visible;
            enterPinGrid.Visibility = System.Windows.Visibility.Collapsed;
            ContentPanel.Visibility = System.Windows.Visibility.Collapsed;
        }

        private void showMainPanel()
        {
            enterPhoneGrid.Visibility = System.Windows.Visibility.Collapsed;
            enterPinGrid.Visibility = System.Windows.Visibility.Collapsed;
            ContentPanel.Visibility = System.Windows.Visibility.Visible;
        }

        private void finishClick(object sender, RoutedEventArgs e)
        {
            if (txtPin.Text.Trim() == "" || txtPin.Text.Trim().Length != 5 || txtPin.Text.Trim() != StaticDB.Pin)
            {
                MessageBox.Show(AppResources.str_badpin);
                return;
            }

            StaticDB.IsRegistered = true;
            StaticDB.SaveSettings();

            MessageBox.Show(string.Format(AppResources.str_phoneregistered, StaticDB.PhoneNumber));

            showMainPanel();
        }

        protected override void OnBackKeyPress(System.ComponentModel.CancelEventArgs e)
        {
            InfocellMenuItem m = (DataContext as InfocellMenuItem);

            if (m.Parent != null)
            {
                e.Cancel = true;

                DataContext = m.Parent;
            }
            base.OnBackKeyPress(e);
        }

        private void mainMenu_SelectionChanged(object sender, SelectionChangedEventArgs e)
        {
            if (mainMenu.SelectedItem == null) return;

            InfocellMenuItem m = mainMenu.SelectedItem as InfocellMenuItem;

            mainMenu.SelectedItem = null;

            if (m.Type == 1)
            {


                Dispatcher.BeginInvoke(() =>
                {
                    DataContext = m;
                });
            }
            else
            {
                doJob(m);
            }
        }

        private void goToParent(object sender, MouseButtonEventArgs e)
        {
            DataContext = StaticDB.mainMenu;
        }

        private async void doJob(InfocellMenuItem m)
        {
            if (m.Type == 2)
            {
                string url = string.Format("msisdn={0}&short={1}&text={2}", StaticDB.PhoneNumber, m.CMD, m.Keyword);

                var rr = HttpHelper.DownloadString("http://tools.dsc.az/infoapp/content.asp", url, null);

                string resp = await rr;

                if (resp == null)
                {
                    MessageBox.Show(AppResources.str_connectionerror);

                    return;
                }
                string s = resp.Substring(0, 1);

                if (s == "0")
                {
                    MessageBox.Show(AppResources.str_requestsent);
                }
                else if (s == "1")
                {
                    MessageBox.Show(AppResources.str_badparameters);
                }
                else if (s == "2")
                {
                    MessageBox.Show(AppResources.str_noenoughtbalance);
                }
                else if (s == "3")
                {
                    MessageBox.Show(AppResources.str_chargindown);
                }
                else
                {
                    MessageBox.Show(resp);
                }
            }
            else if (m.Type == 3 || m.Type == 4 || m.Type == 6)
            {
                NavigationService.Navigate(new Uri("/MakeRequestPage.xaml?id=" + m.ID, UriKind.Relative));
            }
            else if (m.Type == 5)
            {
                Microsoft.Phone.Tasks.SmsComposeTask sms = new Microsoft.Phone.Tasks.SmsComposeTask();
                sms.To = m.CMD;
                sms.Body = m.Keyword;
                sms.Show();
            }
            else if (m.Type == 7)
            {
                Microsoft.Phone.Tasks.PhoneCallTask call = new Microsoft.Phone.Tasks.PhoneCallTask();
                call.DisplayName = m.Title;
                call.PhoneNumber = m.CMD;
                call.Show();
            }
        }
    }
}