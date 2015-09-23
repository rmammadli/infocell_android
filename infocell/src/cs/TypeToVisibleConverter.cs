using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Data;

namespace InfoCell.Classes
{
    public class TypeToVisibleConverter:IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            int intValue = (int)value;

            if (intValue == 1)
            {
                return "/Assets/appbar.chevron.right.png";
            }
            else if (intValue == 5 || intValue==6)
            {
                return "/Assets/appbar.message.send.png";
            }
            else if (intValue == 7)
            {
                return "/Assets/appbar.phone.png";
            }

            return "/Assets/appbar.message.png";
        }

        public object ConvertBack(object value, Type targetType, object parameter, System.Globalization.CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
