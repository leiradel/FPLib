import java.util.Hashtable;
import java.util.StringTokenizer;

// An unexpected character was found in the input
class InvalidCharacterInInputException extends RuntimeException {
  char k;

  InvalidCharacterInInputException(char k) {
    this.k = k;
  }

  public String toString() {
    return "Invalid character in input: '" + k + "'";
  }
}

// An expected token was not found in the input
class UnmatchedTokenException extends RuntimeException {
  Token token;

  UnmatchedTokenException(Token token) {
    this.token = token;
  }

  public String toString() {
    return "Unexpected token in input: " + token;
  }
}

// A terminal (number, identifier or sub-expression) was required but not found
class TerminalExpectedException extends RuntimeException {
  Token token;

  TerminalExpectedException(Token token) {
    this.token = token;
  }

  public String toString() {
    return "Terminal expected, " + token + " found instead";
  }
}

// The expression was ended but an extra token was found
class ExtraInputAfterExpressionException extends RuntimeException {
  Token token;

  ExtraInputAfterExpressionException(Token token) {
    this.token = token;
  }

  public String toString() {
    return "Extra input after expression: " + token;
  }
}

// An unknown command line option was found
class UnknownCommandLineOptionException extends RuntimeException {
  String opt;

  UnknownCommandLineOptionException(String opt) {
    this.opt = opt;
  }

  public String toString() {
    return "Unknown command line option: " + opt;
  }
}

// The expression was empty
class EmptyExpressionException extends RuntimeException {
  public String toString() {
    return "No expression found in the command line";
  }
}  

// This class represents a token found in the input
class Token {
  // The token codes
  static final int EOF  = 0;
  static final int INT  = 1;
  static final int FP   = 2;
  static final int ID   = 3;
  static final int MUL  = 4;
  static final int DIV  = 5;
  static final int MOD  = 6;
  static final int ADD  = 7;
  static final int SUB  = 8;
  static final int LPAR = 9;
  static final int RPAR = 10;
  static final int COMA = 11;

  // The code of this token
  int code;
  // The lexeme of this token
  String lexeme;

  Token(int code, String lexeme) {
    this.code = code;
    this.lexeme = lexeme;
  }

  public String toString() {
    return lexeme;
  }
}

// This is the lexer analyzer
class Lexer {
  // The expression being parsed
  String exp;
  // The position the lexer is analyzing
  int pos;
  // The current token
  Token lookAhead;

  Lexer(String exp) {
    this.exp = exp;
    pos = 0;
  }

  static boolean isDigit(char k) {
    return k >= '0' && k <= '9';
  }

  static boolean isAlpha(char k) {
    return (k >= 'a' && k <= 'z') || (k >= 'A' && k <= 'Z') || k == '_';
  }

  // Match any token in the input stream and retrieves the next one
  void match() throws RuntimeException {
    StringBuffer lexeme = new StringBuffer();

    // Skip any spaces
    while (pos < exp.length() && exp.charAt(pos) == ' ')
      pos++;
    // Return EOF if we've reached the end of the input
    if (pos >= exp.length()) {
      lookAhead = new Token(Token.EOF, "end of input");
      return;
    }
    // If the character is a digit, the token is an integer or a FP number
    if (isDigit(exp.charAt(pos))) {
      // Get all digits
      while (isDigit(exp.charAt(pos)))
        lexeme.append(exp.charAt(pos++));
      // If we have a decimal point, it's a FP number
      if (exp.charAt(pos) == '.') {
        lexeme.append('.');
        pos++;
        // Get any digits after the decimal digit
        while (isDigit(exp.charAt(pos)))
          lexeme.append(exp.charAt(pos++));
        lookAhead = new Token(Token.FP, lexeme.toString());
      } else
        lookAhead = new Token(Token.INT, lexeme.toString());
      return;
    }
    // If the character is alphabetic or '_', the token is an identifier
    if (isAlpha(exp.charAt(pos))) {
      // Get all alphanumeric and '_' and '.' characters
      while (isAlpha(exp.charAt(pos)) || isDigit(exp.charAt(pos)) || exp.charAt(pos) == '.')
        lexeme.append(exp.charAt(pos++));
      lookAhead = new Token(Token.ID, lexeme.toString());
      return;
    }
    // Otherwise the token is one of the bellow
    switch (exp.charAt(pos++)) {
      case '*':
        lookAhead = new Token(Token.MUL, "*");
        return;
      case '/':
        lookAhead = new Token(Token.DIV, "/");
        return;
      case '%':
        lookAhead = new Token(Token.MOD, "%");
        return;
      case '+':
        lookAhead = new Token(Token.ADD, "+");
        return;
      case '-':
        lookAhead = new Token(Token.SUB, "-");
        return;
      case '(':
        lookAhead = new Token(Token.LPAR, "(");
        return;
      case ')':
        lookAhead = new Token(Token.RPAR, ")");
        return;
      case ',':
        lookAhead = new Token(Token.COMA, ",");
        return;
    }
    // Invalid character found
    throw new InvalidCharacterInInputException(exp.charAt(--pos));
  }

  // Match a specific token code, thrown an error on a mismatch
  void match(int code) throws RuntimeException {
    if (lookAhead.code != code)
      throw new UnmatchedTokenException(lookAhead);
    match();
  }
}

// The parser, all methods are static
public class FPComp {
  static Hashtable integers;

