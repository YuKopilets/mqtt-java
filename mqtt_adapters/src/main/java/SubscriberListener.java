import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;

public interface SubscriberListener {
    void messageArrived(MqttMessage mqttMessage) throws IOException;
}
