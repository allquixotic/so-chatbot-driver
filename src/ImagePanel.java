/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.xml.bind.DatatypeConverter;

public class ImagePanel extends JDialog {
	private static final long serialVersionUID = 234303222393133650L;
	private static ImagePanel INSTANCE = null;

	private final ImageIcon ico = new ImageIcon();
	private final ScrollablePicture label = new ScrollablePicture(ico, 8);
	private final JScrollPane jsp = new JScrollPane(label);
	private final Dimension dim = new Dimension(1024, 768);

	private ImagePanel() throws IOException 
	{
		super(new JFrame());
		setModal(true);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		setTitle("Screenshot");
		label.setPreferredSize(dim);
		setPreferredSize(dim);
		getContentPane().setBackground(Color.white);
		getContentPane().add(jsp);
		pack();
	}

	private void display(BufferedImage img) {
		ico.setImage(img);
		label.setPreferredSize(new Dimension(ico.getIconWidth(), ico.getIconHeight()));
		label.revalidate();
		pack();
	}

	public static void displayImage(String b64data) throws IOException 
	{
		if (Driver.displayScreenshots && !GraphicsEnvironment.isHeadless()) 
		{
			byte[] bytes = DatatypeConverter.parseBase64Binary(b64data);
			ByteArrayInputStream bin = new ByteArrayInputStream(bytes);
			BufferedImage image = ImageIO.read(bin);
			if (INSTANCE == null) 
			{
				try{UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}catch(Exception ex){}
				INSTANCE = new ImagePanel();
			}
			INSTANCE.display(image);
			INSTANCE.setVisible(true);
		}
	}
}