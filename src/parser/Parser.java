package parser;

import code.Assignment;
import code.CodeBlock;
import code.ConditionalInstruction;
import code.Function;
import code.FunctionCall;
import code.GetingValue;
import code.Instruction;
import code.Loop;
import code.ReturnInstruction;
import code.SpecyficValue;
import code.Type;
import code.Variable;
import code.VariableUse;
import code.arithmetic.Addition;
import code.arithmetic.ArithmeticExpression;
import code.arithmetic.Multiplication;
import code.arithmetic.ArithmeticExpression.Sign;
import code.logic.AndExpression;
import code.logic.Comparasion;
import code.logic.LogicExpression;
import code.logic.OrExpression;
import code.logic.comparasioninstructions.EqualExpression;
import code.logic.comparasioninstructions.LessEqualExpression;
import code.logic.comparasioninstructions.LessExpression;
import code.logic.comparasioninstructions.MoreEqualExpression;
import code.logic.comparasioninstructions.MoreExpression;
import code.logic.comparasioninstructions.NonEqualExpression;
import errors.BadArgumentsError;
import errors.BadReturnValueError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.LackOfVariableDeclarationError;
import errors.MultipleDeclarationOfFunctionError;
import errors.MultipleDeclarationOfVariableError;
import errors.NotExpectedTokenError;
import errors.NotInicializedVariableException;
import errors.TableDefinitonError;
import errors.ZeroDivideException;
import lexer.Lexer;
import lexer.Token;
import lexer.Token.TokenType;

public class Parser {

	private Token actualToken;
	private Lexer lexer;
	private Program program;
	
	public Parser(Lexer lexer)
	{
		this.lexer = lexer;
		actualToken = lexer.getNextToken();
		program = new Program();
	}
	
	private void nextToken()
	{
		actualToken = lexer.getNextToken();
	}
	
	public Program parseProgram() throws MultipleDeclarationOfFunctionError, NotExpectedTokenError, BadTypeError, TableDefinitonError, LackOfVariableDeclarationError, BadReturnValueError, BadTableUsingException, ZeroDivideException, BadArgumentsError, NumberFormatException, NotInicializedVariableException, MultipleDeclarationOfVariableError
	{

		while(actualToken.getToken() != TokenType.EOF)
		{
			if(!parseDeclaration(program.context, true))
			{
				NotExpectedTokenError error = new NotExpectedTokenError();
				error.setInfo("Oczekiwano deklaracji funkcji/zmiennej");
				throw error;
			}
		}

		return program;
	}

	private Token checkActualToken(TokenType type) throws NotExpectedTokenError
	{
		Token result = actualToken;
		if(actualToken.getToken() != type)
			throw new NotExpectedTokenError();
		nextToken();
		return result;
	}
	
	private boolean nextTokenEqual(TokenType type)
	{
		return actualToken.getToken() == type;
	}
	
	private boolean parseDeclaration(CodeBlock context, boolean alsoFunctions) throws MultipleDeclarationOfFunctionError, NotExpectedTokenError, BadTypeError, TableDefinitonError, LackOfVariableDeclarationError, BadReturnValueError, BadTableUsingException, ZeroDivideException, BadArgumentsError, NumberFormatException, NotInicializedVariableException, MultipleDeclarationOfVariableError
	{
		if(!nextTokenEqual(TokenType.Type))
			return false;
		
		Token type = checkActualToken(TokenType.Type);
		Token id = checkActualToken(TokenType.ID);	
		
		if(alsoFunctions)
		{
			Function newFunction = parseFunctionDeclaration(type, id);
			if(newFunction != null)
			{
				program.addFunction(newFunction);
				return true;
			}		
		}
		
		if(!parseVariableDeclaration(type, id, context))
		{
			NotExpectedTokenError error = new NotExpectedTokenError();
			error.setInfo("Oczekiwano deklaracji funkcji");
			throw error;
		}
		
		return true;
	}
	
