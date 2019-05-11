// Generated from Polygon.g4 by ANTLR 4.4

	package com.bringardner.openscad.antlr;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PolygonLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__6=1, T__5=2, T__4=3, T__3=4, T__2=5, T__1=6, T__0=7, ID=8, D=9, INT=10, 
		DEC=11, WS=12;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] tokenNames = {
		"'\\u0000'", "'\\u0001'", "'\\u0002'", "'\\u0003'", "'\\u0004'", "'\\u0005'", 
		"'\\u0006'", "'\\u0007'", "'\b'", "'\t'", "'\n'", "'\\u000B'", "'\f'"
	};
	public static final String[] ruleNames = {
		"T__6", "T__5", "T__4", "T__3", "T__2", "T__1", "T__0", "ID", "D", "INT", 
		"DEC", "WS"
	};


		
	 

	public PolygonLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Polygon.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\16e\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b"+
		"\3\b\3\t\6\t<\n\t\r\t\16\t=\3\n\6\nA\n\n\r\n\16\nB\3\n\3\n\7\nG\n\n\f"+
		"\n\16\nJ\13\n\6\nL\n\n\r\n\16\nM\3\13\6\13Q\n\13\r\13\16\13R\3\f\5\fV"+
		"\n\f\3\f\3\f\7\fZ\n\f\f\f\16\f]\13\f\3\r\6\r`\n\r\r\r\16\ra\3\r\3\r\2"+
		"\2\16\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16\3\2\6\4"+
		"\2C\\c|\3\2\62;\3\2\60\60\5\2\13\f\17\17\"\"l\2\3\3\2\2\2\2\5\3\2\2\2"+
		"\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21\3"+
		"\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\3\33\3\2\2"+
		"\2\5!\3\2\2\2\7+\3\2\2\2\t-\3\2\2\2\13/\3\2\2\2\r\61\3\2\2\2\17\63\3\2"+
		"\2\2\21;\3\2\2\2\23K\3\2\2\2\25P\3\2\2\2\27U\3\2\2\2\31_\3\2\2\2\33\34"+
		"\7r\2\2\34\35\7c\2\2\35\36\7v\2\2\36\37\7j\2\2\37 \7u\2\2 \4\3\2\2\2!"+
		"\"\7e\2\2\"#\7q\2\2#$\7p\2\2$%\7x\2\2%&\7g\2\2&\'\7z\2\2\'(\7k\2\2()\7"+
		"v\2\2)*\7{\2\2*\6\3\2\2\2+,\7]\2\2,\b\3\2\2\2-.\7.\2\2.\n\3\2\2\2/\60"+
		"\7_\2\2\60\f\3\2\2\2\61\62\7?\2\2\62\16\3\2\2\2\63\64\7r\2\2\64\65\7q"+
		"\2\2\65\66\7k\2\2\66\67\7p\2\2\678\7v\2\289\7u\2\29\20\3\2\2\2:<\t\2\2"+
		"\2;:\3\2\2\2<=\3\2\2\2=;\3\2\2\2=>\3\2\2\2>\22\3\2\2\2?A\t\3\2\2@?\3\2"+
		"\2\2AB\3\2\2\2B@\3\2\2\2BC\3\2\2\2CD\3\2\2\2DH\t\4\2\2EG\t\3\2\2FE\3\2"+
		"\2\2GJ\3\2\2\2HF\3\2\2\2HI\3\2\2\2IL\3\2\2\2JH\3\2\2\2K@\3\2\2\2LM\3\2"+
		"\2\2MK\3\2\2\2MN\3\2\2\2N\24\3\2\2\2OQ\t\3\2\2PO\3\2\2\2QR\3\2\2\2RP\3"+
		"\2\2\2RS\3\2\2\2S\26\3\2\2\2TV\5\25\13\2UT\3\2\2\2UV\3\2\2\2VW\3\2\2\2"+
		"W[\t\4\2\2XZ\5\25\13\2YX\3\2\2\2Z]\3\2\2\2[Y\3\2\2\2[\\\3\2\2\2\\\30\3"+
		"\2\2\2][\3\2\2\2^`\t\5\2\2_^\3\2\2\2`a\3\2\2\2a_\3\2\2\2ab\3\2\2\2bc\3"+
		"\2\2\2cd\b\r\2\2d\32\3\2\2\2\13\2=BHMRU[a\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}