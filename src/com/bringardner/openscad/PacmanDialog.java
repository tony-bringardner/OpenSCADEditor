package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class PacmanDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JLabel label;
	private boolean canceled;
	private ImageIcon imageRight;
	private ImageIcon imageLeft;
	private BufferedImage speakerOn;
	private BufferedImage speakerOff;
	
	private boolean disposed;
	private Object mutex = new Object();
	private int _dir = 1;
	private Clip clip;
	private Rectangle2D cancelBounds;
	
	private Point lastMouse=new Point(-10, 100);
	private Double speakerBounds;
	private Runnable cancelListener;
	private Color backgroundColor = Color.green;
	private Color foregroundColor = Color.cyan;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			PacmanDialog dialog = new PacmanDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public PacmanDialog(Component parent,Runnable cancelListener) {
		this();
		this.cancelListener = cancelListener;
		setLocationRelativeTo(parent);
	}
	
	/**
	 * Create the dialog.
	 */
	public PacmanDialog() {
		setUndecorated(true);
		setBounds(100, 100, 300, 214);
		getContentPane().setLayout(new BorderLayout());
		try {
			clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(getClass().getResource("/pacman_chomp.wav")));
				
		} catch (LineUnavailableException | IOException | UnsupportedAudioFileException e1) {
			e1.printStackTrace();
		}
		JPanel contentPanel = new JPanel() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void paint(Graphics g) {
				//System.out.println("in paint 1");
				synchronized (mutex) {
					Graphics2D g2 = (Graphics2D)g;
					setBackground(backgroundColor);
					super.paint(g);
					Rectangle bb = label.getBounds();

					Rectangle b = new Rectangle(0, 0, getWidth(), bb.y); 

					g2.setColor(foregroundColor);
					g2.fill(b);
					b.height = 20;
					b.y = bb.y;
					if( _dir > 0 ) {
						b.x = 0;
						b.width = bb.x+5;
					} else {
						b.x = bb.x+28;
						b.width = getWidth()+10;
					}
					g2.fill(b);
					// draw cancel image
					int r = 25;
					Rectangle bbb = getBounds();
					FontMetrics fm = g2.getFontMetrics();
					Rectangle2D tw = fm.getStringBounds("Cancel", g2);
					int x = (int) (bbb.getCenterX()-r);
					int y = (int) (bbb.getCenterY()-r);
					cancelBounds = new Rectangle2D.Double(x, y, r*2, r*2);
					
					g2.setColor(Color.lightGray);
					g2.fillOval(x, y, r*2, r*2);
					if( cancelBounds.contains(lastMouse)) {
						g2.setColor(Color.RED);
					} else {
						g2.setColor(Color.BLACK);
					}

					g2.drawOval(x, y, r*2, r*2);
					
					g2.drawString("Cancel", (int) ((bbb.getCenterX()-(tw.getWidth()/2))), (int)(bbb.getCenterY()+tw.getHeight()/2));
					
					
					if( clip.isRunning()) {
						g2.drawImage(speakerOff, 0, 0, null);
					} else {
						g2.drawImage(speakerOn, 0, 0, null);
					}
					if( speakerBounds.contains(lastMouse)) {
						g2.setColor(Color.RED);
						g2.draw(speakerBounds);
					}
				}
			}
		};
		contentPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if( cancelBounds.contains(e.getPoint())) {
					actionCancel();
				} else if( speakerBounds.contains(e.getPoint())) {
					actionToggleSpeaker();
				}
			}
			
		});
		
		contentPanel.addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				lastMouse = e.getPoint();
			}
		});
		contentPanel.setBackground(new Color(152, 251, 152));
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			try {
				speakerOn = ImageIO.read(getClass().getResource("/SpeakerOn30x30.png"));
				speakerOff = ImageIO.read(getClass().getResource("/SpeakerOff30x30.png"));
				speakerBounds = new Rectangle2D.Double(0, 0, speakerOn.getWidth(), speakerOn.getHeight());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			imageRight = new ImageIcon(getClass().getResource("/pacmanRight.gif"));
			imageLeft = new ImageIcon(getClass().getResource("/pacmanLeft.gif"));
			label = new JLabel("",imageRight,JLabel.CENTER);
			label.setBounds(0, 0, 29, 24);

			contentPanel.add(label);

		}
		
		
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setVisible(false);
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						disposed = true;
						clip.stop();
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						actionCancel();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				int step = 27;
				int x = 0;
					
				int y = 0;
				int imgSize = imageLeft.getIconHeight();
				while( !disposed ) {
					int stepx = 5*_dir;
					while(x <= (getWidth()-45) && x >= 0) {
						final int myY = y;
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
						}
						final int myX = x+=stepx;
						synchronized (mutex) {
							SwingUtilities.invokeLater(new Runnable() {
								
								@Override
								public void run() {
									label.setBounds(myX, myY, 35, 24);
									contentPanel.updateUI();
								}
							});
							
						}


					}
					y+=step;
					if( y >= (getHeight()-imgSize)) {
						y = 0;
						Color tmp = foregroundColor;
						foregroundColor = backgroundColor;
						backgroundColor = tmp;

					}
					synchronized (mutex) {
						_dir *= -1;
					}
					stepx = 10*_dir;
					if( _dir==1 ) {
						x = 0;
						SwingUtilities.invokeLater(new Runnable() {
							
							@Override
							public void run() {
								label.setIcon(imageRight);
								contentPanel.updateUI();
							}
						});

					} else {

						x = (getWidth()-45);
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								label.setIcon(imageLeft);
								contentPanel.updateUI();
							}
						});
					}


					final int myX = x;
					final int myY = y;
					SwingUtilities.invokeLater(()->label.setBounds(myX, myY, 35, 24));
					contentPanel.updateUI();
				} //  while true

			}
		}).start();
		
		addWindowListener(new WindowListener() {
			
			@Override
			public void windowOpened(WindowEvent e) {
				
				
			}
			
			@Override
			public void windowIconified(WindowEvent e) {
				
				
			}
			
			@Override
			public void windowDeiconified(WindowEvent e) {
				
				
			}
			
			@Override
			public void windowDeactivated(WindowEvent e) {
				
				
			}
			
			@Override
			public void windowClosing(WindowEvent e) {
				
				
			}
			
			@Override
			public void windowClosed(WindowEvent e) {
				clip.stop();
			}
			
			@Override
			public void windowActivated(WindowEvent e) {
				
				
			}
		});
	}

	private void actionToggleSpeaker() {
		if( clip.isRunning()) {
			clip.stop();
		} else {
			clip.start();
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		}
		
	}

	private void actionCancel() {
		canceled = true;
		disposed = true;
		clip.stop();
		dispose();
		if( cancelListener != null ) {
			cancelListener.run();
		}
	}

	public boolean isCanceled() {
		return canceled;
	}
	
	
}
