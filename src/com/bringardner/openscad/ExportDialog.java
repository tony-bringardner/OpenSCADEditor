package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class ExportDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	private boolean canceled;
	private File stlFile;
	private File tmpFile;
	private ProcessManager process;
	private Editor editor;
	private PacmanDialog pacman;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			ExportDialog dialog = new ExportDialog();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setLocationRelativeTo(null);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public ExportDialog() {
		setUndecorated(true);
		setBounds(100, 100, 250, 132);
		
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);

		ImageIcon loading = new ImageIcon(getClass().getResource("/loader.gif"));
		contentPanel.setLayout(new BorderLayout(0, 0));
		JLabel lblExpoting = new JLabel("Expoting...",loading, JLabel.CENTER);
		contentPanel.add(lblExpoting);
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
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

	@Override
	public void dispose() {
		super.dispose();
		if( pacman != null ) {
			pacman.dispose();
		}
	}
	
	public void start(File previewFile,String type) throws IOException  {
			this.tmpFile = File.createTempFile("Editor", type);
			process = new ProcessManager();
			process.setRestartOnClose(false);
			process.setArgs("-o",tmpFile.getAbsolutePath(),//.replace('\\', '/'),
					//"-D","quality=\"production\"",
					previewFile.getAbsolutePath() //.replace('\\', '/')
					);
			process.start();
		
	}
	
	private void saveExport() throws IOException {
		int exitValue = process.getProcess().exitValue();
		if( exitValue != 0 ) {
			System.out.println(process.getProcessResult());
			editor.logError(new RuntimeException(process.getProcessResult()), "Exit value = "+exitValue);
			return;
		}
		
		if( stlFile != null && tmpFile !=null) {
			byte [] data = Editor.readFile(tmpFile);
			if( data.length == 0) {
				editor.logError(new RuntimeException("Export is empty"), " ");
				return;
			}
			
			OutputStream out = null;
			try {
				out = new FileOutputStream(stlFile);
				out.write(data);
			} finally {
				try {
					out.close();
				} catch (Exception e2) {
				}
			}
		}
	}

	
	public void save(File stlFile,Editor editor) {
		this.editor = editor;
		this.stlFile = stlFile;
		if( process.isAlive()) {
			startThread();
			if(Configuration.getInstance().isPacmanEnabled()) {
				pacman = new PacmanDialog(editor, ()->cancel());
				pacman.setModal(true);
				pacman.setVisible(true);
			} else {
				setModal(true);
				setLocationRelativeTo(editor);
				setVisible(true);
			}
			
		}  else {
			
			try {
				saveExport();
			} catch (IOException e) {
				editor.logError(e, "Exporting File");
			}
		}
		
	}

	private void startThread() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while(process.isAlive() && !canceled) {
					try {
						Thread.sleep(40);
					} catch (InterruptedException e) {
					}
				}
				if( !process.isAlive() ) {
					try {
						saveExport();
					} catch (IOException e) {
						editor.logError(e, "Exporting STL");
					}
				} else {
					process.stop();
				}
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						dispose();
					}
				});
			}
		}).start();
		
	}

	public void cancel() {
		canceled = true;		
	}
}
