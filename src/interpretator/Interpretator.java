package interpretator;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;
import code.CodeBlock;
import code.Function;
import code.InNumberFunction;
import code.InStringFunction;
import code.Instruction;
import code.OutFunction;
import code.Type;
import code.Variable;
import errors.BadArgumentsError;
import errors.BadReturnValueError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.LackOfFunctionDeclarationError;
import errors.LackOfVariableDeclarationError;
import errors.MissingFunctionException;
import errors.MultipleDeclarationOfFunctionError;
import errors.MultipleDeclarationOfVariableError;
import errors.NotExpectedTokenError;
import errors.NotInicializedVariableException;
import errors.TableDefinitonError;
import errors.ZeroDivideException;
import lexer.Lexer;
import lexer.SourcesManager;
import lexer.Token.TokenType;
import parser.Parser;
import parser.Program;

public class Interpretator {
	
	private SourcesManager source;
	private Lexer lex;
	private Parser parser;
	public static Program program;
	private String outputFile;
	private static PrintWriter writer;
	private static final String LINE_SEPARATOR = "\r\n";
	
	public static void main(String[] args) 
	{
		Interpretator interpretator = new Interpretator();
		if(interpretator.parser != null)
			interpretator.interpretate();
		
		writer.close();
	}
	
	private Boolean prepareFiles()
	{
		try {
			@SuppressWarnings("resource")
			Scanner scaner = new Scanner(System.in);
			System.out.println("Podaj scie¿kê do pliku z kodem");
			String sourceFile = scaner.nextLine();
			source.setFile(sourceFile);
			System.out.println("Podaj scie¿kê do pliku wynikowego");
			outputFile = scaner.nextLine();	
			if(sourceFile.equals(outputFile))
				throw new IOException();
			writer = new PrintWriter(outputFile);

			printLine("Sciezka do kodu: " + sourceFile, true);

			return true;
		} catch (FileNotFoundException e) {
			System.out.println("Podany plik nie istnieje!");
			return false;
		} catch (IOException e) {
			System.out.println("Problem z otwarciem pliku!");
			return false;
		}
	}
	
	public Interpretator()
	{
		source = new SourcesManager();
		if(!prepareFiles())
			return;
		lex = new Lexer(source);
		parser = new Parser(lex);
	}
	
	public void prepareIOFunctions() throws MultipleDeclarationOfFunctionError
	{
		Function inNumber = new Function();
		Function inString = new Function();
		Function out = new Function();
		
		inNumber.setId("pobierzLiczbe");
		inNumber.setReturnType(Type.LiczbaZmiennoprzecinkowa);
		inNumber.bodyOfFunction = new CodeBlock();
		inNumber.bodyOfFunction.instructions.add(new InNumberFunction());
		
		inString.setId("pobierzTekst");
		inString.setReturnType(Type.Tekst);
		inString.bodyOfFunction = new CodeBlock();
		inString.bodyOfFunction.instructions.add(new InStringFunction());
		inString.setReturnType(Type.Tekst);

		out.setId("wypisz");
		out.setReturnType(Type.TypPusty);
		out.bodyOfFunction = new CodeBlock();
		out.bodyOfFunction.instructions.add(new OutFunction());
		Variable arg = new Variable();
		arg.setId("arg");
		out.arguments.add(arg);
		out.bodyOfFunction.localVariablesTemplate.add(arg);
		out.setReturnType(Type.TypPusty);

		program.addFunction(inNumber);
		program.addFunction(inString);		
		program.addFunction(out);		
	}
	
	
	public static Variable getVariable(String id)
	{
		Variable result = null;
		if(program.stack.size() > 0)
		{
			Stack<List<Variable>> stack = program.stack.peek();
			result = goDeepInStack(id, stack);
		}
		if(result == null)
			for(Variable var : program.context.localVariablesTemplate)
				if(var.getId().equals(id))
				{
					result = var;
				}
		return result;
	}
	
	private static Variable goDeepInStack(String id, Stack<List<Variable>> stack)
	{
		if(stack.size() == 0)
			return null;
		
		List<Variable> variables = stack.pop();
		
		for(Variable var : variables)
		{
			if(var.getId().equals(id))
			{
				stack.push(variables);
				return var;
			}
		}
		Variable result = goDeepInStack(id, stack);
		stack.push(variables);
		if(result != null)
			return result;
		else
			return null;
	}
	
