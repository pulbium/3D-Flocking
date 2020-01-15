import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class BoidFrame extends JFrame {

	private static final long serialVersionUID = 8772537229529587421L;
	int r = 10;
	JPanel sliderPanel = new JPanel();
	JPanel speedNForcePanel = new JPanel();
	GraphicJPanel graphicPanel = new GraphicJPanel();
	
	public BoidFrame() {
		
		setLayout(new BorderLayout());
		setSize(1000, 1000);
		setResizable(false);
		graphicPanel.cube = new Vertices(3, getHeight()*9/20);
		
		sliderPanel.setLayout(new GridLayout(2,5));
		
		sliderPanel.add(new JLabel("Cohesion Multiplier (x100)"));
		sliderPanel.add(new JLabel("Separation Multiplier (x10)"));
		sliderPanel.add(new JLabel("Alignment Multiplier (x10)"));
		sliderPanel.add(new JLabel("Perception Radius"));
		sliderPanel.add(graphicPanel.restartBtn);
		
		sliderPanel.add(graphicPanel.cohesionSlider);
		sliderPanel.add(graphicPanel.separationSlider);
		sliderPanel.add(graphicPanel.alignmentSlider);
		sliderPanel.add(graphicPanel.perceptionSlider);
		sliderPanel.add(graphicPanel.togglePerceptionBtn);
		
		speedNForcePanel.setLayout(new GridLayout(2,2));
	
		VerticalJLabel speedLabel = new VerticalJLabel("Max speed");
		speedNForcePanel.add(speedLabel);
		speedNForcePanel.add(graphicPanel.speedSlider);
		
		JLabel forceLabel = new VerticalJLabel("Max force");
		speedNForcePanel.add(forceLabel);
		speedNForcePanel.add(graphicPanel.forceSlider);
		
		add(sliderPanel, BorderLayout.NORTH);
		add(graphicPanel, BorderLayout.CENTER);
		add(speedNForcePanel, BorderLayout.WEST);
		setVisible(true);
		for(int i = 0; i < graphicPanel.boids.length; i++) 
			graphicPanel.boids[i] = new Boid(graphicPanel.getWidth(),
					graphicPanel.getHeight(), 
					graphicPanel.speedSlider.getValue(),
					graphicPanel.forceSlider.getValue());
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);	
	}


	public static void main(String[] args) {
		new BoidFrame();
	}
}