package code;

import code.arithmetic.ArithmeticExpression;
import code.logic.LogicExpression;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class ReturnInstruction extends Instruction {

	private LogicExpression logicValue;
	private ArithmeticExpression arithmeticValue;
	private String stringValue;
	
	public LogicExpression getLogicValue() {
		return logicValue;
	}

	public void setLogicValue(LogicExpression logicValue) {
		this.logicValue = logicValue;
	}

	public ArithmeticExpression getArithmeticValue() {
		return arithmeticValue;
	}

	public void setArithmeticValue(ArithmeticExpression arithmeticValue) {
		this.arithmeticValue = arithmeticValue;
	}

	public String getStringValue() {
		return stringValue;
	}

	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}

	@Override
	public void execute() {

	}

	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException {
		if(logicValue != null)
			return logicValue.getValue();
		else if(arithmeticValue != null)
			return arithmeticValue.getValue();	
		else if(stringValue != null)
		{
			SpecyficValue result = new SpecyficValue();
			result.setType(Type.Tekst);
			result.setStringValue(stringValue);
			return result;
		}	
		else
			return null;
	}

}
