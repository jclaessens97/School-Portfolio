using COI.BL.Domain.Foundation;
using System;
using System.Collections.Generic;

namespace COI.UI_MVC.Models
{
    public class FormQuestionViewModel
    {
        public FieldType FieldType { get; set; }
        public string Question { get; set; }
        public List<String> Options { get; set; }
        public string OpenAnswer { get; set; }
        public bool[] MultipleChoiceAnswer { get; set; }
        public int? SingleChoiceAnswer { get; set; }

        public bool Required { get; set; }
        public bool LongAnswer { get; set; }
    }
}