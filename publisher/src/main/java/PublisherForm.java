import org.eclipse.paho.client.mqttv3.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PublisherForm extends JFrame {

    private static final Logger LOGGER = Logger.getLogger(PublisherForm.class.getName());

    private final JFileChooser fileChooser;
    private final PublisherAdapter publisherAdapter;
    private BufferedImage bufferedImage;
    private String fileExtension = "";

    private JPanel mainPanel;
    private JLabel label;
    private JButton choose;
    private JButton send;

    public static void main(String[] args) throws MqttException {
        new PublisherForm();
    }

    private PublisherForm() throws MqttException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setTitle("Publisher");
        setContentPane(mainPanel);
        setVisible(true);
        setResizable(false);

        fileChooser = new JFileChooser();
        publisherAdapter = new PublisherAdapter("tcp://127.0.0.1:1883", "publisher_01");

        choose.addActionListener(e -> {
            fileChooser.showDialog(null, "Open image");
            File file = fileChooser.getSelectedFile();
            int index = file.getName().lastIndexOf('.');
            fileExtension = file.getName().substring(index + 1);

            if("png".equals(fileExtension) || "jpg".equals(fileExtension) || "jpeg".equals(fileExtension)) {
                try {
                    bufferedImage = ImageIO.read(file);
                    label.setIcon(new ImageIcon(bufferedImage));
                    setSize(bufferedImage.getWidth(), bufferedImage.getHeight() + 70);
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to file reading!", ex);
                }
            } else {
                fileExtension = "";
            }
        });

        send.addActionListener(e -> {
            if(!"".equals(fileExtension)) {
                try {
                    MqttMessage message = new MqttMessage(getImageBytes(bufferedImage));
                    publisherAdapter.publish(message, "test/topic");
                } catch (MqttException | IOException ex) {
                    LOGGER.log(Level.SEVERE, "Failed to publish message!", ex);
                }
            }
        });
    }

    private byte[] getImageBytes(BufferedImage image) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            ImageIO.write(image, fileExtension, baos);
            baos.flush();
            return baos.toByteArray();
        }
    }
}
