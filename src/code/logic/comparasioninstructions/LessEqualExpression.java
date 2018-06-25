package code.logic.comparasioninstructions;

import code.SpecyficValue;
import code.Type;
import code.logic.Comparasion;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class LessEqualExpression extends Comparasion{
	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException{
		SpecyficValue result = new SpecyficValue();
		result.setType(Type.ZmiennaLogiczna);
		result.setBooleanValue(leftSite.getValue().equals(rightSite.getValue()) || leftSite.getValue().less(rightSite.getValue()));
		return result;
	}
}
