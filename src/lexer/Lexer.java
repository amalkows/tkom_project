package lexer;

import java.util.HashMap;

import errors.BadTokenException;
import errors.EndOfFileException;
import interpretator.Interpretator;
import lexer.Token.TokenType;

public class Lexer {

	private SourcesManager source;
	private char actualChar;
	private String actualToken;
	private HashMap<String, TokenType> keyWords;
	private TokenType actualTokenType;
	private Boolean wasEndFile;
	
	public Lexer(SourcesManager source)
	{

		try {
			this.source = source;
			actualToken = "";
			actualTokenType = null;
			wasEndFile = false;
			nextChar();	
			prepareKeyWordsMap();
			
		} catch (EndOfFileException e) {
			Interpretator.printLine("B³¹d - Koniec pliku", false);
			//e.printStackTrace();
		}
	}
	
	private void prepareKeyWordsMap()
	{
		keyWords = new HashMap<String, Token.TokenType>();
		keyWords.put("JEZELI", TokenType.If);
		keyWords.put("ALBO", TokenType.Else);
		keyWords.put("I", TokenType.And);
		keyWords.put("LUB", TokenType.Or);
		keyWords.put("DOPOKI", TokenType.While);
		keyWords.put("ZWROC", TokenType.Return);
		keyWords.put("PRAWDA", TokenType.True);
		keyWords.put("FALSZ", TokenType.False);
		keyWords.put("NIE", TokenType.LogicalNegation);
	}
	
	public Token getNextToken()
	{
		try
		{
			if(wasEndFile)
				throw new EndOfFileException();

			while(true)
			{	

				skipWhiteSpaces();
				
				Token oneCharToken = isOneCharToken();
				if(oneCharToken != null)
				{
					try{
						nextChar();
					}
					catch(EndOfFileException e)
					{
						wasEndFile = true;
					}
					return oneCharToken;
				}
				
				if(isKeyWordOrType())
					return createTokenFromKeyWord(actualToken);
				
				if(isID())
					return new Token(TokenType.ID, actualToken);			
	
				if(isNumber())
					return createTokenFromNumber(actualToken);	
				
				if(isString())
					return new Token(TokenType.String, actualToken);
				
				if(isComparsionOperator())
					return new Token(TokenType.ComparsionOperator, actualToken);
				
				Token ascriptionOrDivisionOperator = isAscriptionOrDivisionOperator();
				if(ascriptionOrDivisionOperator != null)
					return ascriptionOrDivisionOperator;
				
				nextChar();
				throw new BadTokenException();

			}
			
		}
		catch (EndOfFileException e)
		{
			return new Token(TokenType.EOF);
		}
		catch (BadTokenException e)
		{
			return new Token(TokenType.ERROR, actualToken);
		}
	}
	
	public Token isOneCharToken()
	{
		switch(actualChar)
		{
			case '(':
				return new Token(TokenType.LeftBracket);
			case ')':
				return new Token(TokenType.RightBracket);
			case '{':
				return new Token(TokenType.LeftBrace);
			case '}':
				return new Token(TokenType.RightBrace);
			case '[':
				return new Token(TokenType.LeftTable);
			case ']':
				return new Token(TokenType.RightTable);
			case ';':
				return new Token(TokenType.Semicolon);
			case '+':
				return new Token(TokenType.AddingOperator, "+");
			case '-':
				return new Token(TokenType.AddingOperator, "-");
			case '*':
				return new Token(TokenType.MultiplicationOperator, "*");
			default :
				return null;
		}
	}
	
	private Token isAscriptionOrDivisionOperator() throws BadTokenException
	{
		if(actualChar != ':')
			return null;
		if(!actualToken.equals(""))
			actualToken = "";
		try
		{
			actualToken += actualChar;
			nextChar();
				
			
			if(actualChar != '=')
				return new Token(TokenType.MultiplicationOperator, ":");
			
			actualToken += actualChar;
			nextChar();
			
			return new Token(TokenType.AssignmentOperator);

		}
		catch(EndOfFileException e)
		{
			wasEndFile = true;
			if (actualToken.equals(":="))
				return new Token(TokenType.AssignmentOperator);
			else
				return new Token(TokenType.MultiplicationOperator, ":");

		}
	}
	
	private Boolean isComparsionOperator() throws BadTokenException
	{
		if(actualChar != '<' && actualChar != '>' && actualChar != '=' && actualChar != '!')
			return false;
		if(!actualToken.equals(""))
			actualToken = "";
		try
		{
			actualToken += actualChar;
			nextChar();

			if(actualChar == '=')
			{
				actualToken += actualChar;
				nextChar();
				return true;
			}
			else
				return true;
		}
		catch(EndOfFileException e)
		{
			wasEndFile = true;
			if(!actualToken.equals("!"))
				return true;
			else
			{
				BadTokenException error = new BadTokenException();
				error.setInfo("Spodziewano siê operatora porównania");
				throw error;
			}
		}
	}
	
