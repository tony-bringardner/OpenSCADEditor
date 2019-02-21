package com.bringardner.openscad;

import javax.swing.text.Segment;

import org.fife.ui.rsyntaxtextarea.AbstractTokenMaker;
import org.fife.ui.rsyntaxtextarea.RSyntaxUtilities;
import org.fife.ui.rsyntaxtextarea.Token;
import org.fife.ui.rsyntaxtextarea.TokenMaker;
import org.fife.ui.rsyntaxtextarea.TokenMap;

public class OpenScadTokenMaker extends AbstractTokenMaker implements TokenMaker {

	


	@Override
	public TokenMap getWordsToHighlight() {
		TokenMap tokenMap = new TokenMap();
		  
		   tokenMap.put("module",  Token.RESERVED_WORD);
		   tokenMap.put("function",  Token.RESERVED_WORD);
		   tokenMap.put("include",  Token.RESERVED_WORD);
		   tokenMap.put("use",  Token.RESERVED_WORD);
		   
		   tokenMap.put("for",    Token.RESERVED_WORD);
		   tokenMap.put("if",     Token.RESERVED_WORD);
		   tokenMap.put("else",   Token.RESERVED_WORD);
		  
		   // 3D Objects
		   tokenMap.put("cube", Token.FUNCTION);
		   tokenMap.put("sphere",  Token.FUNCTION);
		   tokenMap.put("cylinder", Token.FUNCTION);
		   tokenMap.put("polyhedron", Token.FUNCTION);
		   
		   //  2D Objects
		   tokenMap.put("circle", Token.FUNCTION);
		   tokenMap.put("square", Token.FUNCTION);
		   tokenMap.put("polygon", Token.FUNCTION);
		   tokenMap.put("text", Token.FUNCTION);
		   
		   //  Transformations
		   tokenMap.put("translate",  Token.FUNCTION);
		   tokenMap.put("rotate",  Token.FUNCTION);
		   tokenMap.put("scale",  Token.FUNCTION);
		   tokenMap.put("resize",  Token.FUNCTION);
		   tokenMap.put("mirror",  Token.FUNCTION);
		   tokenMap.put("mulmatrix",  Token.FUNCTION);
		   tokenMap.put("color",  Token.FUNCTION);
		   tokenMap.put("offset",  Token.FUNCTION);
		   tokenMap.put("hull",  Token.FUNCTION);
		   tokenMap.put("minkowski",  Token.FUNCTION);
		   
		   //  Boolean operations
		   tokenMap.put("union",  Token.FUNCTION);
		   tokenMap.put("difference",  Token.FUNCTION);
		   tokenMap.put("intersection",  Token.FUNCTION);
		   
		   //  Math
		   tokenMap.put("true",  Token.RESERVED_WORD);
		   tokenMap.put("false",  Token.RESERVED_WORD);
		   tokenMap.put("abs",  Token.FUNCTION);
		   tokenMap.put("sign",  Token.FUNCTION);
		   tokenMap.put("sin",  Token.FUNCTION);
		   tokenMap.put("cos",  Token.FUNCTION);
		   tokenMap.put("tan",  Token.FUNCTION);
		   tokenMap.put("acos",  Token.FUNCTION);
		   tokenMap.put("asin",  Token.FUNCTION);
		   tokenMap.put("atan",  Token.FUNCTION);
		   tokenMap.put("atan2",  Token.FUNCTION);
		   tokenMap.put("hull",  Token.FUNCTION);
		   tokenMap.put("round",  Token.FUNCTION);
		   tokenMap.put("ceil",  Token.FUNCTION);
		   tokenMap.put("ln",  Token.FUNCTION);
		   tokenMap.put("len",  Token.FUNCTION);
		   tokenMap.put("let",  Token.FUNCTION);
		   tokenMap.put("log",  Token.FUNCTION);
		   tokenMap.put("pow",  Token.FUNCTION);
		   tokenMap.put("sqrt",  Token.FUNCTION);
		   tokenMap.put("exp",  Token.FUNCTION);
		   tokenMap.put("hull",  Token.FUNCTION);
		   tokenMap.put("rands",  Token.FUNCTION);
		   tokenMap.put("min",  Token.FUNCTION);
		   tokenMap.put("max",  Token.FUNCTION);
		   
		   //  Functions
		   tokenMap.put("concat",  Token.FUNCTION);
		   tokenMap.put("lookup",  Token.FUNCTION);
		   tokenMap.put("str",  Token.FUNCTION);
		   tokenMap.put("chr",  Token.FUNCTION);
		   tokenMap.put("search",  Token.FUNCTION);
		   tokenMap.put("version",  Token.FUNCTION);
		   tokenMap.put("version_num",  Token.FUNCTION);
		   tokenMap.put("norm",  Token.FUNCTION);
		   tokenMap.put("cross",  Token.FUNCTION);
		   tokenMap.put("parent_name",  Token.FUNCTION);
		   tokenMap.put("echo",  Token.FUNCTION);
		   tokenMap.put("linear_extrude",  Token.FUNCTION);
		   tokenMap.put("rotate_extrude",  Token.FUNCTION);
		   tokenMap.put("surface",  Token.FUNCTION);
		   tokenMap.put("projection",  Token.FUNCTION);
		   tokenMap.put("render",  Token.FUNCTION);
		   tokenMap.put("childeren",  Token.FUNCTION);
		   tokenMap.put("Generate",  Token.FUNCTION);
		   tokenMap.put("Conditions",  Token.FUNCTION);
		   tokenMap.put("Assignments",  Token.FUNCTION);
		   
		  
		   
		   return tokenMap;
	}

