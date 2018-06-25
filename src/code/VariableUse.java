package code;

import code.arithmetic.ArithmeticExpression;
import errors.BadArgumentsError;
import errors.BadTableUsingException;
import errors.BadTypeError;
import errors.NotInicializedVariableException;
import errors.ZeroDivideException;
import interpretator.Interpretator;

public class VariableUse extends Instruction {

	private String id;
	private ArithmeticExpression index;
	private Boolean table;
	
	public VariableUse()
	{
		table = false;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArithmeticExpression getIndex() {
		return index;
	}

	public void setIndex(ArithmeticExpression index) {
		this.index = index;
	}

	public Boolean getTable() {
		return table;
	}

	public void setTable(Boolean table) {
		this.table = table;
	}

	@Override
	public void execute() {

	}

	@Override
	public SpecyficValue getValue() throws BadTypeError, BadTableUsingException, NumberFormatException, BadArgumentsError, ZeroDivideException, NotInicializedVariableException {
		Variable variable = Interpretator.getVariable(id);

		if(table)
		{
			SpecyficValue i = index.getValue();
			if(variable.getTable() && i.getType() == Type.LiczbaNaturalna)
				return variable.tableValue.get(i.getIntValue());
			else
			{
				BadTableUsingException error = new BadTableUsingException();
				error.setInfo("Zle odowlanie do tabeli " + variable.getId());
				throw error;
			}
		}

		return variable.getValue();
	}

}
