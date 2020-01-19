using COI.UI_MVC.Scheduler.Tasks;
using COI.UI_MVC.Util;
using Microsoft.Extensions.Configuration;
using Microsoft.Extensions.DependencyInjection;
using MQTTnet;
using MQTTnet.Client;
using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Text;
using UI_MVC.Scheduler.Scheduling;
using static COI.UI_MVC.Util.Util;

namespace COI.UI_MVC.Services.MQTT
{
    public sealed class MqttBroker
    {
        private const string TOPIC_PREFIX = "City of Ideas - ";

        private readonly IServiceProvider _serviceProvider;
        private readonly IConfiguration _configuration;
        private IMqttClient _client;

        public MqttBroker(IConfiguration configuration, IServiceProvider serviceProvider)
        {
            _configuration = configuration;
            _serviceProvider = serviceProvider;

            Initialize();
        }

        private async void Initialize()
        {
            var factory = new MqttFactory();
            _client = factory.CreateMqttClient();

            string connectionString = _configuration["MQTT:ConnectionString"];
            int port = int.Parse(_configuration["MQTT:Port"]);
            string username = _configuration["MQTT:Username"];
            string password = _configuration["MQTT:Password"];

            // See: https://github.com/chkr1011/MQTTnet/wiki/Client
            var options = new MqttClientOptionsBuilder()
                .WithClientId(Guid.NewGuid().ToString())
                .WithTcpServer(connectionString, port)
                .WithCredentials(username, password)
                //.WithTls()
                .WithCleanSession()
                .Build();

            _client.Connected += Client_Connected;
            _client.ApplicationMessageReceived += Client_ApplicationMessageReceived;

            await _client.ConnectAsync(options);
        }

        public async void Terminate()
        {
            if (_client.IsConnected)
            {
                await _client.DisconnectAsync();
                _client = null;
            }
        }

        #region Events
        private void Client_Connected(object sender, EventArgs e)
        {
            SubscribeToTopic("Vote");
        }

        private void Client_ApplicationMessageReceived(object sender, MqttApplicationMessageReceivedEventArgs e)
        {
            string encryptedBinary = Encoding.ASCII.GetString(e.ApplicationMessage.Payload);
            string encryptedPayload = BinaryToString(encryptedBinary);
            string payload = Rot47.Rotate(encryptedPayload);
            payload = payload.Replace("\0", ""); // remove padding 0's

            Debug.WriteLine("### RECEIVED APPLICATION MESSAGE ###");
            Debug.WriteLine($"+ Topic = {e.ApplicationMessage.Topic}");
            Debug.WriteLine($"+ Binary Payload = {encryptedBinary}");
            Debug.WriteLine($"+ Encrypted Payload = {encryptedPayload}");
            Debug.WriteLine($"+ Decrypted Payload = {payload}");
            Debug.WriteLine($"+ QoS = {e.ApplicationMessage.QualityOfServiceLevel}");
            Debug.WriteLine($"+ Retain = {e.ApplicationMessage.Retain}");

            if (e.ApplicationMessage.Topic.Contains("Vote"))
            {
                Tuple<int, char[]> parsedPayload = ParseVote(payload);
                var iotLinkId = parsedPayload.Item1;
                var votes = parsedPayload.Item2;

                using (var scope = _serviceProvider.CreateScope())
                {
                    var voteSpreadTask =  (VoteSpreadTask)scope.ServiceProvider.GetService<IScheduledTask>();

                    Queue<char> votesQueue;

                    if (voteSpreadTask.VoteQueues.ContainsKey(iotLinkId))
                    {
                        votesQueue = voteSpreadTask.VoteQueues[iotLinkId];
                    } else
                    {
                        votesQueue = new Queue<char>();
                    }

                    foreach (var vote in votes)
                    {
                        votesQueue.Enqueue(vote);
                    }

                    voteSpreadTask.VoteQueues[iotLinkId] = votesQueue;
                }
            }
        }
        #endregion

        #region Helper Methods
        private async void SubscribeToTopic(string topic)
        {
            await _client.SubscribeAsync(
                new TopicFilterBuilder()
                .WithTopic(TOPIC_PREFIX + topic)
                .Build()
            );

            Debug.WriteLine("Subscribed to topic" + TOPIC_PREFIX + topic);
        }

        /// <summary>
        /// Parses the payload received from MQTT and returns a tuple with the usable values
        /// </summary>
        /// <param name="payload">Decrypted payload received from MQTT</param>
        /// <returns>
        /// Item 1: replyIdeationId
        /// Item 2: char array of votes with the delimiters removed (e.g. ++++-+-+)
        /// </returns>
        private static Tuple<int, char[]> ParseVote(string payload)
        {
            var replyId = int.Parse(payload.Substring(0, 4));

            var votes = new List<char>();
            for (var i = 4; i < payload.Length; i++)
            {
                votes.Add(payload[i]);
            }

            return Tuple.Create(replyId, votes.ToArray());
        }
        #endregion
    }
}
