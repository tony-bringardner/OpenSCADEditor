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
import java.awt.GridLayout;
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
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
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
	private static Color gridColor = new Color(Color.lightGray.getRed(), Color.lightGray.getGreen(), Color.lightGray.getBlue(), 100);
	private JPanel contentPane;
	private enum LineCap {BUTT,ROUND,SQUARE};
	private enum LineJoin {MITER,ROUND,BEVEL};
	private BufferedImage image;
	private BufferedImage debugImage;
	private List<Segment> segments = new ArrayList<>();


	private JPanel drawingPanel;
	private  int currentPoint=-1;
	private  Segment currentSegment;
	private int segmentIndex=-1;


	private boolean selected = false;

	private JTextArea textArea;
	private JPopupMenu popupToCurve;
	private JPopupMenu popupToLine;
	private  boolean addMode;
	private Double debugPoint;
	
	private JSpinner lineWidthSpinner;
	private int snapTo = 8;
	private JSpinner whiteThresholdSpinner;
	private JLabel lblHoldTheControl;
	private  Point lastMouse;
	
	private Direction north = new Direction("North",0,-1,-1,0);
	private Direction west = new Direction("West",-1,0,0,+1);
	private Direction south = new Direction("South",0,1,1,0);
	private Direction east = new Direction("East",1,0,0,-1);
	private Direction dirs [] = {north,east,south,west};

	private JSpinner noiseSpinner;
	private JPanel imageControlPanel;
	private JCheckBox invertImageCheckbox;
	private JCheckBox chckbxClosePath;
	private JPanel debugControlPanel;
	private JButton btnPrintPoints;
	private JPanel drawingControlPanel;
	private JSpinner snapToSpinner;
	private JLabel lblGridSnapTo;
	private JLabel lblEpsilon;
	private JSpinner epsilonSpinner;
	private JLabel lblCap;
	
	private JLabel lblJoin;
	private JComboBox<LineCap> capComboBox;
	private JComboBox<LineJoin> joinComboBox;
	private int snapHalf;

	
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

	private static class ControlPoint {
		Point2D point;
		Ellipse2D ellipse;
		public ControlPoint(Point2D point) {
			this.point = point;
			this.ellipse = new Ellipse2D.Double(point.getX()-6, point.getY()-6, 12, 12);
		}
		public void update(Point2D point2) {
			point.setLocation(point2.getX(),point2.getY());
			ellipse.setFrame(point2.getX()-6,point2.getY()-6,12,12);//,new 
		}
	}

	private static class Segment {
		public SegmentType type = SegmentType.Line;
		public 	List<ControlPoint> points = new ArrayList<>();
		public 	List<GeneralPath> paths = new ArrayList<>();

		public void add(ControlPoint point) {
			points.add(point);
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
		}

		private void recalcPaths() {
			paths.clear();
			Point2D last = null;

			if( type == SegmentType.Line) {
				for(int idx=0,sz=points.size(); idx < sz; idx++ ) {
					Point2D p = points.get(idx).point;
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
				int sz = points.size();
				if( sz < 3) {
					// nothing??
				} else if( sz == 3) {
					GeneralPath path = new GeneralPath();
					Point2D a = points.get(0).point;
					Point2D b = points.get(1).point;
					Point2D c = points.get(2).point;
					path.moveTo(a.getX(),a.getY());
					path.quadTo(b.getX(), b.getY(), c.getX(), c.getY());
					paths.add(path);
				} else if( sz == 4) {
					GeneralPath path = new GeneralPath();
					Point2D a = points.get(0).point;
					Point2D b = points.get(1).point;
					Point2D c = points.get(2).point;
					Point2D d = points.get(3).point;
					path.moveTo(a.getX(),a.getY());

					path.curveTo(b.getX(), b.getY(), c.getX(), c.getY(),d.getX(), d.getY());
					paths.add(path);
				} else {
					throw new RuntimeException("Not implemented.");
				}
			}
		}

		public void update(int currentPoint, Point2D point) {
			points.get(currentPoint).update(point);
		}

	}



	/**
	 * Create the frame.
	 */
	public PolygonFrame() {

		setIconImage(Editor.getOpenScadImage());
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1197, 730);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel controlPanel = new JPanel();
		FlowLayout fl_controlPanel = (FlowLayout) controlPanel.getLayout();
		fl_controlPanel.setAlignment(FlowLayout.LEFT);
		contentPane.add(controlPanel, BorderLayout.NORTH);

		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionClear();
			}
		});
		controlPanel.add(btnClear);


		JButton btnPolygon = new JButton("Create Polygon Code");
		btnPolygon.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPolygon();
			}
		});
		controlPanel.add(btnPolygon);

		JButton loadButton = new JButton("Load Image");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionLoadImage();
			}
		});

		lblEpsilon = new JLabel("Epsilon");
		lblEpsilon.setToolTipText("Polygon simplification threshold.");
		controlPanel.add(lblEpsilon);

		epsilonSpinner = new JSpinner();
		epsilonSpinner.setModel(new SpinnerNumberModel(1.0, 0.0, 100.0, 0.5));
		epsilonSpinner.setToolTipText("Polygon simplification threshold.");
		controlPanel.add(epsilonSpinner);

		drawingControlPanel = new JPanel();
		controlPanel.add(drawingControlPanel);
				
						lblGridSnapTo = new JLabel("Grid Snap to");
						drawingControlPanel.add(lblGridSnapTo);
				
						snapToSpinner = new JSpinner();
						snapToSpinner.addChangeListener(new ChangeListener() {
							public void stateChanged(ChangeEvent e) {
								actionResnap();
							}
						});
						
								snapToSpinner.setModel(new SpinnerNumberModel(new Integer(10), new Integer(0), null, new Integer(1)));
								drawingControlPanel.add(snapToSpinner);
		
				JLabel lblLineWidth = new JLabel("Line Width");
				drawingControlPanel.add(lblLineWidth);
		
				lineWidthSpinner = new JSpinner();
				drawingControlPanel.add(lineWidthSpinner);
				lineWidthSpinner.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						drawingPanel.updateUI();
					}
				});
				lineWidthSpinner.setModel(new SpinnerNumberModel(new Integer(4), new Integer(1), null, new Integer(1)));
		
		lblCap = new JLabel("Cap");
		drawingControlPanel.add(lblCap);
		
		capComboBox = new JComboBox<LineCap>();
		capComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingPanel.updateUI();
			}
		});
		capComboBox.setModel(new DefaultComboBoxModel<LineCap>(LineCap.values()));
		drawingControlPanel.add(capComboBox);
		
		lblJoin = new JLabel("Join");
		drawingControlPanel.add(lblJoin);
		
		joinComboBox = new JComboBox<LineJoin>();
		joinComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				drawingPanel.updateUI();
			}
		});
		joinComboBox.setModel(new DefaultComboBoxModel<LineJoin>(LineJoin.values()));
		drawingControlPanel.add(joinComboBox);

		chckbxClosePath = new JCheckBox("Close Path");
		chckbxClosePath.setHorizontalTextPosition(SwingConstants.LEADING);
		drawingControlPanel.add(chckbxClosePath);
		chckbxClosePath.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if( drawingPanel != null ) {
					drawingPanel.updateUI();
				}
			}
		});

		chckbxClosePath.setSelected(true);

		controlPanel.add(loadButton);

		lblHoldTheControl = new JLabel("Hold the control key down to add points.");
		controlPanel.add(lblHoldTheControl);

		imageControlPanel = new JPanel();
		controlPanel.add(imageControlPanel);

		JButton btnHelp = new JButton("");
		controlPanel.add(btnHelp);
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionHelp();
			}
		});
		btnHelp.setPreferredSize(new Dimension(32, 32));
		btnHelp.setIcon(new ImageIcon(Editor.class.getResource("/HelpBlack.png")));
		btnHelp.setToolTipText("Help");
		controlPanel.add(btnHelp);
		//Image White Threshold 
		JLabel whiteThresholdLabel = new JLabel("Image White Threshold");
		whiteThresholdLabel.setToolTipText("Threshold used when converting an image to black and white");
		imageControlPanel.add(whiteThresholdLabel);

		whiteThresholdSpinner = new JSpinner();
		imageControlPanel.add(whiteThresholdSpinner);
		whiteThresholdSpinner.setModel(new SpinnerNumberModel(200, 1, 255, 1));
		whiteThresholdSpinner.setToolTipText(whiteThresholdLabel.getToolTipText());
		
		JLabel lblNoiseThreshold = new JLabel("Noise threshold");
		imageControlPanel.add(lblNoiseThreshold);
		// Neighbor 
		lblNoiseThreshold.setToolTipText("Neighbor count when removing noise");
		noiseSpinner = new JSpinner();
		noiseSpinner.setModel(new SpinnerNumberModel(1, 0, 4, 1));
		noiseSpinner.setToolTipText(lblNoiseThreshold.getToolTipText());
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
				g1.setColor(Color.white);
				g1.fillRect(0, 0, getWidth(), getHeight());


				if( loadedImage != null) {
					g1.drawImage(loadedImage, 10, 10, loadedImage.getWidth(), loadedImage.getHeight(), null);
					g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
					g.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
					g.setColor(Color.LIGHT_GRAY);
					
					if( selection != null ) {
						g.draw(selection);
						
					} else {
						drawImageFromPoints(g1,false);
						g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
						drawControls(g);
					}
				} else {
					
					drawGrid(g1);					
					drawImageFromPoints(g1,false);
					g.drawImage(image, 0, 0, getWidth(), getHeight(), null);

					drawControls(g);
				}
			}

			private void drawControls(Graphics2D g) {

				//  Control points
				int lines = 0;
				int points = 0;
				Point2D first = null;
				Point2D last = null;
				g.setColor(Color.green);
				for (Segment segment : segments) {
					if( segment.type==SegmentType.Line) {
						lines++;
					} 
					points+=segment.points.size();

					for(int idx=0,sz=segment.points.size(); idx < sz; idx++ ) {

						Point2D p = segment.points.get(idx).point;
						if( first == null ) {
							first = p;
						}
						g.fillOval((int)p.getX()-2, (int)p.getY()-2, 4, 4);
						Ellipse2D el = segment.points.get(idx).ellipse;
						g.draw(el);
						if( segment.type == SegmentType.Curve) {
							if( last != null ) {
								g.drawLine((int)last.getX(), (int)last.getY(), (int)p.getX(), (int)p.getY());
							}
						}
						last = p;
					}
					if(segment.type == SegmentType.Curve ) {
						g.setColor(Color.red);
						g.draw(segment.paths.get(0));
						g.setColor(Color.green);
					}

				}

				if( lines > 0 && last != null && points>2 && chckbxClosePath.isSelected()) {
					if( last != null) {
						g.drawLine((int)last.getX(), (int)last.getY(), (int)first.getX(), (int)first.getY());
					}
				}

				if( currentSegment != null ) {
					if( currentPoint >= 0 && currentPoint< currentSegment.points.size()) {
						if( selected ) {
							g.setColor(Color.blue);
							Point2D p = currentSegment.points.get(currentPoint).point;

							g.drawLine(0, (int)p.getY(), getWidth(), (int)p.getY());
							g.drawLine((int)p.getX(), 0, (int)p.getX(),getHeight());
						} else {
							g.setColor(Color.red);
						}
						g.fill(currentSegment.points.get(currentPoint).ellipse);
					}

					if( currentLine >=0  ) {
						GeneralPath path = currentSegment.paths.get(currentLine);
						g.setColor(Color.ORANGE);
						g.draw(path);

					}
				}

				if( debugPoint != null ) {
					g.setColor(Color.ORANGE);
				}

				if( addMode && lastMouse != null ) {
					g.drawLine(0, (int)lastMouse.getY(), getWidth(), (int)lastMouse.getY());
					g.drawLine((int)lastMouse.getX(), 0, (int)lastMouse.getX(),getHeight());

				}
			}


		};
		drawingPanel.setBorder(new TitledBorder(null, "Drawing panel", TitledBorder.LEADING, TitledBorder.TOP, null, null));

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
					g.drawImage(debugImage, 30, 30, null);
				} else {
					g.drawString("No Debug Image", getWidth()/2, getHeight()/2);
				}
			}
		};
		debugPanel.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Result Panel", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));


		splitPane.setRightComponent(debugPanel);

		debugControlPanel = new JPanel();
		debugControlPanel.setVisible(false);
		contentPane.add(debugControlPanel, BorderLayout.WEST);

		btnPrintPoints = new JButton("Print Points");
		btnPrintPoints.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPrintPoints();
			}
		});
		debugControlPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		debugControlPanel.add(btnPrintPoints);
		drawingPanel.addMouseMotionListener(new MouseMotionAdapter() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if(!addMode && loadedImage == null && segments.size() == 0 ) {
					drawingPanel.setToolTipText("Press and hold CTRL to add points at "+e.getX()+","+e.getY());
				} else {
					drawingPanel.setToolTipText(""+e.getX()+","+e.getY());
				}
			}
		});


		drawingPanel.addKeyListener(new KeyListener() {

			@Override
			public void keyTyped(KeyEvent e) {


			}

			@Override
			public void keyReleased(KeyEvent e) {
				if( e.getKeyCode() == KeyEvent.VK_CONTROL) {
					drawingPanel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
					addMode = false;
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
				super.mouseMoved(e);
			}
		});

		drawingPanel.addMouseMotionListener(new MouseAdapter() {
		
			
			@Override
			public void mouseMoved(MouseEvent e) {
				lastMouse = e.getPoint();
				if( popupToCurve.isShowing() || popupToLine.isShowing()) {
					return;
				}
				segmentIndex = -1;
				currentPoint = -1;
				currentLine = -1;
				currentSegment = null;
				Point2D p = new Point2D.Double(e.getX(), e.getY());
				for (int sidx = 0, sz1 = segments.size(); sidx < sz1; sidx++) {
					Segment s = segments.get(sidx);
				
					for (int idx=0,sz=s.points.size(); idx < sz; idx++) {
						if( s.points.get(idx).ellipse.contains(p)) {
							currentSegment = s;
							currentPoint = idx;
							segmentIndex = sidx;
							break;
						}
					}
					if( currentPoint == -1) {
						for (int idx = 0, sz = s.paths.size(); idx < sz; idx++) {
							if( s.paths.get(idx).contains(p)) {
								currentSegment = s;
								currentLine = idx;
								segmentIndex = sidx;
								break;
							}
						}
					}
				}

				drawingPanel.updateUI();
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				lastMouse = e.getPoint();
				if( popupToCurve.isShowing() || popupToLine.isShowing()) {
					return;
				}
				if(loadedImage == null) {
					if(currentSegment != null && !addMode && currentPoint >= 0 && selected) {
						currentSegment.update(currentPoint, snapTo(e.getPoint()));
						drawingPanel.updateUI();					
					}
				} else {
					//  image is loaded
					if(currentSegment != null && !addMode && currentPoint >= 0 && selected) {
						currentSegment.update(currentPoint, snapTo(e.getPoint()));
						drawingPanel.updateUI();					
					} else if( selection == null ) {
						selection = new Rectangle(lastMouse);
						selection.width = selection.height = 1;
						segments.clear();
						currentLine = currentPoint = -1;
						currentSegment = null;
					} else {
						selection.setBounds(selection.x, selection.y, Math.abs(selection.x-lastMouse.x), Math.abs(selection.y-lastMouse.y));
					}
					drawingPanel.updateUI();
					
				}
			}

		});

		drawingPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent e) {
				lastMouse = e.getPoint();
				drawingPanel.requestFocus();
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				lastMouse = e.getPoint();
				if( popupToCurve.isShowing() || popupToLine.isShowing()) {
					return;
				}
				//if(loadedImage == null) {
					if(addMode) {
						int sz = segments.size();
						currentSegment = null;
						if( sz>0) {
							currentSegment = segments.get(sz-1);
						}
						if( currentSegment == null ) {
							if( sz == 0 ) {
								currentSegment = new Segment();
								segments.add(currentSegment);
							}
						}

						Point2D p = snapTo(e.getPoint());
						
						ControlPoint point = new ControlPoint(p);

						if( currentSegment.points.size() < 2) {
							currentSegment.add(point);
							lblHoldTheControl.setVisible(false);
						} else {
							Segment tmp = new Segment();
							tmp.add(currentSegment.points.get(currentSegment.points.size()-1));
							tmp.add(point);
							segments.add(tmp);
							currentSegment = tmp;
						}

						drawingPanel.updateUI();
					} else if( loadedImage != null ) {
						segments.clear();
					}
				//}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				lastMouse = e.getPoint();
				selection = null;
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
				} else {//if( loadedImage == null ) {
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
				
				lastMouse = e.getPoint();
				if( popupToCurve.isShowing() || popupToLine.isShowing()) {
					return;
				}
				selected = false;
				drawingPanel.updateUI();
				if( selection != null ) {
					segments.clear();
					ControlPoint c1 = (new ControlPoint(new Point2D.Double(selection.x, selection.y)));
					ControlPoint c2 = (new ControlPoint(new Point2D.Double(selection.x+selection.width, selection.y)));
					ControlPoint c3 = (new ControlPoint(new Point2D.Double(selection.x+selection.width, selection.y+selection.height)));
					ControlPoint c4 = (new ControlPoint(new Point2D.Double(selection.x, selection.y+selection.height)));
					Segment s1 = new Segment();
					s1.add(c1);
					s1.add(c2);
					segments.add(s1);
					s1 = new Segment();
					s1.add(c2);
					s1.add(c3);
					segments.add(s1);
					
					s1 = new Segment();
					s1.add(c3);
					s1.add(c4);
					segments.add(s1);
					
					s1 = new Segment();
					s1.add(c4);
					s1.add(c1);
					segments.add(s1);
					
					selection = null;
				}
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

		item = new JMenuItem("Add control point to line");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionAddControllPoint();
			}
		});
		popupToCurve.add(item);

		item = new JMenuItem("Remove segment");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionRemoveSegmwent();
			}
		});

		popupToCurve.add(item);


		popupToLine = new JPopupMenu("curve pop");
		item = new JMenuItem("Add control point to curve");
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

		item = new JMenuItem("Remove segment");
		item.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				actionRemoveSegmwent();
			}
		});

		popupToLine.add(item);


	}

	protected void actionResnap() {
		snapTo = (int) snapToSpinner.getValue();
		snapHalf = snapTo/2;
		for (Segment s : segments) {
			for (ControlPoint cp : s.points) {
				cp.update(snapTo(cp.point));
			}
			
		}
		
		drawingPanel.updateUI();
		
	}

	private Point2D snapTo(Point2D p) {
		int x = (int) p.getX();
		int y = (int) p.getY();
		
		int delta =  (x % snapTo);
		if( delta >= snapHalf) {
			x +=  snapTo - delta;	
		} else {
			x -=  delta;
		}
		delta =  (y % snapTo);
		 if( delta >= snapHalf) {
			 y +=  snapTo - delta;	
		 } else {
			 y -=  delta;
		 }
		
		 return new Point2D.Double(x, y);
		
	}

	protected void actionRemoveSegmwent() {
		int idx=segments.lastIndexOf(currentSegment);
		if( idx == 0 || idx == segments.size()-1) {
			segments.remove(idx);
		} else {
			Segment s2 = segments.get(idx+1);
			s2.points.set(0, currentSegment.points.get(0));
			segments.remove(idx);
		}
	}

	protected void actionPrintPoints() {

		StringBuilder buf = new StringBuilder();

		for (Segment s : segments) {
			for (ControlPoint p : s.points) {
				buf.append(p.point.toString());
				buf.append('\n');
			}
		}

		textArea.setText(buf.toString());
	}

	

	protected void drawGrid(Graphics2D g) {
		int w = drawingPanel.getWidth();
		int h = drawingPanel.getHeight();
		g.setColor(gridColor);
		for(int y=0; y < h; y+=snapTo) {
			g.drawLine(0, y, w, y);
		}
		for(int x=0; x < w; x+=snapTo) {
			g.drawLine(x, 0, x, h);
		}

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
		debugImage = null;
		setThresholdVisbility();
		drawingPanel.updateUI();
		debugPanel.updateUI();
		lblHoldTheControl.setVisible(true);
	}

	private void setThresholdVisbility() {
		imageControlPanel.setVisible(loadedImage != null);
		drawingControlPanel.setVisible(loadedImage == null);
	}

	protected void drawImageFromPoints(Graphics2D g1,boolean close) {
		GeneralPath path = new GeneralPath();
		
		Segment last = null;
		//g1.setStroke(new BasicStroke((int) lineWidthSpinner.getValue()+2,0, 2));
		for (int idx = 0, sz = segments.size(); idx < sz; idx++) {
			Segment s = segments.get(idx);
			s.recalcPaths();

			if( last != null  && last.points.size()>0 && s.points.size()>0) {
				Point2D a = last.points.get(last.points.size()-1).point;
				Point2D b = s.points.get(0).point;
				g1.drawLine((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
			} else {
				Point2D p = s.points.get(0).point;
				path.moveTo(p.getX(), p.getY());
			}

			drawSegment(path,s);
			last = s;
		}

		if( close ) {
			if( chckbxClosePath.isSelected()) {
				if( segments.size()>1) {
					path.closePath();
				}
			}
		}
		if( loadedImage != null ) {
			g1.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{9}, 0));
			g1.setColor(Color.LIGHT_GRAY);
			
		} else {
			g1.setColor(Color.black);
			g1.setStroke(new BasicStroke((int) lineWidthSpinner.getValue()+2,capComboBox.getSelectedIndex(), joinComboBox.getSelectedIndex()));
		}
		g1.draw(path);
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

	protected void actionAddControllPoint() {
		if( currentSegment != null ) {
			if( currentSegment.type == SegmentType.Curve) {
				if( currentSegment.points.size()==3) {
					Point2D a = currentSegment.points.get(1).point;
					Point2D b = currentSegment.points.get(2).point;
					Line2D line = new Line2D.Double(a, b);
					double cx = line.getBounds2D().getCenterX();
					double cy = line.getBounds2D().getCenterY();

					debugPoint = new Point2D.Double(cx, cy);
					currentSegment.points.add(2, new ControlPoint(debugPoint));
					drawingPanel.updateUI();
				}
			} else {
				ControlPoint a = currentSegment.points.get(0);
				ControlPoint b = currentSegment.points.get(1);
				
				Line2D line = new Line2D.Double(a.point, b.point);
				double cx = line.getBounds().getCenterX();
				double cy = line.getBounds().getCenterY();
				ControlPoint newPoint = new ControlPoint(new Point2D.Double(cx, cy));
				currentSegment.points.set(1, newPoint);
				
				Segment newSegment = new Segment();
				
				
				newSegment.add(newPoint);
				newSegment.add(b);
				
				
				segments.add(segmentIndex+1, newSegment);
				currentPoint = 1;
				drawingPanel.updateUI();
			}
		}
	}

	protected void actionConvertToLine() {
		if( currentSegment != null ) {
			if( currentSegment.type == SegmentType.Curve) {
				currentSegment.type = SegmentType.Line;

				while( currentSegment.points.size()>2) {
					currentSegment.points.remove(1);	
				}
				drawingPanel.updateUI();
			}
		}

	}

	protected void actionConvertToCurve() {
		if( currentLine >= 0 && currentSegment != null) {
			if( currentSegment.type == SegmentType.Line) {
				Line2D line = new Line2D.Double(currentSegment.points.get(0).point, currentSegment.points.get(1).point);
				double cx = line.getBounds().getCenterX();
				double cy = line.getBounds().getCenterY();
				currentSegment.points.add(1, new ControlPoint(new Point2D.Double(cx, cy)));
				currentSegment.type = SegmentType.Curve;
				currentPoint = 1;
				drawingPanel.updateUI();
			}
		}


	}

	private File file = new File("C:\\Users\\Tony\\Documents\\ATest.png");
	private BufferedImage loadedImage;
	private int currentLine;
	private Rectangle selection;

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
					debugImage = null;
					debugPanel.updateUI();
				}
			} catch (Throwable e) {
				logError(e, "Error loading "+file);
			}
		}

	}

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
			g.setColor(new Color(Color.red.getRed(), Color.red.getGreen(), Color.red.getBlue(), 100));

			if( chckbxClosePath.isSelected()) {
				g.fill(path);
			} else {
				g.setStroke(new BasicStroke((int)(lineWidthSpinner.getValue())+2,capComboBox.getSelectedIndex(), joinComboBox.getSelectedIndex()));
				g.draw(path);	
			}
			updateDebug(g);
		}

		return ret;
	}

	public boolean[][] toBoolean(BufferedImage img) {
		int BLACK = Color.black.getRGB();
		int WHITE = Color.white.getRGB();

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

	private DebugFrame debugFrame;
	private boolean debug = false;
	
	protected void actionPolygon() {
		textArea.setText("");
		if( image != null ) {
			debugImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_4BYTE_ABGR);
			debugPanel.updateUI();

			new Thread(new Runnable() {


				@Override
				public void run() {

					Graphics2D dbg = (Graphics2D) debugImage.getGraphics();
					int firstY = 0;
					int firstX = image.getWidth();
					BufferedImage image1 = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
					if( loadedImage != null ) {
						
						image1 = loadedImage;
						if( selection != null ) {
							
							if( selection.width > 1 || selection.height>1) {
								int w = selection.width;
								if( selection.x+w > loadedImage.getWidth()) {
									w = loadedImage.getWidth()-selection.x;
								}
								int h = selection.height;
								if( selection.y+h > loadedImage.getHeight()) {
									h = loadedImage.getHeight()-selection.y;
								}
								
								image1 = loadedImage.getSubimage(selection.x, selection.y, w, h);
							}
						} else if(segments.size()>2) {
							GeneralPath path = new GeneralPath();
							for (Segment s : segments) {
								for (ControlPoint cp : s.points) {
									if(path.getCurrentPoint() == null ) {
										path.moveTo(cp.point.getX(), cp.point.getY());
									} else {
										path.lineTo(cp.point.getX(), cp.point.getY());
									}
								}
							}
							path.closePath();
							image1 = new BufferedImage(loadedImage.getWidth(), loadedImage.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
							Graphics2D g1 = (Graphics2D) image1.getGraphics();
							g1.setColor(Color.white);
							g1.fillRect(0, 0, image1.getWidth(), image1.getHeight());
							g1.setClip(path);
							g1.drawImage(loadedImage, 0, 0, null);
							g1.dispose();
						}
						
					} else {
						Graphics2D g1 = (Graphics2D) image1.getGraphics();
						g1.setColor(Color.white);
						g1.fillRect(0, 0, image1.getWidth(), image1.getHeight());
							
						
						drawImageFromPoints(g1,true);
						g1.dispose();
					}
					boolean[][] bimg = toBoolean(image1);
					if( loadedImage != null ) {
						removeNoise(bimg);
					}
					Rectangle p = new Rectangle(image1.getWidth(), image1.getHeight());
					List<Rectangle> diff = ImageDiff.findEdges(bimg, p, 2, 2);

					if( diff.size()== 0 ) {
						throw new RuntimeException("No Image data");
					}

					int translate = 0;
					//  show drawing in the debug panel
					int color = Color.green.getRGB();
					color = new Color(Color.green.getRed(), Color.green.getGreen(), Color.green.getBlue(), 100).getRGB();
					for (int idx = 0,sz=diff.size(); idx < sz; idx++) {
						Rectangle r = diff.get(idx);
						for(int y= r.y,hh=(r.y+r.height)-1; y < hh; y++ ) {
							for(int x= r.x,ww=(r.x+r.width)-1; x < ww; x++ ) {
								if( bimg[y][x] ) {
									try {
										debugImage.setRGB(x, y, color);
										if( firstY == 0 ) {
											firstY =  y;
											firstX =  x;
										}
									}catch (Exception e) {
									}
								}
							}
						}

						updateDebug(dbg);

						List<Point> points1 = trace(null, p, bimg, firstX, firstY);
						double epsilon = (double)epsilonSpinner.getValue();
						if( epsilon >= 0 ) {
							List<Point> points2 = new ArrayList<Point>();
							LineSimplification.ramerDouglasPeucker(points1, epsilon, points2);
							points1 = points2;
						} 
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

	protected void setText2D(List<Point2D> list, int translate) {
		StringBuilder buf = new StringBuilder();
		if( translate > 0 ) {
			buf.append("\ntranslate([0,"+translate+",0])\n");
		}

		buf.append("\npolygon( points=\n\t[\n\t");

		double minX = 0;
		double minY = 0;

		if( list.size() > 0 ) {
			minX = minY = Integer.MAX_VALUE;
			for (Point2D point : list) {
				minX = Math.min(minX, point.getX());
				minY = Math.min(minY, point.getY());
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
				textArea.append(buf.toString());
			}
		});


	}

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


		return ret;
	}


	public void logError(Throwable error, String title) {
	
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

	protected void drawImageFromPoints1(Graphics2D g1,boolean close) {
		Segment last = null;
		g1.setStroke(new BasicStroke((int) lineWidthSpinner.getValue()+2,capComboBox.getSelectedIndex(), joinComboBox.getSelectedIndex()));
		
		for (int idx = 0, sz = segments.size(); idx < sz; idx++) {
			Segment s = segments.get(idx);
			s.recalcPaths();
	
			if( last != null  && last.points.size()>0 && s.points.size()>0) {
				Point2D a = last.points.get(last.points.size()-1).point;
				Point2D b = s.points.get(0).point;
				g1.drawLine((int)a.getX(), (int)a.getY(), (int)b.getX(), (int)b.getY());
			}
	
			drawSegment1(g1,s);
			last = s;
		}
	
		if( close ) {
			if( chckbxClosePath.isSelected()) {
				if( segments.size()>1) {
					Point2D p1 = segments.get(0).points.get(0).point;
					Point2D p2 = last.points.get(last.points.size()-1).point;
	
					g1.drawLine((int)p1.getX(), (int)p1.getY(), (int)p2.getX(), (int)p2.getY());
				}
			}
		}
	}

	public static void drawSegment(GeneralPath path,Segment segment) {
		if( segment.type == SegmentType.Curve) {
			if( segment.points.size() == 3) {
				path.quadTo(segment.points.get(1).point.getX(), segment.points.get(1).point.getY(), 
						segment.points.get(2).point.getX(), segment.points.get(2).point.getY());
			} else if( segment.points.size() == 4) {
				path.curveTo(segment.points.get(1).point.getX(), segment.points.get(1).point.getY(), 
						segment.points.get(2).point.getX(), segment.points.get(2).point.getY(),
						segment.points.get(3).point.getX(), segment.points.get(3).point.getY()
						);
			} else if( segment.points.size() == 2) {
				path.lineTo((int)segment.points.get(1).point.getX(),	 (int)segment.points.get(1).point.getY());
			} else {
				throw new RuntimeException("Curve with too many points = "+segment.points.size());
			}
		} else {
			if( segment.points.size() == 2) {
				path.lineTo((int)segment.points.get(1).point.getX(),	 (int)segment.points.get(1).point.getY());
			}
		}
	}

	public static void drawSegment1(Graphics2D g1,Segment segment) {
		g1.setColor(Color.black);
		if( segment.type == SegmentType.Curve) {
			if( segment.points.size() == 3) {
				GeneralPath path = new GeneralPath();
				path.moveTo(segment.points.get(0).point.getX(), segment.points.get(0).point.getY());
				path.quadTo(segment.points.get(1).point.getX(), segment.points.get(1).point.getY(), 
						segment.points.get(2).point.getX(), segment.points.get(2).point.getY());
				g1.draw(path);
			} else if( segment.points.size() == 4) {
				GeneralPath path = new GeneralPath();
				path.moveTo(segment.points.get(0).point.getX(), segment.points.get(0).point.getY());
				path.curveTo(segment.points.get(1).point.getX(), segment.points.get(1).point.getY(), 
						segment.points.get(2).point.getX(), segment.points.get(2).point.getY(),
						segment.points.get(3).point.getX(), segment.points.get(3).point.getY()
						);
				g1.draw(path);
			} else if( segment.points.size() == 2) {

				g1.drawLine((int)segment.points.get(0).point.getX(),(int) segment.points.get(0).point.getY(), 
						(int)segment.points.get(1).point.getX(),	 (int)segment.points.get(1).point.getY());
			} else {
				throw new RuntimeException("Curve with too many points = "+segment.points.size());
			}
		} else {
			if( segment.points.size() == 2) {
				g1.drawLine((int)segment.points.get(0).point.getX(),(int) segment.points.get(0).point.getY(), 
						(int)segment.points.get(1).point.getX(),	 (int)segment.points.get(1).point.getY());
			}
		}
	}

}
