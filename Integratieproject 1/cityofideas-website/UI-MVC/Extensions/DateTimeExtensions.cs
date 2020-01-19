using System;

namespace COI.UI_MVC.Extensions
{
    public static class DateTimeExtensions
    {
        public static string FormatDateTime(this DateTime dateTime)
        {
            return dateTime.ToString("dd/MM/yyyy HH:mm");
        }

        public static string FormatDate(this DateTime dateTime)
        {
            return dateTime.ToString("dd/MM/yyyy");
        }

        public static string FormatParasableDate(this DateTime dateTime)
        {
            return dateTime.ToString("MM dd yyyy HH:mm:ss");
        }
    }
}