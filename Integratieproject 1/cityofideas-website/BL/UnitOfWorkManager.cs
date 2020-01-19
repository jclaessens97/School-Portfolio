using COI.DAL;

namespace COI.BL
{
    public class UnitOfWorkManager
    {
        internal UnitOfWork UnitOfWork { get; }
        
        public UnitOfWorkManager(UnitOfWork unitOfWork)
        {
            UnitOfWork = unitOfWork;
        }

        public void Save()
        {
            UnitOfWork.CommitChanges();
        }

        // only used one time where 2 dbcontexts were unavoidable -> UI.MVC/scheduler
        public void FixUnchangedEntries() 
        {
            UnitOfWork.FixUnchangedEntries();
        }
    }
}