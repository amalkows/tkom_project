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

public class CodeBlock {

	public List<Instruction> instructions;
	public List<Variable> localVariablesTemplate;
	private CodeBlock parent;
	private SpecyficValue returnValue = null;
	
	
	public void execute() throws BadTypeError, BadTableUsingException, BadArgumentsError, NumberFormatException, ZeroDivideException, NotInicializedVariableException
	{
		Stack<List<Variable>> stack = Interpretator.program.stack.pop();
		List<Variable> variables = new ArrayList<Variable>();
		
		
		for(Variable var : localVariablesTemplate) {
			variables.add(var.clone());
		}
		
		stack.push(variables);
		Interpretator.program.stack.push(stack);
		
		for(int i = 0; i < instructions.size(); ++i)
		{
			instructions.get(i).execute();
			SpecyficValue propablyReturnValue = instructions.get(i).getValue();
			if((instructions.get(i) instanceof ReturnInstruction) ||
				((instructions.get(i) instanceof ConditionalInstruction ||
						instructions.get(i) instanceof Loop) &&
						propablyReturnValue != null))
			{
				returnValue = propablyReturnValue;
				break;
			}
		}
		
		stack = Interpretator.program.stack.pop();
		stack.pop();
		Interpretator.program.stack.push(stack);
	}
	
	
	
	public CodeBlock()
	{
		instructions = new ArrayList<Instruction>();
		localVariablesTemplate = new ArrayList<Variable>();
	}
	
	public CodeBlock getParent() {
		return parent;
	}
	public void setParent(CodeBlock parent) {
		this.parent = parent;
	}
	public Variable findVariable(String id)
	{
		Variable result = null;
		for(Variable variable : localVariablesTemplate)
		{
			if(variable.getId().equals(id))
			{
				result = variable;
				break;
			}
		}
		return result;
	}
	public Variable findVariableRecurency(String id)
	{
		Variable result = null;
		for(Variable variable : localVariablesTemplate)
		{
			if(variable.getId().equals(id))
			{
				result = variable;
				break;
			}
		}
		if(result == null && parent != null)
			return parent.findVariableRecurency(id);
		return result;
	}
	public Boolean addVariable(Variable variable)
	{
		if(findVariable(variable.getId()) == null)
		{
			localVariablesTemplate.add(variable);
			return true;
		}
		else
			return false;
	}



	public SpecyficValue getReturnValue() {
		return returnValue;
	}



	public void setReturnValue(SpecyficValue returnValue) {
		this.returnValue = returnValue;
	}
}
