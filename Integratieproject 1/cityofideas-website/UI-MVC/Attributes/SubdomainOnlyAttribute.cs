using COI.UI_MVC.Util;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Mvc.Filters;
using System.Diagnostics;


namespace COI.UI_MVC.Attributes
{
    public class SubdomainOnlyAttribute : ActionFilterAttribute
    {
        public override void OnActionExecuting(ActionExecutingContext context)
        {
            var requestHostname = context.HttpContext.Request.Host.ToString();

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
                        return;
                    } else
                    {
                        Debug.WriteLine("No subdomain detected");
                        context.Result = new RedirectToActionResult("Index", "Home", null);
                        return;
                    }
                }
            }
        }
    }
}
