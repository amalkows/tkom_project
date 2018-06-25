package code.logic;

import code.Instruction;
import code.SpecyficValue;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class LogicExpression extends Instruction  {

	protected LogicExpression left;
	protected LogicExpression right;
	protected Boolean unary = true;
	protected SpecyficValue value;	
	protected Instruction instructionValue;

	
	public LogicExpression getLeft() {
		return left;
	}

	public void setLeft(LogicExpression left) {
		this.left = left;
	}

	public LogicExpression getRight() {
		return right;
	}

	public void setRight(LogicExpression right) {
		this.right = right;
		if(right == null)
			unary = true;
		else
			unary = false;
	}

	public Boolean getUnary() {
		return unary;
	}

	public void setUnary(Boolean unary) {
		this.unary = unary;
	}

	public Instruction getInstructionValue() {
		return instructionValue;
	}

	public void setInstructionValue(Instruction instructionValue) {
		this.instructionValue = instructionValue;
	}

	public void setValue(SpecyficValue value) {
		this.value = value;
	}

	
	@Override
	public void execute() throws BadTypeError {
		
	}

	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException {
		if(value == null)
			throw new NotInicializedVariableException();
			
		return value;
	}

}
