using System;
using System.Diagnostics;

using MQTTnet;
using MQTTnet.Client;

namespace IoT_Simulator.lib
{
	internal class MqttManager : IDisposable
	{
        private const string CONNECTIONSTRING = "m24.cloudmqtt.com";
        private const int PORT = 16724;
        private const string USERNAME = "gtzxycux";
        private const string PASSWORD = "Ue30VWoC6fIx";
        private const string TOPIC = "City of Ideas - Vote";

        private IMqttClient _client;

        public MqttManager()
        {
            Initialize();
        }

        private async void Initialize()
        {
            var factory = new MqttFactory();
            _client = factory.CreateMqttClient();

            // See: https://github.com/chkr1011/MQTTnet/wiki/Client
            var options = new MqttClientOptionsBuilder()
                .WithClientId(Guid.NewGuid().ToString())
                .WithTcpServer(CONNECTIONSTRING, PORT)
                .WithCredentials(USERNAME, PASSWORD)
                .WithCleanSession()
                .Build();

            _client.Connected += Client_Connected;
            _client.Disconnected += Client_Disconnected;

            await _client.ConnectAsync(options);
        }

        private async void Stop()
        {
            if (_client.IsConnected)
            {
                await _client.DisconnectAsync();
            }
        }

        public async void SendMessage(string msg)
        {
            var message = new MqttApplicationMessageBuilder()
                .WithTopic(TOPIC)
                .WithPayload(msg)
                .WithExactlyOnceQoS()
                .WithRetainFlag()
                .Build();

            await _client.PublishAsync(message);
        }

        private void Client_Connected(object sender, EventArgs e)
        {
            Debug.WriteLine("Client connected");
        }

        private void Client_Disconnected(object sender, EventArgs e)
        {
            Debug.WriteLine("Client disconnected");
        }

        public void Dispose()
        {
            Stop();
        }
    }
}
