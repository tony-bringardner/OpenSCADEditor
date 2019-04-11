package com.bringardner.polygon;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.bringardner.openscad.Editor;
import com.bringardner.openscad.Help;



public class PolygonFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;


	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PolygonFrame frame = new PolygonFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private enum SegmentType{Line,Curve};
	private static class Segment {
		public SegmentType type = SegmentType.Line;
		public 	List<Point2D> points = new ArrayList<>();
		public 	List<Ellipse2D> elipses = new ArrayList<>();
		public 	List<GeneralPath> paths = new ArrayList<>();

		public void add(Point2D point) {
			points.add(point);
			elipses.add(new Ellipse2D.Double(point.getX()-6, point.getY()-6, 12, 12));
			recalcPaths();
		}

		@Override
		public boolean equals(Object obj) {
			boolean ret = false;
			if (obj instanceof Segment) {
				Segment seg = (Segment) obj;
				if( seg.type == type ) {
					ret = points.equals(seg.points);
				}
			}

			return ret;
		}
		public void remove(int currentPoint) {
			points.remove(currentPoint);
			elipses.remove(currentPoint);
			recalcPaths();
		}

		private void recalcPaths() {
			paths.clear();
			Point2D last = null;
		
			if( type == SegmentType.Line) {
				for(int idx=0,sz=points.size(); idx < sz; idx++ ) {
					Point2D p = points.get(idx);
					if( last != null ) {

						double x1 = last.getX();
						double x2 = p.getX();
						double y1 = last.getY();
						double y2 = p.getY();
						double L = Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));

						double offsetPixels = 6;

						// This is the second line
						double p1x = x1 + offsetPixels * (y2-y1) / L;
						double p2x = x2 + offsetPixels * (y2-y1) / L;
						double p1y = y1 + offsetPixels * (x1-x2) / L;
						double p2y = y2 + offsetPixels * (x1-x2) / L;

						// This is the second line
						double p4x = x1 - offsetPixels * (y2-y1) / L;
						double p3x = x2 - offsetPixels * (y2-y1) / L;
						double p4y = y1 - offsetPixels * (x1-x2) / L;
						double p3y = y2 - offsetPixels * (x1-x2) / L;

						GeneralPath path = new GeneralPath();
						path.moveTo(p1x,p1y);
						path.lineTo(p2x,p2y); // line 1

						path.lineTo(p3x,p3y); 
						path.lineTo(p4x,p4y); // line 2
						path.closePath();
						paths.add(path);
					}
					last = p;
				
				}
			} else {
				if( points.size() < 3) {
					// nothing??
				} else if( points.size() == 3) {
					GeneralPath path = new GeneralPath();
					Point2D a = points.get(0);
					Point2D b = points.get(1);
					Point2D c = points.get(2);
					path.moveTo(a.getX(),a.getY());
					path.quadTo(b.getX(), b.getY(), c.getX(), c.getY());
					paths.add(path);
				} else if( points.size() == 4) {
					GeneralPath path = new GeneralPath();
					Point2D a = points.get(0);
					Point2D b = points.get(1);
					Point2D c = points.get(2);
					Point2D d = points.get(3);
					path.moveTo(a.getX(),a.getY());

					path.curveTo(b.getX(), b.getY(), c.getX(), c.getY(),d.getX(), d.getY());
					paths.add(path);
				} else {
					throw new RuntimeException("Not implemented.");
				}
			}
		}

		public void set(int currentPoint, Double point) {
			points.set(currentPoint, new Point2D.Double(point.x,point.y));
			elipses.set(currentPoint,new Ellipse2D.Double(point.getX()-6, point.getY()-6, 12, 12));
			recalcPaths();
		}

	}

	BufferedImage image;
	BufferedImage debugImage;
	private List<Segment> segments = new ArrayList<>();


	private JPanel drawingPanel;
	protected int currentPoint;
	protected Segment currentSegment;

	private boolean selected = false;

	private JTextArea textArea;
	private JPopupMenu popupToCurve;
	private JPopupMenu popupToLine;
	protected boolean addMode;
	private Double debugPoint;
	protected boolean hasCurves;
	private JSpinner lineWidthSpinner;
	private int snapTo = 4;
	private JSpinner whiteThresholdSpinner;
	private JLabel lblHoldTheControl;



	/**
	 * Create the frame.
	 */
	public PolygonFrame() {
		setIconImage(Editor.getOpenScadImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1036, 730);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel contraolPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) contraolPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(contraolPanel, BorderLayout.NORTH);

		JLabel lblLineWidth = new JLabel("Line Width");
		contraolPanel.add(lblLineWidth);

		lineWidthSpinner = new JSpinner();
		lineWidthSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				drawingPanel.updateUI();
			}
		});
		lineWidthSpinner.setModel(new SpinnerNumberModel(new Integer(4), new Integer(1), null, new Integer(1)));
		contraolPanel.add(lineWidthSpinner);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionClear();
			}
		});
		contraolPanel.add(btnClear);


		JButton btnPolygon = new JButton("Create Polygon Code");
		btnPolygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPolygon();
			}
		});
		contraolPanel.add(btnPolygon);

		JButton loadButton = new JButton("Load Image");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionLoadImage();
			}
		});

		contraolPanel.add(loadButton);
		
		lblHoldTheControl = new JLabel("Hold the control key down to add points.");
		contraolPanel.add(lblHoldTheControl);

		imageControlPanel = new JPanel();
		contraolPanel.add(imageControlPanel);

		JButton btnHelp = new JButton("");
		contraolPanel.add(btnHelp);
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionHelp();
			}
		});
		btnHelp.setPreferredSize(new Dimension(32, 32));
		btnHelp.setIcon(new ImageIcon(Editor.class.getResource("/HelpBlack.png")));
		btnHelp.setToolTipText("Help");
		contraolPanel.add(btnHelp);

		JLabel whiteThresholdLabel = new JLabel("Image White Threshold");
		imageControlPanel.add(whiteThresholdLabel);

		whiteThresholdSpinner = new JSpinner();
		imageControlPanel.add(whiteThresholdSpinner);
		whiteThresholdSpinner.setModel(new SpinnerNumberModel(200, 1, 255, 1));

		JLabel lblNoiseThreshold = new JLabel("Noise threshold");
		imageControlPanel.add(lblNoiseThreshold);

		noiseSpinner = new JSpinner();
		noiseSpinner.setModel(new SpinnerNumberModel(1, 0, 4, 1));
		imageControlPanel.add(noiseSpinner);

		invertImageCheckbox = new JCheckBox("Invert Image");
		imageControlPanel.add(invertImageCheckbox);
		setThresholdVisbility();
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setPreferredSize(new Dimension(2, 200));
		contentPane.add(scrollPane, BorderLayout.SOUTH);

		textArea = new JTextArea();
		textArea.setTabSize(4);
		scrollPane.setViewportView(textArea);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane();
		splitPane.setResizeWeight(0.5);
		panel.add(splitPane, BorderLayout.CENTER);

		drawingPanel = new JPanel() {

			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics panelG) {
				Graphics2D g = (Graphics2D) panelG;
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());

				image = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_BYTE_GRAY);



				Graphics2D g1 = (Graphics2D) image.getGraphics();
				g1.setStroke(new BasicStroke((int) lineWidthSpinner.getValue()+2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
				g1.setColor(Color.white);
				g1.fillRect(0, 0, getWidth(), getHeight());
				g1.setColor(Color.black);

				if( loadedImage != null) {
					g1.drawImage(loadedImage, 10, 10, loadedImage.getWidth(), loadedImage.getHeight(), null);
					g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
				} else {
					drawImageFromPoints(g1,false);
					g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
					drawControls(g);
				}
			}

			private void drawControls(Graphics2D g) {

				//  Control points
				int lines = 0;
				int points = 0;
				hasCurves = false;
				Point2D first = null;
				Point2D last = null;
				g.setColor(Color.green);
				for (Segment segment : segments) {
					if( segment.type==SegmentType.Line) {
						lines++;
					} else {
						hasCurves = true;
					}
					points+=segment.points.size();

					for(int idx=0,sz=segment.points.size(); idx < sz; idx++ ) {

						Point2D p = segment.points.get(idx);
						if( first == null ) {
							first = p;
						}
						g.fillOval((int)p.getX()-2, (int)p.getY()-2, 4, 4);
						Ellipse2D el = segment.elipses.get(idx);
						g.draw(el);
						if( segment.type == SegmentType.Curve) {
							if( last != null ) {
								g.drawLine((int)last.getX(), (int)last.getY(), (int)p.getX(), (int)p.getY());
							}
						}
						last = p;
					}
					if( debugAdd ) {
						//System.out.println("Here");
					}
					if(segment.type == SegmentType.Curve ) {
						g.setColor(Color.red);
						g.draw(segment.paths.get(0));
						g.setColor(Color.green);
					}

				}

				if( lines > 0 && last != null && points>2) {
					if( last != null) {
						g.drawLine((int)last.getX(), (int)last.getY(), (int)first.getX(), (int)first.getY());
					}
				}

				if( currentSegment != null ) {
					if( currentPoint >= 0 && currentPoint< currentSegment.points.size()) {
						if( selected ) {
							g.setColor(Color.blue);
							Point2D p = currentSegment.points.get(currentPoint);

							g.drawLine(0, (int)p.getY(), getWidth(), (int)p.getY());
							g.drawLine((int)p.getX(), 0, (int)p.getX(),getHeight());
						} else {
							g.setColor(Color.red);
						}
						g.fill(currentSegment.elipses.get(currentPoint));
					}

					if( currentLine >=0  ) {
						GeneralPath path = currentSegment.paths.get(currentLine);
						g.setColor(Color.ORANGE);
						g.draw(path);

					}
				}

				if( debugPoint != null ) {
					g.setColor(Color.ORANGE);
					//g.fillRect((int)debugPoint.getX()-4, (int)debugPoint.getY()-4, 8, 8);
				}


			}


		};

		splitPane.setLeftComponent(drawingPanel);
		drawingPanel.setFocusable(true);

		debugPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g1) {
				Graphics2D g = (Graphics2D) g1;
				g.setColor(Color.white);
				g.fillRect(0, 0, getWidth(), getHeight());

				if( debugImage != null ) {
					g.drawImage(debugImage, 0, 0, null);
				} else {
					g.drawString("No Debug Image", getWidth()/2, getHeight()/2);
				}
			}
		};

		debugPanel.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				debugPanel.setToolTipText(""+e.getX()+","+e.getY());
			}
		});

		splitPane.setRightComponent(debugPanel);
		drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				drawingPanel.setToolTipText(""+e.getX()+","+e.getY());
			}
		});


		drawingPanel.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {


			}

			@Override
			public void keyReleased(KeyEvent e) {
				//System.out.println("key="+e);
				if( e.getKeyCode() == KeyEvent.VK_CONTROL) {
					drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					addMode = false;
				} else if( currentSegment != null ) {
					if( e.getKeyCode() == KeyEvent.VK_DELETE && selected && currentPoint >= 0 && currentPoint < currentSegment.points.size()) {
						currentSegment.remove(currentPoint);
						currentPoint = -1;
						currentLine = -1;
						selected = false;
						drawingPanel.updateUI();
					}
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_CONTROL) {
					drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
					addMode = true;
				}

			}
		});

		drawingPanel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				if( popupToCurve.isShowing() || popupToLine.isShowing()) {
					return;
				}

				currentPoint = -1;
				currentLine = -1;
				currentSegment = null;
				Point2D p = new Point2D.Double(e.getX(), e.getY());
				for (Segment s : segments) {
					for (int idx=0,sz=s.points.size(); idx < sz; idx++) {
						if( s.elipses.get(idx).contains(p)) {
							currentSegment = s;
							currentPoint = idx;
							break;
						}
					}
					if( currentPoint == -1) {
						for (int idx = 0, sz = s.paths.size(); idx < sz; idx++) {
							if( s.paths.get(idx).contains(p)) {
								currentSegment = s;
								currentLine = idx;
								break;
							}
						}
					}
				}

				drawingPanel.updateUI();
			}

			@Override
			public void mouseDragged(MouseEvent e) {

				if(currentSegment != null && !addMode && currentPoint >= 0 && selected) {
					Point p = e.getPoint();
					p.x +=  snapTo - (p.x % snapTo);
					p.y +=  snapTo - (p.y % snapTo);
					currentSegment.set(currentPoint, new Point2D.Double(p.x, p.y));
					drawingPanel.updateUI();					
				}

			}

		});

		drawingPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				drawingPanel.requestFocus();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if(loadedImage == null) {
					if(addMode) {
						currentSegment = null;
						if( segments.size()>0) {
							currentSegment = segments.get(segments.size()-1);
						}
						if( currentSegment == null ) {
							if( segments.size() == 0 ) {
								segments.add(new Segment());
							}
							currentSegment = segments.get(0);
						}
						Point p = e.getPoint();
						p.x +=  snapTo - (p.x % snapTo);
						p.y +=  snapTo - (p.y % snapTo);
						currentSegment.add(new Point2D.Double(p.x, p.y));
						lblHoldTheControl.setVisible(false);
						drawingPanel.updateUI();
					} 
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if(currentLine >=0  && e.getButton() == 3 ) {
					Point pp = e.getPoint();
					for (Segment s : segments) {
						for(GeneralPath p : s.paths) {
							if( p.contains(pp.getX(),pp.y)) {
								if( s.type == SegmentType.Line) {
									popupToCurve.show(PolygonFrame.this, pp.x, pp.y);
								} else {
									popupToLine.show(PolygonFrame.this, pp.x, pp.y);
								}
							}
						}
					}
				} else if( loadedImage == null ) {
					drawingPanel.requestFocus();
					if( !addMode && currentSegment != null && currentPoint >= 0) {
						selected = true;
					} else {
						selected = false;
					}
					drawingPanel.updateUI();
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				selected = false;
				drawingPanel.updateUI();
			}

		});

		popupToCurve = new JPopupMenu("line pop");
		JMenuItem item = new JMenuItem("Convert to Curve segment");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionConvertToCurve();
			}
		});
		popupToCurve.add(item);

		item = new JMenuItem("Add control point");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionConvertToCurve();
			}
		});
		popupToCurve.add(item);

		popupToLine = new JPopupMenu("curve pop");
		item = new JMenuItem("Add control point");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionAddControllPoint();
			}
		});
		popupToLine.add(item);

		item = new JMenuItem("Remove control point");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionRemoveControllPoint();
			}
		});

		popupToLine.add(item);

		item = new JMenuItem("Convert to Line segment");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionConvertToLine();
			}
		});

		popupToLine.add(item);


	}

	protected void actionHelp() {
		try {
			final Help help = new Help("/HelpPolygon.html");
			help.setLocationRelativeTo(null);
			SwingUtilities.invokeLater(()-> help.setVisible(true));
		} catch (IOException e) {
			logError(e, "Open help dialog");
		}

	}
	
	protected void actionClear() {
		segments.clear();
		loadedImage = null;
		setThresholdVisbility();
		drawingPanel.updateUI();
		lblHoldTheControl.setVisible(true);
	}

	private void setThresholdVisbility() {
		imageControlPanel.setVisible(loadedImage != null);
	}

	protected void drawImageFromPoints(Graphics2D g1,boolean close) {
		Segment last = null;
		Point2D firstPoint = null;
		Point2D lastPoint = null;

		for (int idx = 0, sz = segments.size(); idx < sz; idx++) {
			Segment s = segments.get(idx);
			if( firstPoint == null && s.points.size()>0) {
				firstPoint = s.points.get(0);
			}
			if( last != null  && last.points.size()>0 && s.points.size()>0) {
				Point2D a = last.points.get(last.points.size()-1);
				Point2D b = s.points.get(0);
				g1.drawLine((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
				lastPoint = b;
			}
			drawSegment(g1,s);
			last = s;
		}

		if( close) {
			if( firstPoint != null && lastPoint != null ) {
				g1.drawLine((int)lastPoint.getX(), (int)lastPoint.getY(), (int)firstPoint.getX(), (int)firstPoint.getY());
			}
		}
	}

	protected void actionRemoveControllPoint() {
		if( currentSegment != null ) {
			if( currentSegment.type == SegmentType.Curve) {
				if( currentSegment.points.size()>3) {
					currentSegment.remove(1);
					drawingPanel.updateUI();
				}
			}
		}

	}

	boolean debugAdd = false;
	protected void actionAddControllPoint() {
		if( currentSegment != null ) {
			if( currentSegment.type == SegmentType.Curve) {
				if( currentSegment.points.size()<4) {
					Point2D a = currentSegment.points.get(currentSegment.points.size()-2);
					Point2D b = currentSegment.points.get(currentSegment.points.size()-1);
					Line2D line = new Line2D.Double(a, b);
					double cx = line.getBounds2D().getCenterX();
					double cy = line.getBounds2D().getCenterY();

					debugPoint = new Point2D.Double(cx, cy);
					currentSegment.add(debugPoint);
					debugAdd = true;
					drawingPanel.updateUI();
				}
			}
		}
	}

	protected void actionConvertToLine() {
		if( currentSegment != null ) {
			if( currentSegment.type == SegmentType.Curve) {
				currentSegment.type = SegmentType.Line;
				currentSegment.recalcPaths();
			}
		}

	}

	protected void actionConvertToCurve() {
		//System.out.println("Convert1");
		if( currentLine >= 0) {
			//currentSegment.type = SegmentType.Curve;
			//System.out.println("Convert--");
			int lines = currentSegment.paths.size();
			//System.out.println("idx="+currentLine+" lines="+lines);
			Point2D a = currentSegment.points.get(currentLine);
			Point2D b = currentSegment.points.get(currentLine+1);
			Line2D line = new Line2D.Double(a, b);
			double cx = line.getBounds2D().getCenterX();
			double cy = line.getBounds2D().getCenterY();
			debugPoint = new Point2D.Double(cx, cy);
			Segment s2 = new Segment();
			s2.type = SegmentType.Curve;
			s2.add(a);
			s2.add(new Point2D.Double(cx,cy));
			s2.add(b);

			if( lines == (currentLine+1)) { // append 
				currentSegment.remove(currentLine+1);
				segments.add(s2);
				segments.add(new Segment());
			} else if( lines < (currentLine+1)) { // insert after
				throw new RuntimeException("No done.");
			} else if( lines > (currentLine+1)) { // insert before
				//  convert current into two
				Segment s1 = new Segment();
				s1.type = currentSegment.type;
				for (int i = 0; i < currentLine+1; i++) {
					s1.add(currentSegment.points.get(i));
				}

				Segment s3 = new Segment();
				s3.type = currentSegment.type;
				for (int i = currentLine+2,sz=currentSegment.points.size(); i < sz; i++) {
					s3.add(currentSegment.points.get(i));
				}
				int sidx = segments.indexOf(currentSegment);

				segments.set(sidx, s3);

				/*
				if( sidx >= segments.size()-1) {
					segments.add(s3);
				} else {
					segments.set(sidx+1, s3);
				}
				 */

				segments.add(sidx, s2);

			} else {
				// split into two segments

				currentSegment.remove(currentLine+1);
				segments.add(s2);
				segments.add(new Segment());

				throw new RuntimeException("No done.");
			}

			currentLine = -1;
			currentPoint = -1;
			currentSegment = s2;
			drawingPanel.updateUI();
		}


	}

	File file = new File("C:\\Users\\Tony\\Documents\\ATest.png");
	private BufferedImage loadedImage;
	private int currentLine;
	protected void actionLoadImage() {
		lblHoldTheControl.setVisible(false);
		JFileChooser fc = new JFileChooser();
		if( file != null ) {
			fc.setSelectedFile(file);
		}
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.addChoosableFileFilter(new FileNameExtensionFilter("Images", ImageIO.getReaderFormatNames()));
		fc.setAcceptAllFileFilterUsed(false);
		fc.setAcceptAllFileFilterUsed(false);
		if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File tmp = fc.getSelectedFile();
			try {
				loadedImage = ImageIO.read(tmp);
				if( loadedImage == null ) {
					throw new IOException("Unsuported file type.");
				} else {
					setThresholdVisbility();
					file = tmp;
					segments.clear();
					currentSegment = null;
					currentPoint = -1;
					currentLine = -1;
					drawingPanel.updateUI();
					debugImage = null;
					debugPanel.updateUI();
				}
			} catch (Throwable e) {
				logError(e, "Error loading "+file);
			}
		}

	}

	private static final int BLACK = Color.black.getRGB();
	private static final int WHITE = Color.white.getRGB();
	private JPanel debugPanel;


	public  List<Point>  removeDuplicates(Graphics2D g, List<Point> points) {
		List<Point> ret = new ArrayList<Point>();
		GeneralPath path = new GeneralPath();
		Point p = points.get(0);
		ret.add(p);
		path.moveTo(p.getX(), p.getY());
		if( g != null ) g.setColor(Color.black);
		for(int idx=0,sz=points.size(); idx < sz; idx++ ) {
			Point pp = points.get(idx);
			if( !pp.equals(p)) {
				if( g != null ) g.drawLine(p.x, p.y, pp.x, pp.y);
				ret.add(pp);				
				p = pp;
				path.lineTo(p.getX(), p.getY());
			}
		}
		if( g != null ) updateDebug(g);
		path.closePath();

		if( g != null ) {
			//g.fill(path);
			g.setColor(Color.red);
			g.setStroke(new BasicStroke((int)(lineWidthSpinner.getValue())+2));
			g.draw(path);
			updateDebug(g);
		}

		return ret;
	}

	public boolean[][] toBoolean(BufferedImage img) {
		int threshold = (int) whiteThresholdSpinner.getValue();
		boolean invert = invertImageCheckbox.isSelected();
		boolean ret [][] = new boolean[img.getHeight()][img.getWidth()];
		for(int y=0; y < ret.length ; y++  ) {
			for(int x=0; x < ret[y].length ; x++  ) {

				int pix = img.getRGB(x, y);
				if( pix != WHITE) {
					if( pix != BLACK) {
						// convert to grey then use threshold to determine if it's black or white.
						int r = (pix>>16)&0xff;
						int g = (pix>>8)&0xff;
						int b = pix&0xff;

						//calculate average
						int avg = (r+g+b)/3;
						if( avg >= threshold) {
							pix = WHITE;
						} else {
							pix = BLACK;
						}
					}
					ret[y][x] = pix != WHITE;

				}
				if( invert ) {
					ret[y][x] = !ret[y][x];
				}
			}

		}

		return ret;
	}

	DebugFrame debugFrame;
	boolean debug = false;

	protected void actionPolygon() {
		textArea.setText("");
		if( loadedImage == null && !hasCurves ) {
			List<Point2D> points = new ArrayList<Point2D>();
			for (Segment segment : segments) {
				points.addAll(segment.points);
			}
			setText2D(points);
		} else if( image != null ) {
			debugImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			debugPanel.updateUI();

			new Thread(new Runnable() {


				@Override
				public void run() {

					Graphics2D dbg = (Graphics2D) debugImage.getGraphics();
					int w = image.getWidth();
					int firstY = 0;
					int firstX = w;
					BufferedImage image1 = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
					Graphics2D g1 = (Graphics2D) image1.getGraphics();
					g1.setColor(Color.white);
					g1.fillRect(0, 0, image.getWidth(), image.getHeight());
					drawImageFromPoints(g1,true);
					g1.dispose();
					boolean[][] bimg = toBoolean(image);
					removeNoise(bimg);
					Rectangle p = new Rectangle(image1.getWidth(), image1.getHeight());
					List<Rectangle> diff = ImageDiff.findEdges(bimg, p, 2, 2);

					if( diff.size()== 0 ) {
						throw new RuntimeException("No Image data");
					}
					int translate = 0;
					//  show drawing in the debug panel
					for (int idx = 0,sz=diff.size(); idx < sz; idx++) {
						Rectangle r = diff.get(idx);
						for(int y= r.y; y < (r.y+r.height); y++ ) {
							for(int x= r.x; x < (r.x+r.width); x++ ) {
								if( bimg[y][x] ) {
									debugImage.setRGB(x, y, Color.red.getRGB());
									if( firstY == 0 ) {
										firstY =  y;
										firstX =  x;
									}
								}
							}
						}

						updateDebug(dbg);

						List<Point> points1 = trace(null, p, bimg, firstX, firstY);
						List<Point> points2 = removeDuplicates(dbg,points1);

						setText(points2,translate);
						translate+= r.height;
						firstY = 0;
					}
				}
			}).start();
		}
	}


	private void removeNoise(boolean[][] bimg) {
		int threshold = (int) noiseSpinner.getValue();
		for(int y=0; y < bimg.length ; y++  ) {
			for(int x=0; x < bimg[y].length ; x++  ) {
				if( bimg[y][x] ) {
					int cnt = countNeighbors(bimg,x,y);
					if( cnt <= threshold) {
						bimg[y][x] = false;
						//System.out.println("remove "+x+","+y);
					}
				}
			}
		}		
	}

	
	public static int countNeighbors(boolean[][] bimg, int x, int y) {
		int ret = 0;
		int h = bimg.length-1;
		int w = bimg[0].length-1;

		if( y > 0 && y <= h) {
			//  top row
			int yy = y-1;
			if(x > 0 && x <= w && bimg[yy][x-1]) {
				ret++;
			}
			if(x >= 0 && x <= w && bimg[yy][x]) {
				ret++;
			}
			if(x < w && bimg[yy][x+1]) {
				ret++;
			}
		}
		if( y >= 0 && y <= h) {
			//  middle  row
			int yy = y;
			if(x > 0 && x <= w && bimg[yy][x-1]) {
				ret++;
			}
			if(x < w && bimg[yy][x+1]) {
				ret++;
			}
		}
		if( y < h ) {
			//  bottom row
			int yy = y+1;
			if(x > 0 && x <= w && bimg[yy][x-1]) {
				ret++;
			}
			if(x >= 0 && x <= w && bimg[yy][x]) {

				ret++;
			}
			if(x < w && bimg[yy][x+1]) {
				ret++;
			}
		}

		return ret;
	}

	private void updateDebug(Graphics2D dbg) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				drawingPanel.updateUI();
				debugPanel.updateUI();
			}
		});
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
		}
	}

	/*
		The formula for a 2-points curve:
		P = (1-t)P1 + tP2
		x = (1-t)2x1 + 2(1-t)tx2
		y = (1-t)2y1 + 2(1-t)ty2

		For 3 control points:

		P = (1-t)2P1 + 2(1-t)tP2 + t2P3
		x = (1-t)2x1 + 2(1-t)tx2 + t2x3
		y = (1-t)2y1 + 2(1-t)ty2 + t2y3
		y = (1-t)2y1 + 2(1-t)ty2 + t2y3
	Instead of x1, y1, x2, y2, x3, y3 we should put coordinates of 3 control points, and then as t moves from 0 to 1, for each value of t we’ll have (x,y) of the curve.

	For instance, if control points are (0,0), (0.5, 1) and (1, 0), the equations become:

		x = (1-t)2 * 0 + 2(1-t)t * 0.5 + t2 * 1 = (1-t)t + t2 = t
		y = (1-t)2 * 0 + 2(1-t)t * 1 + t2 * 0 = 2(1-t)t = –t2 + 2t
	 */


	protected void setText(List<Point> list, int translate) {
		StringBuilder buf = new StringBuilder();
		if( translate > 0 ) {
			buf.append("\ntranslate([0,"+translate+",0])\n");
		}

		buf.append("\npolygon( points=\n\t[\n\t");

		int minX = 0;
		int minY = 0;

		if( list.size() > 0 ) {
			minX = minY = Integer.MAX_VALUE;
			for (Point point : list) {
				minX = Math.min(minX, point.x);
				minY = Math.min(minY, point.y);
			}
		}

		int cnt =0;
		for (Point point : list) {
			buf.append("["+(point.x-minX)+","+(point.y-minY)+"],");
			if( ++cnt % 20 == 0 ) {
				buf.append("\n\t");
			}
		}
		buf.append("\n\t]\n);");

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textArea.append(buf.toString());
			}
		});


	}

	protected void setText2D(List<Point2D> list) {
		StringBuilder buf = new StringBuilder();
		buf.append("\npolygon( points=\n\t[\n\t");

		int minX = 0;
		int minY = 0;

		if( list.size() > 0 ) {
			minX = minY = Integer.MAX_VALUE;
			for (Point2D point : list) {
				minX = (int) Math.min(minX, point.getX());
				minY = (int) Math.min(minY, point.getY());
			}
		}

		int cnt =0;
		for (Point2D point : list) {
			buf.append("["+(point.getX()-minX)+","+(point.getY()-minY)+"],");
			if( ++cnt % 20 == 0 ) {
				buf.append("\n\t");
			}
		}
		buf.append("\n\t]\n);");
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				textArea.setText(buf.toString());
			}
		});


	}


	/*
			The formula for a 2-points curve:
			P = (1-t)P1 + tP2
			x = (1-t)2x1 + 2(1-t)tx2
			y = (1-t)2y1 + 2(1-t)ty2

			For 3 control points:

			P = (1-t)2P1 + 2(1-t)tP2 + t2P3
			x = (1-t)2x1 + 2(1-t)tx2 + t2x3
			y = (1-t)2y1 + 2(1-t)ty2 + t2y3
			y = (1-t)2y1 + 2(1-t)ty2 + t2y3
		Instead of x1, y1, x2, y2, x3, y3 we should put coordinates of 3 control points, and then as t moves from 0 to 1, for each value of t we’ll have (x,y) of the curve.

		For instance, if control points are (0,0), (0.5, 1) and (1, 0), the equations become:

			x = (1-t)sq * 0 + 2(1-t)t * 0.5 + tsq * 1 
			y = (1-t)sq * 0 + 2(1-t)t * 1   + tsq * 0 

			(1-t)cubeP1 + 
				3(1-t)sqtP2 +
				3(1-t)tsqP3 + 
				t3P4
	 */


	public static final int NORTH = 0;
	public static final int EAST = 1;
	public static final int SOUTH = 2;
	public static final int WEST = 3;


	private static class Direction {
		public int xdir;
		public int ydir;
		public int x1dir;
		public int y1dir;
		public String name;
		public Direction(String string, int i, int j, int x, int y) {
			name = string;
			xdir = i;
			ydir = j;
			x1dir = x;
			y1dir = y;
		}

		public String toString() {
			return name+" xdir="+xdir+" ydir="+ydir;
		}

	}

	Direction north = new Direction("North",0,-1,-1,0);

	Direction west = new Direction("West",-1,0,0,+1);

	Direction south = new Direction("South",0,1,1,0);

	Direction east = new Direction("East",1,0,0,-1);

	Direction dirs [] = {north,east,south,west};
	private JSpinner noiseSpinner;
	private JPanel imageControlPanel;
	private JCheckBox invertImageCheckbox;

	public boolean isOn(Point p, boolean bimg[][]) {
		boolean ret = false;
		if( p.y >= 0 && p.y < bimg.length ) {
			if( p.x >= 0 && p.x < bimg[0].length) {
				ret = bimg[p.y][p.x];
			}
		}

		return ret;
	}

	private int turnLeft(int dir) {
		if(--dir < 0 ) {
			dir = dirs.length-1; 
		}
		return dir;
	}

	private int turnRight(int dir) {
		if(++dir >= dirs.length ) {
			dir = 0; 
		}
		return dir;
	}


	protected List<Point> trace(Graphics2D g, Rectangle parent, boolean[][] bimg, int startX, int y) {
		List<Point> ret = new ArrayList<Point>();

		int x = startX-1;


		if( g != null ) {
			g.setColor(Color.blue);
		}



		if( g != null ) {
			g.fillOval(x, y, 4, 4);
			updateDebug(g);
		}
		//System.out.println("Start @ "+(x-r.x)+","+(y-r.y));
		if( debug ) {
			if( debugFrame == null || !debugFrame.isDisplayable()) {
				debugFrame = new DebugFrame();
				SwingUtilities.invokeLater(()->debugFrame.setVisible(true));
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}
			final int myx = x;
			final int myy = y;
			SwingUtilities.invokeLater(()->debugFrame.setValues(myx,myy,bimg,ret,null));
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		Point [] p = {new Point(0,0),new Point(0,0),new Point(0,0),new Point(0,0)};
		int dir = 2; 
		int ox = x;
		int oy = y;

		p[0].x = x;
		p[0].y = y;

		p[1].x = x+(dirs[dir].x1dir);
		p[1].y = y+(dirs[dir].y1dir);

		p[2].x = x+dirs[dir].xdir;
		p[2].y = y+dirs[dir].ydir;

		p[3].x = p[2].x+(dirs[dir].x1dir);
		p[3].y = p[2].y+(dirs[dir].y1dir);




		boolean inited = false;
		int cnt = 0;
		Point last = null;
		do {
			if( isOn(p[0],bimg) ) {
				if( g != null ) {
					g.setColor(Color.yellow);
					g.fillOval(p[0].x, p[0].y, 4, 4);
					updateDebug(g);
				}
				throw new RuntimeException(" "+p[0]+" must be white");
			}

			if( !isOn(p[1],bimg) ) {
				if( g != null ) {
					g.setColor(Color.yellow);
					g.fillOval(p[1].x, p[1].y, 4, 4);
					updateDebug(g);
				}
				//System.out.println("0="+p[0]+" 1="+p[1]);
				throw new RuntimeException(" "+p[1]+" must be black");
			}


			if( isOn(p[3],bimg) && isOn(p[2],bimg)  ) {
				// |B|B|
				// |B|W|

				x = p[0].x;
				y = p[0].y; 
				ret.add(new Point(x,y));
				dir = turnRight(dir);
				cnt = 0;
			} else if(isOn(p[3],bimg) && !isOn(p[2],bimg) ) {
				//|B|W|
				//|B|W|
				//straight 
				x += dirs[dir].xdir;
				y += dirs[dir].ydir;
				inited = true;
				cnt++;
			} else {

				cnt = 0;
				x = p[3].x;
				y = p[3].y; 
				ret.add(new Point(x,y));
				dir = turnLeft(dir);			
			}



			p[0].x = x;
			p[0].y = y;

			p[1].x = x+(dirs[dir].x1dir);
			p[1].y = y+(dirs[dir].y1dir);

			p[2].x = x+dirs[dir].xdir;
			p[2].y = y+dirs[dir].ydir;

			p[3].x = p[2].x+(dirs[dir].x1dir);
			p[3].y = p[2].y+(dirs[dir].y1dir);

			if( g != null ) {
				g.setColor(Color.yellow);
				g.fillOval(x, y, 4, 4);
				updateDebug(g);
			}

			if( debug ) {
				final int myx = x;
				final int myy = y;
				final Point myLAst = last; 
				SwingUtilities.invokeLater(()->debugFrame.setValues(myx,myy,bimg,ret,myLAst));
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
				}
			}

			last = new Point(x,y);
		} while (inited == false || !(x==ox && y== oy));


		if( cnt > 0 ) {
			ret.add(new Point(x,y));
		} 


		//System.out.println("end x="+x+" ox="+ox+" y="+y+" oy="+oy);
		return ret;
	}


	public void logError(Throwable error, String title) {
		//	error.printStackTrace();
		if(SwingUtilities.isEventDispatchThread()) {
			JOptionPane.showMessageDialog(this, error.toString(), title, JOptionPane.ERROR_MESSAGE);
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(PolygonFrame.this, error.toString(), title, JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}

	public static void drawSegment(Graphics2D g1,Segment segment) {
		g1.setColor(Color.black);
		if( segment.type == SegmentType.Curve) {
			if( segment.points.size() == 3) {
				GeneralPath path = new GeneralPath();
				path.moveTo(segment.points.get(0).getX(), segment.points.get(0).getY());
				path.quadTo(segment.points.get(1).getX(), segment.points.get(1).getY(), 
						segment.points.get(2).getX(), segment.points.get(2).getY());
				g1.draw(path);
			} else if( segment.points.size() == 4) {
				GeneralPath path = new GeneralPath();
				path.moveTo(segment.points.get(0).getX(), segment.points.get(0).getY());
				path.curveTo(segment.points.get(1).getX(), segment.points.get(1).getY(), 
						segment.points.get(2).getX(), segment.points.get(2).getY(),
						segment.points.get(3).getX(), segment.points.get(3).getY()
						);
				g1.draw(path);
			} else if( segment.points.size() == 2) {

				g1.drawLine((int)segment.points.get(0).getX(),(int) segment.points.get(0).getY(), 
						(int)segment.points.get(1).getX(),	 (int)segment.points.get(1).getY());
			} else {
				throw new RuntimeException("Curve with too many points = "+segment.points.size());
			}
		} else {
			Point2D last = null;
			for(int idx=0,sz=segment.points.size(); idx < sz; idx++ ) {
				Point2D p = segment.points.get(idx);
				if( last != null) {
					g1.drawLine((int)last.getX(), (int)last.getY(), (int)p.getX(), (int)p.getY());
					//g1.draw(segment.paths.get(idx-1));

				}
				last = p;
			}
		}
	}

}
