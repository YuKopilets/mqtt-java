import org.eclipse.paho.client.mqttv3.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SubscriberForm extends JFrame implements ActionListener, SubscriberListener {

    private static final Logger LOGGER = Logger.getLogger(SubscriberForm.class.getName());

    private final JFileChooser directChooser;
    private ImageIcon activeImage = null;
    private String imageExtension = "";

    private JLabel label;
    private JPanel mainPanel;
    private JButton save;

    public static void main(String[] args) throws MqttException {
        new SubscriberForm();
    }

    private SubscriberForm() throws MqttException {
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(500, 400);
        setLocationRelativeTo(null);
        setTitle("Subscriber");
        setContentPane(mainPanel);
        setVisible(true);
        setResizable(false);

        directChooser = new JFileChooser();
        directChooser.setDialogTitle("Save image");
        save.addActionListener(this::actionPerformed);
        new SubscriberAdapter(this, "tcp://127.0.0.1:1883", "subscriber_01", "test/topic");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(imageExtension.equals("") || activeImage == null) {
            return;
        }

        BufferedImage bufferedImage = toBufferedImage(activeImage.getImage());
        FileNameExtensionFilter filter = new FileNameExtensionFilter("*." + imageExtension,"*.*");

        try {
            directChooser.setFileFilter(filter);
            if (directChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                String path = directChooser.getSelectedFile() + "." + imageExtension;
                File file = new File(path);
                ImageIO.write(bufferedImage, imageExtension, file);
            }
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "File write exception!", ex);
        }
    }

    private BufferedImage toBufferedImage(Image image){
        int width = image.getWidth(null);
        int height = image.getHeight(null);
        BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics g = bufferedImage.getGraphics();
        g.drawImage(image, 0,0, null);
        g.dispose();

        return bufferedImage;
    }

    @Override
    public void messageArrived(MqttMessage mqttMessage) throws IOException {
        activeImage = new ImageIcon(mqttMessage.getPayload());
        label.setIcon(activeImage);
        setSize(activeImage.getIconWidth(), activeImage.getIconHeight() + 70);

        ByteArrayInputStream bais = new ByteArrayInputStream(mqttMessage.getPayload());
        imageExtension = URLConnection.guessContentTypeFromStream(bais);

        int index = 0;
        while(imageExtension.charAt(index) != '/') {
            index++;
        }
        imageExtension = imageExtension.substring(index + 1);
        String info = "Image extension: " + imageExtension;
        LOGGER.info(info);
    }
}
