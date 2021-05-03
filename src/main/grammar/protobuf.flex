package io.kanro.idea.plugin.protobuf.lang.lexer;

import io.kanro.idea.plugin.protobuf.lang.psi.token.ProtobufTokens;

import com.intellij.lexer.FlexLexer;
import com.intellij.psi.tree.IElementType;

%%

%public
%class _ProtobufLexer
%implements FlexLexer
%function advance
%type IElementType
%unicode
%state LINE_COMMENT, BLOCK_COMMENT, AFTER_NUMBER

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
LeadingWhitespace = {NewLine} {WhitespaceNoNewline}*

// Comments.
CLineComment = ("//" [^\n]*)
CLineComments = {CLineComment} ({LeadingWhitespace} {CLineComment})*
CBlockComment = "/*" !([^]* "*/" [^]*) "*/"?

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

  "="                       { return ProtobufTokens.ASSIGN; }
  ":"                       { return ProtobufTokens.COLON; }
  ","                       { return ProtobufTokens.COMMA; }
  "."                       { return ProtobufTokens.DOT; }
  ">"                       { return ProtobufTokens.GT; }
  "{"                       { return ProtobufTokens.LBRACE; }
  "["                       { return ProtobufTokens.LBRACK; }
  "("                       { return ProtobufTokens.LPAREN; }
  "<"                       { return ProtobufTokens.LT; }
  "-"                       { return ProtobufTokens.MINUS; }
  "}"                       { return ProtobufTokens.RBRACE; }
  "]"                       { return ProtobufTokens.RBRACK; }
  ")"                       { return ProtobufTokens.RPAREN; }
  ";"                       { return ProtobufTokens.SEMI; }
  "/"                       { return ProtobufTokens.SLASH; }

  "default"                 { return ProtobufTokens.DEFAULT; }
  "enum"                    { return ProtobufTokens.ENUM; }
  "extend"                  { return ProtobufTokens.EXTEND; }
  "extensions"              { return ProtobufTokens.EXTENSIONS; }
  "group"                   { return ProtobufTokens.GROUP; }
  "import"                  { return ProtobufTokens.IMPORT; }
  "json_name"               { return ProtobufTokens.JSON_NAME; }
  "map"                     { return ProtobufTokens.MAP; }
  "max"                     { return ProtobufTokens.MAX; }
  "message"                 { return ProtobufTokens.MESSAGE; }
  "oneof"                   { return ProtobufTokens.ONEOF; }
  "option"                  { return ProtobufTokens.OPTION; }
  "optional"                { return ProtobufTokens.OPTIONAL; }
  "package"                 { return ProtobufTokens.PACKAGE; }
  "public"                  { return ProtobufTokens.PUBLIC; }
  "repeated"                { return ProtobufTokens.REPEATED; }
  "required"                { return ProtobufTokens.REQUIRED; }
  "reserved"                { return ProtobufTokens.RESERVED; }
  "returns"                 { return ProtobufTokens.RETURNS; }
  "rpc"                     { return ProtobufTokens.RPC; }
  "service"                 { return ProtobufTokens.SERVICE; }
  "stream"                  { return ProtobufTokens.STREAM; }
  "syntax"                  { return ProtobufTokens.SYNTAX; }
  "to"                      { return ProtobufTokens.TO; }
  "true"                    { return ProtobufTokens.TRUE; }
  "false"                   { return ProtobufTokens.FALSE; }
  "weak"                    { return ProtobufTokens.WEAK; }

  {Identifier}              { return ProtobufTokens.IDENTIFIER_LITERAL; }
  {String}                  { return ProtobufTokens.STRING_LITERAL; }
  {Integer}                 { yybegin(AFTER_NUMBER); return ProtobufTokens.INTEGER_LITERAL; }
  {Float}                   { yybegin(AFTER_NUMBER); return ProtobufTokens.FLOAT_LITERAL; }

  // C-style comments, allowed when injected into protobuf.
  "//" {
    yypushback(2);
    yybegin(LINE_COMMENT);
  }

  "/*" {
    yypushback(2);
    yybegin(BLOCK_COMMENT);
  }

  // Additional unmatched symbols are matched individually as SYMBOL.
  {Symbol} { return ProtobufTokens.SYMBOL; }

  // All other unmatched characters.
  [^] { return com.intellij.psi.TokenType.BAD_CHARACTER; }
}

<LINE_COMMENT> {
  {CLineComments}           { yybegin(YYINITIAL); return ProtobufTokens.LINE_COMMENT; }
}

<BLOCK_COMMENT> {
  {CBlockComment}           { yybegin(YYINITIAL); return ProtobufTokens.BLOCK_COMMENT; }
}

<AFTER_NUMBER> {
  // An identifier immediately following a number (with no whitespace) is an error. We return
  // the special IDENTIFIER_AFTER_NUMBER token type to signal this scenario.
  {Identifier} { yybegin(YYINITIAL); return ProtobufTokens.IDENTIFIER_AFTER_NUMBER; }

  // Any other token is valid. Push the token back and return to the initial state.
  [^] { yybegin(YYINITIAL); yypushback(yylength()); }
}