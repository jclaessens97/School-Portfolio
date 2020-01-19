using Google.Apis.Auth.OAuth2;
using Google.Cloud.Storage.V1;
using Microsoft.AspNetCore.Http;
using System.IO;
using System.Threading.Tasks;

namespace COI.UI_MVC.Services.Impl
{
    public class GoogleStorageUploader : IFileUploader
    {
        private const string BUCKET = "ui-gen";
        private readonly StorageClient _storageClient;

        public GoogleStorageUploader()
        {
            using (StreamReader r = new StreamReader("GOOGLE_CLOUD_CREDS.json"))
            {
                string json = r.ReadToEnd();
                var creds = GoogleCredential.FromJson(json);
                _storageClient = StorageClient.Create(creds);
            }
        }

        public async Task<string> UploadFile(string filename, string folder, IFormFile file)
        {
            var fileAcl = PredefinedObjectAcl.PublicRead;

            var fileObj = await _storageClient.UploadObjectAsync(
                bucket: BUCKET,
                objectName: $"{folder}/{filename}",
                contentType: file.ContentType,
                source: file.OpenReadStream(),
                options: new UploadObjectOptions { PredefinedAcl = fileAcl }
            );

            return fileObj.MediaLink;
        }

        public async Task DeleteFile(string filename, string folder)
        {
            try
            {
                var objNameToDelete = Util.Util.GenerateDataStoreObjectName(filename);
                await _storageClient.DeleteObjectAsync(BUCKET, $"{folder}/{objNameToDelete}");
            }
            catch (Google.GoogleApiException ex)
            {
                // ignore 404, the image is already deleted or never existed
                if (ex.Error.Code != 404)
                    throw ex;
            }
        }
    }
}
