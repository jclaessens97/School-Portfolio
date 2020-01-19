using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using System.IO;
using System.Threading.Tasks;

namespace COI.UI_MVC.Services.Impl
{
    public class LocalFileUploader : IFileUploader
    {
        private readonly string BASE_DIR;

        public LocalFileUploader(IHostingEnvironment env)
        {
            BASE_DIR = Path.Combine(env.WebRootPath, "uploads");
        }

        public async Task<string> UploadFile(string filename, string folder, IFormFile file)
        {
            if (file.Length > 0)
            {
                var folderPath = Path.Combine(BASE_DIR, folder);
                
                if (!Directory.Exists(folderPath))
                {
                    Directory.CreateDirectory(folderPath);
                }

                var filePath = Path.Combine(folderPath, filename);
                using (var fileStream = new FileStream(filePath, FileMode.Create))
                {
                    await file.CopyToAsync(fileStream);
                }

                var path = Path.Combine(folder, filename);
                path = path.Replace("\\", "/");
                return path;
            }

            return string.Empty;
        }

        public Task DeleteFile(string filename, string folder)
        {
            var filePath = Path.Combine(BASE_DIR, folder, filename);

            if (File.Exists(filePath))
            {
                File.Delete(filePath);
            }

            return Task.CompletedTask;
        }
    }
}
