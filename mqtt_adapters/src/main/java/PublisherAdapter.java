import org.eclipse.paho.client.mqttv3.*;

public class PublisherAdapter {

    private final MqttClient mqttClient;

    public PublisherAdapter(String serverURL, String clientId) throws MqttException {
        mqttClient = new MqttClient(serverURL, clientId);
        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setKeepAliveInterval(1000);
        mqttClient.connect(connectOptions);
    }

    public void publish(MqttMessage message, String topic) throws MqttException {
        message.setQos(1);
        message.setRetained(true);
        MqttTopic mqttTopic = mqttClient.getTopic(topic);
        mqttTopic.publish(message);
    }
}
