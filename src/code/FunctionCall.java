package code;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;
import interpretator.Interpretator;

public class FunctionCall extends Instruction {

	public List<Instruction> arguments;
	private Function function;
	private SpecyficValue returnValue;
	
	public FunctionCall()
	{
		arguments = new ArrayList<Instruction>();
	}
	
	@Override
	public void execute() throws BadTypeError, BadTableUsingException, BadArgumentsError, NumberFormatException, ZeroDivideException, NotInicializedVariableException {
		if(arguments.size() != function.arguments.size())
		{
			BadArgumentsError error = new BadArgumentsError();
			error.setInfo("B³êdne wywo³anie funkcji: " + function.getId() + ". U¿yto " + arguments.size() + " argumentów " + " a funkcja zdefiniowana jest z " + function.arguments.size() + " argumentami");
			throw error;
		}
		
		Stack<List<Variable>> stack = new Stack<List<Variable>>();
		List<Variable> variables = new ArrayList<Variable>();
		
		int i;
		for(i = 0; i < arguments.size(); ++i)
		{
			Variable newVariable = function.bodyOfFunction.localVariablesTemplate.get(i).clone();
			Instruction argumentValue = arguments.get(i);
			if(argumentValue instanceof FunctionCall)
				argumentValue.execute();
			newVariable.setValue(argumentValue.getValue().typeConversion(newVariable.getType()));
			variables.add(newVariable);
		}
		
		for(; i < function.bodyOfFunction.localVariablesTemplate.size(); ++i) {
			variables.add(function.bodyOfFunction.localVariablesTemplate.get(i).clone());
		}
		
		stack.push(variables);
		Interpretator.program.stack.push(stack);

		
		for(int j = 0; j < function.bodyOfFunction.instructions.size(); ++j)
		{
			function.bodyOfFunction.instructions.get(j).execute();
			SpecyficValue propablyReturnValue = function.bodyOfFunction.instructions.get(j).getValue();
			if((function.bodyOfFunction.instructions.get(j) instanceof ReturnInstruction ||
					function.bodyOfFunction.instructions.get(j) instanceof InStringFunction||
					function.bodyOfFunction.instructions.get(j) instanceof InNumberFunction) ||
					((function.bodyOfFunction.instructions.get(j) instanceof ConditionalInstruction ||
							function.bodyOfFunction.instructions.get(j) instanceof Loop) &&
							propablyReturnValue != null))
			{
				returnValue = propablyReturnValue;
				break;
			}
		}
		
		Interpretator.program.stack.pop();
	}

	@Override
	public SpecyficValue getValue() throws NumberFormatException, BadTypeError, BadTableUsingException, BadArgumentsError, ZeroDivideException {
		//execute();
		if(returnValue != null)
			return returnValue.typeConversion(function.getReturnType());
		else
			return null;
	}

	public Function getFunction() {
		return function;
	}

	public void setFunction(Function function) {
		this.function = function;
	}

}
