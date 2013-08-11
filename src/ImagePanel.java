import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.xml.bind.DatatypeConverter;

public class ImagePanel extends JPanel{

    private final BufferedImage image;
    private final JLabel label = new JLabel();

    public ImagePanel(String data) throws IOException
    {       
          byte[] bytes = DatatypeConverter.parseBase64Binary(data);
          ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
          image = ImageIO.read(bin);
          label.setIcon(new ImageIcon(image));
          this.add("label", label);
    }
    
    public static void displayImage(String b64data) throws IOException
    {
    	if(Driver.displayScreenshots && !GraphicsEnvironment.isHeadless())
    	{
	    	ImagePanel ip = new ImagePanel(b64data);
	    	JDialog jd = new JDialog(new JFrame());
	    	jd.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    	jd.setTitle("Screenshot of error");
	    	jd.setContentPane(ip);
	    	jd.pack();
	    	jd.setModal(true);
	    	jd.setVisible(true);
    	}
    }
}