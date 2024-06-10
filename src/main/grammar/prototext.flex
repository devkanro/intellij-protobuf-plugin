package io.kanro.idea.plugin.protobuf.lang.lexer.text;

import io.kanro.idea.plugin.protobuf.lang.psi.text.token.ProtoTextTokens;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

%%

%public
%class _ProtoTextLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%state LINE_COMMENT, SHARP_LINE_COMMENT, BLOCK_COMMENT, AFTER_NUMBER

// General classes
Alpha = [a-zA-Z_]
Digit = [0-9]
NonZeroDigit = [1-9]
HexDigit = [0-9a-fA-F]
OctDigit = [0-7]
Alphanumeric = {Alpha} | {Digit}

// Catch-all for symbols not handled elsewhere.
//
// From tokenizer.h:
//   Any other printable character, like '!' or '+'. Symbols are always a single character, so
//   "!+$%" is four tokens.
Symbol = [!#$%&()*+,-./:;<=>?@\[\\\]\^`{|}~]

// Whitespace.
WhitespaceNoNewline = [\ \t\r\f\x0b] // '\x0b' is '\v' (vertical tab) in C.
NewLine = "\n"
Whitespace = ({WhitespaceNoNewline} | {NewLine})+

// Comments.
SharpLineComments = ("#" [^\n]*)

// Identifiers.
//
// From tokenizer.h:
//   A sequence of letters, digits, and underscores, not starting with a digit.  It is an error for
//   a number to be followed by an identifier with no space in between.
Identifier = {Alpha} {Alphanumeric}*

// Integers.
//
// From tokenizer.h:
//   A sequence of digits representing an integer.  Normally the digits are decimal, but a prefix of
//   "0x" indicates a hex number and a leading zero indicates octal, just like with C numeric
//   literals.  A leading negative sign is NOT included in the token; it's up to the parser to
//   interpret the unary minus operator on its own.
DecInteger = "0" | {NonZeroDigit} {Digit}*
OctInteger = "0" {OctDigit}+
HexInteger = "0" [xX] {HexDigit}+
Integer = {DecInteger} | {OctInteger} | {HexInteger}

// Floats.
//
// From tokenizer.h:
//   A floating point literal, with a fractional part and/or an exponent.  Always in decimal.
//   Again, never negative.
Float = ("." {Digit}+ {Exponent}? | {DecInteger} "." {Digit}* {Exponent}? | {DecInteger} {Exponent})
Exponent = [eE] [-+]? {Digit}+

// Strings.
//
// From tokenizer.h:
//   A quoted sequence of escaped characters.  Either single or double quotes can be used, but they
//   must match. A string literal cannot cross a line break.
SingleQuotedString = \' ([^\\\'\n] | \\[^\n])* (\' | \\)?
DoubleQuotedString = \" ([^\\\"\n] | \\[^\n])* (\" | \\)?
String = {SingleQuotedString} | {DoubleQuotedString}

%%

<YYINITIAL> {
  {Whitespace}              { return com.intellij.psi.TokenType.WHITE_SPACE; }

  "="                       { return ProtoTextTokens.ASSIGN; }
  ":"                       { return ProtoTextTokens.COLON; }
  ","                       { return ProtoTextTokens.COMMA; }
  "."                       { return ProtoTextTokens.DOT; }
  ">"                       { return ProtoTextTokens.GT; }
  "{"                       { return ProtoTextTokens.LBRACE; }
  "["                       { return ProtoTextTokens.LBRACK; }
  "("                       { return ProtoTextTokens.LPAREN; }
  "<"                       { return ProtoTextTokens.LT; }
  "-"                       { return ProtoTextTokens.MINUS; }
  "+"                       { return ProtoTextTokens.PLUS; }
  "}"                       { return ProtoTextTokens.RBRACE; }
  "]"                       { return ProtoTextTokens.RBRACK; }
  ")"                       { return ProtoTextTokens.RPAREN; }
  ";"                       { return ProtoTextTokens.SEMI; }
  "/"                       { return ProtoTextTokens.SLASH; }

  "true"                    { return ProtoTextTokens.TRUE; }
  "false"                   { return ProtoTextTokens.FALSE; }

  {Identifier}              { return ProtoTextTokens.IDENTIFIER_LITERAL; }
  {String}                  { return ProtoTextTokens.STRING_LITERAL; }
  {Integer}                 { yybegin(AFTER_NUMBER); return ProtoTextTokens.INTEGER_LITERAL; }
  {Float}                   { yybegin(AFTER_NUMBER); return ProtoTextTokens.FLOAT_LITERAL; }

  "#" {
    yypushback(1);
    yybegin(SHARP_LINE_COMMENT);
  }

  // Additional unmatched symbols are matched individually as SYMBOL.
  {Symbol} { return ProtoTextTokens.SYMBOL; }

  // All other unmatched characters.
  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<SHARP_LINE_COMMENT> {
  {SharpLineComments}           { yybegin(YYINITIAL); return ProtoTextTokens.SHARP_LINE_COMMENT; }
}

<AFTER_NUMBER> {
  // An identifier immediately following a number (with no whitespace) is an error. We return
  // the special IDENTIFIER_AFTER_NUMBER token type to signal this scenario.
  {Identifier} { yybegin(YYINITIAL); return ProtoTextTokens.IDENTIFIER_AFTER_NUMBER; }

  // Any other token is valid. Push the token back and return to the initial state.
  [^] { yybegin(YYINITIAL); yypushback(yylength()); }
}