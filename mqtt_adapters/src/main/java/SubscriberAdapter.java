import org.eclipse.paho.client.mqttv3.*;

import java.io.IOException;
import java.util.logging.Logger;

public class SubscriberAdapter implements MqttCallback{

    private static final Logger LOGGER = Logger.getLogger(SubscriberAdapter.class.getName());

    private final SubscriberListener subscriberListener;

    public SubscriberAdapter(SubscriberListener subscriberListener, String serverURL, String clientId, String topic) throws MqttException {
        this.subscriberListener = subscriberListener;

        MqttConnectOptions connectOptions = new MqttConnectOptions();
        connectOptions.setCleanSession(true);
        connectOptions.setKeepAliveInterval(1000);

        MqttClient client = new MqttClient(serverURL, clientId);
        client.setCallback(this);
        client.connect(connectOptions);
        client.subscribe(topic);
    }

    @Override
    public void connectionLost(Throwable throwable) {
        LOGGER.info("Connection is lost!");
    }

    @Override
    public void messageArrived(String s, MqttMessage mqttMessage) throws IOException {
        subscriberListener.messageArrived(mqttMessage);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
        LOGGER.info("Delivery is complete!");
    }
}