	private boolean parseVariableDeclaration(Token type, Token id, CodeBlock context) throws BadTypeError, NotExpectedTokenError, TableDefinitonError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadArgumentsError, NumberFormatException, NotInicializedVariableException, MultipleDeclarationOfVariableError
	{		
		if(!(nextTokenEqual(TokenType.LeftTable) ||
				nextTokenEqual(TokenType.AssignmentOperator) ||
				nextTokenEqual(TokenType.Semicolon)))
			return false;
		
		Variable result = new Variable();
		result.setType(type.getStringValue());
		result.setId(id.getStringValue());
		
		if(nextTokenEqual(TokenType.LeftTable))
		{
			checkActualToken(TokenType.LeftTable);	
			result.setTable(true);
			result.setTableSize(parseArithmeticExpression(context, null));
			checkActualToken(TokenType.RightTable);	
		}
		
		if(!context.addVariable(result))
		{
			MultipleDeclarationOfVariableError error = new MultipleDeclarationOfVariableError();
			error.setInfo("Zmienna: " + id.getStringValue());
			throw error;
		}

		Instruction assigment = parseVariableAssigment(id, context, false, null);
		if(assigment != null)
		{
			if(result.getTable())
			{
				TableDefinitonError error = new TableDefinitonError();
				error.setInfo("Nie mo¿na po³¹czyæ deklaracji tablicy i przypisania");
				throw error;
			}
			context.instructions.add(assigment);
		}
		else
			checkActualToken(Token.TokenType.Semicolon);
		
		return true;
	}
	
	private Instruction parseVariableAssigment(Token id, CodeBlock context, Boolean table, ArithmeticExpression index) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(!nextTokenEqual(TokenType.AssignmentOperator))
			return null;
		
		Assignment instruction = new Assignment();
		
		instruction.setTable(table);
		instruction.setIndex(index);

		checkActualToken(TokenType.AssignmentOperator);
		
		
		Variable variable = context.findVariableRecurency(id.getStringValue());
		if(variable == null)
			variable = program.findGlobalVariable(id.getStringValue());
		
		if(variable != null)
		{
			if((!variable.getTable() && table) || (variable.getTable() && !table))
			{
				BadTableUsingException error = new BadTableUsingException();
				error.setInfo("Nie mo¿na przypisaæ wartoœci do ca³ej tablicy.");
				throw error;
			}
		}
		else
		{
			LackOfVariableDeclarationError error = new LackOfVariableDeclarationError();
			error.setInfo("Zmienna: " + id.getStringValue());
			throw error;
		}
		
		
		instruction.setVariable(variable);
		
		instruction.setValue(parseValue(context));
		
		if(!instruction.getValueAssignment().isFitType(instruction.getVariable().getType()))
		{
			BadTypeError error = new BadTypeError();
			error.setInfo("Przypisanie " + instruction.getValueAssignment() + " do " + instruction.getVariable().getType());
			throw error;
		}
		
		checkActualToken(TokenType.Semicolon);
				
