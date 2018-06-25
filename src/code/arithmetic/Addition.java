package code.arithmetic;

import code.SpecyficValue;
import code.Type;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class Addition extends ArithmeticExpression {

	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException {

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
						
			if(sign == Sign.Add)
				value = addSpecyficsValues(leftValue, rigthValue, false);
			else if (sign == Sign.Subtract)
				value = addSpecyficsValues(leftValue, rigthValue, true);
		}	
		return value;
	}
	
	private SpecyficValue addSpecyficsValues(SpecyficValue first, SpecyficValue second, boolean minus) throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException
	{
		SpecyficValue result = new SpecyficValue();
		
		if(first.getType() == Type.LiczbaZmiennoprzecinkowa || second.getType() == Type.LiczbaZmiennoprzecinkowa)
		{
			first = first.typeConversion(Type.LiczbaZmiennoprzecinkowa);
			second = second.typeConversion(Type.LiczbaZmiennoprzecinkowa);	
			result.setType(Type.LiczbaZmiennoprzecinkowa);

			if(!minus)
				result.setDoubleValue(first.getDoubleValue() + second.getDoubleValue());
			else
				result.setDoubleValue(first.getDoubleValue() - second.getDoubleValue());
		}
		else if(first.getType() == Type.UlamekZwykly || second.getType() == Type.UlamekZwykly)
		{
			first = first.typeConversion(Type.UlamekZwykly);
			second = second.typeConversion(Type.UlamekZwykly);
			result = addFractions(first, second, minus);	
		}
		else if(first.getType() == Type.LiczbaNaturalna && second.getType() == Type.LiczbaNaturalna)
		{
			result.setType(Type.LiczbaNaturalna);
			if(!minus)
				result.setIntValue(first.getIntValue() + second.getIntValue());
			else
				result.setIntValue(first.getIntValue() - second.getIntValue());
		}
		else
		{
			BadTypeError error = new BadTypeError();
			error.setInfo("Dodawanie " + first.getType() + " i " + second.getType());
			throw error;
		}
		
		return result;
	}
	
	private SpecyficValue addFractions(SpecyficValue first, SpecyficValue second, boolean minus) throws ZeroDivideException
	{
		SpecyficValue result = new SpecyficValue();
		result.setType(Type.UlamekZwykly);
		
		result.setDenumerator(first.getDenumerator() * second.getDenumerator());
		if(!minus)
			result.setNumerator(first.getNumerator() * second.getDenumerator() +
								second.getNumerator() * first.getDenumerator());
		else
			result.setNumerator(first.getNumerator() * second.getDenumerator() -
					second.getNumerator() * first.getDenumerator());
		
		return result.simplifyFraction();
	}
	
}
