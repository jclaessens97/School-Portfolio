using System;
using System.Collections.Generic;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Form
{
    public class Question : IComparable<Question>
    {
        public int QuestionId { get; set; }
        public FieldType FieldType { get; set; }
        public string QuestionString { get; set; }
        
        // For multiple choice questions
        public List<string> Options { get; set; }
        // For open questions
        public bool LongAnswer { get; set; }
        // For location question
        public Location Location { get; set; }
        

        public bool Required { get; set; }
        
        public int OrderIndex { get; set; }

        public int CompareTo(Question other)
        {
            return OrderIndex.CompareTo(other.OrderIndex);
        }
    }
}