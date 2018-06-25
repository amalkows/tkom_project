package code.arithmetic;


import code.FunctionCall;
import code.Instruction;
import code.SpecyficValue;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class ArithmeticExpression extends Instruction {

	public enum Sign { Add, Subtract, Multiply, Divide};
	
	protected ArithmeticExpression left;
	protected ArithmeticExpression right;
	protected Boolean unary = true;
	protected SpecyficValue value;	
	protected Instruction instructionValue;

	protected Sign sign;
	
	public Sign getSign() {
		return sign;
	}

	public void setSign(Sign sign) {
		this.sign = sign;
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
		if(instructionValue != null)
		{
			if(instructionValue instanceof FunctionCall)
				instructionValue.execute();
			value = instructionValue.getValue();
		}
		if(value == null)
		{
			NotInicializedVariableException error = new NotInicializedVariableException();
			throw error;
		}
		return value;
	}

	public ArithmeticExpression getLeft() {
		return left;
	}

	public void setLeft(ArithmeticExpression left) {
		this.left = left;
	}

	public ArithmeticExpression getRight() {
		return right;
	}

	public void setRight(ArithmeticExpression right) {
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

}
