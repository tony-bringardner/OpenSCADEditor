package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class ExecFileDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private JTextField pathTextField;
	protected boolean canceled;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ExecFileDialog dialog = new ExecFileDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ExecFileDialog() {
		setIconImage(Editor.getOpenScadIcon());
		setBounds(100, 100, 538, 258);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblPathToOpenscad = new JLabel("Path to OpenSCAD executable : ");
		lblPathToOpenscad.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblPathToOpenscad.setBounds(10, 24, 231, 17);
		contentPanel.add(lblPathToOpenscad);
		
		pathTextField = new JTextField();
		pathTextField.setText("T:\\Applications\\OpenSCAD\\openscad.exe");
		pathTextField.setBounds(10, 65, 318, 20);
		contentPanel.add(pathTextField);
		pathTextField.setColumns(10);
		
		JButton btnNewButton = new JButton("Browse");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionBrowse();
			}
		});
		
		btnNewButton.setBounds(338, 64, 89, 23);
		contentPanel.add(btnNewButton);
		
		JLabel lblOutputLabel = new JLabel("Output Label\\nLine 2");
		lblOutputLabel.setVerticalAlignment(SwingConstants.TOP);
		lblOutputLabel.setHorizontalAlignment(SwingConstants.LEFT);
		lblOutputLabel.setBounds(10, 107, 462, 68);
		contentPanel.add(lblOutputLabel);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
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
						canceled = true;
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public boolean showDialog(String path) {
		this.pathTextField.setText(path);
		setModal(true);
		return !canceled;
	}
	
	public String getPath() {
		return pathTextField.getText();
	}

	protected void actionBrowse() {
		// TODO Auto-generated method stub
		
	}
}
