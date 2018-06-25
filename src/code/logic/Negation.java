package code.logic;
import code.SpecyficValue;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class Negation extends LogicExpression {
	
	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException 
	{
		if(left!= null)
			left.execute();
		
		value = new SpecyficValue();
		value.setBooleanValue(!left.getValue().getBooleanValue());
		return value;
	}
}
