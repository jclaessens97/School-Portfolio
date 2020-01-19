using System;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Answer
{
    public class MediaAnswer : Answer
    {
        public Media Value { get; set; }

        public override object GetValue()
        {
            return Value;
        }
    }
}
