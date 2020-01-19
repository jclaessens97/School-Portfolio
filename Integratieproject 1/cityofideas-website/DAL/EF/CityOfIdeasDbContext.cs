using COI.BL.Domain.Activity;
using COI.BL.Domain.Answer;
using COI.BL.Domain.Form;
using COI.BL.Domain.Foundation;
using COI.BL.Domain.Ideation;
using COI.BL.Domain.Platform;
using COI.BL.Domain.Project;
using COI.BL.Domain.User;
using Microsoft.AspNetCore.Identity;
using Microsoft.AspNetCore.Identity.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore;
using Microsoft.EntityFrameworkCore.ChangeTracking;
using Microsoft.EntityFrameworkCore.Storage.ValueConversion;
using Microsoft.Extensions.Logging;
using Microsoft.Extensions.Logging.Debug; // NuGet-package!
using System;
using System.Collections.Generic;
using System.Linq;


namespace COI.DAL.EF
{
    public class CityOfIdeasDbContext : IdentityDbContext<User>
    {
        public DbSet<Platform> Platforms { get; set; }
        public DbSet<Project> Projects { get; set; }
        public DbSet<Phase> Phases { get; set; }
        public DbSet<Ideation> Ideations { get; set; }
        public DbSet<IdeationReply> IdeationReplies { get; set; }
        public DbSet<IdeationReport> IdeationReports { get; set; }
        public DbSet<Comment> Comments { get; set; }
        public DbSet<Report> Reports { get; set; }
        public DbSet<Vote> Votes { get; set; }
        public DbSet<Form> Forms { get; set; }
        public DbSet<FormReply> FormReplies { get; set; }
        public DbSet<Person> Persons { get; set; }
        public DbSet<Organisation> Organisations { get; set; }
        public DbSet<VerifyRequest> VerifyRequests { get; set; }
        public DbSet<PlatformRequest> PlatformRequests { get; set; }
        public DbSet<Media> MediaFiles { get; set; }

        // Antwoorden
        public DbSet<Answer> Answers { get; set; }
        public DbSet<OpenTextAnswer> TextAnswers { get; set; }
        public DbSet<LocationAnswer> LocationAnswers { get; set; }
        public DbSet<MediaAnswer> ImageAnswers { get; set; }
        public DbSet<MultipleChoiceAnswer> MultipleChoiceAnswers { get; set; }
        public DbSet<SingleChoiceAnswer> SingleChoiceAnswers { get; set; }

        // IoT
        public DbSet<IotLink> IotLinks { get; set; }

        // Activity Feed
        public DbSet<Activity> Activities { get; set; }

        private bool DelaySave { get; set; }
        
        //Wordt gebruikt bij DI
        public CityOfIdeasDbContext(DbContextOptions<CityOfIdeasDbContext> options) 
            : base (options)
        {
            InitializeDb();
        }
        
        internal CityOfIdeasDbContext(bool isUnitOfWorkPresent)
        {
            SetUnitOfWorkPresent(isUnitOfWorkPresent);
            InitializeDb();
        } 

        private void InitializeDb()
        {
            CityOfIdeasDbInitializer.Initialize(this, dropCreateDatabase: false);
        }

        public void SetUnitOfWorkPresent(bool isUnitOfWorkPresent = true)
        {
            DelaySave = isUnitOfWorkPresent;
        }

        protected override void OnConfiguring(DbContextOptionsBuilder optionsBuilder)
        {
            // configure logging-information
            /*optionsBuilder.UseLoggerFactory(new LoggerFactory(
                new[] { new DebugLoggerProvider(
                    (category, level) => category == DbLoggerCategory.Database.Command.Name
                                         && level == LogLevel.Information
                )}
            ));*/
        }

        protected override void OnModelCreating(ModelBuilder builder)
        {
            // USER is een reserved keyword in SQL Server -> tabelnaam veranderen dus
            builder.Entity<User>().ToTable("tblUser");

            #region Converters
            // Converts a list of <type> to a string concatenated with ';' to store in the database.
            // List<string> => list<bool> && vice versa
            var splitBoolConverter = new ValueConverter<List<bool>, string>(
                v => string.Join(";", v),
                v => new List<bool>(
                    Array.ConvertAll(
                        v.Split(new[] { ';' }),
                        s => bool.Parse(s)
                    )
                )
            );

            // string => list<string> && vice versa
            var splitStringConverter = new ValueConverter<List<string>, string>(
                v => string.Join(";", v),
                v => new List<string>(
                    v.Split(new[] { ';' })
                )
            );
            #endregion

            #region Enable Splitconverters
            // Activates the splitStringConverter instantiated before
            builder
                .Entity<Question>()
                .Property(nameof(Question.Options))
                .HasConversion(splitStringConverter);

            // Activates the splitBoolConverter instantiated before
            builder
                .Entity<MultipleChoiceAnswer>()
                .Property(nameof(MultipleChoiceAnswer.SelectedChoices))
                .HasConversion(splitBoolConverter);
            #endregion

            #region Delete Behavior
            builder
                .Entity<Comment>()
                .HasOne(l => l.User)
                .WithMany().OnDelete(DeleteBehavior.SetNull);

            builder
                .Entity<IdeationReply>()
                .HasOne(l => l.User)
                .WithMany().OnDelete(DeleteBehavior.SetNull);

            builder
                .Entity<Report>()
                .HasOne(l => l.User)
                .WithMany().OnDelete(DeleteBehavior.SetNull);

            builder
                .Entity<Vote>()
                .HasOne(l => l.User)
                .WithMany().OnDelete(DeleteBehavior.SetNull);

            builder
                .Entity<FormReply>()
                .HasOne(r => r.Form)
                .WithMany(f => f.Replies).OnDelete(DeleteBehavior.SetNull);
            #endregion

            base.OnModelCreating(builder);
            builder.Ignore <IdentityUserRole<string>>();
        }

        public override int SaveChanges()
        {
            if (DelaySave)
            {
                return -1;
            }

            return base.SaveChanges();
        } 

        internal int CommitChanges()
        {
            if (DelaySave)
            {
                int infectedRecords = base.SaveChanges();       
                return infectedRecords;
            }
            
            throw new InvalidOperationException("No UnitOfWork present, use SaveChanges instead");
        }


        /// <summary>
        /// only used one time where 2 dbcontexts were unavoidable -> UI.MVC/scheduler
        /// <see cref="Unit Of Work example project"/>
        /// </summary>
        // Reference: 
        public void SetEntitiesUnchanged()
        {
            foreach (EntityEntry e in this.ChangeTracker.Entries().ToList())
            {
                if (e.State == EntityState.Added && e.IsKeySet)
                {
                    //'temporary values'? => e.IsKeySet (=> PK-waarde != default(Type)) is altijd 'true', 
                    // want gebruikt 'temporary values' (gelijk aan +/-Type.MinValue)
                    foreach (PropertyEntry p in e.Properties)
                    {
                        if (p.Metadata.IsKey() && !p.IsTemporary)
                        {
                            e.State = EntityState.Unchanged;
                        }
                    }
                }
            }
        }
    }
}