using System;

namespace COI.BL.Domain.Foundation
{
    public class Location
    {
        public int LocationId { get; set; }
        public double Latitude { get; set; }
        public double Longitude { get; set; }
        
        public double ZoomLevel { get; set; }
    }
}