	private Boolean isString() throws BadTokenException
	{
		if(actualChar != '"')
			return false;
		if(!actualToken.equals(""))
			actualToken = "";
		try
		{
			nextChar();
			
			do
			{
				actualToken += actualChar;
				nextChar();
				if(actualChar == '\\')
				{
					try
					{
						nextChar();
						if(actualChar != '\\' && actualChar != '"')
						{
							BadTokenException error = new BadTokenException();
							error.setInfo("Spodziewano siê symbolu specjalnego (\\\\ lub \\\")");
							throw error;
						}
						actualToken += actualChar;
						nextChar();

					}
					catch(EndOfFileException e)
					{
						BadTokenException error = new BadTokenException();
						error.setInfo("Spodziewano siê symbolu zakoñczenia tekstu (\")");
						throw error;
					}
				}
			}
			while(actualChar != '"');			
			nextChar();
			
			return true;
		}
		catch(EndOfFileException e)
		{
			wasEndFile = true;
			
			if(actualChar == '"')
				return true;
			else
			{
				BadTokenException error = new BadTokenException();
				error.setInfo("Spodziewano siê symbolu zakoñczenia tekstu (\")");
				throw error;
			}
		}	
	}
	
	private Boolean isID()
	{
		if(!Character.isLowerCase(actualChar))
			return false;
		if(!actualToken.equals(""))
			actualToken = "";
		try
		{
			do
			{
				actualToken += actualChar;
				nextChar();
			}
			while(Character.isLowerCase(actualChar) || Character.isDigit(actualChar) || Character.isUpperCase(actualChar));
			
			return true;	
		}
		catch(EndOfFileException e)
		{
			wasEndFile = true;
			return true;
		}	
	}
	
	private Boolean isNumber() throws BadTokenException
	{
		if(!Character.isDigit(actualChar))
			return false;
		if(!actualToken.equals(""))
			actualToken = "";
		try
		{
			actualTokenType = TokenType.Integer;
			
			do
			{
				actualToken += actualChar;
				nextChar();
			}
			while(Character.isDigit(actualChar) && !actualToken.startsWith("0"));
			
			if(actualChar == '/')
			{
				actualToken += actualChar;
				nextChar();
				actualTokenType = TokenType.Fraction;
			}
			else if(actualChar == '.')
			{
				actualToken += actualChar;
				nextChar();
				actualTokenType = TokenType.Double;
			}
			else if(!Character.isUpperCase(actualChar) && !Character.isLowerCase(actualChar))
				return true;
			else
			{
				BadTokenException error = new BadTokenException();
				error.setInfo("B³êdna wartoœæ liczbowa");
				throw error;
			}
				
			
			while(Character.isUpperCase(actualChar) || Character.isLowerCase(actualChar))
			{
				actualToken += actualChar;
				nextChar();
			}
			
			do
			{
				actualToken += actualChar;
				nextChar();
			}
			while(Character.isDigit(actualChar));
			
			if(!Character.isUpperCase(actualChar) && !Character.isLowerCase(actualChar))
				return true;
			else
				throw new BadTokenException();
			
		}
		catch(EndOfFileException e)
		{
			wasEndFile = true;
			if(actualToken.endsWith(".") || actualToken.endsWith("/"))
			{
				BadTokenException error = new BadTokenException();
				error.setInfo("Niedokoñczona liczba");
				throw error;
			}
			return true;
		}	
	}
	
	private Token createTokenFromNumber(String tokenString) throws BadTokenException
	{
		
		switch(actualTokenType)
		{
			case Double:
				return new Token(TokenType.Double, Double.parseDouble(tokenString));
			case Integer:
				return new Token(TokenType.Integer, Integer.parseInt(tokenString));
			case Fraction:
				return new Token(TokenType.Fraction, 
						Integer.parseInt(tokenString.substring(0, tokenString.indexOf('/'))), 
						Integer.parseInt(tokenString.substring(tokenString.indexOf('/')+1)));
			default:
				return new Token(TokenType.ERROR, tokenString);
			
		}
	}
	
	private Boolean isKeyWordOrType() throws BadTokenException
	{
		if(!Character.isUpperCase(actualChar))
			return false;
		if(!actualToken.equals(""))
			actualToken = "";
		if(actualTokenType!= null)
			actualTokenType = null;
		try
		{
			do
			{
				actualToken += actualChar;
				nextChar();
			}
			while(Character.isUpperCase(actualChar));
			
			if(Character.isLowerCase(actualChar))
			{
				actualToken += actualChar;
				nextChar();
				actualTokenType = TokenType.Type;
			}
			
			while(Character.isUpperCase(actualChar) || Character.isLowerCase(actualChar))
			{
				actualToken += actualChar;
				nextChar();
			}
			
			return true;
		}
		catch(EndOfFileException e)
		{
			wasEndFile = true;
			return true;
		}	
	}
	
	private Token createTokenFromKeyWord(String tokenString)
	{
		if(keyWords.containsKey(tokenString))
			return new Token(keyWords.get(tokenString));
		else
			return new Token(TokenType.Type, tokenString);
	}
	
	private void nextChar() throws EndOfFileException
	{
		actualChar = source.getNextChar();
	}

	private void skipWhiteSpaces() throws EndOfFileException
	{
		while(Character.isWhitespace(actualChar))
			actualChar = source.getNextChar();
	}
			
}
