using Microsoft.AspNetCore.Http;
using System.Threading.Tasks;

namespace COI.UI_MVC.Services
{
    public interface IFileUploader
    {
        Task<string> UploadFile(string filename, string folder, IFormFile file);
        Task DeleteFile(string filename, string folder);
    }
}
