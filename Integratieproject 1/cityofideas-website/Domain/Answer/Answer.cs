using System;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Answer
{
    public abstract class Answer : IComparable<Answer>
    {

        public int AnswerId { get; set; }
        public int QuestionIndex { get; set; }

        public int OrderIndex { get; set; }

        public abstract object GetValue();

        public int CompareTo(Answer other)
        {
            return OrderIndex.CompareTo(other.OrderIndex);
        }
    }
}
