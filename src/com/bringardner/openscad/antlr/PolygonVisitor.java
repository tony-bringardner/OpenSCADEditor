// Generated from Polygon.g4 by ANTLR 4.4

	package com.bringardner.openscad.antlr;

import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link PolygonParser}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface PolygonVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link PolygonParser#number}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNumber(@NotNull PolygonParser.NumberContext ctx);
	/**
	 * Visit a parse tree produced by {@link PolygonParser#code}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCode(@NotNull PolygonParser.CodeContext ctx);
	/**
	 * Visit a parse tree produced by {@link PolygonParser#array}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitArray(@NotNull PolygonParser.ArrayContext ctx);
	/**
	 * Visit a parse tree produced by {@link PolygonParser#paths}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPaths(@NotNull PolygonParser.PathsContext ctx);
	/**
	 * Visit a parse tree produced by {@link PolygonParser#convexity}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConvexity(@NotNull PolygonParser.ConvexityContext ctx);
	/**
	 * Visit a parse tree produced by {@link PolygonParser#point}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPoint(@NotNull PolygonParser.PointContext ctx);
	/**
	 * Visit a parse tree produced by {@link PolygonParser#points}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPoints(@NotNull PolygonParser.PointsContext ctx);
}