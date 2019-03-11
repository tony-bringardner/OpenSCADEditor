package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class PreferencesFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JSpinner maxRecentSpinner;
	private JSpinner maxBackupSpinner;
	private JSpinner minVariableNameLengthSpinner;
	private JTextField backupFolderTextField;
	private JTextField execTextField;
	private JTextArea textArea;
	private Editor editor;
	private JCheckBox chckbxEnablePacmanEport;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PreferencesFrame frame = new PreferencesFrame(new Editor());
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
	public PreferencesFrame(Editor editor) {
		setIconImage(Editor.getOpenScadIcon());
		this.editor = editor;
		Configuration config = Configuration.getInstance();
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 760, 537);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		JButton btnSaveChanges = new JButton("Save Changes");
		btnSaveChanges.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnSaveChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actioinSaveChanges();
			}
		});
		
		JButton btnRestoreDefaults = new JButton("Restore Defaults");
		btnRestoreDefaults.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel.add(btnRestoreDefaults);
		btnRestoreDefaults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actinRestoreDefaults();
			}
		});
		
		JButton btnUndoChanges = new JButton("Undo Changes");
		btnUndoChanges.setFont(new Font("Tahoma", Font.PLAIN, 13));
		panel.add(btnUndoChanges);
		btnUndoChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionUndoChanges();
			}
		});
		panel.add(btnSaveChanges);
		
		JButton btnCancel = new JButton("Close Without saving");
		btnCancel.setFont(new Font("Tahoma", Font.PLAIN, 13));
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionCancel();
			}
		});
		panel.add(btnCancel);
		
		JPanel panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(null);
		
		JLabel lblMaxRecentFiles = new JLabel("Max Recent Files");
		lblMaxRecentFiles.setBounds(23, 31, 124, 14);
		panel_1.add(lblMaxRecentFiles);
		
		maxRecentSpinner = new JSpinner();
		maxRecentSpinner.setModel(new SpinnerNumberModel(config.getMaxRecent(), null, 30, 1));
		maxRecentSpinner.setBounds(147, 28, 48, 20);
		panel_1.add(maxRecentSpinner);
		
		JLabel lblMaxBackupFiles = new JLabel("Max Backup files");
		lblMaxBackupFiles.setBounds(23, 84, 118, 14);
		panel_1.add(lblMaxBackupFiles);
		
		maxBackupSpinner = new JSpinner();
		maxBackupSpinner.setModel(new SpinnerNumberModel(new Integer(4), new Integer(0), null, new Integer(1)));
		maxBackupSpinner.setBounds(147, 81, 48, 20);
		panel_1.add(maxBackupSpinner);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBorder(new TitledBorder(null, "Open SCAD Executable", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		FlowLayout fl_panel_2 = (FlowLayout) panel_2.getLayout();
		fl_panel_2.setAlignment(FlowLayout.LEFT);
		panel_2.setBounds(23, 170, 660, 49);
		panel_1.add(panel_2);
		
		execTextField = new JTextField();
		panel_2.add(execTextField);
		execTextField.setText(config.getExecPath());
		execTextField.setColumns(40);
		
		JButton browseExec = new JButton("Browse");
		browseExec.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionBrowse();
			}
		});
		panel_2.add(browseExec);
		
		JButton btnValidate = new JButton("Validate ");
		btnValidate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionValidate();
			}
		});
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		panel_2.add(horizontalStrut_1);
		panel_2.add(btnValidate);
		
		JPanel panel_3 = new JPanel();
		panel_3.setBounds(235, 67, 448, 49);
		panel_1.add(panel_3);
		
		backupFolderTextField = new JTextField();
		backupFolderTextField.setText(config.getBackupFolder());
		backupFolderTextField.setColumns(40);
		backupFolderTextField.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "Backup Folder", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		panel_3.add(backupFolderTextField);
		
		JButton browseBackupFolder = new JButton("Browse");
		panel_3.add(browseBackupFolder);
		
		JLabel lblMinimumVariableName = new JLabel("Minimum Variable Name Length");
		lblMinimumVariableName.setBounds(235, 31, 202, 14);
		panel_1.add(lblMinimumVariableName);
		
		minVariableNameLengthSpinner = new JSpinner();
		minVariableNameLengthSpinner.setModel(new SpinnerNumberModel(new Integer(config.getMinVariableNameLength()), null, null, new Integer(1)));
		minVariableNameLengthSpinner.setBounds(412, 28, 29, 20);
		panel_1.add(minVariableNameLengthSpinner);
		
		JPanel panel_4 = new JPanel();
		panel_4.setBounds(23, 230, 660, 123);
		panel_1.add(panel_4);
		panel_4.setLayout(new BorderLayout(0, 0));
		
		JScrollPane scrollPane = new JScrollPane();
		panel_4.add(scrollPane);
		
		textArea = new JTextArea();
		textArea.setLocation(23, 0);
		scrollPane.setViewportView(textArea);
		
		JButton btnNewButton = new JButton("Auto Complete Templates");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionEditemplates();
			}
		});
		btnNewButton.setBounds(483, 27, 200, 23);
		panel_1.add(btnNewButton);
		
		chckbxEnablePacmanEport = new JCheckBox("Enable Pacman Eport Dialog");
		chckbxEnablePacmanEport.setHorizontalTextPosition(SwingConstants.LEFT);
		chckbxEnablePacmanEport.setBounds(23, 126, 216, 23);
		chckbxEnablePacmanEport.setSelected(config.isPacmanEnabled());
		
		panel_1.add(chckbxEnablePacmanEport);
		String path = execTextField.getText();
		if( path != null && !path.trim().isEmpty()) {
			actionValidate();
		}
	}

	protected void actionEditemplates() {
		final TemplateFrame frame = new TemplateFrame();
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				frame.setVisible(true);
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						int changes = 0;
						
						while(!frame.isDisposed()) {
							if( frame.getChanges()!=changes) {
								changes = frame.getChanges();
								editor.createAutoComplete();
							}
							try {
								Thread.sleep(300);
							} catch (InterruptedException e) {
							}
						}
						
					}
				}).start();
			}
		});
		
	}

	protected void actionCancel() {
		dispose();		
	}

	protected void actionUndoChanges() {
		Configuration config = Configuration.getInstance();
		maxBackupSpinner.setValue(config.getMaxBackup());
		backupFolderTextField.setText(config.getBackupFolder());
		maxRecentSpinner.setValue(config.getMaxRecent());
		minVariableNameLengthSpinner.setValue(config.getMinVariableNameLength());
		execTextField.setText(config.getExecPath());
		chckbxEnablePacmanEport.setSelected(config.isPacmanEnabled());
	}

	protected void actinRestoreDefaults() {
		Configuration config = Configuration.createDefault();
		maxBackupSpinner.setValue(config.getMaxBackup());
		backupFolderTextField.setText(config.getBackupFolder());
		maxRecentSpinner.setValue(config.getMaxRecent());
		minVariableNameLengthSpinner.setValue(config.getMinVariableNameLength());
		chckbxEnablePacmanEport.setSelected(config.isPacmanEnabled());
		
	}

	protected void actionBrowse() {
		JFileChooser fc = new JFileChooser();
		String tmp = execTextField.getText().trim();
		if( !tmp.isEmpty()) {
			fc.setSelectedFile(new File(tmp));
		}
		
		if( (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION)) {
			execTextField.setText(fc.getSelectedFile().getAbsolutePath());
			actionValidate();
		}
		
	}

	protected void actionValidate() {
		String processResult="";
		BufferedReader br=null;
		try {
			Process process = new ProcessBuilder(execTextField.getText(),	"--info").start();
			br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;

			while ((line = br.readLine()) != null) {
				processResult += line+"\n";
			}

		} catch(Throwable e) {
			processResult += e.toString()+"\n";						
		} finally {
			if( br != null ) {
				try {
					br.close();
				} catch (Exception e2) {
				}
			}
		}
		textArea.setText(processResult);
	}
	
	protected void actioinSaveChanges() {
		Configuration config = Configuration.getInstance();
		config.setExecPath(execTextField.getText());
		config.setBackupFolder(backupFolderTextField.getText());
		config.setMaxBackup((Integer) maxBackupSpinner.getValue());
		config.setMinVariableNameLength((Integer)minVariableNameLengthSpinner.getValue());
		config.setMaxRecent((Integer)maxRecentSpinner.getValue());
		config.setPacmanEnabled(chckbxEnablePacmanEport.isSelected());
		try {
			config.save();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dispose();
	}
}
