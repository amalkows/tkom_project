package code.arithmetic;

import code.SpecyficValue;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class Negation extends ArithmeticExpression {
	
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException 
	{		
		value = negateValue(left.getValue());
		return value;

	}
	private SpecyficValue negateValue(SpecyficValue value) throws BadTypeError
	{
		switch(value.getType())
		{
			case LiczbaNaturalna:
				value.setIntValue(value.getIntValue() * -1);
				break;
			case UlamekZwykly:
				value.setNumerator((value.getNumerator() * -1));
				break;
			case LiczbaZmiennoprzecinkowa:
				value.setDoubleValue((value.getDoubleValue() * -1));;
				break;
			default:
				BadTypeError error = new BadTypeError();
				error.setInfo("Negacja " + value.getType());
				throw error;
		}
		
		return value;
	}
	
}
