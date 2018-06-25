package code;

import java.util.Vector;

import code.arithmetic.ArithmeticExpression;
import errors.BadTypeError;

public class Variable {
	
	private Type type;
	private String id;
	private Boolean table;
	private ArithmeticExpression tableSize;
	private SpecyficValue value;
	public Vector<SpecyficValue> tableValue;
	

	public Variable clone(){
		Variable result = new Variable();
		result.type = type;
		result.id = id;
		result.table = table;
		result.tableSize = tableSize;
		return result;
	}
	
	public SpecyficValue getValue() {
		return value;
	}

	public void setValue(SpecyficValue value) {
		this.value = value;
	}

	public void setType(Type type) {
		this.type = type;
	}
	
	public Variable()
	{
		table = false;
	}
	
	public Type getType() {
		return type;
	}
	public void setType(String type) throws BadTypeError {
		this.type = SpecyficValue.checkType(type);
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getTable() {
		return table;
	}
	public void setTable(Boolean table) {
 		this.table = table;
	}
	public ArithmeticExpression getTableSize() {
		return tableSize;
	}
	public void setTableSize(ArithmeticExpression tableSize) {
		this.tableSize = tableSize;
	}
}
