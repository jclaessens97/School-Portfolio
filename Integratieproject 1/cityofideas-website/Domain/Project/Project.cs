using System;
using System.Collections.Generic;
using COI.BL.Domain.Foundation;

namespace COI.BL.Domain.Project
{
    public class Project
    {
        public int ProjectId { get; set; }
        public string Title { get; set; }
        public string Goal { get; set; }
        public Media Logo { get; set; }
        public int CurrentPhaseNumber { get; set; }

        public Phase CurrentPhase
        {
            get
            {
                if (Phases != null && Phases.Count >= 1)
                {
                    return Phases[CurrentPhaseNumber - 1];
                }

                return null;
            }
        } 
        public DateTime StartDate { get; set; }
        public DateTime EndDate { get; set; }

        public Platform.Platform Platform { get; set; }
        public List<Phase> Phases { get; set; }
        public List<ProjectModerator> Moderators { get; set; }

        public Project()
        {
            Moderators = new List<ProjectModerator>();
            Phases = new List<Phase>();
        }
    }
}
