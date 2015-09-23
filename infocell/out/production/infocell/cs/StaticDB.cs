using System;
using System.Collections.Generic;
using System.IO;
using System.IO.IsolatedStorage;
using System.Linq;
using System.Text;
using System.Xml.Linq;

namespace InfoCell.Classes
{
    public static class StaticDB
    {
        public static InfocellMenuItem mainMenu = null;

        public static string PhoneNumber = "";
        public static string Pin = "";

        public static List<InfocellMenuItem> AllMenus = new List<InfocellMenuItem>();
        public static bool IsRegistered=false;

        static StaticDB()
        {
            loadSettings();
            loadMenu();
        }

        private static void loadSettings()
        {
            if (IsolatedStorageSettings.ApplicationSettings.Contains("phone"))
            {
                PhoneNumber = (string)IsolatedStorageSettings.ApplicationSettings["phone"];
            }

            if (IsolatedStorageSettings.ApplicationSettings.Contains("pin"))
            {
                Pin = (string)IsolatedStorageSettings.ApplicationSettings["pin"];
            }

            if (IsolatedStorageSettings.ApplicationSettings.Contains("IsRegistered"))
            {
                IsRegistered = (bool)IsolatedStorageSettings.ApplicationSettings["IsRegistered"];
            }
        }

        public static void SaveSettings()
        {
            IsolatedStorageSettings.ApplicationSettings["pin"] = Pin;
            IsolatedStorageSettings.ApplicationSettings["phone"] = PhoneNumber;
            IsolatedStorageSettings.ApplicationSettings["IsRegistered"] = IsRegistered;

            IsolatedStorageSettings.ApplicationSettings.Save();

        }

        private static void loadMenu()
        {
            string xml;

            using (StreamReader sr = File.OpenText(@"data\menu.xml"))
            {
                xml = sr.ReadToEnd();
            }

            XDocument xd = XDocument.Parse(xml);


            mainMenu = new InfocellMenuItem(xd.Root, null, AllMenus);
            
        }
    }
}
