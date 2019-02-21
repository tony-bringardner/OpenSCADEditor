package com.bringardner.openscad;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;

import org.fife.ui.autocomplete.AutoCompletion;
import org.fife.ui.autocomplete.DefaultCompletionProvider;

public class TemplatePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField nameTextField;
	private JTextField descriptionTextField;
	private JScrollPane scrollPane;
	private RSyntaxTextAreaOpenScad codeEditorPane;
	private JCheckBox chckbxDelete;
	private Template template;
	private AutoCompletion autoComplete;
	
	/**
	 * Create the panel.
	 */
	public TemplatePanel(Template template) {
		this.template = template;
		setLayout(new BorderLayout(0, 0));
		JPanel panel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) panel.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel, BorderLayout.NORTH);
		
		nameTextField = new JTextField();
		nameTextField.setToolTipText("name");
		nameTextField.setText(template.getName());
		panel.add(nameTextField);
		nameTextField.setColumns(20);
		
		descriptionTextField = new JTextField();
		descriptionTextField.setToolTipText("Short description");
		descriptionTextField.setText(template.getDescription());
		panel.add(descriptionTextField);
		descriptionTextField.setColumns(40);
		
		chckbxDelete = new JCheckBox("Delete");
		chckbxDelete.setToolTipText("Flag to delete on save");
		panel.add(chckbxDelete);
		
		scrollPane = new JScrollPane();
		add(scrollPane, BorderLayout.CENTER);
		
		codeEditorPane = new RSyntaxTextAreaOpenScad();
		codeEditorPane.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				actionFocusGained();
			}
			@Override
			public void focusLost(FocusEvent e) {
				actionFocusLost();
			}
		});
		codeEditorPane.setBorder(new TitledBorder(null, "Code", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		codeEditorPane.setToolTipText("Code");
		scrollPane.setViewportView(codeEditorPane);
		codeEditorPane.setText(template.getCode());
		
	}
	
	protected void actionFocusLost() {
		if( autoComplete != null ) {
			autoComplete.setCompletionProvider(new DefaultCompletionProvider());
		}
		
	}

	protected void actionFocusGained() {
		createAutoComplete();		
	}

	protected void createAutoComplete()  {

		DefaultCompletionProvider provider = new DefaultCompletionProvider(){
			@Override
			protected boolean isValidChar(char ch) {
				return Character.isLetterOrDigit(ch) || ch=='_'  || ch=='.' || ch=='#';
			}
		};

		Configuration config = Configuration.getInstance();
		for(Template t : config.getTemplates()) {
			t.addCompetion(provider);
		}
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if(autoComplete == null ) {
					autoComplete = new AutoCompletion(provider); 
					autoComplete.setAutoCompleteEnabled(true);
					autoComplete.setParameterAssistanceEnabled(true);
					autoComplete.install(codeEditorPane);
				} else {
					autoComplete.setCompletionProvider(provider);
				}
			}
		});

	}

	public void hideDelete() {
		chckbxDelete.setVisible(false);
		nameTextField.setBorder(new TitledBorder(null, "Name", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		descriptionTextField.setBorder(new TitledBorder(null, "Description", TitledBorder.LEADING, TitledBorder.TOP, null, null));
	}
	
	public boolean hasChanged () {
		boolean ret = !(!chckbxDelete.isSelected() && template.getName().equals(nameTextField.getText()) 
				&& template.getCode().equals(codeEditorPane.getText()))
				;
		if( !ret ) {
			String d = template.getDescription();
			if( d != null) {
				ret = !d.equals(descriptionTextField.getText());
			} else {
				ret = !descriptionTextField.getText().isEmpty();
			}
		}
		
		return ret;
	}

	public boolean isDeleted() {
		return chckbxDelete.isSelected();
	}
	
	public Template getTemplate() {
		return new Template(nameTextField.getText().trim(), descriptionTextField.getText().trim(), codeEditorPane.getText().trim());
	}
	
}
