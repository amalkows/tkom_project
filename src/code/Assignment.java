package code;


import java.util.Vector;

import code.arithmetic.ArithmeticExpression;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;
import interpretator.Interpretator;

public class Assignment extends Instruction {

	private Variable variable;
	private GetingValue value;
	private boolean table;
	private ArithmeticExpression index;
	
	public void setVariable(Variable variable) {
		this.variable = variable;
	}
	
	public boolean isTable() {
		return table;
	}

	public void setTable(boolean table) {
		this.table = table;
	}

	public ArithmeticExpression getIndex() {
		return index;
	}

	public void setIndex(ArithmeticExpression index) {
		this.index = index;
	}

	public void setValue(GetingValue value) {
		this.value = value;
	}
	public GetingValue getValueAssignment()
	{
		return value;
	}
	
	public Variable getVariable() {
		return variable;
	}

	@Override
	public void execute() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException {
		variable = Interpretator.getVariable(variable.getId());
		
		if(table)
		{
			if(variable.tableValue == null)
			{
				int size = variable.getTableSize().getValue().typeConversion(Type.LiczbaNaturalna).getIntValue();
				variable.tableValue = new Vector<SpecyficValue>(size);
				variable.tableValue.setSize(size);
			}
			
			variable.tableValue.setElementAt(
					value.getValue().typeConversion(variable.getType()),
					index.getValue().typeConversion(Type.LiczbaNaturalna).getIntValue());

		}
		else
		{
			variable.setValue(value.getValue().typeConversion(variable.getType()));
		}
	}

	@Override
	public SpecyficValue getValue() {
		return null;
	}
}
