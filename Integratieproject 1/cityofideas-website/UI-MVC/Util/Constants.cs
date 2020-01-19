namespace COI.UI_MVC.Util
{
    public static class Constants
    {
        // General
        public static readonly string[] hostnames = new string[] {
            "10.0.2.2:5001",
            "127.0.0.1:5001",
            "localhost:5001",
            "cityofidea.be"
        };

        // Regexpressions
        internal const string NoSpaceRegex = @"^\S*$";
        internal const string ColorHexRegex = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";

        // Autocomplete minimum length
        internal const int AutoCompleteMinLength = 3;
    }
}
