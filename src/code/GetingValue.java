package code;

import code.arithmetic.ArithmeticExpression;
import code.logic.LogicExpression;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class GetingValue extends Instruction {

	private ArithmeticExpression arithmetic;
	private LogicExpression logic;
	private String string;
	
	public void setArithmetic(ArithmeticExpression arithmetic) {
		this.arithmetic = arithmetic;
	}

	public void setLogic(LogicExpression logic) {
		this.logic = logic;
	}

	public void setString(String string) {
		this.string = string;
	}

	public ArithmeticExpression getArithmetic() {
		return arithmetic;
	}

	public LogicExpression getLogic() {
		return logic;
	}

	public String getString() {
		return string;
	}

	public boolean isFitType(Type type)
	{
		if(type == Type.Tekst && string != null)
			return true;
		else if(type == Type.ZmiennaLogiczna && logic != null)
			return true;
		else if((type == Type.LiczbaNaturalna || type == Type.LiczbaZmiennoprzecinkowa || type == Type.UlamekZwykly) && arithmetic != null)
			return true;
		else
			return false;
	}
	
	@Override
	public void execute() {
	}

	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException {
		if(logic != null)
		{
			return logic.getValue();
		}
		else if(arithmetic != null)
		{
			return arithmetic.getValue();	
		}
		else if(string != null)
		{
			SpecyficValue result = new SpecyficValue();
			result.setType(Type.Tekst);
			result.setStringValue(string);
			return result;
		}	
		else
			return null;
	}

}
