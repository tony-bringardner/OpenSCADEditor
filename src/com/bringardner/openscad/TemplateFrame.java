package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class TemplateFrame extends JFrame {


	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JPanel displayPanel;
	private JTextField filterTextField;
	private Pattern filter;
	private List<TemplatePanel> _templates;
	private boolean disposed = false;
	private JCheckBox chckbxRegex;
	private int changes = 0;
	private JLabel statusLabel;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					TemplateFrame frame = new TemplateFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	
	public int getChanges() {
		return changes;
	}


	/**
	 * Create the frame.
	 */
	public TemplateFrame() {
		setTemplates(Configuration.getInstance().getTemplates());
	
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				disposed = true;
			}
		});
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 907, 574);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel controlPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(controlPanel, BorderLayout.NORTH);

		filterTextField = new JTextField();
		controlPanel.add(filterTextField);
		filterTextField.setColumns(10);

		JButton btnFilter = new JButton("Filter");
		btnFilter.setVisible(false);
		btnFilter.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionFilter();
			}
		});

		chckbxRegex = new JCheckBox("RegEx");
		chckbxRegex.setVisible(false);
		controlPanel.add(chckbxRegex);
		controlPanel.add(btnFilter);

		JButton btnUndoChanges = new JButton("Undo changes");
		btnUndoChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionUndoChanges();
			}
		});
		
		JButton btnNew = new JButton("New");
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNew();
			}
		});
		controlPanel.add(btnNew);

		controlPanel.add(btnUndoChanges);

		JButton btnRestorDefaults = new JButton("Restore Defaults");
		btnRestorDefaults.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionRestoreDefaults();
			}
		});
		controlPanel.add(btnRestorDefaults);

		JButton btnSaveChanges = new JButton("Save Changes");
		btnSaveChanges.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSaveChanges();
			}
		});
		controlPanel.add(btnSaveChanges);

		JButton btnCloseWithoutSaving = new JButton("Close without saving");
		btnCloseWithoutSaving.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				closeWithoutSaving();
			}
		});
		controlPanel.add(btnCloseWithoutSaving);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		displayPanel = new JPanel();
		scrollPane.setViewportView(displayPanel);
		displayPanel.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.SOUTH);
		
		statusLabel = new JLabel("");
		panel.add(statusLabel);
		build(true);
		new Thread(new Runnable() {

			@Override
			public void run() {
				while( !disposed ) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
					}
					String tmp = "No Changes";
					if( hasChanged() ) {
						tmp = "*Changed";
					}
					final String title = tmp;
					SwingUtilities.invokeLater(()->setTitle(title));
				}
			}
		}).start();
		new Thread(new Runnable() {

			@Override
			public void run() {
				String cur = filterTextField.getText();
				while( !disposed ) {
					try {
						Thread.sleep(200);
						String tmp = filterTextField.getText();
						if( !tmp.equals(cur) ) {
							cur = tmp;
							actionFilter();
						}
					} catch (Throwable e) {
					}

				}
			}
		}).start();

	}

	private void setTemplates(List<Template> newTemplates) {
		List<TemplatePanel> tmp = new ArrayList<>();
		for (Template t : newTemplates) {
			tmp.add(new TemplatePanel(t.copy()));
		}
		_templates = tmp;
	}

	protected void actionNew() {
		TemplateNewDialog dialog = new TemplateNewDialog();
		if( dialog.showDialog()) {
			_templates.add(new TemplatePanel(dialog.getTemplate()));
			build(true);
		}		
	}

	protected void closeWithoutSaving() {
		dispose();
	}

	public boolean hasChanged() {
		boolean ret = _templates.size() != Configuration.getInstance().getTemplates().size();
		//System.out.println("start = "+ret+" sz="+_templates.size()+" s2="+Configuration.getInstance().getTemplates().size());
		if( !ret ) {
			for(TemplatePanel tp : _templates) {
					if( tp.hasChanged()) {
						ret = true;
						break;
					}
			}
		}
		return ret;
	}

	protected void actionSaveChanges() {
		List<Template> tmp = new ArrayList<>();
		for(TemplatePanel tp : _templates) {
			if( !tp.isDeleted()) {
				Template t = tp.getTemplate();
				String nm = t.getName().trim();
				if(!nm.isEmpty()) {
					tmp.add(t);
				}
			}
		}

		Configuration config = Configuration.getInstance();
		config.setTemplates(tmp);
		
		try {
			config.save();
			setTemplates(config.getTemplates());
			build(true);
			changes++;
		} catch (IOException e) {
			logError(e, "Can't save your configuration.");
		}

	}

	private void logError(Throwable error, String title) {
		if(SwingUtilities.isEventDispatchThread()) {
			JOptionPane.showMessageDialog(this, error.toString(), title, JOptionPane.ERROR_MESSAGE);
		} else {
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					JOptionPane.showMessageDialog(TemplateFrame.this, error.toString(), title, JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}

	protected void actionRestoreDefaults() {
		setTemplates(Configuration.createDefault().getTemplates());
		build(true);
	}

	protected void actionUndoChanges() {
		setTemplates(Configuration.getInstance().getTemplates());
		build(true);
	}


	private String escape(String str ) {
		StringBuilder ret = new StringBuilder();
		byte [] data = str.getBytes();
		for (int idx = 0; idx < data.length; idx++) {
			switch (data[idx]) {
			case '*':
			case '+':
			case '{':
			case '}':
			case '[':
			case ']':
			case '.':
			case '$':
			case '?':
			case '(':
			case ')':
				
				ret.append('\\');
				break;
			}
			ret.append((char)data[idx]);
		}
		
		return ret.toString();
	}
	
	protected void actionFilter() {
		filter = null;
		String tmp = filterTextField.getText().trim();
		if( !tmp.isEmpty()) {
			if(!chckbxRegex.isSelected()) {
				tmp = "("+escape(tmp)+").*";
				final String tmp2 = tmp;
				SwingUtilities.invokeLater(()->statusLabel.setText(tmp2));
			}
			filter = Pattern.compile(tmp,Pattern.CASE_INSENSITIVE);
		}
		build(false);
	}

	private void build(boolean sort) {
		if( sort ) {
			Collections.sort(_templates, new Comparator<TemplatePanel>() {
				@Override
				public int compare(TemplatePanel o1, TemplatePanel o2) {
					return o1.getTemplate().getName().compareTo(o2.getTemplate().getName());
				}
			});
		}
		
		if( SwingUtilities.isEventDispatchThread()) {
			displayPanel.removeAll();
			for (TemplatePanel t : _templates) {
				if( filter == null || filter.matcher(t.getTemplate().getName()).matches()) {
					displayPanel.add((t));
				}
			}
			contentPane.updateUI();
		} else {
			SwingUtilities.invokeLater(()->{
				displayPanel.removeAll();
				for (TemplatePanel t : _templates) {
					if( filter == null || filter.matcher(t.getTemplate().getName()).matches()) {
						displayPanel.add((t));
					}
				}			
				contentPane.updateUI();
			});
		}

	}

	public boolean isDisposed() {
		return disposed;
	}

}
