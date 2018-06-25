package code;

import java.util.Scanner;

import errors.ZeroDivideException;
import interpretator.Interpretator;

public class InNumberFunction extends Instruction {
	
	private SpecyficValue inValue;

	@SuppressWarnings("resource")
	@Override
	public void execute() throws NumberFormatException, ZeroDivideException{
		inValue = new SpecyficValue();
		
		String input = null;
		Scanner scaner = new Scanner(System.in);
		input = scaner.nextLine();
		int intInput;
		double doubleInput;
		int numerator, denumerator;

		Interpretator.printLine(input, true);
		
		try
		{
			intInput = Integer.parseInt(input);
			inValue.setIntValue(intInput);
			inValue.setType(Type.LiczbaNaturalna);
			return;
		}
		catch(NumberFormatException e){}
		
		try
		{
			doubleInput = Double.parseDouble(input);
			inValue.setDoubleValue(doubleInput);
			inValue.setType(Type.LiczbaZmiennoprzecinkowa);
			return;
		}
		catch(NumberFormatException e){}
		
		String[] inputStrings = input.split("/");
		numerator = Integer.parseInt(inputStrings[0]);
		denumerator = Integer.parseInt(inputStrings[1]);
		inValue.setNumerator(numerator);
		inValue.setDenumerator(denumerator);
		inValue.setType(Type.UlamekZwykly);

	}

	@Override
	public SpecyficValue getValue() {
		return inValue;
	}

}
