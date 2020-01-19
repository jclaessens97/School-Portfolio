using System;
using System.Collections.Generic;

namespace COI.BL.Domain.Project
{
    public class Phase : IComparable<Phase>
    {
        public int PhaseId { get; set; }
        public int Number { get; set; }
        public string Title { get; set; }
        public string Description { get; set; }

        public List<Ideation.Ideation> Ideations { get; set; }
        public List<Form.Form> Forms { get; set; }

        public Phase()
        {
            Ideations = new List<Ideation.Ideation>();
            Forms = new List<Form.Form>();
        }

        public int CompareTo(Phase other)
        {
            return Number.CompareTo(other.Number);
        }
    }
}
