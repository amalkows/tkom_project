package code;

import java.util.ArrayList;
import java.util.List;

import errors.BadTypeError;

public class Function {

	private String id;
	private Type returnType;
	public List<Variable> arguments;
	public CodeBlock bodyOfFunction;
	
	public Function()
	{
		arguments = new ArrayList<Variable>();
	}
	
	public Type getReturnType() {
		return returnType;
	}

	public void setReturnType(Type type) {
		this.returnType = type;
	}
	
	public void setReturnType(String type) throws BadTypeError {
		this.returnType = SpecyficValue.checkType(type);
	}
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
}
