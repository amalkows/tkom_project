package code.arithmetic;

import code.SpecyficValue;
import code.Type;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public class Multiplication extends ArithmeticExpression {

	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException 
	{
		
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
						
			if(sign == Sign.Multiply)
				value = multiplySpecyficsValues(leftValue, rigthValue, false);
			else
				value = multiplySpecyficsValues(leftValue, rigthValue, true);
		}
		return value;
	}
		
	private SpecyficValue multiplySpecyficsValues(SpecyficValue first, SpecyficValue second, boolean divide) throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException
	{
		SpecyficValue result = new SpecyficValue();
		
		if(first.getType() == Type.LiczbaZmiennoprzecinkowa || second.getType() == Type.LiczbaZmiennoprzecinkowa)
		{
			first = first.typeConversion(Type.LiczbaZmiennoprzecinkowa);
			second = second.typeConversion(Type.LiczbaZmiennoprzecinkowa);	
			result.setType(Type.LiczbaZmiennoprzecinkowa);

			if(!divide)
				result.setDoubleValue(first.getDoubleValue() * second.getDoubleValue());
			else
			{
				if(second.getDoubleValue() == 0.0)
					throw new ZeroDivideException();
				result.setDoubleValue(first.getDoubleValue() / second.getDoubleValue());
			}
		}
		else if(first.getType() == Type.UlamekZwykly || second.getType() == Type.UlamekZwykly)
		{
			first = first.typeConversion(Type.UlamekZwykly);
			second = second.typeConversion(Type.UlamekZwykly);
			result = multiplyFractions(first, second, divide);	
		}
		else if(first.getType() == Type.LiczbaNaturalna && second.getType() == Type.LiczbaNaturalna)
		{
			result.setType(Type.LiczbaNaturalna);
			if(!divide)
				result.setIntValue(first.getIntValue() * second.getIntValue());
			else
			{
				if(second.getIntValue() == 0.0)
					throw new ZeroDivideException();
				result.setIntValue(first.getIntValue() / second.getIntValue());
			}
		}
		else
		{
			BadTypeError error = new BadTypeError();
			error.setInfo("Dodawanie " + first.getType() + " i " + second.getType());
			throw error;
		}
		
		return result;
	}
	
	private SpecyficValue multiplyFractions(SpecyficValue first, SpecyficValue second, boolean divide) throws ZeroDivideException
	{
		SpecyficValue result = new SpecyficValue();
		result.setType(Type.UlamekZwykly);
		if(!divide)
		{
			result.setNumerator(first.getNumerator() * second.getNumerator());
			result.setDenumerator(first.getDenumerator() * second.getDenumerator());
		}
		else
		{
			result.setNumerator(first.getNumerator() * second.getDenumerator());
			result.setDenumerator(first.getDenumerator() * second.getNumerator());
		}
		
		return result.simplifyFraction();
	}
	
}
