package parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import code.CodeBlock;
import code.Function;
import code.Variable;
import errors.MultipleDeclarationOfFunctionError;
import errors.MultipleDeclarationOfVariableError;

public class Program {
	public List<Function> functions;
	public Stack<Stack<List<Variable>>> stack;
	public CodeBlock context;
	
	public Program()
	{
		functions = new ArrayList<Function>();
		stack = new Stack<Stack<List<Variable>>>();
		context = new CodeBlock();
	}
	
	public Function findFunction(String id)
	{
		Function result = null;
		for(Function function : functions)
		{
			if(function.getId().equals(id))
			{
				result = function;
				break;
			}
		}
		return result;
	}
	public Variable findGlobalVariable(String id)
	{
		Variable result = null;
		for(Variable variable : context.localVariablesTemplate)
		{
			if(variable.getId().equals(id))
			{
				result = variable;
				break;
			}
		}
		return result;
	}
	public void addFunction(Function function) throws MultipleDeclarationOfFunctionError
	{
		Function exist = findFunction(function.getId());
		if(exist == null )
			functions.add(function);
		else if(exist.bodyOfFunction  == null)
		{
			exist.bodyOfFunction = function.bodyOfFunction;
			exist.setReturnType(function.getReturnType());
			exist.arguments = function.arguments;
		}
		else
		{
			MultipleDeclarationOfFunctionError error = new MultipleDeclarationOfFunctionError();
			error.setInfo("Funkcja " + function.getId());
			throw error;
		}
	}
	public void addVariable(Variable variable) throws MultipleDeclarationOfVariableError
	{
		if(findGlobalVariable(variable.getId()) == null)
			context.localVariablesTemplate.add(variable);
		else
		{
			MultipleDeclarationOfVariableError error = new MultipleDeclarationOfVariableError();
			error.setInfo("Zmienna " + variable.getId());
			throw error;
		}
	}
}
