using System;
using System.Diagnostics;
using System.Text;

namespace COI.UI_MVC.Util
{
    internal static class Util
    {
        internal static string GenerateDataStoreObjectName(string fileName)
        {
            return $"{DateTimeOffset.UtcNow.ToUnixTimeMilliseconds()}_{fileName}";
        }

        internal static string BinaryToString(string binaryString)
        {
            var trimmedBinary = binaryString.TrimEnd('\n');

            byte[] byteArray = new byte[trimmedBinary.Length / 8];

            for (var i = 0; i < trimmedBinary.Length; i += 8)
            {
                byteArray[i / 8] = Convert.ToByte(trimmedBinary.Substring(i, 8), 2);
            }

            return Encoding.ASCII.GetString(byteArray);
        }

        internal static string GetSubdomain(string requestHostname)
        {
            foreach (var hostname in Constants.hostnames)
            {
                if (requestHostname.Contains(hostname))
                {
                    var trimmed = requestHostname.Replace(hostname, "");

                    if (trimmed.Contains("www."))
                    {
                        trimmed = trimmed.Replace("www.", "");
                    }

                    if (trimmed.Length > 1)
                    {
                        Debug.WriteLine("Subdomain detected");
                        return trimmed.Replace(".", "");
                    }
                }
            }

            return null;
        }
    }
}
