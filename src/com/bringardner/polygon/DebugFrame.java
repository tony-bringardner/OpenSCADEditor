package com.bringardner.polygon;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class DebugFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private Point current;
	private Point last;
	private boolean [][] img;
	private List<Point> list;
	private JPanel panel;
	private JSpinner widthSpinner;
	private JSpinner heightSpinner;
	private JSpinner sizeSpinner;
	private boolean isVisable;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					DebugFrame frame = new DebugFrame();
					Point last2 = new Point(3,8);
					boolean[][] bimg= {
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,true ,false,false,false,false,false,false},
							{false,false,false,false,true ,true ,false,false,false,false,false,false},
							{false,false,false,true ,true ,true ,false,false,false,false,false,false},
							{false,false,false,true ,true ,true ,false,false,false,false,false,false},
							{false,false,false,false,true ,true ,false,false,false,false,false,false},
							{false,false,false,false,false,true ,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
							{false,false,false,false,false,false,false,false,false,false,false,false},
					};
					List<Point> list2 = new ArrayList<>();
					list2.add(new Point(4,9));
					frame.setValues(3, 7, bimg, list2, last2);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}


	/**
	 * Create the frame.
	 */
	public DebugFrame() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1092, 713);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		panel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paintComponent(Graphics g1) {
				Graphics2D g = (Graphics2D)g1;
				g1.clearRect(0, 0, getWidth(), getHeight());
				if( current != null ) {
					int w = (int) widthSpinner.getValue();
					int h = (int) heightSpinner.getValue();
					int sz = (int) sizeSpinner.getValue();
					int startX = current.x-(w/2);
					int startY = current.y-(h/2);

					for(int y=0; y < h; y++ ) {
						for(int x=0; x < w; x++ ) {
							int cx = startX+x;
							int cy = startY+y;
							if(cx < 0 || cy < 0 || cx >= img[0].length || cy >= img.length) {
								g.setColor(Color.blue);
							} else if( current.x == cx && current.y == cy) {
								g.setColor(Color.red);
							} else if(last != null && last.x == cx && last.y == cy) {
								g.setColor(Color.yellow);
							} else if( img[cy][cx] ) {
								g.setColor(Color.black);
							} else {
								g.setColor(Color.lightGray);	
							}

							int posx = x*sz;
							int posy = y*sz;
							g.fillRect(posx, posy, sz, sz);								
						}
					}
					g.setColor(Color.white);
					for(int y=0; y < h; y++ ) {
						g.drawLine(0, y*sz, getWidth(), y*sz);
					}
					for(int x=0; x < w; x++ ) {
						g.drawLine(x*sz, 0, x*sz, getHeight());
					}
					java.awt.FontMetrics fm = g.getFontMetrics();
					int th = fm.getHeight();
					//g.setColor(Color.green);
					for(int y=0; y < h; y++ ) {
						for(int x=0; x < w; x++ ) {
							int cx = startX+x;
							int cy = startY+y;
							if(cx < 0 || cy < 0 || cx >= img[0].length || cy >= img.length) {
								g.setColor(Color.yellow);
							} else if( current.x == cx && current.y == cy) {
								g.setColor(Color.white);
							} else if(last !=null && last.x == cx && last.y == cy) {
								g.setColor(Color.black);
							} else if( img[cy][cx] ) {
								g.setColor(Color.white);
							} else {
								g.setColor(Color.white);	
							}

							int posx = x*sz;
							int posy = (y+1)*sz;
							String str = ""+cx+","+cy;
							int tw = fm.stringWidth(str);
							g.drawString(str, posx+(tw/2), posy-(th/2));
							if( list.contains(new Point(cx,cy))) {
								g.setColor(Color.magenta);
								g.fillOval(posx, posy-sz, 10, 10);
								g.setColor(Color.white);
							} 
						}
					}
				}
			}

		};
		panel.addMouseMotionListener(new MouseMotionListener() {

			@Override
			public void mouseMoved(MouseEvent e) {
				if( current != null ) {
					int w = (int) widthSpinner.getValue();
					int h = (int) heightSpinner.getValue();
					int sz = (int) sizeSpinner.getValue();
					
					int startX = current.x-(w/2);
					int startY = current.y-(h/2);
					int x = (int)(e.getX()/sz)+startX;
					int y = (int)(e.getY()/sz)+startY;
					panel.setToolTipText(""+x+","+y);
				}
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});
		panel.addMouseListener(new MouseListener() {

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub

			}
		});

		scrollPane.setViewportView(panel);

		JPanel controlPanel = new JPanel();
		contentPane.add(controlPanel, BorderLayout.NORTH);

		JLabel lblWidth = new JLabel("Width");
		controlPanel.add(lblWidth);

		widthSpinner = new JSpinner();
		widthSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				actionStateChanged();
			}
		});

		widthSpinner.setModel(new SpinnerNumberModel(new Integer(10), new Integer(3), null, new Integer(1)));
		controlPanel.add(widthSpinner);

		JLabel lblHeight = new JLabel("Height");
		controlPanel.add(lblHeight);

		heightSpinner = new JSpinner();
		heightSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				actionStateChanged();
			}
		});
		heightSpinner.setModel(new SpinnerNumberModel(new Integer(10), new Integer(3), null, new Integer(1)));
		controlPanel.add(heightSpinner);

		JLabel lblSize = new JLabel("Size");
		controlPanel.add(lblSize);

		sizeSpinner = new JSpinner();
		sizeSpinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				actionStateChanged();
			}
		});
		sizeSpinner.setModel(new SpinnerNumberModel(new Integer(70), new Integer(1), null, new Integer(1)));
		controlPanel.add(sizeSpinner);
		isVisable = true;
		addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowIconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeiconified(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowDeactivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				isVisable = false;
			}

			@Override
			public void windowActivated(WindowEvent e) {
				// TODO Auto-generated method stub

			}
		});


	}



	public boolean isDisplayable() {
		return isVisable;
	}


	protected void actionStateChanged() {
		int w = (int) widthSpinner.getValue();
		int h = (int) heightSpinner.getValue();
		int sz = (int) sizeSpinner.getValue();
		panel.setPreferredSize(new Dimension(w*sz, h*sz));
		panel.updateUI();		
	}


	public void setValues(int x, int y, boolean[][] bimg, List<Point> list2, Point last2) {
		current = new Point(x,y);
		last = last2;
		img = bimg;
		list = list2;
		int w = (int) widthSpinner.getValue();
		int h = (int) heightSpinner.getValue();
		int sz = (int) sizeSpinner.getValue();
		panel.setPreferredSize(new Dimension(w*sz, h*sz));
		panel.updateUI();
	}

}
