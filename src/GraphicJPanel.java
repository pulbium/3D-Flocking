import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class GraphicJPanel extends JPanel implements MouseListener, MouseMotionListener {

	private static final long serialVersionUID = -1747958764628533399L;
	int mx, my, azimuth = 0, elevation = 0;
	Boid[] boids = new Boid[300];
	Vertices cube;

	class Edge {
		int a, b;

		public Edge(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	JSlider separationSlider = new JSlider(0, 20, 0);
	JSlider cohesionSlider = new JSlider(0, 20, 0);
	JSlider alignmentSlider = new JSlider(0, 20, 0);
	JSlider perceptionSlider = new JSlider(0, 500, 100);
	JSlider speedSlider = new JSlider(1, 10, 4);
	JSlider forceSlider = new JSlider(1, 10, 1);

	JButton restartBtn = new JButton("RESTART");
	JToggleButton togglePerceptionBtn = new JToggleButton("Show Perception Radiuses");

	public GraphicJPanel() {
		super();

		togglePerceptionBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (togglePerceptionBtn.isSelected())
					togglePerceptionBtn.setText("Hide Perception Radiuses");
				else
					togglePerceptionBtn.setText("Show Perception Radiuses");
			}
		});

		restartBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < boids.length; i++)
					boids[i] = new Boid(getWidth(), getHeight(), speedSlider.getValue(), forceSlider.getValue());

				perceptionSlider.setValue(100);
				// azimuth = 0;
				// elevation = 0;
			}
		});

		perceptionSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				for (Boid boid : boids)
					boid.perceptionRadius = perceptionSlider.getValue();
			}
		});

		speedSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				for (Boid boid : boids)
					boid.maxSpeed = speedSlider.getValue();
			}
		});

		forceSlider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent e) {
				for (Boid boid : boids)
					boid.maxForce = forceSlider.getValue();
			}
		});

		cohesionSlider.setMajorTickSpacing(10);
		cohesionSlider.setMinorTickSpacing(2);
		cohesionSlider.setPaintTicks(true);
		cohesionSlider.setPaintLabels(true);

		separationSlider.setMajorTickSpacing(10);
		separationSlider.setMinorTickSpacing(2);
		separationSlider.setPaintTicks(true);
		separationSlider.setPaintLabels(true);

		alignmentSlider.setMajorTickSpacing(10);
		alignmentSlider.setMinorTickSpacing(2);
		alignmentSlider.setPaintTicks(true);
		alignmentSlider.setPaintLabels(true);

		perceptionSlider.setMajorTickSpacing(100);
		perceptionSlider.setMinorTickSpacing(20);
		perceptionSlider.setPaintTicks(true);
		perceptionSlider.setPaintLabels(true);

		speedSlider.setMajorTickSpacing(3);
		speedSlider.setMinorTickSpacing(1);
		speedSlider.setPaintTicks(true);
		speedSlider.setPaintLabels(true);
		speedSlider.setOrientation(SwingConstants.VERTICAL);

		forceSlider.setMajorTickSpacing(3);
		forceSlider.setMinorTickSpacing(1);
		forceSlider.setPaintTicks(true);
		forceSlider.setPaintLabels(true);
		forceSlider.setOrientation(SwingConstants.VERTICAL);

		togglePerceptionBtn.setEnabled(false);

		addMouseListener(this);
		addMouseMotionListener(this);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setColor(Color.white);
		g2.fillRect(0, 0, getWidth(), getHeight());
		g2.setStroke(new BasicStroke(1));
		g2.translate(getWidth() / 2, getHeight() / 2);

		double theta = Math.PI * azimuth / 180.0;
		double phi = Math.PI * elevation / 180.0;
		float cosT = (float) Math.cos(theta), sinT = (float) Math.sin(theta), cosP = (float) Math.cos(phi),
				sinP = (float) Math.sin(phi);
		float cosTcosP = cosT * cosP, cosTsinP = cosT * sinP, sinTcosP = sinT * cosP, sinTsinP = sinT * sinP;

		Edge[] edges = new Edge[12];
		edges[0] = new Edge(0, 1);
		edges[1] = new Edge(0, 2);
		edges[2] = new Edge(0, 4);
		edges[3] = new Edge(1, 3);
		edges[4] = new Edge(1, 5);
		edges[5] = new Edge(2, 3);
		edges[6] = new Edge(2, 6);
		edges[7] = new Edge(3, 7);
		edges[8] = new Edge(4, 5);
		edges[9] = new Edge(4, 6);
		edges[10] = new Edge(5, 7);
		edges[11] = new Edge(6, 7);

		Point[] points = new Point[cube.vertices.length];

		for (int i = 0; i < cube.vertices.length; i++) {
			float x = cosT * cube.vertices[i][0] + sinT * cube.vertices[i][2];
			float y = sinTsinP * cube.vertices[i][0] - cosP * cube.vertices[i][1] - cosTsinP * cube.vertices[i][2];
			float z = cosTcosP * cube.vertices[i][2] - sinTcosP * cube.vertices[i][0] - sinP * cube.vertices[i][1];

			x *= getHeight() / (z + 2 * getHeight());
			y *= getHeight() / (z + 2 * getHeight());

			points[i] = new Point((int) x, (int) y);
		}

		// cube
		g2.setStroke(new BasicStroke(2));
		g2.setColor(Color.black);
		for (int j = 0; j < edges.length; j++)
			g2.drawLine(points[edges[j].a].x, points[edges[j].a].y, points[edges[j].b].x, points[edges[j].b].y);

		// X axis
		g2.setColor(Color.red);
		g2.drawLine(points[edges[0].a].x, points[edges[0].a].y, points[edges[0].b].x, points[edges[0].b].y);

		// Y axis
		g2.setColor(Color.blue);
		g2.drawLine(points[edges[3].a].x, points[edges[3].a].y, points[edges[3].b].x, points[edges[3].b].y);

		// Z axis
		g2.setColor(Color.green);
		g2.drawLine(points[edges[4].a].x, points[edges[4].a].y, points[edges[4].b].x, points[edges[4].b].y);

		// boids
		g2.setStroke(new BasicStroke(1));
		for (Boid boid : boids) {
			g2.setColor(boid.color);

			float x = cosT * (boid.position[0] - getHeight() / 2) + sinT * (boid.position[2] - getHeight() / 2);
			float y = sinTsinP * (boid.position[0] - getHeight() / 2) - cosP * (boid.position[1] - getHeight() / 2)
					- cosTsinP * (boid.position[2] - getHeight() / 2);
			float z = cosTcosP * (boid.position[2] - getHeight() / 2) - sinTcosP * (boid.position[0] - getHeight() / 2)
					- sinP * (boid.position[1] - getHeight() / 2);

			x *= getHeight() / (z + 2 * getHeight());
			y *= getHeight() / (z + 2 * getHeight());

			int drawRadius = (int) (boid.radius * getHeight() / (z + 2 * getHeight()));

			g2.drawOval((int) x - drawRadius, (int) y - drawRadius, 2 * drawRadius, 2 * drawRadius);

			// drawing circles outside the panel
			/*
			 * if((int)x + drawRadius > getWidth())
			 * g2.drawOval((int)x-drawRadius-getWidth(), (int)y-drawRadius, 2*drawRadius,
			 * 2*drawRadius);
			 * 
			 * if((int)x - drawRadius < 0) g2.drawOval((int)x-drawRadius+getWidth(),
			 * (int)y-drawRadius, 2*drawRadius, 2*drawRadius);
			 * 
			 * if((int)y + drawRadius > getHeight()) g2.drawOval((int)x-drawRadius,
			 * (int)y-drawRadius-getHeight(), 2*drawRadius, 2*drawRadius);
			 * 
			 * if((int)y - drawRadius < 0) g2.drawOval((int)x-drawRadius,
			 * (int)y-drawRadius+getHeight(), 2*drawRadius, 2*drawRadius);
			 * 
			 * //perception circles if(togglePerceptionBtn.isSelected()) {
			 * g2.setColor(Color.red); g2.drawOval((int)x-boid.perceptionRadius,
			 * (int)y-boid.perceptionRadius, 2*boid.perceptionRadius,
			 * 2*boid.perceptionRadius);
			 * 
			 * //drawing perception circles outside the panel
			 * 
			 * if((int)x + boid.perceptionRadius > getWidth())
			 * g2.drawOval((int)x-boid.perceptionRadius-getWidth(),
			 * (int)y-boid.perceptionRadius, 2*boid.perceptionRadius,
			 * 2*boid.perceptionRadius);
			 * 
			 * if((int)x - boid.perceptionRadius < 0)
			 * g2.drawOval((int)x-boid.perceptionRadius+getWidth(),
			 * (int)y-boid.perceptionRadius, 2*boid.perceptionRadius,
			 * 2*boid.perceptionRadius);
			 * 
			 * if((int)y + boid.perceptionRadius > getHeight())
			 * g2.drawOval((int)x-boid.perceptionRadius,
			 * (int)y-boid.perceptionRadius-getHeight(), 2*boid.perceptionRadius,
			 * 2*boid.perceptionRadius);
			 * 
			 * if((int)y - boid.perceptionRadius < 0)
			 * g2.drawOval((int)x-boid.perceptionRadius,
			 * (int)y-boid.perceptionRadius+getHeight(), 2*boid.perceptionRadius,
			 * 2*boid.perceptionRadius); }
			 */
			// let the boids flock the panel

			boid.flock(boids, this, cohesionSlider.getValue(), separationSlider.getValue(), alignmentSlider.getValue());

			boid.update(this);
		}
		try {
			Thread.sleep(10);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		repaint();

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		int new_mx = e.getX();
		int new_my = e.getY();

		azimuth -= new_mx - mx;
		elevation += new_my - my;

		mx = new_mx;
		my = new_my;

		repaint();
		e.consume();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
		e.consume();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}
}
