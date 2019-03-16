package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

public class Help extends JDialog {

	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			Help dialog = new Help();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 * @throws IOException 
	 */
	public Help() throws IOException {
		setIconImage(Editor.getOpenScadImage());
		setBounds(100, 100, 769, 618);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			JScrollPane scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				JEditorPane editorPane = new JEditorPane();
				editorPane.setContentType("text/html;charset=\"UTF-8\"");
				editorPane.setEditorKit(JEditorPane.createEditorKitForContentType("text/html"));
				editorPane.setEditable(false);
				editorPane.addHyperlinkListener(new HyperlinkListener() {
				    public void hyperlinkUpdate(HyperlinkEvent e) {
				        if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
				        	if(Desktop.isDesktopSupported()) {
				        	    try {
									Desktop.getDesktop().browse(e.getURL().toURI());
								} catch (Throwable e1) {
								}
				        	}
				        }
				    }
				});
				InputStream in = getClass().getResourceAsStream("/Help.html");
				try {
					byte data[] = new byte[1024*3];
					StringBuilder html = new StringBuilder();
					int got = in.read(data);
					while(got >=0) {
						html.append(new String(data,0,got));
						got = in.read(data);
					}
					
					editorPane.setText(html.toString());
					
				} finally {
					try {
						in.close();
					} catch (Exception e) {
					}
				}
				
				scrollPane.setViewportView(editorPane);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("Close");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
	}

}
