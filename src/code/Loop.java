package code;

import code.logic.LogicExpression;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class Loop extends Instruction {
	
	private CodeBlock bodyOfLoop;
	private SpecyficValue returnValue;

	public CodeBlock getBodyOfLoop() {
		return bodyOfLoop;
	}

	public void setBodyOfLoop(CodeBlock bodyOfLoop) {
		this.bodyOfLoop = bodyOfLoop;
	}

	public LogicExpression getConditional() {
		return conditional;
	}

	public void setConditional(LogicExpression conditional) {
		this.conditional = conditional;
	}

	private Boolean conditionalValue;

	private LogicExpression conditional;
	@Override
	public void execute() throws BadTypeError, BadTableUsingException, BadArgumentsError, NumberFormatException, ZeroDivideException, NotInicializedVariableException {
		conditionalValue = conditional.getValue().getBooleanValue();
		while(conditionalValue)
		{
			bodyOfLoop.execute();
			returnValue = bodyOfLoop.getReturnValue();
			if(returnValue != null)
				break;
			
			conditionalValue = conditional.getValue().getBooleanValue();
		}
		
	}

	@Override
	public SpecyficValue getValue() {
		return returnValue;
	}

	public Boolean getConditionalValue() {
		return conditionalValue;
	}

	public void setConditionalValue(Boolean conditionalValue) {
		this.conditionalValue = conditionalValue;
	}

}
