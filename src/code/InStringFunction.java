package code;

import java.util.Scanner;

import interpretator.Interpretator;

public class InStringFunction extends Instruction {

	private SpecyficValue inValue = new SpecyficValue();
	
	@Override
	public void execute() {
		String input;
		@SuppressWarnings("resource")
		Scanner scaner = new Scanner(System.in);
		input = scaner.nextLine();
		inValue.setType(Type.Tekst);
		inValue.setStringValue(input);
		
		Interpretator.printLine(input, true);

	}

	@Override
	public SpecyficValue getValue() {
		return inValue;
	}

}
