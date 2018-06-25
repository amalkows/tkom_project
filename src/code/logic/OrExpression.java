package code.logic;

import code.SpecyficValue;
import code.Type;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class OrExpression extends LogicExpression {
	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException{

		if(left == null && right == null)
			return value;
		
		SpecyficValue leftValue = left.getValue();
		
		if(unary)
		{
			value = leftValue;
		}
		else
		{
			SpecyficValue rigthValue = right.getValue();
						
			value = new SpecyficValue();
			value.setType(Type.ZmiennaLogiczna);
			value.setBooleanValue(leftValue.getBooleanValue() || rigthValue.getBooleanValue());
		}	
		return value;
	}
	
}
