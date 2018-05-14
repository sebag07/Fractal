import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;

public class Fractal extends JFrame {

	// Default limits for the complex plane of the Fractal
	double x_min = -2.0, x_max = 2.0, y_min = -1.6, y_max = 1.6;
	
	// Fixed limits for the complex plane of Julia set
	private final double juliaXmin = -2, juliaXmax = 2, juliaYmin = -1.6, juliaYmax = 1.6;
	
	// Default iterations
	private int n = 100;
	private int iter;
	boolean orbit = false, curves = false, burning = false, quadratic = false;
	
	// JFrame for the Julia Sets that have been saved
	JFrame favouriteFrame = new JFrame("Favourite Julia");
	
	// ComboBox to store the saved Julia Sets as image objects
	String[] julias = { "Select image " };
	DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(julias);
	JComboBox combo = new JComboBox<String>(model);

	MandelbrotPanel mandelbrotPanel;

	JPanel toolsBar, coordinates;
	
	// Label which stores the MouseMoved coordinates of the Mandelbrot Complex plane
	JLabel mandelLabel = new JLabel();

	JCheckBox curvesTrap, orbitTrap, burningShip, quadraticMandelbrot;
	
	// Button to submit the new entered value and to draw the Mandelbrot accordingly
	// Restore button to draw the Mandelbrot with the default values
	JButton submit, restoreDefault;

	JTextField xMax, xMin, yMax, yMin, iteration;