	public void checkFunctionsBodyies() throws MissingFunctionException
	{
		for(Function function : program.functions)
		{
			if(function.bodyOfFunction == null)
			{
				MissingFunctionException error = new MissingFunctionException();
				error.setInfo("Brak cia³a funkcji " + function.getId() );
				throw error;
			}
		}
	}
	
	private void initializeGlobalVariables() throws NumberFormatException, BadTypeError, BadTableUsingException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException
	{
		for(Instruction instrution : program.context.instructions)
		{
			instrution.execute();
		}
	}
	
	private void executeFunction(Function function) throws BadTypeError, BadTableUsingException, BadArgumentsError, NumberFormatException, ZeroDivideException, NotInicializedVariableException
	{
		Stack<List<Variable>> stack = new Stack<List<Variable>>();
		
		List<Variable> variables = new ArrayList<Variable>();
		
		initializeGlobalVariables();
		
		for(Variable var : function.bodyOfFunction.localVariablesTemplate) {
			variables.add(var.clone());
		}
		
		stack.push(variables);
		program.stack.push(stack);
		
		for(int i = 0; i < function.bodyOfFunction.instructions.size(); ++i)
		{

			function.bodyOfFunction.instructions.get(i).execute();
		}
	}
	
	public void interpretate()
	{
		try {
			program = parser.parseProgram();
			
			prepareIOFunctions();
			checkFunctionsBodyies();
			
			Function main = program.findFunction("funkcjaGlowna");
			
			if(main == null)
			{
				LackOfFunctionDeclarationError error = new LackOfFunctionDeclarationError();
				error.setInfo("Brak funkcji g³ównej!");
				throw error;
			}
			
			executeFunction(main);
						
			
		} catch (MultipleDeclarationOfFunctionError e) {
			printErrorInfo("wielokrotna deklaracja funkcji!", e.getInfo());
			//e.printStackTrace();
		} catch (NotExpectedTokenError e) {
			printErrorInfo("nieoczekiwany token!", e.getInfo());
			//e.printStackTrace();
		} catch (BadTypeError e) {
			printErrorInfo("niezgodnoœæ typów!", e.getInfo());
			//e.printStackTrace();
		} catch (TableDefinitonError e) {
			printErrorInfo("z³a definicja tabeli!", e.getInfo());
			//e.printStackTrace();
		} catch (LackOfVariableDeclarationError e) {
			printErrorInfo("brak deklaracji zmiennej!", e.getInfo());
			//e.printStackTrace();
		} catch (BadReturnValueError e) {
			printErrorInfo("z³a wartosæ zwracana!", e.getInfo());
			//e.printStackTrace();
		} catch (MissingFunctionException e) {
			printErrorInfo("brak cia³a funkcji!", e.getInfo());
			//e.printStackTrace();
		} catch (BadTableUsingException e) {
			printErrorInfo("z³e u¿ycie tabeli!", e.getInfo());
			//e.printStackTrace();
		} catch (LackOfFunctionDeclarationError e) {
			printErrorInfo("brak deklaracji funkcji!", e.getInfo());
			//e.printStackTrace();
		} catch (ZeroDivideException e) {
			printErrorInfo("dzielenie przez zero!", e.getInfo());
			e.printStackTrace();
		} catch (BadArgumentsError e) {
			printErrorInfo("niepasuj¹ce argumenty funkcji!", e.getInfo());
			//e.printStackTrace();
		} catch (NumberFormatException e) {
			printErrorInfo("z³y format liczby!", null);
			//e.printStackTrace();
		} catch (NotInicializedVariableException e) {
			printErrorInfo("u¿ycie niezainicjalizowanej zmiennej!", e.getInfo());
			//e.printStackTrace();
		} catch (MultipleDeclarationOfVariableError e) {
			printErrorInfo("wielokrotna deklaracja zmiennej!", e.getInfo());
			//e.printStackTrace();
		}
		
	}
	
	private void printErrorInfo(String error, String addionalInfo)
	{
		printLine("B³¹d! - " + error, false);
		if(addionalInfo != null)
			printLine(addionalInfo, false);
		if(parser.getActualToken().getToken() != TokenType.EOF)
		{
			printLine("Przeanalizowano do:", false);
			printLine("Nr. linijki - " + source.getNumberLine(), false);
			printLine("Nr. znaku - " + source.getNumberCharInLine(), false);
		}
	}
	
	public static void printLine(String string, Boolean onlyFile)
	{
		if(!onlyFile)
			System.out.println(string);
		writer.write(string + LINE_SEPARATOR);
	}

}
