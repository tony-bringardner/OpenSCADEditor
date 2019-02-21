package com.bringardner.openscad;

import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

import org.fife.com.swabunga.spell.engine.SpellDictionaryHashMap;
import org.fife.ui.rsyntaxtextarea.AbstractTokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.RSyntaxTextAreaEditorKit;
import org.fife.ui.rsyntaxtextarea.TokenMakerFactory;
import org.fife.ui.rsyntaxtextarea.folding.CurlyFoldParser;
import org.fife.ui.rsyntaxtextarea.folding.FoldParserManager;
import org.fife.ui.rsyntaxtextarea.spell.SpellingParser;

import com.sun.glass.events.KeyEvent;

public class RSyntaxTextAreaOpenScad extends RSyntaxTextArea {
	private static final long serialVersionUID = 1L;
	private static final String SCAD_TYPE = "text/scad2";

	public RSyntaxTextAreaOpenScad() {
		super();
		initMe();
	}

	public RSyntaxTextAreaOpenScad(int i, int j) {
		super(i, j);
		initMe();
	}

	public static SpellingParser createEnglishSpellingParser(InputStream resource) throws IOException {

		List<String> files = new ArrayList<>();

		for(String s : new String[]{"eng_com.dic", "color.dic", "labeled.dic", "center.dic", "ize.dic","yze.dic" }) {
			files.add(s);
		}

		SpellDictionaryHashMap dict=null;

		ZipInputStream zf = new ZipInputStream(resource);

		try {
			ZipEntry e = zf.getNextEntry();
			while(e != null) {
				if( files.contains(e.getName())) {
					BufferedReader r = new BufferedReader(new InputStreamReader(zf));
					if( dict == null ) {
						dict = new SpellDictionaryHashMap(r);
					} else {
						dict.addDictionary(r);
					}
				}
				e = zf.getNextEntry();
			}
		} finally {
			zf.close();
		}

		return new SpellingParser(dict);

	}


	private void initMe() {
		AbstractTokenMakerFactory atmf = (AbstractTokenMakerFactory)TokenMakerFactory.getDefaultInstance();
		atmf.putMapping(SCAD_TYPE, com.bringardner.openscad.parser.OpenScadTokenMaker.class.getName());
		FoldParserManager.get().addFoldParserMapping(SCAD_TYPE, new CurlyFoldParser());
		setSyntaxEditingStyle(SCAD_TYPE);
		setCodeFoldingEnabled(true);

		InputStream in = getClass().getResourceAsStream("/english_dic.zip");
		SpellingParser parser;
		try {
			parser = createEnglishSpellingParser(in);
			addParser(parser);
		} catch (IOException e) {
			e.printStackTrace();
		}


		ActionMap aMap = getActionMap();
		InputMap inMap = getInputMap();

		String name = RSyntaxTextAreaEditorKit.rstaCollapseAllFoldsAction;
		Action a = new RSyntaxTextAreaEditorKit.CollapseAllFoldsAction();
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SUBTRACT, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ),name);
		aMap.put(name,  a);

		name = RSyntaxTextAreaEditorKit.rstaExpandAllFoldsAction;
		a = new RSyntaxTextAreaEditorKit.CollapseAllFoldsAction();
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ADD, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ),name);
		aMap.put(name,  a);


		name = RSyntaxTextAreaEditorKit.rstaCollapseAllCommentFoldsAction;
		a = new RSyntaxTextAreaEditorKit.CollapseAllCommentFoldsAction();
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ),name);
		aMap.put(name,  a);

		name = RSyntaxTextAreaEditorKit.rstaCollapseAllCommentFoldsAction;
		a = new RSyntaxTextAreaEditorKit.CollapseAllCommentFoldsAction();
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK ),name);
		aMap.put(name,  a);

		/*
		name = RSyntaxTextAreaEditorKit.rstaToggleCommentAction;
		a = new RSyntaxTextAreaEditorKit.ToggleCommentAction();
		inMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_DOWN_MASK  ),name);
		aMap.put(name,  a);
*/


		//System.exit(0);
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
						setCaretPosition(pos);
						if( select ) {
							select(startPos, pos);
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
}