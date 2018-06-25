package code.logic;

import code.SpecyficValue;
import code.Type;
import code.arithmetic.ArithmeticExpression;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class Comparasion extends LogicExpression {
	protected ArithmeticExpression leftSite;
	protected ArithmeticExpression rightSite;

	public ArithmeticExpression getLeftSite() {
		return leftSite;
	}
	public void setLeftSite(ArithmeticExpression leftSite) {
		this.leftSite = leftSite;
	}
	public ArithmeticExpression getRightSite() {
		return rightSite;
	}
	public void setRightSite(ArithmeticExpression rightSite) {
		this.rightSite = rightSite;
	}
	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException {
		if(leftSite != null)
			value = leftSite.getValue();
		if(value.getType() != Type.ZmiennaLogiczna)
		{
			BadTypeError error = new BadTypeError();
			error.setInfo("Wykorzystanie " + value.getType() + " w wyra¿eniu logicznym.");
			throw error;
		}
		
		return value;
	}
}
