using COI.DAL.EF;

namespace COI.DAL
{
    public class UnitOfWork
    {
        internal CityOfIdeasDbContext Ctx { get; }

        public UnitOfWork(CityOfIdeasDbContext cityOfIdeasDbContext)
        {
            Ctx = cityOfIdeasDbContext;
            Ctx.SetUnitOfWorkPresent(true);
        }
        
        public void CommitChanges()
        {
            Ctx.CommitChanges();
        }

        // only used one time where 2 dbcontexts were unavoidable -> UI.MVC/scheduler
        public void FixUnchangedEntries()
        {
            Ctx.SetEntitiesUnchanged();
        }
    }
}