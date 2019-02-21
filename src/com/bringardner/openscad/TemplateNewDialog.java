package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class TemplateNewDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	protected boolean cancel;
	private TemplatePanel templatePanel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			TemplateNewDialog dialog = new TemplateNewDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public TemplateNewDialog() {
		setLocationRelativeTo(null);
		setBounds(100, 100, 831, 341);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			templatePanel = new TemplatePanel(new Template("","",""));
			templatePanel.hideDelete();
			contentPanel.add(templatePanel);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Template tmp = templatePanel.getTemplate();
						String name = tmp.getName();
						if( name != null) {
							name = name.trim();
						}
						if(name == null || name.isEmpty()) {
							JOptionPane.showMessageDialog(TemplateNewDialog.this, "Name is a required Field", "Warning", JOptionPane.WARNING_MESSAGE);
							return;
						}

						cancel = false;
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
						cancel = true;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public boolean showDialog() {
		setModal(true);
		setLocationRelativeTo(null);
		setVisible(true);
		
		return !cancel;
	}
	
	public Template getTemplate() {
		return templatePanel.getTemplate();
	}
	

}