	// Constructor creates the frame and initializes it
	public Fractal() {
		super("Fractal");

		this.setSize(1100, 1000);

		mandelbrotPanel = new MandelbrotPanel(this);
		toolsBar = new JPanel();
		coordinates = new JPanel();

		Container pane = new Container();
		this.setContentPane(pane);
		pane.setLayout(new BorderLayout());

		this.initTools();

		pane.add(coordinates, BorderLayout.NORTH);
		pane.add(toolsBar, BorderLayout.SOUTH);
		pane.add(mandelbrotPanel, BorderLayout.CENTER);

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);
	}

	// Initializes the tools panel
	public void initTools() {
		orbitTrap = new JCheckBox("Orbit");
		burningShip = new JCheckBox("Burning Ship");
		quadraticMandelbrot = new JCheckBox("Quadratic Mb");
		curvesTrap = new JCheckBox("Curves");

		coordinates.add(mandelLabel);
		toolsBar.setLayout(new FlowLayout());

		toolsBar.add(new JLabel("X-Max"));
		xMax = new JTextField(" 2.0 ");
		toolsBar.add(xMax);

		toolsBar.add(new JLabel("X-Min"));
		xMin = new JTextField(" -2.0 ");
		toolsBar.add(xMin);

		toolsBar.add(new JLabel("Y-Max"));
		yMax = new JTextField(" 1.6 ");
		toolsBar.add(yMax);

		toolsBar.add(new JLabel("Y-Min"));
		yMin = new JTextField(" -1.6 ");
		toolsBar.add(yMin);

		toolsBar.add(new JLabel("Iter"));
		iteration = new JTextField(" 100 ");
		toolsBar.add(iteration);

		submit = new JButton("Submit");
		toolsBar.add(submit);
		submit.addActionListener(new SubmitListener(xMax, xMin, yMax, yMin, iteration));

		// Adds a new JButton restoreDefault to the toolsBar
		// If the button is clicked, it redraws the Fractal with the default values of
		// x_max, x_min, y_max, y_min, n(iterations)
		restoreDefault = new JButton("Default");
		toolsBar.add(restoreDefault);
		restoreDefault.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Fractal.this.setXmax(2.0);
				Fractal.this.setXmin(-2.0);
				Fractal.this.setYmax(1.6);
				Fractal.this.setYmin(-1.6);
				Fractal.this.setNIterations(100);
				repaint();
			}
		});

		// Adds the orbitTrap checkBox to the toolsBar
		// If the checkBox is checked, it draws the orbitTrap on the Fractal
		toolsBar.add(orbitTrap);
		orbitTrap.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					orbit = true;
					repaint();
				} else {
					orbit = false;
					repaint();
				}
			}
		});

		// Adds the curvesTrap checkBox to the toolsBar
		// If the checkBox is checked, it draws the curvesTrap on the Fractal
		toolsBar.add(curvesTrap);
		curvesTrap.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					curves = true;
					repaint();
				} else {
					curves = false;
					repaint();
				}
			}
		});

		// Adds the burningShip checkBox to the toolsBar
		// If the checkBox is checked, it draws the Burning Ship Fractal
		toolsBar.add(burningShip);
		burningShip.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					burning = true;
					repaint();
				} else {
					burning = false;
					repaint();
				}
			}
		});

		// Adds the quadraticMandelbrot checkBox to the toolsBar
		// If the checkBox is checked, it draws the Quadratic Mandelbrot Fractal
		toolsBar.add(quadraticMandelbrot);
		quadraticMandelbrot.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					quadratic = true;
					repaint();
				} else {
					quadratic = false;
					repaint();
				}
			}
		});

		// Adds the ComboBox which stores the saved Julia Sets to the toolsBar
		// When an item is selected from the ComboBox, a new JPanel with the
		// saved Julia Set is created. The panel appears in a new JFrame.
		toolsBar.add(combo);
		combo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ImageIcon image = new ImageIcon(combo.getSelectedItem().toString());
				JLabel imageLabel = new JLabel(image);
				JPanel favouritePanel = new JPanel();
				favouritePanel.add(imageLabel);
				favouriteFrame.setContentPane(favouritePanel);
				favouriteFrame.setSize(700, 700);
				favouriteFrame.setLocationRelativeTo(null);
				favouriteFrame.setVisible(true);
			}
		});
	}

	// Private class for the SubmitListener which is added on the Submit Button
	// It takes 5 Textfields as parameters which are the values of the limits and iterations
	// The user can choose new Limits and Iterations to draw the Fractal
	private class SubmitListener implements ActionListener {
		JTextField xMax;
		JTextField xMin;
		JTextField yMax;
		JTextField yMin;
		JTextField iteration;

		public SubmitListener(JTextField xMax, JTextField xMin, JTextField yMax, JTextField yMin,
				JTextField iteration) {
			this.xMax = xMax;
			this.xMin = xMin;
			this.yMax = yMax;
			this.yMin = yMin;
			this.iteration = iteration;
		}

		// The Limits that the user enters in the JTextfield are parsed
		// as doubles and set as the new variables for the Fractal.
		// It redraws the Fractal with the new variables.
		// If the user enters a wrong input, it throws an exception and
		// displays an error message.
		public void actionPerformed(ActionEvent e) {
			try {
				Fractal.this.setXmax(Double.parseDouble(xMax.getText()));
				Fractal.this.setXmin(Double.parseDouble(xMin.getText()));
				Fractal.this.setYmax(Double.parseDouble(yMax.getText()));
				Fractal.this.setYmin(Double.parseDouble(yMin.getText()));
				Fractal.this.setNIterations((int) Double.parseDouble(iteration.getText()));
				repaint();
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(mandelbrotPanel, "Please enter numbers", "Error",
						JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	public void setXmax(double x_max) {
		this.x_max = x_max;
	}

	public double getXmax() {
		return x_max;
	}

	public void setXmin(double x_min) {
		this.x_min = x_min;
	}

	public double getXmin() {
		return x_min;
	}

	public void setYmax(double y_max) {
		this.y_max = y_max;
	}

	public double getYmax() {
		return y_max;
	}

	public void setYmin(double y_min) {
		this.y_min = y_min;
	}

	public double getYmin() {
		return y_min;
	}

	public void setNIterations(int iterations) {
		this.n = iterations;
	}

	public double getNIterations() {
		return n;
	}

	public double getJuliaXmin() {
		return juliaXmin;
	}

	public double getJuliaXmax() {
		return juliaXmax;
	}

	public double getJuliaYmin() {
		return juliaYmin;
	}

	public double getJuliaYmax() {
		return juliaYmax;
	}

	public JLabel getMandelLabel() {
		return mandelLabel;
	}

	public void setMandelLabel(JLabel mandelLabel) {
		this.mandelLabel = mandelLabel;
	}

	public DefaultComboBoxModel<String> getModel() {
		return model;
	}

	public boolean isOrbit() {
		return orbit;
	}

	public boolean isCurved() {
		return curves;
	}

	public boolean isBurning() {
		return burning;
	}

	public boolean isQuadratic() {
		return quadratic;
	}

	public int getIter() {
		return iter;
	}

	public void setIter(int iter) {
		this.iter = iter;
	}

}

class MandelbrotPanel extends JPanel {

	//This variable is used to know when the Rectangle needs to be drawn
	private boolean drag = false;
	
	// Uses this two variable to know when the mouse was pressed and then released.
	private MouseEvent pressed, dragged;
	
	private JuliaPanel juliaPanel;
	private int x = 0;
	private Fractal fractal;
	
	//JFrame which holds the JPanel of the Julia Set
	JFrame juliaFrame = new JFrame("Julia Set");

	//Constructor initializes the MandelbrotPanel
	public MandelbrotPanel(Fractal fractal) {
		this.fractal = fractal;
		this.addMouseMotionListener(new JuliaListener());
		this.addMouseListener(new JuliaListener());
	}
	
	//Calls the drawMandelbrot function to draw the Mandelbrot on the JPanel
	//If the users starts to zoom ( drag == true ), it creates a rectangle with 
	//the variables x, y to determine the extreme corners and width and height 
	//to determine the width and height of the rectangle.
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		drawMandelbrot(g);
		if (drag) {
			g.setColor(Color.WHITE);
			int x = Math.min(pressed.getX(), dragged.getX());
			int y = Math.min(pressed.getY(), dragged.getY());
			int width = Math.max(pressed.getX(), dragged.getX()) - x;
			int height = Math.max(pressed.getY(), dragged.getY()) - y;
			g.fillRect(x, y, width, height);
		}
	}

	//This function iterates through each pixel and we assign each pixel
	//to a complex number in the complex plane. A function is then applied
	//to the complex number "n" given times. 
	public void drawMandelbrot(Graphics g) {
		for (int y = 0; y < MandelbrotPanel.this.getHeight(); y++) {
			for (int x = 0; x < MandelbrotPanel.this.getWidth(); x++) {
				Complex c = new Complex(
						((fractal.getXmax() - fractal.getXmin()) * x / MandelbrotPanel.this.getWidth())
								+ fractal.getXmin(),
						((fractal.getYmin() - fractal.getYmax()) * y / MandelbrotPanel.this.getHeight())
								+ fractal.getYmax());
				drawPixel(g, c, new Complex(0.0, 0.0), x, y);
			}
		}
	}
	
	//This function draws the pixel corresponding to the complex number in the complex plane.
	//If the function diverges, we assign each pixel a color based on the iterations; 
	//if the function doesn't diverge we assign the color black.
	public void drawPixel(Graphics g, Complex c, Complex d, int x, int y) {
		if (diverge(c, d)) {
			g.setColor(new Color(Math.round((3 * fractal.getIter()) % 255), Math.round((6 * fractal.getIter()) % 255),
					Math.round((3 * fractal.getIter()) % 255)));
		} else {
			g.setColor(Color.black);
		}
		g.drawLine(x, y, x, y);
	}
	
	
	public boolean diverge(Complex c, Complex d) {
		Complex z = d;
		for (int i = 0; i < fractal.getNIterations(); i++) {
			
			//If the Burning CheckBox and the Quadratic CheckBox are deselected
			//It applies the Mandelbrot function
			if (fractal.isBurning() == false && fractal.isQuadratic() == false) {
				if (z.modulusSquared() > 4) {
					fractal.setIter(i);
					return true;
				}
				z = z.square().add(c);
			} 
			
			//If the Burning CheckBox is selected, it applies the Burning Ship function
			else if (fractal.isQuadratic() == false && fractal.isBurning() == true) {
				if (z.modulusSquared() > 4) {
					fractal.setIter(i);
					return true;
				}
				z = new Complex(Math.abs(z.getReal()), Math.abs(z.getImaginary())).square().subtract(c);
			} 
			
			//If the Quadratic CheckBox is selected, it applies the Quadratic Mandelbrot function
			else if (fractal.isQuadratic() == true && fractal.isBurning() == false) {
				if (z.modulusSquared() > 4) {
					fractal.setIter(i);
					return true;
				}
				z = z.square().square().add(c);
			}
			
			//If the Orbit CheckBox is selected, it traps the numbers who satisfy the conditions below
			//
			if (fractal.isOrbit() == true && fractal.isCurved() == false) {
				if (Math.abs(z.getReal() - Math.sin(z.getImaginary() * 180)) < 0.01) {
					fractal.setIter(i);
					return true;
				}
				if (Math.abs(z.getImaginary() - Math.sin(z.getReal() * 180)) < 0.01) {
					fractal.setIter(i);
					return true;
				}
			}
			
			//If the Curve CheckBox is selected, it traps the numbers who satisfy the conditions below
			//and draws curves
			if (fractal.isOrbit() == false && fractal.isCurved() == true) {
				if (Math.abs(z.getImaginary()) < 0.04) {
					fractal.setIter(i);
					return true;
				}
			}
			
			//If both the Orbit and the Curve CheckBox are selected, it traps the numbers who satisfy
			//both of the conditions below
			if (fractal.isOrbit() == true && fractal.isCurved() == true) {
				if (Math.abs(z.getImaginary()) < 0.04) {
					fractal.setIter(i);
					return true;
				}
				if (Math.abs(z.getReal() - Math.sin(z.getImaginary() * 180)) < 0.01) {
					fractal.setIter(i);
					return true;
				}
				if (Math.abs(z.getImaginary() - Math.sin(z.getReal() * 180)) < 0.01) {
					fractal.setIter(i);
					return true;
				}
			}
		}
		return false;
	}

	class JuliaListener implements MouseListener, MouseMotionListener {
		
		int xStart, xStop, yStart, yStop;
		
		//If the users presses on the right mouse button in the MandelbrotPanel,
		//the Julia Set corresponding to that complex number is saved.
		public void mouseClicked(MouseEvent e) {
			if (e.getButton() == 3) {
				BufferedImage toBeSaved = new BufferedImage(700, 700, BufferedImage.TYPE_3BYTE_BGR);
				Graphics2D g2 = toBeSaved.createGraphics();
				juliaPanel.paint(g2);
				String name;
				x++;
				name = "Julia" + x + ".jpg";
				//Each saved Julia Set is added to the DefaultComboBox 
				//The DefaultComboBox holds the image objects
				fractal.getModel().addElement(name);
				try {
					ImageIO.write(toBeSaved, "jpg", new File(name));
					JOptionPane.showMessageDialog(null, "Image has been saved!");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}

		public void mouseEntered(MouseEvent e) {
		}

		public void mouseExited(MouseEvent e) {
		}
		
		//Variables xStart and yStart are used to calculate the new limits
		//where it needs to zoom
		//Drag is set to false so it doesn't start zooming and drawing the rectangle
		//when the mouse is pressed
		public void mousePressed(MouseEvent e) {
			drag = false;
			pressed = e;
			xStart = e.getX();
			yStart = e.getY();
		}
		
		//Drag is set to true to start drawing the rectangle
		public void mouseDragged(MouseEvent e) {
			drag = true;
			dragged = e;
			MandelbrotPanel.this.repaint();
		}
		
		//When the mouse is released, it checks whether the MouseEvent was a drag or a click.
		//If it was a drag, the difference between the x's and y's should be greater than 0.
		//The new limits x1,x2,y1,y2 are calculated based on the xStart,xStop,yStart,yStop
		//The Mandelbrot is repainted with the new values
		public void mouseReleased(MouseEvent e) {
			if (Math.abs(pressed.getX() - e.getX()) > 1 || Math.abs(pressed.getY() - e.getY()) > 1) {
				drag = false;
				xStop = e.getX();
				yStop = e.getY();
				double x1 = ((fractal.getXmax() - fractal.getXmin()) * xStart / MandelbrotPanel.this.getWidth())
						+ fractal.getXmin();
				double x2 = ((fractal.getXmax() - fractal.getXmin()) * xStop / MandelbrotPanel.this.getWidth())
						+ fractal.getXmin();
				double y1 = ((fractal.getYmin() - fractal.getYmax()) * yStart / MandelbrotPanel.this.getHeight())
						+ fractal.getYmax();
				double y2 = ((fractal.getYmin() - fractal.getYmax()) * yStop / MandelbrotPanel.this.getHeight())
						+ fractal.getYmax();
				fractal.setXmax(Math.max(x1, x2));
				fractal.setXmin(Math.min(x1, x2));
				fractal.setYmax(Math.max(y1, y2));
				fractal.setYmin(Math.min(y1, y2));
				MandelbrotPanel.this.repaint();
			}
		}
 
		//This method provides live updates of the Julia Set and shows the coordinates 
		//of the mouse while moving in the Mandelbrot fractal area. 
		public void mouseMoved(MouseEvent e) {
			Complex c = new Complex(
					((fractal.getXmax() - fractal.getXmin()) * e.getX() / MandelbrotPanel.this.getBounds().getWidth())
							+ fractal.getXmin(),
					((fractal.getYmin() - fractal.getYmax()) * e.getY() / MandelbrotPanel.this.getBounds().getHeight())
							+ fractal.getYmax());
			fractal.getMandelLabel().setText("Real: " + c.getReal() + " " + "Imag: " + c.getImaginary());
			JLabel juliaLabel = new JLabel();
			juliaFrame.setSize(700, 700);
			juliaPanel = new JuliaPanel(c);
			juliaPanel.add(juliaLabel);
			juliaLabel.setForeground(Color.WHITE);
			juliaLabel.setText("Real: " + c.getReal() + " " + "Imag: " + c.getImaginary());
			GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
			juliaFrame.setContentPane(juliaPanel);
			juliaFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			juliaFrame.setLocation((int) ge.getMaximumWindowBounds().getWidth() - juliaFrame.getWidth(), 0);
			juliaFrame.setVisible(true);
		}
	}

	class JuliaPanel extends JPanel {
		Complex c;
		JLabel label;
		
		//This variable will hold the image of the Julia Set.
		BufferedImage juliaImage;

		//Constructor initializes the JuliaPanel
		public JuliaPanel(Complex c) {
			this.c = c;
			juliaImage = new BufferedImage(juliaFrame.getWidth(), juliaFrame.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
			this.paintComponent(juliaImage.getGraphics());
		}

		public BufferedImage getJuliaImage() {
			return juliaImage;
		}

		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			drawJuliaSet(g, c);
		}
		//The Julia Set is calculated in a very similar way to the Mandelbrot Set
		//The sequence of values is given by the same formula, but we fix c and
		//range over the complex plane with the complex value d.
		public void drawJuliaSet(Graphics g, Complex c) {
			for (int x = 0; x < this.getWidth(); x++) {
				for (int y = 0; y < this.getHeight(); y++) {
					Complex d = new Complex(
							((fractal.getJuliaXmax() - fractal.getJuliaXmin()) * x / this.getWidth())
									+ fractal.getJuliaXmin(),
							((fractal.getJuliaYmin() - fractal.getJuliaYmax()) * y / this.getHeight())
									+ fractal.getJuliaYmax());
					drawPixel(g, c, d, x, y);
				}
			}
		}
	}
}
