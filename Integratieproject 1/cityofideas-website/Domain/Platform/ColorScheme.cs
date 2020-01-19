using System;

namespace COI.BL.Domain.Platform
{
    /// <summary>
    /// Holds the color scheme of a platform.
    /// Colors are represented as hexcolor strings.
    /// </summary>
    public class ColorScheme
    {
        public int ColorSchemeId { get; set; }

        public string SocialBarColor { get; set; }
        public string NavBarColor { get; set; }
        public string BannerColor { get; set; }
        public string ButtonColor { get; set; }
        public string ButtonTextColor { get; set; }
        public string TextColor { get; set; }
        public string BodyColor { get; set; }
    }
}
