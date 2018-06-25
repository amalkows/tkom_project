package code;

import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;

public abstract class Instruction {

	public abstract void execute() throws BadTypeError, BadTableUsingException, BadArgumentsError, NumberFormatException, ZeroDivideException, NotInicializedVariableException;
	
	public abstract SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException;
	
	public SpecyficValue value;
}
