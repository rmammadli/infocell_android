using System;
using System.Collections.Generic;
using System.Linq;
using System.Net;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Navigation;
using InfoCell.Classes;
using Microsoft.Phone.Controls;
using Microsoft.Phone.Shell;

namespace InfoCELLW7
{
    public partial class MakeRequestPage : PhoneApplicationPage
    {
        private bool _isLoaded;

        InfocellMenuItem menu = null;

        public MakeRequestPage()
        {
            InitializeComponent();
        }

        protected override void OnNavigatedTo(NavigationEventArgs e)
        {
            if (!_isLoaded)
            {
                _isLoaded = true;

                if (this.NavigationContext.QueryString.ContainsKey("id"))
                {
                    string id = this.NavigationContext.QueryString["id"];

                    menu = StaticDB.AllMenus.FirstOrDefault(x => x.ID == id);

                    DataContext = menu;

                    Dispatcher.BeginInvoke(() =>
                    {
                        prepareForm();
                    });
                }
            }
            base.OnNavigatedTo(e);
        }

        private void prepareForm()
        {
            if (menu.Type == 3 || menu.Type == 4 || menu.Type == 6)
            {
                for (int i = 1; i <= menu.InputCount; i++)
                {
                    TextBlock txt = new TextBlock();
                    txt.Text = menu.Labels[i - 1];
                    paramPanel.Children.Add(txt);

                    TextBox tb = new TextBox();
                    tb.Name = "tb_" + i;

                    if (menu.InputType == 1)
                    {
                        tb.Text = "9945";
                        tb.InputScope = new InputScope
                        {
                            Names =
                                {
                                    new InputScopeName(){NameValue=InputScopeNameValue.TelephoneNumber}
                                }
                        };
                    }

                    paramPanel.Children.Add(tb);
                }
            }
        }

        private void Button_Click(object sender, RoutedEventArgs e)
        {
            NavigationService.GoBack();
        }

        private async void Button_Click_1(object sender, RoutedEventArgs e)
        {
            List<string> inputValues = new List<string>();

            foreach (var c in paramPanel.Children)
            {
                if (c is TextBox)
                {
                    if ((c as TextBox).Text.Trim() == "")
                    {
                        (c as TextBox).Focus();

                        return;
                    }
                    else
                    {
                        inputValues.Add((c as TextBox).Text.Trim());
                    }
                }
            }


            if (menu.Type == 3 || menu.Type == 4)
            {
                string key = "";

                if (menu.Type == 4)
                {
                    List<string> allValues = new List<string>();

                    allValues.AddRange(inputValues);
                    allValues.Add((list1.SelectedItem as DataListItem).Value);
                    allValues.Add((list2.SelectedItem as DataListItem).Value);

                    key = string.Format(menu.Keyword, allValues.ToArray());
                }
                else
                {
                    key = string.Format(menu.Keyword, inputValues.ToArray());
                }

                string url = string.Format("msisdn={0}&short={1}&text={2}", StaticDB.PhoneNumber, menu.CMD, key);

                string resp = await HttpHelper.DownloadString("http://tools.dsc.az/infoapp/content.asp", url, null);
                string s = resp.Substring(0, 1);

                if (s == "0")
                {
                    MessageBox.Show(AppResources.str_requestsent);

                    NavigationService.GoBack();
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
            else if (menu.Type == 6)
            {
                Microsoft.Phone.Tasks.SmsComposeTask sms = new Microsoft.Phone.Tasks.SmsComposeTask();
                sms.To = menu.CMD;
                sms.Body = inputValues[0];
                sms.Show();
            }


        }

        private void TextBlock_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
        {
            NavigationService.GoBack();
        }
    }
}