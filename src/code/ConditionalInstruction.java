package code;

import code.logic.LogicExpression;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class ConditionalInstruction extends Instruction {

	private CodeBlock codeBlockWhenTrue;
	private CodeBlock codeBlockWhenFalse;
	private Boolean conditionalValue;
	private LogicExpression conditional;
	private SpecyficValue returnValue;
	
	public CodeBlock getCodeBlockWhenTrue() {
		return codeBlockWhenTrue;
	}

	public void setCodeBlockWhenTrue(CodeBlock codeBlockWhenTrue) {
		this.codeBlockWhenTrue = codeBlockWhenTrue;
	}

	public CodeBlock getCodeBlockWhenFalse() {
		return codeBlockWhenFalse;
	}

	public void setCodeBlockWhenFalse(CodeBlock codeBlockWhenFalse) {
		this.codeBlockWhenFalse = codeBlockWhenFalse;
	}
	
	public Boolean getConditionalValue() {
		return conditionalValue;
	}

	public void setConditionalValue(Boolean conditionalValue) {
		this.conditionalValue = conditionalValue;
	}

	public LogicExpression getConditional() {
		return conditional;
	}

	public void setConditional(LogicExpression conditional) {
		this.conditional = conditional;
	}

	@Override
	public void execute() throws BadTypeError, BadTableUsingException, BadArgumentsError, NumberFormatException, ZeroDivideException, NotInicializedVariableException {
		conditionalValue = conditional.getValue().getBooleanValue();
		
		if(conditionalValue)
		{
			codeBlockWhenTrue.execute();
			returnValue = codeBlockWhenTrue.getReturnValue();
		}
		else if(codeBlockWhenFalse != null)
		{
			codeBlockWhenFalse.execute();
			returnValue = codeBlockWhenFalse.getReturnValue();
		}
	}

	@Override
	public SpecyficValue getValue() {
		return returnValue;
	}
	
	
}
