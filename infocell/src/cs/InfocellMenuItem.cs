using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Text;

namespace InfoCell.Classes
{
    public class InfocellMenuItem
    {
        public InfocellMenuItem Parent { get; set; }

        public string Title { get; set; }
        public int Type { get; set; }
        public string CMD { get; set; }
        public string Keyword { get; set; }
        public int InputCount { get; set; }
        public int InputType { get; set; }

        public ObservableCollection<InfocellMenuItem> Submenus { get; set; }

        public List<string> Labels { get; set; }

        public List<DataListItem> List1 { get; set; }
        public List<DataListItem> List2 { get; set; }

        public string List1Label { get; set; }
        public string List2Label { get; set; }

        public string ID { get; set; } 

        public InfocellMenuItem()
        {
            Type = 1; //submenu    
            Submenus = new ObservableCollection<InfocellMenuItem>();
            Labels = new List<string>();
            List1 = new List<DataListItem>();
            List2 = new List<DataListItem>();
        }

        public InfocellMenuItem(System.Xml.Linq.XElement xElement, InfocellMenuItem parent, List<InfocellMenuItem> AllMenus)
            :this()
        {
            AllMenus.Add(this);

            Parent=parent;
            
            ID = Guid.NewGuid().ToString();

            if (xElement.Attribute("title") == null)
            {
                //root menu

                foreach (var item in xElement.Elements())
                {
                    InfocellMenuItem menu = new InfocellMenuItem(item, this, AllMenus);

                    Submenus.Add(menu);
                }
            }
            else
            {
                Title = xElement.Attribute("title").Value;
                Type = int.Parse(xElement.Attribute("type").Value);

                Keyword = (xElement.Attribute("keyword") != null) ? xElement.Attribute("keyword").Value : "";
                CMD = (xElement.Attribute("cmd") != null) ? xElement.Attribute("cmd").Value : "";

                if (Type == 1)
                {
                    //submenu
                    foreach (var item in xElement.Elements())
                    {
                        InfocellMenuItem menu = new InfocellMenuItem(item, this, AllMenus);

                        Submenus.Add(menu);
                    }
                }
                else if (Type == 2)
                {
                    //direct web call
                    //nothing to load, already got everything
                }
                else if (Type == 3)
                {
                    // 3 - web request with text input from user (inputCount - count of text inputs) / inputType (0-text, 1-phone number) , input1Label,input2Label.. - labels for text inputs

                    InputCount = int.Parse(xElement.Attribute("inputCount").Value);
                    InputType = (xElement.Attribute("inputType")!=null) ? int.Parse(xElement.Attribute("inputType").Value) : 0;

                    for (int i = 1; i <= InputCount; i++ )
                    {
                        if (xElement.Attribute(string.Format("input{0}Label", i)) != null)
                        {
                            Labels.Add(xElement.Attribute(string.Format("input{0}Label", i)).Value);
                        }
                    }

                    if (InputCount > 0 && Labels.Count == 0)
                    {
                        //if no labels provided add title as label
                        Labels.Add(Title);
                    }
                }
                else if (Type == 4)
                {
                      //4 - web request with selection from two lists, inputCount count of additional text fields, firstLabel. secondLabel - labesl for lists, input1Label,input2Label.. - labels for text inputs

                    InputCount = (xElement.Attribute("inputCount")!=null) ? int.Parse(xElement.Attribute("inputCount").Value) : 0;
                    InputType = (xElement.Attribute("inputType") != null) ? int.Parse(xElement.Attribute("inputType").Value) : 0;

                    List1Label = (xElement.Attribute("firstLabel") != null) ? xElement.Attribute("firstLabel").Value : "";
                    List2Label = (xElement.Attribute("secondLabel") != null) ? xElement.Attribute("secondLabel").Value : "";

                    
                    for (int i = 1; i <= InputCount; i++)
                    {
                        if (xElement.Attribute(string.Format("input{0}Label", i)) != null)
                        {
                            Labels.Add(xElement.Attribute(string.Format("input{0}Label", i)).Value);
                        }
                    }

                    if (InputCount > 0 && Labels.Count == 0)
                    {
                        //if no labels provided add title as label
                        Labels.Add(Title);
                    }

                    foreach (var item in xElement.Element("firstList").Elements())
                    {
                        DataListItem dt = new DataListItem();
                        dt.Title = item.Attribute("title").Value;
                        dt.Value = item.Attribute("value").Value;

                        List1.Add(dt);
                    }

                    foreach (var item in xElement.Element("secondList").Elements())
                    {
                        DataListItem dt = new DataListItem();
                        dt.Title = item.Attribute("title").Value;
                        dt.Value = item.Attribute("value").Value;

                        List2.Add(dt);
                    }
                }
                else if (Type == 5)
                {
                    //SMS with predefined keyword
                    //nothing to read
                }
                else if (Type == 6)
                {
                    //SMS with input from user (inputCount - count of text inputs) / inputType (0-text, 1-phone number)
                    InputCount = (xElement.Attribute("inputCount") != null) ? int.Parse(xElement.Attribute("inputCount").Value) : 0;
                    InputType = (xElement.Attribute("inputType") != null) ? int.Parse(xElement.Attribute("inputType").Value) : 0;

                    for (int i = 1; i <= InputCount; i++)
                    {
                        if (xElement.Attribute(string.Format("input{0}Label", i)) != null)
                        {
                            Labels.Add(xElement.Attribute(string.Format("input{0}Label", i)).Value);
                        }
                    }

                    if (InputCount > 0 && Labels.Count == 0)
                    {
                        //if no labels provided add title as label
                        Labels.Add(Title);
                    }
                }
                else if (Type == 7)
                {
                    //call to number
                    //nothing to read
                }
            }

            
        }

        
    }
}
