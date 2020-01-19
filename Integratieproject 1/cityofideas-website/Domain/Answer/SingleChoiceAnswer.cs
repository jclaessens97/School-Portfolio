using System;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Answer
{
    public class SingleChoiceAnswer : Answer
    {
        public int SelectedChoice { get; set; }

        public override object GetValue()
        {
            return SelectedChoice;
        }
    }
}
