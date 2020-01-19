using Microsoft.Extensions.Configuration;

namespace COI.UI_MVC.Extensions
{
    public static class ConfigurationExtensions
    {
        public static string GetMqttSetting(this IConfiguration configuration, string key)
        {
            return configuration.GetSection("MQTT")[key];
        }
    }
}
