import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.Icon;
import javax.swing.JLabel;

public class VerticalJLabel extends JLabel {

	public VerticalJLabel(String arg0) {
		super(arg0);
	}
	
	@Override
	public void paintComponent(Graphics g) {
		Graphics2D gx = (Graphics2D) g;
		gx.rotate(-Math.PI/2, getWidth() / 2, getHeight() / 2); 
		super.paintComponent(g);
	}
}