		return instruction;
	}
	
	private Function parseFunctionDeclaration(Token type, Token id) throws BadTypeError, NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadReturnValueError, TableDefinitonError, BadTableUsingException, ZeroDivideException, BadArgumentsError, NumberFormatException, NotInicializedVariableException, MultipleDeclarationOfVariableError 
	{
		if(!nextTokenEqual(TokenType.LeftBracket))
			return null;
		
		Variable argument;
		Function result = new Function();
		
		result.setReturnType(type.getStringValue());	
		result.setId(id.getStringValue());
		
		checkActualToken(TokenType.LeftBracket);
		
		while(true)
		{
			argument = parseArgument();
			
			if(argument == null)
				break;
			
			result.arguments.add(argument);
		}
		
		checkActualToken(TokenType.RightBracket);

		result.bodyOfFunction = parseCodeBlock(null, result);
		
		return result;
	}
	
	private Variable parseArgument() throws BadTypeError, NotExpectedTokenError
	{
		Variable argument = null;
		if(!nextTokenEqual(TokenType.Type))
			return argument;
		
		argument = new Variable();
		argument.setType(checkActualToken(TokenType.Type).getStringValue());
		argument.setId(checkActualToken(TokenType.ID).getStringValue());
		return argument;
	}
	
	private CodeBlock parseCodeBlock(CodeBlock parent, Function function) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadReturnValueError, BadTypeError, TableDefinitonError, BadTableUsingException, ZeroDivideException, BadArgumentsError, NumberFormatException, NotInicializedVariableException, MultipleDeclarationOfVariableError
	{
 		CodeBlock result = new CodeBlock();
		result.setParent(parent);
		Instruction instruction = null;
		boolean parsedDeclaration = false;
		
		if(parent == null && function.arguments.size() != 0)
			for(Variable var : function.arguments)
				result.localVariablesTemplate.add(var);
		
		checkActualToken(TokenType.LeftBrace);		
		
		do
		{
			parsedDeclaration = false;
			instruction = parseConditionalInstruction(result, function);
			
			if(instruction == null)
				instruction = parseLoop(result, function);
			if(instruction == null)
			{
				instruction = parseIDUsing(result);
				if(instruction != null && nextTokenEqual(TokenType.Semicolon))
					checkActualToken(TokenType.Semicolon);
			}
			if(instruction == null)
				instruction = parseReturnInstruction(result, function.getReturnType());		
			
			if(instruction == null)
			{
				if(parseDeclaration(result, false))
					parsedDeclaration = true;
			}
			else
				result.instructions.add(instruction);
			
		} while(instruction != null || parsedDeclaration);
		
		checkActualToken(TokenType.RightBrace);		
		return result;
	}
	
	private Instruction parseConditionalInstruction(CodeBlock parent, Function function) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadReturnValueError, BadTypeError, TableDefinitonError, BadTableUsingException, ZeroDivideException, BadArgumentsError, NumberFormatException, NotInicializedVariableException, MultipleDeclarationOfVariableError
	{
		if(!nextTokenEqual(TokenType.If))
			return null;
		
		ConditionalInstruction instruction = new ConditionalInstruction();
		
		checkActualToken(TokenType.If);
		
		instruction.setConditional(parseLogicExpression(parent));
		
		instruction.setCodeBlockWhenTrue(parseCodeBlock(parent, function));
		
		if(nextTokenEqual(TokenType.Else))
		{
			checkActualToken(TokenType.Else);
			instruction.setCodeBlockWhenFalse(parseCodeBlock(parent, function));
		}
		
		return instruction;
	}
	
	private Instruction parseLoop(CodeBlock parent, Function function) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadReturnValueError, BadTypeError, TableDefinitonError, BadTableUsingException, ZeroDivideException, BadArgumentsError, NumberFormatException, NotInicializedVariableException, MultipleDeclarationOfVariableError
	{
		if(!nextTokenEqual(TokenType.While))
			return null;
		
		Loop instruction = new Loop();
		
		checkActualToken(TokenType.While);

		instruction.setConditional(parseLogicExpression(parent));
		instruction.setBodyOfLoop(parseCodeBlock(parent, function));
		
		return instruction;
	}
	
	private Instruction parseIDUsing(CodeBlock context) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(!nextTokenEqual(TokenType.ID))
			return null;
		
		Token id = checkActualToken(TokenType.ID);
		Boolean table = false;
		ArithmeticExpression expression = null;
		Instruction result = null;
		
		
		result = parseFunctionCall(context, id);
		
		if(result != null)
			return result;
		else {
			if(nextTokenEqual(TokenType.LeftTable))
			{
				checkActualToken(TokenType.LeftTable);
				table = true;
				expression = parseArithmeticExpression(context, null);
				checkActualToken(TokenType.RightTable);
			}
			
			Instruction assigment = parseVariableAssigment(id, context, table, expression);
			if(assigment != null)
				return assigment;
			else
				return parseVariableUse(id, context, table, expression);
		}
			
	}
	
	private Instruction parseVariableUse(Token id, CodeBlock context, Boolean table, ArithmeticExpression index) throws LackOfVariableDeclarationError, BadTableUsingException 
	{
		VariableUse result = new VariableUse();
		result.setId(id.getStringValue());
		result.setTable(table);
		result.setIndex(index);

		Variable variable = context.findVariableRecurency(id.getStringValue());
		if(variable == null)
			variable = program.findGlobalVariable(id.getStringValue());
		
		if(variable != null)
		{
			if((!variable.getTable() && table) || (variable.getTable() && !table))
			{
				BadTableUsingException error = new BadTableUsingException();
				error.setInfo("Nie mo¿na pobraæ wartoœci ca³ej tablicy.");
				throw error;
			}
		}
		else
		{
			LackOfVariableDeclarationError error = new LackOfVariableDeclarationError();
			error.setInfo("Zmienna: " + id.getStringValue());
			throw error;
		}
		
		return result;
	}
	

	
	private GetingValue parseValue(CodeBlock context) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException 
	{
		if(nextTokenEqual(TokenType.RightBracket))
			return null;
		
		GetingValue instruction = new GetingValue();
		
		if(nextTokenEqual(TokenType.String))
			instruction.setString(checkActualToken(TokenType.String).getStringValue());
		else
		{	
			LogicExpression logicExpression = parseLogicExpression(context);
			Instruction tmp = checkLogicExpressionIsArithmeticExpression(context, logicExpression);
			if(tmp != null)
				instruction.setArithmetic((ArithmeticExpression) tmp);
			else
				instruction.setLogic(logicExpression);
		}
		return instruction;
	} 
	
	private Instruction checkLogicExpressionIsArithmeticExpression(CodeBlock context, LogicExpression logic)
	{
		while(logic.getUnary() && !(logic instanceof Comparasion))
			if(logic.getLeft() == null)
				break;
			else
				logic = logic.getLeft();
		
		if(logic instanceof Comparasion && ((Comparasion) logic).getRightSite() == null)
		{
			ArithmeticExpression result;
			result =((Comparasion) logic).getLeftSite();
			return result;
		}
		else
			return null;
	}
	
	private ArithmeticExpression parseArithmeticExpression(CodeBlock context, ArithmeticExpression left) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException 
	{
		Addition instruction = new Addition();

		instruction.setLeft(parseArithmeticExpressionMulti(context, left));

		return parseArithmeticExpressionPrim(context, instruction);
	}
	
	private ArithmeticExpression parseArithmeticExpressionPrim(CodeBlock context, ArithmeticExpression exp) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(nextTokenEqual(TokenType.AddingOperator))
		{
			Token sign = checkActualToken(TokenType.AddingOperator);
			Addition instruction = new Addition();
			if(sign.getStringValue() == "+")
				exp.setSign(Sign.Add);
			else
				exp.setSign(Sign.Subtract);
			exp.setRight(parseArithmeticExpressionMulti(context, null));
			instruction.setLeft(exp);
			return parseArithmeticExpressionPrim(context, instruction);
		}
		else
		{
			exp.setRight(null);
			return exp;
		}
	}	
	
	private ArithmeticExpression parseArithmeticExpressionMulti(CodeBlock context, ArithmeticExpression left) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		Multiplication instruction = new Multiplication();
		if(left == null)
			instruction.setLeft(parseArithmeticExpressionBase(context));
		else
			instruction.setLeft(left);
		return parseArithmeticExpressionMultiPrim(context, instruction);
	}	
	
	private ArithmeticExpression parseArithmeticExpressionMultiPrim(CodeBlock context, ArithmeticExpression exp) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(nextTokenEqual(TokenType.MultiplicationOperator))
		{
			Token sign = checkActualToken(TokenType.MultiplicationOperator);
			Multiplication instruction = new Multiplication();

			if(sign.getStringValue() == "*")
				exp.setSign(Sign.Multiply);
			else
				exp.setSign(Sign.Divide);
			exp.setRight(parseArithmeticExpressionBase(context));
			instruction.setLeft(exp);
			return parseArithmeticExpressionMultiPrim(context, instruction);
		}
		else
		{
			exp.setRight(null);
			return exp;
		}
	}
	
	private ArithmeticExpression parseArithmeticExpressionBase(CodeBlock context) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		ArithmeticExpression result = new ArithmeticExpression();
		code.arithmetic.Negation negation = null;
		
		if(nextTokenEqual(TokenType.AddingOperator))
		{
			if(checkActualToken(TokenType.AddingOperator).getStringValue() != "-")
			{
				NotExpectedTokenError error = new NotExpectedTokenError();
				error.setInfo("Spodziewano siê operatora negacji \"-\"");
				throw error;
			}
			
			negation = new code.arithmetic.Negation();
		}

		if(nextTokenEqual(TokenType.LeftBracket))
		{
			checkActualToken(TokenType.LeftBracket);
			result = parseArithmeticExpression(context, null);
			checkActualToken(TokenType.RightBracket);
		}
		else
		{
			if(nextTokenEqual(TokenType.ID))
				result.setInstructionValue(parseIDUsing(context));
			else
				result.setValue(parseArithmeticAtom());
		}
		
		if(negation != null)
		{
			negation.setLeft(result);
			return negation;
		}
		return result;
	}
	
	private SpecyficValue parseArithmeticAtom() throws NotExpectedTokenError, ZeroDivideException
	{
		SpecyficValue value = new SpecyficValue();
		if(nextTokenEqual(TokenType.Integer))
		{
			value.setType(Type.LiczbaNaturalna);
			value.setIntValue(checkActualToken(TokenType.Integer).getIntValue());
		}
		else if(nextTokenEqual(TokenType.Double))
		{
			value.setType(Type.LiczbaZmiennoprzecinkowa);
			value.setDoubleValue(checkActualToken(TokenType.Double).getDoubleValue());
		}
		else if(nextTokenEqual(TokenType.Fraction))
		{
			value.setType(Type.UlamekZwykly);
			Token fraction = checkActualToken(TokenType.Fraction);
			value.setNumerator(fraction.getIntValue());
			value.setDenumerator(fraction.getSecondIntValue());
		}
		else
		{
			NotExpectedTokenError error = new NotExpectedTokenError();
			error.setInfo("Spodziewano siê wartoœci liczbowej");
			throw new NotExpectedTokenError();
		}
			
		return value;
	}
	
	private LogicExpression parseLogicExpression(CodeBlock context) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		AndExpression expression = new AndExpression();
		expression.setLeft(parseLogicExpressionAlternative(context));
 		return parseLogicExpressionPrim(context, expression);
	}
	
	private LogicExpression parseLogicExpressionPrim(CodeBlock context, LogicExpression expression) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(nextTokenEqual(TokenType.And))
		{
			checkActualToken(TokenType.And);
			AndExpression newExpression = new AndExpression();
			expression.setRight(parseLogicExpressionAlternative(context));
			newExpression.setLeft(expression);
			return parseLogicExpressionPrim(context, newExpression);
		}
		else
		{
			expression.setRight(null);
			return expression;
		}
	}
	
	private LogicExpression parseLogicExpressionAlternative(CodeBlock context) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		OrExpression expression = new OrExpression();
		expression.setLeft(parseLogicExpressionNegationAndBracket(context));
		return parseLogicExpressionAlternativePrim(context, expression);
	}
	
	private LogicExpression parseLogicExpressionAlternativePrim(CodeBlock context, LogicExpression expression) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(nextTokenEqual(TokenType.Or))
		{
			checkActualToken(TokenType.Or);
			OrExpression newExpression = new OrExpression();
			expression.setRight(parseLogicExpressionNegationAndBracket(context));
			newExpression.setLeft(expression);
			return parseLogicExpressionAlternativePrim(context, newExpression);
		}
		else
		{
			expression.setRight(null);
			return expression;
		}
	}
	
	private LogicExpression parseLogicExpressionNegationAndBracket(CodeBlock context) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		LogicExpression result = null;
		code.logic.Negation negation= null;
		
		if(nextTokenEqual(TokenType.LogicalNegation))
		{
			checkActualToken(TokenType.LogicalNegation);
			negation = new code.logic.Negation();
		}
		
		
		result = parseLogicExpressionBrackets(context);
		
		if(result == null)
		{
			result = new LogicExpression();
			SpecyficValue value = parseLogicAtom();
			
			if(value != null)
				result.setValue(value);
			else
				result = parseLogicExpressionBase(context, parseArithmeticExpression(context, null));
		}
		
		
		if(negation != null)
		{
			negation.setLeft(result);
			return negation;
		}
		
		return result;
	}		

	private LogicExpression parseLogicExpressionBrackets(CodeBlock context) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(!nextTokenEqual(TokenType.LeftBracket))
			return null;
		
		checkActualToken(TokenType.LeftBracket);
		
		LogicExpression result = new LogicExpression();
		
		result = parseLogicExpression(context);

		Instruction tmp = checkLogicExpressionIsArithmeticExpression(context, result);
		if(tmp != null)
		{
			checkActualToken(TokenType.RightBracket);
			ArithmeticExpression arithmeticExp = parseArithmeticExpression(context, (ArithmeticExpression) tmp);
			result = parseLogicExpressionBase(context, arithmeticExp);
		}
		else
			checkActualToken(TokenType.RightBracket);
		
		return result;
	}
	
	private SpecyficValue parseLogicAtom() throws NotExpectedTokenError
	{
		
		SpecyficValue result = new SpecyficValue();
		result.setType(Type.ZmiennaLogiczna);
		if(nextTokenEqual((TokenType.False)))
		{
			checkActualToken(TokenType.False);
			result.setBooleanValue(false);
		}
		else if(nextTokenEqual((TokenType.True)))
		{
			checkActualToken(TokenType.True);
			result.setBooleanValue(true);
		}
		else
			return null;
		return result;
	}
	
	private LogicExpression parseLogicExpressionBase(CodeBlock context, ArithmeticExpression left) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{

		Comparasion result = null;
		ArithmeticExpression right;
				
		if(nextTokenEqual(TokenType.ComparsionOperator))
		{
			Token comparator = checkActualToken(TokenType.ComparsionOperator);
			
			right = parseArithmeticExpression(context, null);
			
			switch(comparator.getStringValue())
			{
				case "<=":
					result = new LessEqualExpression();
					break;
				case "<":
					result = new LessExpression();
					break;
				case ">=":
					result = new MoreEqualExpression();
					break;
				case ">":
					result = new MoreExpression();
					break;
				case "=":
					result = new EqualExpression();
					break;
				case "!=":
					result = new NonEqualExpression();
					break;
				default:
					NotExpectedTokenError error = new NotExpectedTokenError();
					error.setInfo("Spodziewano siê operatora porównania");
					throw error;
			}
			result.setLeftSite(left);
			result.setRightSite(right);	
		}
		else
		{
			result = new Comparasion();
			result.setLeftSite(left);
		}
		

		return result;
	}	
	
	
	
	private Instruction parseFunctionCall(CodeBlock context, Token id) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(!nextTokenEqual(TokenType.LeftBracket))
			return null;
		
		Boolean dontDeclaratedFunction = false;
		FunctionCall instruction = new FunctionCall();
		Function function = program.findFunction(id.getStringValue());
		if(function == null)
		{
			dontDeclaratedFunction = true;
			function = new Function();
			function.setId(id.getStringValue());
			program.addFunction(function);
		}
		else if (function.bodyOfFunction == null)
			dontDeclaratedFunction = true;
		
		instruction.setFunction(function);

		int argumentCount = instruction.getFunction().arguments.size();
		
		checkActualToken(TokenType.LeftBracket);
		
		for(int i = 0; i < argumentCount || dontDeclaratedFunction; ++i)
		{
			GetingValue arg = parseValue(context);
			
			if(dontDeclaratedFunction && arg == null)
				break;
			else if(arg == null)
			{
				BadArgumentsError error = new BadArgumentsError();
				error.setInfo("Oczekiwano wiêkszej liczby argumentów w funkcji " + id.getStringValue());
				throw error;
			}
			
			instruction.arguments.add(arg);	
			
			if(instruction.getFunction().arguments.size() != 0)
				if(!arg.isFitType(instruction.getFunction().arguments.get(i).getType()))
				{
					BadArgumentsError error = new BadArgumentsError();
					error.setInfo("Oczekiwano argumentu typu " + instruction.getFunction().arguments.get(i).getType() + " zamiast " + arg.getValue().getType() + " w funkcji " + id.getStringValue());
					throw error;
				}
			

				
		}
		
		checkActualToken(TokenType.RightBracket);
		
		
		return instruction;
	}
	
	private ReturnInstruction parseReturnInstruction(CodeBlock context, Type type) throws NotExpectedTokenError, LackOfVariableDeclarationError, MultipleDeclarationOfFunctionError, BadTableUsingException, ZeroDivideException, BadTypeError, BadArgumentsError, NumberFormatException, NotInicializedVariableException
	{
		if(!nextTokenEqual(TokenType.Return))
			return null;
		
		ReturnInstruction instruction = new ReturnInstruction();
		
		checkActualToken(TokenType.Return);

		if(!nextTokenEqual(TokenType.Semicolon))
		{		
			GetingValue value = parseValue(context);
			
			if(!value.isFitType(type))
			{
				BadTypeError error = new BadTypeError();
				error.setInfo("Funkcja powinna zawracaæ " + type);
				throw error;
			}
			
			if(type == Type.Tekst)
				instruction.setStringValue(value.getString());
			else if(type == Type.ZmiennaLogiczna)
				instruction.setLogicValue(value.getLogic());
			else
				instruction.setArithmeticValue(value.getArithmetic());	
		}
		checkActualToken(TokenType.Semicolon);
		
		return instruction;
	}
	
	public Token getActualToken() {
		return actualToken;
	}
}