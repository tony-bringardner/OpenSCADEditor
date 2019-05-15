// Generated from Polygon.g4 by ANTLR 4.4

	package com.bringardner.openscad.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PolygonParser}.
 */
public interface PolygonListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PolygonParser#number}.
	 * @param ctx the parse tree
	 */
	void enterNumber(@NotNull PolygonParser.NumberContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolygonParser#number}.
	 * @param ctx the parse tree
	 */
	void exitNumber(@NotNull PolygonParser.NumberContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolygonParser#code}.
	 * @param ctx the parse tree
	 */
	void enterCode(@NotNull PolygonParser.CodeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolygonParser#code}.
	 * @param ctx the parse tree
	 */
	void exitCode(@NotNull PolygonParser.CodeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PolygonParser#point}.
	 * @param ctx the parse tree
	 */
	void enterPoint(@NotNull PolygonParser.PointContext ctx);
	/**
	 * Exit a parse tree produced by {@link PolygonParser#point}.
	 * @param ctx the parse tree
	 */
	void exitPoint(@NotNull PolygonParser.PointContext ctx);
}