	@Override
	public void addToken(Segment segment, int start, int end, int tokenType, int startOffset) {
	   // This assumes all keywords, etc. were parsed as "identifiers."
	   if (tokenType==Token.IDENTIFIER) {
	      int value = wordsToHighlight.get(segment, start, end);
	      if (value != -1) {
	         tokenType = value;
	      }
	   }
	   super.addToken(segment, start, end, tokenType, startOffset);
	}
	
	/**
	 * Returns a list of tokens representing the given text.
	 *
	 * @param text The text to break into tokens.
	 * @param startTokenType The token with which to start tokenizing.
	 * @param startOffset The offset at which the line of tokens begins.
	 * @return A linked list of tokens representing <code>text</code>.
	 */
	public Token getTokenList(Segment text, int startTokenType, int startOffset) {

	   resetTokenList();

	   char[] array = text.array;
	   int offset = text.offset;
	   int count = text.count;
	   int end = offset + count;

	   // Token starting offsets are always of the form:
	   // 'startOffset + (currentTokenStart-offset)', but since startOffset and
	   // offset are constant, tokens' starting positions become:
	   // 'newStartOffset+currentTokenStart'.
	   int newStartOffset = startOffset - offset;

	   int currentTokenStart = offset;
	   int currentTokenType = startTokenType;

	   for (int i=offset; i<end; i++) {

	      char c = array[i];

	      switch (currentTokenType) {

	         case Token.NULL:

	            currentTokenStart = i;   // Starting a new token here.

	            switch (c) {

	               case ' ':
	               case '\t':
	                  currentTokenType = Token.WHITESPACE;
	                  break;

	               case '"':
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               case '#':
	                  currentTokenType = Token.COMMENT_EOL;
	                  break;

	               default:
	                  if (RSyntaxUtilities.isDigit(c)) {
	                     currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
	                     break;
	                  }
	                  else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
	                     currentTokenType = Token.IDENTIFIER;
	                     break;
	                  }
	                  
	                  // Anything not currently handled - mark as an identifier
	                  currentTokenType = Token.IDENTIFIER;
	                  break;

	            } // End of switch (c).

	            break;

	         case Token.WHITESPACE:

	            switch (c) {

	               case ' ':
	               case '\t':
	                  break;   // Still whitespace.

	               case '"':
	                  addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               case '#':
	                  addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.COMMENT_EOL;
	                  break;

	               default:   // Add the whitespace token and start anew.

	                  addToken(text, currentTokenStart,i-1, Token.WHITESPACE, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;

	                  if (RSyntaxUtilities.isDigit(c)) {
	                     currentTokenType = Token.LITERAL_NUMBER_DECIMAL_INT;
	                     break;
	                  }
	                  else if (RSyntaxUtilities.isLetter(c) || c=='/' || c=='_') {
	                     currentTokenType = Token.IDENTIFIER;
	                     break;
	                  }

	                  // Anything not currently handled - mark as identifier
	                  currentTokenType = Token.IDENTIFIER;

	            } // End of switch (c).

	            break;

	         default: // Should never happen
	         case Token.IDENTIFIER:

	            switch (c) {

	               case ' ':
	               case '\t':
	                  addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.WHITESPACE;
	                  break;

	               case '"':
	                  addToken(text, currentTokenStart,i-1, Token.IDENTIFIER, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               default:
	                  if (RSyntaxUtilities.isLetterOrDigit(c) || c=='/' || c=='_') {
	                     break;   // Still an identifier of some type.
	                  }
	                  // Otherwise, we're still an identifier (?).

	            } // End of switch (c).

	            break;

	         case Token.LITERAL_NUMBER_DECIMAL_INT:

	            switch (c) {

	               case ' ':
	               case '\t':
	                  addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.WHITESPACE;
	                  break;

	               case '"':
	                  addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
	                  currentTokenStart = i;
	                  currentTokenType = Token.LITERAL_STRING_DOUBLE_QUOTE;
	                  break;

	               default:

	                  if (RSyntaxUtilities.isDigit(c)) {
	                     break;   // Still a literal number.
	                  }

	                  // Otherwise, remember this was a number and start over.
	                  addToken(text, currentTokenStart,i-1, Token.LITERAL_NUMBER_DECIMAL_INT, newStartOffset+currentTokenStart);
	                  i--;
	                  currentTokenType = Token.NULL;

	            } // End of switch (c).

	            break;

	         case Token.COMMENT_EOL:
	            i = end - 1;
	            addToken(text, currentTokenStart,i, currentTokenType, newStartOffset+currentTokenStart);
	            // We need to set token type to null so at the bottom we don't add one more token.
	            currentTokenType = Token.NULL;
	            break;

	         case Token.LITERAL_STRING_DOUBLE_QUOTE:
	            if (c=='"') {
	               addToken(text, currentTokenStart,i, Token.LITERAL_STRING_DOUBLE_QUOTE, newStartOffset+currentTokenStart);
	               currentTokenType = Token.NULL;
	            }
	            break;

	      } // End of switch (currentTokenType).

	   } // End of for (int i=offset; i<end; i++).

	   switch (currentTokenType) {

	      // Remember what token type to begin the next line with.
	      case Token.LITERAL_STRING_DOUBLE_QUOTE:
	         addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
	         break;

	      // Do nothing if everything was okay.
	      case Token.NULL:
	         addNullToken();
	         break;

	      // All other token types don't continue to the next line...
	      default:
	         addToken(text, currentTokenStart,end-1, currentTokenType, newStartOffset+currentTokenStart);
	         addNullToken();

	   }

	   // Return the first token in our linked list.
	   return firstToken;

	}
}
