package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;
import org.fife.ui.autocomplete.ShorthandCompletion;
import org.fife.ui.rtextarea.RTextScrollPane;


public class Editor extends JFrame {

	private static final long serialVersionUID = 1L;
	private static List<Editor> active = new ArrayList<>();
	protected static final Preferences prefs = Preferences.userNodeForPackage(Editor.class);
	private static final String KEY_SCREEN_LOCATION = "Location";
	private static final String KEY_RECENT = "recent";
	public static final String KEY_MAX_RECENT = "maxrecent";
	public static final String KEY_MAX_BACKUPS = "maxbackups";
	public static final String KEY_BACKUPDIR = "backupdir";
	public static final String KEY_EXEC_LOCATION = "execlocation";
	public static final String KEY_MIN_VAR_LEN = "minVarLen";

	private Object autocompleteMutex = new Object();
	private Object previewMutex = new Object();
	private static List<String> recentFiles = new ArrayList<>();

	private JPanel contentPane;
	private String originalText = "";
	private File file;
	private File lastDir;
	private ProcessManager previewProcess;

	private AutoCompletion autoComplete;
	private RSyntaxTextAreaOpenScad editorPane;
	private JMenu recentMenu;
	private List<ModuleAutoCompleteManager> modules = new ArrayList<>();
	private Map<String,IncludeFile> included = new HashMap<>();
	private List<String> variables = new ArrayList<>();
	private FileBackupManager backupManager = new FileBackupManager();
	private RTextScrollPane scrollPane;



	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		String tmp = prefs.get(KEY_RECENT, "");
		if( !tmp.isEmpty()) {
			tmp = tmp.substring(1);
			tmp = tmp.substring(0,tmp.length()-1);
			for (String str : tmp.split("[,]")) {
				recentFiles.add(str.trim());
			}
		}

