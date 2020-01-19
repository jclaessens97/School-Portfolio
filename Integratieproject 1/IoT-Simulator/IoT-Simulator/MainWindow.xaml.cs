using System.Windows;
using IoT_Simulator.lib;

namespace IoT_Simulator
{
    /// <summary>
    /// Interaction logic for MainWindow.xaml
    /// </summary>
    public partial class MainWindow : Window
    {
        private MqttManager _mgr;

        public MainWindow()
        {
            InitializeComponent();
            _mgr = new MqttManager();
        }

        private void ProButton_Click(object sender, RoutedEventArgs e)
        {
            var payload = TxtIdentifier.Text + ";+";
            _mgr.SendMessage(payload);
        }

        private void ConButton_Click(object sender, RoutedEventArgs e)
        {
            var payload = TxtIdentifier.Text + ";-";
            _mgr.SendMessage(payload);
        }

        private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
        {
            _mgr.Dispose();
        }
    }
}
