package lexer;


public class Token {

	public enum TokenType {
		ID, 
		If, 
		Else, 
		While, 
		And, 
		Or, 
		Return, 
		Type,
		LeftTable, 
		RightTable, 
		LeftBrace, 
		RightBrace, 
		LeftBracket, 
		RightBracket, 
		Semicolon, 
		ComparsionOperator, 
		AddingOperator, 
		MultiplicationOperator, 
		AssignmentOperator, 
		Integer, 
		Double, 
		String, 
		False,
		True,
		Fraction,
		EOF,
		ERROR,
		LogicalNegation
	}
	
	private String stringValue;
	private Boolean boolValue;
	private Integer intValue;
	private Integer secondIntValue;
	private Double doubleValue;
	private TokenType token;

	public Token(TokenType token)
	{
		this.token = token;
	}
	
	public Token(TokenType token, String stringValue)
	{
		this.token = token;
		this.stringValue = stringValue;
	}
	
	public Token(TokenType token, Integer intValue)
	{
		this.token = token;
		this.intValue = intValue;
	}
	
	public Token(TokenType token, int intValue, int secondIntValue)
	{
		this.token = token;
		this.intValue = intValue;
		this.secondIntValue = secondIntValue;
	}

	public Token(TokenType token, Double doubleValue)
	{
		this.token = token;
		this.doubleValue = doubleValue;
	}
	
	public Token(TokenType token, Boolean boolValue)
	{
		this.token = token;
		this.boolValue = boolValue;
	}

	public Integer getSecondIntValue() {
		return secondIntValue;
	}
	
	public String getStringValue() {
		return stringValue;
	}

	public Boolean getBoolValue() {
		return boolValue;
	}

	public Integer getIntValue() {
		return intValue;
	}

	public Double getDoubleValue() {
		return doubleValue;
	}

	public TokenType getToken() {
		return token;
	}
	
	
	
}