		Editor frame = new Editor();		
		tmp = prefs.get(KEY_SCREEN_LOCATION, "");
		if( !tmp.isEmpty()) {
			String parts[] = tmp.split("[,]");
			if( parts.length == 4 ) {
				frame.setBounds(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]), Integer.parseInt(parts[3]));
			}
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

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
	public Editor() {

		try {
			URL imgUrl = Editor.class.getResource("/openscad2.png");

			Image img = ImageIO.read(imgUrl);
			setIconImage(img);
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}


		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 900, 600);
		setLocationRelativeTo(null);
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		JMenuItem mntmOpen = new JMenuItem("Open");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionOpen();
			}
		});
		mnFile.add(mntmOpen);

		JMenuItem mntmNew = new JMenuItem("New");
		mntmNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionNew();
			}
		});
		mnFile.add(mntmNew);
		recentMenu = new JMenu("Recent...");
		mnFile.add(recentMenu);
		buildMenu();

		JMenuItem mntmSave = new JMenuItem("Save");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave();
			}
		});
		mnFile.add(mntmSave);

		JMenuItem mntmSaveAs = new JMenuItem("Save As");
		mntmSaveAs.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSaveAs();
			}
		});
		mnFile.add(mntmSaveAs);

		JMenuItem mntmReload = new JMenuItem("Reload");
		mntmReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionReload();
			}
		});

		mnFile.add(mntmReload);


		JMenuItem mntmPreferences = new JMenuItem("Preferences");
		mntmPreferences.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPreferences();
			}
		});
		mnFile.add(mntmPreferences);

		JSeparator separator = new JSeparator();
		mnFile.add(separator);

		JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExit();
			}
		});

		mnFile.add(mntmExit);

		JMenu exFile = new JMenu("Export");
		menuBar.add(exFile);

		JMenuItem export = new JMenuItem("Export as STL");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExport(".stl");
			}
		});
		exFile.add(export);

		export = new JMenuItem("Export as OFF");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExport(".off");
			}
		});
		exFile.add(export);

		export = new JMenuItem("Export as DXF");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExport(".dxf");
			}
		});
		exFile.add(export);

		export = new JMenuItem("Export as CSG");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExport(".csg");
			}
		});
		exFile.add(export);

		export = new JMenuItem("Export as PNG");
		export.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExport(".png");
			}
		});
		exFile.add(export);

		JPanel panel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) panel.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		menuBar.add(panel);

		JButton btnSave = new JButton("");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionSave();
			}
		});

		Component horizontalStrut_2 = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut_2);
		btnSave.setPreferredSize(new Dimension(32, 32));
		btnSave.setToolTipText("Save  CTR-S");
		btnSave.setIcon(new ImageIcon(Editor.class.getResource("/Save-32.png")));
		panel.add(btnSave);

		JButton btnOpen = new JButton("");
		btnOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionOpen();
			}
		});

		Component horizontalStrut = Box.createHorizontalStrut(5);
		panel.add(horizontalStrut);
		btnOpen.setPreferredSize(new Dimension(32, 32));
		btnOpen.setToolTipText("Open - CTR-O");
		btnOpen.setIcon(new ImageIcon(Editor.class.getResource("/Open-32.png")));
		panel.add(btnOpen);

		Component horizontalStrut_1 = Box.createHorizontalStrut(5);
		panel.add(horizontalStrut_1);

		JButton btnPreview = new JButton("");
		panel.add(btnPreview);
		btnPreview.setPreferredSize(new Dimension(32, 32));
		btnPreview.setToolTipText("Preview - F5");
		btnPreview.setIcon(new ImageIcon(Editor.class.getResource("/preview-33.png")));

		Component horizontalStrut_4 = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut_4);

		JButton btnExportStl = new JButton("");
		btnExportStl.setToolTipText("Export STL");
		btnExportStl.setPreferredSize(new Dimension(32, 32));
		btnExportStl.setIcon(new ImageIcon(Editor.class.getResource("/export.png")));

		btnExportStl.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionExport(".stl");
			}
		});
		panel.add(btnExportStl);

		Component horizontalStrut_3 = Box.createHorizontalStrut(200);
		panel.add(horizontalStrut_3);

		JButton btnHelp = new JButton("");
		panel.add(btnHelp);
		btnHelp.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionHelp();
			}
		});
		btnHelp.setPreferredSize(new Dimension(32, 32));
		btnHelp.setIcon(new ImageIcon(Editor.class.getResource("/Help.png")));
		btnHelp.setToolTipText("Help");
		btnPreview.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionPreview();
			}
		});
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);


		editorPane = new RSyntaxTextAreaOpenScad(20,20);
		Font font = new Font("Sans", Font.PLAIN, 20);
		editorPane.setFont(font);
		createAutoComplete();
		scrollPane = new RTextScrollPane(editorPane);
		contentPane.add(scrollPane, BorderLayout.CENTER);
		editorPane.setLineWrap(false);
		editorPane.setCodeFoldingEnabled(true);
		editorPane.addKeyListener(new KeyAdapter() {
			private FindDialog findDialog;

			@Override

			public void keyPressed(KeyEvent e) {
				int c = e.getKeyCode();

				boolean keep = true;
				if( e.isControlDown()) {
					// P = 16,  F = 6 ,   S = 19
					if( c == KeyEvent.VK_F) {// 6 == F
						if( findDialog == null ) {
							findDialog = new FindDialog();
						}
						if( !findDialog.isVisible()) {
							findDialog.showDialog(editorPane);
						}
						keep = false;
					} else if( c == KeyEvent.VK_G ) {
						GoToDialog dialog = new GoToDialog();
						Point loc3 = editorPane.getLocationOnScreen();
						Point loc = editorPane.getCaret().getMagicCaretPosition();
						if( loc != null ) {
							loc3.x+=loc.x;
							loc3.y+=loc.y;
						}

						dialog.setLocation(loc3);
						int line = dialog.getLine();
						if( line >0) {
							try {
								editorPane.setCaretPosition(editorPane.getLineStartOffset(line-1));
							} catch (BadLocationException e1) {
							}

						}
					} else if( c == KeyEvent.VK_B ) {
						actionPreview();
						keep = false;
					} else if( c == KeyEvent.VK_P ) { // P
						keep = false;
						Document doc = editorPane.getDocument();
						int dot = editorPane.getCaretPosition();
						String left = null;
						String right = null;
						int ch = -1;
						try {right = doc.getText(dot, 1);	ch = right.charAt(0);} catch (BadLocationException e1) {			}
						if( ch != '{' && ch != '}') {
							try {left = doc.getText(--dot, 1);	ch = left.charAt(0);} catch (BadLocationException e1) {			}
						}
						switch (ch) {
						case '{':
							highlightMatching((char)ch, '}', dot+1, 1, doc,e.isShiftDown());
							break;
						case '}':
							highlightMatching((char)ch, '{', dot-1, -1, doc,e.isShiftDown());
							break;

						}
					} else if( c == KeyEvent.VK_O ) { // O
						actionOpen();
						keep = false;
					} else if( c == KeyEvent.VK_S ) { // S
						actionSave();
						keep = false;
					} else if( c == KeyEvent.VK_I ) { // I
						actionFormat();
						keep = false;
					} 
				} else if(e.isAltDown()) {
					if( c == KeyEvent.VK_F) {// 102 == F
						findDialog.find();
						keep = false;
					}
				} else if( c == KeyEvent.VK_F5 ) {
					actionPreview();
					keep = false;
				} else if( c == KeyEvent.VK_F3 ) {
					actionFindDecloration();
					keep = false;
				}

				if( keep ) {
					super.keyPressed(e);
				} else {
					e.consume();
				}
			}

		});

		mntmReload = new JMenuItem("Reload");
		mntmReload.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				actionReload();
			}
		});
		editorPane.getPopupMenu().add(mntmReload);

		JPanel controlPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) controlPanel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		contentPane.add(controlPanel, BorderLayout.NORTH);

		addComponentListener(new ComponentAdapter( ) {
			public void componentResized(ComponentEvent ev) {
				updatePrefs(ev);
			}

			private void updatePrefs(ComponentEvent ev) {
				String val = ev.paramString();
				val = val.substring(val.indexOf('(')).replace("(", "").replace(")", "").replace(" ", ",").replace("x", ",");
				//System.out.println("resize ev="+val);
				prefs.put(KEY_SCREEN_LOCATION, val);
				try {
					prefs.flush();
				} catch (BackingStoreException e) {
					logError(e, "save location");
				}
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				updatePrefs(e);
			};
		});

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowOpened(WindowEvent e) {
				active.add(Editor.this);
				super.windowOpened(e);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				if( hasChanged()) {
					if( showConfirmDialog() != JOptionPane.OK_OPTION) {
						return;
					}
				}

				if( previewProcess != null ) {
					previewProcess.stop();
					previewProcess = null;
				}
				dispose();
				active.remove(Editor.this);
				if( active.size() == 0 ) {
					System.exit(0);
				}
			}

		});
		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
					SwingUtilities.invokeLater(new Runnable() {

						@Override
						public void run() {
							updateTitle();
						}
					});
				}
			}
		}).start();

		new Thread(new Runnable() {

			@Override
			public void run() {
				while(true) {
					try {
						Thread.sleep(4000);
					} catch (InterruptedException e) {
					}
					updateAutoComplete(false);
				}
			}
		}).start();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				String exec = Configuration.getInstance().getExecPath();
				if( exec == null || exec.trim().isEmpty()) {
					if(JOptionPane.showConfirmDialog(Editor.this, "The path to the OpenSCAD executable is require before you can prefiew your 3D modles.\nWould you like to set it now?", "Open prefereences", JOptionPane.INFORMATION_MESSAGE) == JOptionPane.OK_OPTION) {
						actionPreferences();
					}
				}
			}
		});


	}

	private File lastExport;
	protected void actionExport(String type) {
		ExportDialog dialog = new ExportDialog();
		//stl / .off / .dxf, .csg).

		//  make sure the process is running and the file is current
		actionPreview();
		try {
			dialog.start(previewProcess.getPreviewFile(),type);
		} catch (IOException e) {
			logError(e, "Can't export");
			return;
		}

		JFileChooser fc = new JFileChooser();
		fc.setFileHidingEnabled(true);
		if( lastExport != null ) {
			if( lastExport.getName().endsWith(type)) {
				fc.setSelectedFile(lastExport);
			} else {
				String name = "new"+type;
				if( file != null ) {
					name = file.getName();
					if( name.endsWith(".scad")) {
						name = name.substring(0,name.length()-5)+type;
					}
				}
				fc.setCurrentDirectory(new File(lastExport.getParentFile(),name));
			}
		} else if( lastDir != null ) {
			String name = "new"+type;
			if( file != null ) {
				name = file.getName();
				if( name.endsWith(".scad")) {
					name = name.substring(0,name.length()-5)+type;
				}
			}
			fc.setSelectedFile(new File(lastDir,name));
		}

		if( fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File tmp = fc.getSelectedFile();
			if( !tmp.getName().toLowerCase().endsWith(type)) {
				JOptionPane.showMessageDialog(this, "File name MUST end with "+type, "Type missmatch", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if( tmp.exists()) {
				if( JOptionPane.showConfirmDialog(this, tmp.getName()+" already exists.  Do you want to over write it?") != JOptionPane.OK_OPTION) {
					return;
				}
			}
			lastExport = tmp;
			dialog.save(lastExport,this);
		} else {
			dialog.cancel();
		}

	}

	protected void actionPreferences() {
		PreferencesFrame frame = new PreferencesFrame(Editor.this);
		frame.setLocationRelativeTo(null);
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame.setVisible(true);						
			}
		});

	}

	protected void actionHelp() {
		try {
			final Help help = new Help();
			help.setLocationRelativeTo(null);
			SwingUtilities.invokeLater(()-> help.setVisible(true));
		} catch (IOException e) {
			logError(e, "Open help dialog");
		}

	}

	protected  void updateAutoComplete(boolean forceUpdate) {
		synchronized (autocompleteMutex) {


			//  This should never be called from the dispatch thread but we'll protect against it anyway.
			if( !SwingUtilities.isEventDispatchThread()) {
				if( forceUpdate || hasChanged()) {
					boolean changed = false;
					String code = editorPane.getText();
					List<ModuleAutoCompleteManager> tmp = ModuleAutoCompleteManager.getModules(code);
					if( tmp != null ) { //  we don't want to update the auto complete.
						Map<String, IncludeFile> inc = IncludeFile.findAllIncluded(code, file);
						if( inc != null ) {
							if( !inc.equals(included)) {
								included = inc;
							}
						}

						//System.out.println("Included files="+included);
						for (IncludeFile file : included.values()) {
							List<ModuleAutoCompleteManager> tmp2 = ModuleAutoCompleteManager.getModules(file.getCode());
							if( tmp2 != null ) {
								//System.out.println("adding for included="+tmp2);
								for (ModuleAutoCompleteManager compete : tmp2) {
									if( !tmp.contains(compete)) {
										tmp.add(compete);
									}
								}
							}
						}

						if(!modules.equals(tmp)) {
							modules = tmp;
							changed = true;						
						} 

						List<String> vars = VariableManager.findVariables(code, editorPane.getCaretPosition(),modules);
						if( !vars.equals(variables)) {
							variables = vars;
							changed = true;
						}

						if( changed) {
							createAutoComplete();
						}
					}
				} else {
					List<String> vars = VariableManager.findVariables(editorPane.getText(), editorPane.getCaretPosition(),modules);
					if( !vars.equals(variables)) {
						variables = vars;
						createAutoComplete();
					}
				}
			} 
		}
	}

	protected void actionFindDecloration() {
		String code = editorPane.getText();
		String selected = editorPane.getSelectedText();
		if( selected == null || selected.isEmpty()) {
			if( !code.isEmpty()) {
				selected = textAtCurrrentPosition();
			}			
		}

		if( selected != null && !selected.isEmpty()) {
			int pos = code.indexOf("module "+selected+" ");
			if( pos < 0 ) {
				pos = code.indexOf("module "+selected+"\t");
				if( pos < 0 ) {
					pos = code.indexOf("module "+selected+"(");
				}
			}
			if( pos >=0 ) {
				editorPane.setCaretPosition(pos);
			} else {
				if(included != null ) {
					for (IncludeFile inc : included.values()) {
						code = inc.getCode();
						if( code != null ) {
							pos = code.indexOf("module "+selected+" ");
							if( pos < 0 ) {
								pos = code.indexOf("module "+selected+"\t");
								if( pos < 0 ) {
									pos = code.indexOf("module "+selected+"(");
								}
							}
							if( pos >= 0 ) {
								// open new Editor
								final int myPos = pos;
								new Thread(new Runnable() {

									@Override
									public void run() {
										Editor ed = new Editor();
										Rectangle b = getBounds();
										b.x += 100;
										b.y += 20;
										ed.setBounds(b);
										ed.load(inc.getFile());
										SwingUtilities.invokeLater(new Runnable() {

											@Override
											public void run() {
												try {
													ed.setVisible(true);
													ed.editorPane.setCaretPosition(myPos);
												} catch (Exception e) {
												}
											}
										});										
									}
								}).start();

							}
						}
					}
				}
			}
		}

	}

	private String textAtCurrrentPosition() {
		String ret = null;
		Document doc = editorPane.getDocument();
		int len = doc.getLength();
		int start = editorPane.getCaretPosition();
		int end = start;
		try {
			while(start >=0 && Character.isJavaIdentifierPart(doc.getText(start, 1).charAt(0))) {
				start--;
			}
			while(end < len && Character.isJavaIdentifierPart(doc.getText(end, 1).charAt(0))) {
				end++;
			}
			if( start < 0 ) {
				start = 0;
			}
			if( end > start ) {
				ret = doc.getText(start,end-start).trim();
			}
		} catch (BadLocationException e) {
			e.printStackTrace();
		}


		return ret;
	}

	protected void actionPreview() {
		final String code = editorPane.getText();
		new Thread(new Runnable() {

			@Override
			public void run() {
				synchronized (previewMutex) {
					if( previewProcess == null ) {
						previewProcess = new ProcessManager();
						previewProcess.start();
					}
					String myCode = code;
					try {
						if( included != null ) {
							//System.out.println(included.toString());
							for(IncludeFile inc : included.values()) {
								File file = inc.getFile();
								if( file != null ) {
									String name = inc.getFileName();
									String path = file.getAbsolutePath();
									if( !path.equals(name) ) {
										myCode = myCode.replace(name, path);
									}
								}
							}
						}
						previewProcess.updatePreviewFile(myCode);
					} catch (IOException e) {
						logError(e, "Update preview file");
					}
				}
			}
		}).start();

	}

	protected void actionNew() {
		if( hasChanged()) {
			if( showConfirmDialog() != JOptionPane.OK_OPTION) {
				return;
			}
		}
		originalText = "";
		file = null;
		editorPane.setText("");
		new Thread(new Runnable() {

			@Override
			public void run() {
				updateAutoComplete(true);
			}
		}).start();
	}

	private void buildMenu() {
		int maxRecentFiles = Configuration.getInstance().getMaxRecent();
		if( recentFiles.size()>maxRecentFiles) {
			recentFiles.remove(recentFiles.size()-1);
		}
		recentMenu.removeAll();
		for (int idx=0,sz=recentFiles.size(); idx < sz; idx++ ) {
			final String val = recentFiles.get(idx);
			if( val != null ) {
				JMenuItem item = new JMenuItem(val);
				item.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if( hasChanged()) {
							if( showConfirmDialog() != JOptionPane.OK_OPTION) {
								return;
							}
						}

						load(new File(val));
					}
				});
				recentMenu.add(item);
			}
		}

		String val = recentFiles.toString();
		prefs.put(KEY_RECENT, val);

		try {
			prefs.flush();
		} catch (BackingStoreException e) {
			logError(e, "save recent");
		}
	}



	protected void actionFormat() {
		String text = editorPane.getSelectedText();
		if( text != null && !text.isEmpty()) {
			int start = editorPane.getSelectionStart();
			int end   = editorPane.getSelectionEnd();

			Document doc = editorPane.getDocument();
			//System.out.println("dco class="+doc.getClass().getName());
			try {
				doc.remove(start, end-start);
				doc.insertString(start, ScadFormatter.format(text), null);
			} catch (BadLocationException e) {
				logError(e, "insert formatted code");
			}			
		}
	}

	private void updateTitle() {

		if( file != null ) {
			String nm = file.getAbsolutePath();
			if(!hasChanged()) {
				setMyTitle(nm);
			} else {
				setMyTitle("* "+nm);
			}
		} else {
			setMyTitle("");
		}

	}

	private void setMyTitle(final String nm) {
		SwingUtilities.invokeLater(()-> setTitle(nm));		
	}

	private boolean hasChanged() {
		return !originalText.equals(editorPane.getText());
	}

	protected void createAutoComplete()  {

		DefaultCompletionProvider provider = new DefaultCompletionProvider(){
			@Override
			protected boolean isValidChar(char ch) {
				return Character.isLetterOrDigit(ch) || ch=='_'  || ch=='.' || ch=='#' || ch=='$' ;
			}
		};

		Configuration config = Configuration.getInstance();
		for(Template t : config.getTemplates()) {
			t.addCompetion(provider);
		}


		for(String var : variables) {
			provider.addCompletion(new ShorthandCompletion(provider, var+"-variable", var));
		}

		if( modules != null ) {
			for (ModuleAutoCompleteManager mod : modules) {
				//System.out.println("put in provider="+mod.getName());
				provider.addCompletion(mod.getCompletion(provider));
			}
		}


		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(autoComplete == null ) {
					autoComplete = new AutoCompletion(provider); 
					autoComplete.setAutoCompleteEnabled(true);
					autoComplete.setParameterAssistanceEnabled(true);
					autoComplete.install(editorPane);
				} else {
					autoComplete.setCompletionProvider(provider);
				}
			}
		});

	}

	protected boolean highlightMatching(char start,char end, int pos, int inc,Document doc,boolean select)  {
		boolean ret = false;
		int cnt = 1;
		int len= doc.getLength();
		int startPos = pos;
		try {
			while( pos >=0 && pos < len) {
				char c = doc.getText(pos, 1).charAt(0);
				if( c == start) {
					cnt++;
				} else if( c == end) {
					if( --cnt == 0 ) {
						editorPane.setCaretPosition(pos);
						if( select ) {
							editorPane.select(startPos, pos);
						}
						return true;
					}
				}
				pos+=inc;
			}
		} catch(BadLocationException e) {

		}
		return ret;
	}

	protected void actionExit() {
		if( hasChanged()) {
			if( showConfirmDialog() != JOptionPane.OK_OPTION) {
				return;
			}
		}
		dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));

	}

	protected void actionReload() {
		if( file != null) {
			if( hasChanged()) {
				if( showConfirmDialog() != JOptionPane.OK_OPTION) {
					return;
				}
			}

			load(file);
		}

	}

	public void logError(Throwable error, String title) {
		//	error.printStackTrace();
		if(SwingUtilities.isEventDispatchThread()) {
			JOptionPane.showMessageDialog(this, error.toString(), title, JOptionPane.ERROR_MESSAGE);
		} else {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					JOptionPane.showMessageDialog(Editor.this, error.toString(), title, JOptionPane.ERROR_MESSAGE);
				}
			});
		}
	}

	public static byte[] readFile(File file) throws IOException {
		int sz = (int)file.length();
		byte data [] = new byte[sz];
		if( sz > 0 ) {
			
			DataInputStream in = new DataInputStream(new FileInputStream(file));
			
			try {
				in.readFully(data);
				
			}finally{
				try {
					in.close();
				} catch (Exception e) {
				}
			}
		}

		return data;
	}

	private void write(File tmp) {
		try {
			originalText = editorPane.getText();
			backupManager.save(tmp, editorPane.getText());
			setMyTitle(tmp.getName());
		} catch (IOException e) {
			logError(e, "Can't save to "+tmp);
		} 
	}


	protected void actionSave() {
		if( file == null ) {
			actionSaveAs();
		} else {
			write(file);
		}

	}


	protected void actionOpen() {

		if( hasChanged()) {
			if( showConfirmDialog() != JOptionPane.OK_OPTION) {
				return;
			}
		}

		if (lastDir == null ) {
			if( recentFiles.size() > 0 ) {
				File file = new File(recentFiles.get(0));
				lastDir = file.getParentFile();
			}
		}

		JFileChooser fc = new JFileChooser(lastDir);
		fc.setFileHidingEnabled(true);
		fc.setAcceptAllFileFilterUsed(true);
		fc.addChoosableFileFilter(new javax.swing.filechooser.FileFilter() {

			@Override
			public String getDescription() {
				return ".scad";
			}

			@Override
			public boolean accept(File f) {
				return f.getName().toLowerCase().endsWith(".scad");
			}
		});


		if( file != null ) {
			fc.setSelectedFile(file);
		} 

		if( fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			load(fc.getSelectedFile());
		}

	}

	private int showConfirmDialog() {
		Object[] options = { "Discard and continue", "Cancel" };

		int ret = JOptionPane.showOptionDialog(this, " There are unsaved changes.\nDo you want to discard them?", "Warning",
				JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.QUESTION_MESSAGE, 
				null, // do not use a custom Icon
				options, // the titles of buttons
				options[0]); // default button title

		return ret;
	}

	private void load(File tmpFile) {
		try {
			originalText = new String(readFile(tmpFile));
			editorPane.setText(originalText);
			editorPane.setCaretPosition(1);
			file = tmpFile;
			String tmp = file.getAbsolutePath();
			recentFiles.remove(tmp);
			recentFiles.add(0, tmp);
			buildMenu();
			new Thread(new Runnable() {

				@Override
				public void run() {
					updateAutoComplete(true);
				}
			}).start();
			lastDir = tmpFile.getParentFile();
			if( previewProcess != null) {
				actionPreview();
			}
		} catch (IOException e) {
			logError(e, "Can't open "+tmpFile);
		}

	}

	protected void actionSaveAs() {
		JFileChooser fc = new JFileChooser();
		fc.setFileHidingEnabled(true);
		if( file != null ) {
			fc.setSelectedFile(file);
		} else if( lastDir != null ) {
			fc.setSelectedFile(new File(lastDir,"new.scad"));
		}

		if( fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
			File tmp = fc.getSelectedFile();
			if( tmp.exists()) {
				if( JOptionPane.showConfirmDialog(this, tmp.getName()+" already exists.  Do you want to over write it?") != JOptionPane.OK_OPTION) {
					return;
				}
			}
			write(tmp);
			file = tmp;
			recentFiles.remove(file.getAbsolutePath());
			recentFiles.add(0, file.getAbsolutePath());
			buildMenu();
		}

	}

}
