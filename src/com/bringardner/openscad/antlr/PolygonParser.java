// Generated from Polygon.g4 by ANTLR 4.4

	package com.bringardner.openscad.antlr;

import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PolygonParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.4", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__6=1, T__5=2, T__4=3, T__3=4, T__2=5, T__1=6, T__0=7, ID=8, D=9, INT=10, 
		DEC=11, WS=12;
	public static final String[] tokenNames = {
		"<INVALID>", "'paths'", "'convexity'", "'['", "','", "']'", "'='", "'points'", 
		"ID", "D", "INT", "DEC", "WS"
	};
	public static final int
		RULE_code = 0, RULE_convexity = 1, RULE_points = 2, RULE_paths = 3, RULE_point = 4, 
		RULE_array = 5, RULE_number = 6;
	public static final String[] ruleNames = {
		"code", "convexity", "points", "paths", "point", "array", "number"
	};

	@Override
	public String getGrammarFileName() { return "Polygon.g4"; }

	@Override
	public String[] getTokenNames() { return tokenNames; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }


		
	 
	public PolygonParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class CodeContext extends ParserRuleContext {
		public PointsContext points() {
			return getRuleContext(PointsContext.class,0);
		}
		public ConvexityContext convexity() {
			return getRuleContext(ConvexityContext.class,0);
		}
		public PathsContext paths() {
			return getRuleContext(PathsContext.class,0);
		}
		public CodeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_code; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PolygonVisitor ) return ((PolygonVisitor<? extends T>)visitor).visitCode(this);
			else return visitor.visitChildren(this);
		}
	}

	public final CodeContext code() throws RecognitionException {
		CodeContext _localctx = new CodeContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_code);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(14); points();
			setState(17);
			switch ( getInterpreter().adaptivePredict(_input,0,_ctx) ) {
			case 1:
				{
				setState(15); match(T__3);
				setState(16); paths();
				}
				break;
			}
			setState(21);
			switch ( getInterpreter().adaptivePredict(_input,1,_ctx) ) {
			case 1:
				{
				setState(19); match(T__3);
				setState(20); convexity();
				}
				break;
			}
			setState(24);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(23); match(T__3);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConvexityContext extends ParserRuleContext {
		public NumberContext number() {
			return getRuleContext(NumberContext.class,0);
		}
		public ConvexityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_convexity; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PolygonVisitor ) return ((PolygonVisitor<? extends T>)visitor).visitConvexity(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ConvexityContext convexity() throws RecognitionException {
		ConvexityContext _localctx = new ConvexityContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_convexity);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(26); match(T__5);
			setState(27); match(T__1);
			setState(28); number();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PointsContext extends ParserRuleContext {
		public PointContext point(int i) {
			return getRuleContext(PointContext.class,i);
		}
		public List<PointContext> point() {
			return getRuleContexts(PointContext.class);
		}
		public PointsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_points; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PolygonVisitor ) return ((PolygonVisitor<? extends T>)visitor).visitPoints(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PointsContext points() throws RecognitionException {
		PointsContext _localctx = new PointsContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_points);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(30); match(T__0);
			setState(31); match(T__1);
			{
			setState(32); match(T__4);
			setState(33); point();
			setState(38);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(34); match(T__3);
					setState(35); point();
					}
					} 
				}
				setState(40);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,3,_ctx);
			}
			setState(42);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(41); match(T__3);
				}
			}

			setState(44); match(T__2);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PathsContext extends ParserRuleContext {
		public ArrayContext array(int i) {
			return getRuleContext(ArrayContext.class,i);
		}
		public List<ArrayContext> array() {
			return getRuleContexts(ArrayContext.class);
		}
		public PathsContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_paths; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PolygonVisitor ) return ((PolygonVisitor<? extends T>)visitor).visitPaths(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PathsContext paths() throws RecognitionException {
		PathsContext _localctx = new PathsContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_paths);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(46); match(T__6);
			setState(47); match(T__1);
			setState(48); match(T__4);
			setState(49); array();
			setState(54);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(50); match(T__3);
					setState(51); array();
					}
					} 
				}
				setState(56);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,5,_ctx);
			}
			setState(58);
			_la = _input.LA(1);
			if (_la==T__3) {
				{
				setState(57); match(T__3);
				}
			}

			setState(60); match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class PointContext extends ParserRuleContext {
		public NumberContext x;
		public NumberContext y;
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public PointContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_point; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PolygonVisitor ) return ((PolygonVisitor<? extends T>)visitor).visitPoint(this);
			else return visitor.visitChildren(this);
		}
	}

	public final PointContext point() throws RecognitionException {
		PointContext _localctx = new PointContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_point);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(62); match(T__4);
			setState(63); ((PointContext)_localctx).x = number();
			setState(64); match(T__3);
			setState(65); ((PointContext)_localctx).y = number();
			setState(66); match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ArrayContext extends ParserRuleContext {
		public List<NumberContext> number() {
			return getRuleContexts(NumberContext.class);
		}
		public NumberContext number(int i) {
			return getRuleContext(NumberContext.class,i);
		}
		public ArrayContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_array; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PolygonVisitor ) return ((PolygonVisitor<? extends T>)visitor).visitArray(this);
			else return visitor.visitChildren(this);
		}
	}

	public final ArrayContext array() throws RecognitionException {
		ArrayContext _localctx = new ArrayContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_array);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(68); match(T__4);
			setState(69); number();
			setState(74);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==T__3) {
				{
				{
				setState(70); match(T__3);
				setState(71); number();
				}
				}
				setState(76);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(77); match(T__2);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class NumberContext extends ParserRuleContext {
		public TerminalNode DEC() { return getToken(PolygonParser.DEC, 0); }
		public TerminalNode INT() { return getToken(PolygonParser.INT, 0); }
		public NumberContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_number; }
		@Override
		public <T> T accept(ParseTreeVisitor<? extends T> visitor) {
			if ( visitor instanceof PolygonVisitor ) return ((PolygonVisitor<? extends T>)visitor).visitNumber(this);
			else return visitor.visitChildren(this);
		}
	}

	public final NumberContext number() throws RecognitionException {
		NumberContext _localctx = new NumberContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_number);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(79);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==DEC) ) {
			_errHandler.recoverInline(this);
			}
			consume();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\16T\4\2\t\2\4\3\t"+
		"\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\3\2\3\2\3\2\5\2\24\n\2\3\2"+
		"\3\2\5\2\30\n\2\3\2\5\2\33\n\2\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\7\4\'\n\4\f\4\16\4*\13\4\3\4\5\4-\n\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5"+
		"\7\5\67\n\5\f\5\16\5:\13\5\3\5\5\5=\n\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3"+
		"\6\3\7\3\7\3\7\3\7\7\7K\n\7\f\7\16\7N\13\7\3\7\3\7\3\b\3\b\3\b\2\2\t\2"+
		"\4\6\b\n\f\16\2\3\3\2\f\rT\2\20\3\2\2\2\4\34\3\2\2\2\6 \3\2\2\2\b\60\3"+
		"\2\2\2\n@\3\2\2\2\fF\3\2\2\2\16Q\3\2\2\2\20\23\5\6\4\2\21\22\7\6\2\2\22"+
		"\24\5\b\5\2\23\21\3\2\2\2\23\24\3\2\2\2\24\27\3\2\2\2\25\26\7\6\2\2\26"+
		"\30\5\4\3\2\27\25\3\2\2\2\27\30\3\2\2\2\30\32\3\2\2\2\31\33\7\6\2\2\32"+
		"\31\3\2\2\2\32\33\3\2\2\2\33\3\3\2\2\2\34\35\7\4\2\2\35\36\7\b\2\2\36"+
		"\37\5\16\b\2\37\5\3\2\2\2 !\7\t\2\2!\"\7\b\2\2\"#\7\5\2\2#(\5\n\6\2$%"+
		"\7\6\2\2%\'\5\n\6\2&$\3\2\2\2\'*\3\2\2\2(&\3\2\2\2()\3\2\2\2),\3\2\2\2"+
		"*(\3\2\2\2+-\7\6\2\2,+\3\2\2\2,-\3\2\2\2-.\3\2\2\2./\7\7\2\2/\7\3\2\2"+
		"\2\60\61\7\3\2\2\61\62\7\b\2\2\62\63\7\5\2\2\638\5\f\7\2\64\65\7\6\2\2"+
		"\65\67\5\f\7\2\66\64\3\2\2\2\67:\3\2\2\28\66\3\2\2\289\3\2\2\29<\3\2\2"+
		"\2:8\3\2\2\2;=\7\6\2\2<;\3\2\2\2<=\3\2\2\2=>\3\2\2\2>?\7\7\2\2?\t\3\2"+
		"\2\2@A\7\5\2\2AB\5\16\b\2BC\7\6\2\2CD\5\16\b\2DE\7\7\2\2E\13\3\2\2\2F"+
		"G\7\5\2\2GL\5\16\b\2HI\7\6\2\2IK\5\16\b\2JH\3\2\2\2KN\3\2\2\2LJ\3\2\2"+
		"\2LM\3\2\2\2MO\3\2\2\2NL\3\2\2\2OP\7\7\2\2P\r\3\2\2\2QR\t\2\2\2R\17\3"+
		"\2\2\2\n\23\27\32(,8<L";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}