  // Parses a terminal (number, identifier, function call or sub-expression)
  static String terminal(Lexer lexer) throws RuntimeException {
    String  t;
    boolean isInt;

    switch (lexer.lookAhead.code) {
      case Token.INT:
        // Returns the integer transformed into a FP number
        t = "" + (Integer.parseInt(lexer.lookAhead.lexeme) << 16);
        lexer.match();
        break;
      case Token.FP:
        // Returns the FP number
        t = "" + (long)(Double.parseDouble(lexer.lookAhead.lexeme) * 65536);
        lexer.match();
        break;
      case Token.ID:
        // Gets the identifier
        t = lexer.lookAhead.lexeme;
        lexer.match();
        // Check if it's defined as an integer
        isInt = integers.containsKey(t);
        // If there is an '(' following the id, it's a method call
        if (lexer.lookAhead.code == Token.LPAR) {
          StringBuffer call = new StringBuffer();

          call.append(t);
          call.append('(');
          lexer.match();
          // Check if there are any parameters
          if (lexer.lookAhead.code != Token.RPAR) {
            call.append(term(lexer));
            // Repeat until there are no more parameters
            while (lexer.lookAhead.code == Token.COMA) {
              call.append(',');
              lexer.match();
               	call.append(term(lexer));
            }
          }
          call.append(')');
          lexer.match(Token.RPAR);
          t = call.toString();
        }
        // If the identifier is defined as an integer, shift it left by 16 bits
        if (isInt)
          t = "(" + t + "<<16)";
        break;
      case Token.LPAR:
        // Returns the sub-expression
        lexer.match();
        t = "(" + term(lexer) + ")";
        lexer.match(Token.RPAR);
        break;
      default:
        // A terminal wasn't found
        throw new TerminalExpectedException(lexer.lookAhead);
    }
    return t;
  }

  // Parses an unary operator (currently only the unary minus)
  static String unary(Lexer lexer) {
    if (lexer.lookAhead.code == Token.SUB) {
      lexer.match();
      return "-" + terminal(lexer);
    }
    return terminal(lexer);
  }

  // Parses a multiplication or division
  static String fact(Lexer lexer) {
    String       left;
    StringBuffer buf = new StringBuffer();

    // Get the left operand
    left = unary(lexer);
    // Parse multiplications and divisions
    while (lexer.lookAhead.code == Token.MUL || lexer.lookAhead.code == Token.DIV || lexer.lookAhead.code == Token.MOD) {
      // Empty the buffer
      buf.setLength(0);
      if (lexer.lookAhead.code == Token.MUL) {
        lexer.match();
        // Cast both operands to long, multiply and then cast the result back to an int
        buf.append("((int)((((long)");
        buf.append(left);
        buf.append(")*((long)");
        buf.append(unary(lexer));
        buf.append("))>>16))");
      } else if (lexer.lookAhead.code == Token.DIV) {
        lexer.match();
             	// Cast the left operand to a long, divide and cast the result back to an int
        buf.append("((int)(((((long)");
        buf.append(left);
        buf.append(")<<32)/");
        buf.append(unary(lexer));
        buf.append(")>>16))");
      } else {
             	lexer.match();
             	// Evaluate the mod
             	buf.append('(');
             	buf.append(left);
             	buf.append('%');
             	buf.append(unary(lexer));
             	buf.append(')');
      }       	
      // The left operand is now the resulting expression
      left = buf.toString();
    }
    // Return the resulting expression
    return left;
  }

  // Parses an addition or a subtraction
  static String term(Lexer lexer) {
    String       left;
    StringBuffer buf = new StringBuffer();

    // Get the left operand
    left = fact(lexer);
    // Parse addtions and subtractions
    while (lexer.lookAhead.code == Token.ADD || lexer.lookAhead.code == Token.SUB) {
      // Empty the buffer
      buf.setLength(0);
      if (lexer.lookAhead.code == Token.ADD) {
        lexer.match();
        // Just add the operands
        buf.append('(');
        buf.append(left);
        buf.append('+');
        buf.append(fact(lexer));
        buf.append(')');
      } else {
        lexer.match();
        // Just subtract the operands
        buf.append('(');
        buf.append(left);
        buf.append('-');
        buf.append(fact(lexer));
        buf.append(')');
      }
      // The left operand is now the resulting expression
      left = buf.toString();
    }
    // Return the resulting expression
    return left;
  }

  // Parse the expression
  static String parse(Lexer lexer) throws RuntimeException {
    String result = term(lexer);
    if (lexer.lookAhead.code != Token.EOF)
      throw new ExtraInputAfterExpressionException(lexer.lookAhead);
    return result;
  }

  // Compile the expression in the string exp
  static String compile(String exp) {
    Lexer lexer = new Lexer(exp);

    lexer.match();
    return parse(lexer);
  }

  // The main method
  public static void main(String[] args) {
    int    i;
    StringBuffer buf = new StringBuffer();

    // Initialize the integers hashtable
    integers = new Hashtable(16);
    // Check for command line options
    i = 0;
    while (i < args.length && args[i].charAt(0) == '-') {
      if (args[i].charAt(1) == 'i') {
        integers.put(args[i].substring(2), integers);
        i++;
      } else
        throw new UnknownCommandLineOptionException(args[i]);
    }
    // Concatenate all args into an expression
    while (i < args.length) {
      buf.append(args[i]);
      buf.append(' ');
      i++;
    }
    // Check for empty expression
    if (buf.length() == 0)
      throw new EmptyExpressionException();
    // Compile and print the result
    System.out.println(compile(buf.toString()));
  }
}
