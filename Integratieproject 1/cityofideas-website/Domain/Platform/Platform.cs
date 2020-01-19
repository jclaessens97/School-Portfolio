using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations.Schema;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;

namespace COI.BL.Domain.Platform
{
    public class Platform
    {
        public int PlatformId { get; set; }
        public string Name { get; set; }
        public string Tenant { get; set; }
        public Media Logo { get; set; }
        public string Description { get; set; }

        public Media Banner { get; set; }
        public ColorScheme ColorScheme { get; set; }
        public List<Project.Project> Projects { get; set; }

        [NotMapped]
        public List<Project.Project> ActiveProjects
        {
            get
            {
                if (Projects != null)
                {
                    return Projects.FindAll(p => DateTime.Compare(p.EndDate, DateTime.Now) > 0);
                }

                return new List<Project.Project>();
            }
        }

        [NotMapped]
        public List<Project.Project> FinishedProjects
        {
            get
            {
                if (Projects != null)
                {
                    return Projects.FindAll(p => DateTime.Compare(p.EndDate, DateTime.Now) < 0);
                }

                return new List<Project.Project>();
            }
        }

        public override bool Equals(object obj)
        {
            var platform = obj as Platform;
            return platform != null &&
                   PlatformId == platform.PlatformId;
        }

        public override int GetHashCode()
        {
            return -1554650153 + PlatformId.GetHashCode();
        }
    }
}
