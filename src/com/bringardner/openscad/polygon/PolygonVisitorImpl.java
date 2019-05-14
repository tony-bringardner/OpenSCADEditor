package com.bringardner.openscad.polygon;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

import com.bringardner.openscad.antlr.PolygonBaseVisitor;
import com.bringardner.openscad.antlr.PolygonLexer;
import com.bringardner.openscad.antlr.PolygonParser;
import com.bringardner.openscad.antlr.PolygonParser.ArrayContext;
import com.bringardner.openscad.antlr.PolygonParser.CodeContext;
import com.bringardner.openscad.antlr.PolygonParser.ConvexityContext;
import com.bringardner.openscad.antlr.PolygonParser.NumberContext;
import com.bringardner.openscad.antlr.PolygonParser.PathsContext;
import com.bringardner.openscad.antlr.PolygonParser.PointContext;
import com.bringardner.openscad.antlr.PolygonParser.PointsContext;

public class PolygonVisitorImpl extends PolygonBaseVisitor<Object> {
	public static class Polygon {
		public List<Point2D> points;
		public List<List<Double>> paths;
		public Double con;
	}

	@Override
	public Polygon visitCode(CodeContext ctx) {
		Polygon ret = new Polygon();
		ret.points = visitPoints(ctx.points());
		ret.paths = visitPaths(ctx.paths());
		ret.con = visitConvexity(ctx.convexity());

		return ret;
	}

	@Override
	public Double visitConvexity(ConvexityContext ctx) {
		Double ret = 0.0;
		if( ctx != null ) {
			if( ctx.number() != null ) {
				ret = Double.parseDouble(ctx.number().getText());
			}
		}
		return ret;
	}

	@Override
	public List<Point2D> visitPoints(PointsContext ctx) {
		List<Point2D> ret = new ArrayList<Point2D>();
		if( ctx != null ) {
			for (PointContext p : ctx.point()) {
				ret.add(visitPoint(p));
			}
		}
		return ret;
	}

	@Override
	public List<List<Double>> visitPaths(PathsContext ctx) {
		List<List<Double>> ret = new ArrayList<List<Double>>();
		if( ctx != null ) {
			for(ArrayContext ary : ctx.array()) {
				ret.add(visitArray(ary));
			}
		}
		return ret;
	}

	@Override
	public Point2D visitPoint(PointContext ctx) {
		Point2D ret = new Point2D.Double(Double.parseDouble(ctx.x.getText()), Double.parseDouble(ctx.y.getText()));

		return ret;
	}

	@Override
	public List<Double> visitArray(ArrayContext ctx) {
		List<Double> ret = new ArrayList<Double>();
		if( ctx != null ) {
			for (NumberContext p : ctx.number()) {
				ret.add(Double.parseDouble(p.getText()));
			}
		}
		return ret;
	}

	public static void main(String[] args) {
		String code [] = {
				"points=[[8,17],[0,144],[1,154],[179,10],[176,8],[16,0],[8,1],[193,195],[331,74],[327,72],[199,64],[192,65],[130,175],[16,202],[8,282],[9,291],[131,178],[134,175]],\r\n" + 
						"	paths= [[0,1,2,3,4,5,6],[7,8,9,10,11],[12,13,14,15,16,17,12]],convexity=10"
						,
						"points=[[8,17],[0,144],[1,154],[179,10],[176,8],[16,0],[8,1],[193,195],[331,74],[327,72],[199,64],[192,65],[130,175],[16,202],[8,282],[9,291],[131,178],[134,175]],\r\n" + 
								"	paths= [[0,1,2,3,4,5,6],[7,8,9,10,11],[12,13,14,15,16,17,12]]"
								,
								"points=[[8,17],[0,144],[1,154],[179,10],[176,8],[16,0],[8,1],[193,195],[331,74],[327,72],[199,64],[192,65],[130,175],[16,202],[8,282],[9,291],[131,178],[134,175]],\r\n"  
								,
								"points=[[8,17],[0,144],[1,154],[179,10],[176,8],[16,0],[8,1],[193,195],[331,74],[327,72],[199,64],[192,65],[130,175],[16,202],[8,282],[9,291],[131,178],[134,175]],\r\n" + 
										",convexity=10"
										,

		};

		for (String c : code) {
			parse(c);
		}


	}

	public static Polygon parse(String code) {
		//System.out.println("code="+code);
		ANTLRInputStream input = new ANTLRInputStream(code);
		PolygonLexer lex = new PolygonLexer(input);
		TokenStream tokens = new CommonTokenStream(lex);
		PolygonParser parser = new PolygonParser(tokens); 

		parser.addErrorListener(new ANTLRErrorListener() {
			@Override
			public void syntaxError(Recognizer<?, ?> arg0, Object arg1, int line, int col, String msg,RecognitionException arg5) {
				throw (new RuntimeException("sytax error "+line+":"+col+" "+msg));
			}

			@Override
			public void reportContextSensitivity(Parser arg0, DFA arg1, int arg2, int arg3, int arg4, ATNConfigSet arg5) {
			}

			@Override
			public void reportAttemptingFullContext(Parser arg0, DFA arg1, int arg2, int arg3, BitSet arg4, ATNConfigSet arg5) {
			}

			@Override
			public void reportAmbiguity(Parser arg0, DFA arg1, int arg2, int arg3, boolean arg4, BitSet arg5,ATNConfigSet arg6) {
			}

		});

		PolygonVisitorImpl visitor = new PolygonVisitorImpl();

		Polygon ret = visitor.visitCode(parser.code());


		/*
		 System.out.println("Done sz="+ret.points.size());
		 
		for (Point2D pp : ret.points) {
			System.out.println(pp);
		}

		System.out.println("Done path sz="+ret.paths.size());
		for (List<Double> ary : ret.paths) {

			for (Double d : ary) {
				System.out.print(d);
			}
			System.out.println();
		}

		System.out.println("Done con="+ret.con);
	*/

		return ret;
	}

}
