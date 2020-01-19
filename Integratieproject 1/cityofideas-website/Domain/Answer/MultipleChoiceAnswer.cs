using System;
using System.Collections.Generic;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Answer
{
    public class MultipleChoiceAnswer : Answer
    {
        public List<bool> SelectedChoices { get; set; }

        public override object GetValue()
        {
            return SelectedChoices;
        }
    }
}
