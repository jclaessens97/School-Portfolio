using System;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Answer
{
    public class LocationAnswer : Answer
    {
        public Location Value { get; set; }
        
        public override object GetValue()
        {
            return Value;
        }
    }
}