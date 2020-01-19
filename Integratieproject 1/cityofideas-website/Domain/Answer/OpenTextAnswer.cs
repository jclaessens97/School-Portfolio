using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Answer
{
    public class OpenTextAnswer : Answer
    {
        public string Value { get; set; }

        public override object GetValue()
        {
            return Value;
        }
    }
}