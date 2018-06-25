package code;

import errors.BadTypeError;
import errors.ZeroDivideException;

public class SpecyficValue {

	private Integer intValue;
	private Double doubleValue;
	private Boolean booleanValue;
	private String stringValue;
	private Integer numerator;
	private Integer denumerator;
	
	private Type type;
	
	public SpecyficValue typeConversion(Type type) throws BadTypeError, ZeroDivideException
	{
		if(type == null && getIntValue() != null)
		{
			setType(Type.LiczbaNaturalna);
			return this;
		}
		else if(type == null && getDoubleValue() != null)
		{
			setType(Type.LiczbaZmiennoprzecinkowa);
			return this;
		}
		else if(type == null && getNumerator() != null && getDenumerator() != null)
		{
			setType(Type.UlamekZwykly);
			return this;
		}
		else if(type == null && getStringValue() != null)
		{
			setType(Type.Tekst);
			return this;
		}	
		else if(type == null && getBooleanValue() != null)
		{
			setType(Type.ZmiennaLogiczna);
			return this;
		}	
		else if(type == null)
		{
			setType(Type.TypPusty);
			return this;	
		}
		
		switch(type)
		{
			case LiczbaNaturalna:
				return toInt();
			case UlamekZwykly:
				return toFraction();
			case LiczbaZmiennoprzecinkowa:
				return toDouble();
			default:
				if(getType() != type)
				{
					BadTypeError error = new BadTypeError();
					error.setInfo("Nie mo¿na rzutowaæ " + getType() + " na " + type);
					throw new BadTypeError();
				}
				else
					return this;
		}
	}
	
	private SpecyficValue toDouble() throws BadTypeError, ZeroDivideException
	{
		SpecyficValue result = new SpecyficValue();
		result.setType(Type.LiczbaZmiennoprzecinkowa);
		switch(getType())
		{
			case LiczbaNaturalna:
				result.setDoubleValue((double)(int) getIntValue());
				break;
			case UlamekZwykly:
				double first = getNumerator();
				double second = getDenumerator();
				result.setDoubleValue(first/second);
				break;
			case LiczbaZmiennoprzecinkowa:
				return this;
			default:
				BadTypeError error = new BadTypeError();
				error.setInfo("Nie mo¿na rzutowaæ " + getType() + " na LiczbaZmiennoprzecinkowa");
				throw new BadTypeError();
		}
		return result;
	}
	
	private SpecyficValue toFraction() throws BadTypeError, ZeroDivideException
	{
		SpecyficValue result = new SpecyficValue();
		result.setType(Type.UlamekZwykly);
		switch(getType())
		{
			case LiczbaNaturalna:
				result.setNumerator(getIntValue());
				result.setDenumerator(1);
				break;
			case UlamekZwykly:
				return this;
			case LiczbaZmiennoprzecinkowa:
				result.setNumerator((int)(double) getDoubleValue());
				result.setDenumerator(1);
				break;
			default:
				BadTypeError error = new BadTypeError();
				error.setInfo("Nie mo¿na rzutowaæ " + getType() + " na UlamekZwykly");
				throw new BadTypeError();
		}
		return result;
	}
	
	private SpecyficValue toInt() throws BadTypeError
	{
		SpecyficValue result = new SpecyficValue();
		result.setType(Type.LiczbaNaturalna);
		switch(getType())
		{
			case LiczbaNaturalna:
				return this;
			case UlamekZwykly:
				result.setIntValue(getNumerator()/getDenumerator());
				break;
			case LiczbaZmiennoprzecinkowa:
				result.setIntValue((int)(double) doubleValue);
				break;
			default:
				throw new BadTypeError();
		}
		return result;
	}
	
	public boolean equals(SpecyficValue obj) throws BadTypeError, ZeroDivideException {
		
		if(getType() == Type.LiczbaZmiennoprzecinkowa || obj.getType() == Type.LiczbaZmiennoprzecinkowa)
		{
			double first = obj.typeConversion(Type.LiczbaZmiennoprzecinkowa).getDoubleValue();
			double second = typeConversion(Type.LiczbaZmiennoprzecinkowa).getDoubleValue();
			return first == second;
				
		}
		else if(getType() == Type.UlamekZwykly || obj.getType() == Type.UlamekZwykly)
			return
					obj.typeConversion(Type.UlamekZwykly).getNumerator() * typeConversion(Type.UlamekZwykly).getDenumerator() == 
					obj.typeConversion(Type.UlamekZwykly).getDenumerator() * typeConversion(Type.UlamekZwykly).getNumerator();
		else if(getType() == Type.LiczbaNaturalna && obj.getType() == Type.LiczbaNaturalna)
			return obj.typeConversion(Type.LiczbaNaturalna).getIntValue() == 
				typeConversion(Type.LiczbaNaturalna).getIntValue();
		
		switch(type)
		{
			case ZmiennaLogiczna:
				return obj.type == Type.ZmiennaLogiczna && obj.booleanValue == booleanValue;
			case TypPusty:
				return obj.type == Type.TypPusty;
			case Tekst:
				return obj.type == Type.Tekst && obj.stringValue == stringValue;
			default:
				return false;
		}
	}
	
	public boolean less(SpecyficValue obj) throws BadTypeError, ZeroDivideException {		
		if(getType() == Type.LiczbaZmiennoprzecinkowa || obj.getType() == Type.LiczbaZmiennoprzecinkowa)
		{
			double first = obj.typeConversion(Type.LiczbaZmiennoprzecinkowa).getDoubleValue();
			double second = typeConversion(Type.LiczbaZmiennoprzecinkowa).getDoubleValue();
			return first > second;
				
		}
		else if(getType() == Type.UlamekZwykly || obj.getType() == Type.UlamekZwykly)
			return
					obj.typeConversion(Type.UlamekZwykly).getNumerator() * typeConversion(Type.UlamekZwykly).getDenumerator() > 
					obj.typeConversion(Type.UlamekZwykly).getDenumerator() * typeConversion(Type.UlamekZwykly).getNumerator();
		else if(getType() == Type.LiczbaNaturalna && obj.getType() == Type.LiczbaNaturalna)
			return obj.typeConversion(Type.LiczbaNaturalna).getIntValue() >
				typeConversion(Type.LiczbaNaturalna).getIntValue();
		
		return false;
	}
	
	public SpecyficValue simplifyFraction() throws ZeroDivideException
	{
		SpecyficValue result = new SpecyficValue();
		
		int u = getNumerator();
		int v = getDenumerator();
		int temp;

		while (v != 0) {
		    temp = u % v;
		    u = v;
		    v = temp;
		}
		
		result.setType(Type.UlamekZwykly);
		result.setNumerator(getNumerator()/u);
		result.setDenumerator((getDenumerator()/u));

		return result;
	}
	
	public Integer getIntValue() {
		return intValue;
	}


	public void setIntValue(Integer intValue) {
		this.intValue = intValue;
	}


	public Double getDoubleValue() {
		return doubleValue;
	}


	public void setDoubleValue(Double doubleValue) {
		this.doubleValue = doubleValue;
	}


	public Boolean getBooleanValue() {
		return booleanValue;
	}


	public void setBooleanValue(Boolean booleanValue) {
		this.booleanValue = booleanValue;
	}


	public String getStringValue() {
		return stringValue;
	}


	public void setStringValue(String stringValue) {
		this.stringValue = stringValue;
	}


	public Integer getNumerator() {
		return numerator;
	}


	public void setNumerator(Integer numerator) {
		this.numerator = numerator;
	}


	public Integer getDenumerator() {
		return denumerator;
	}


	public void setDenumerator(Integer denumerator) throws ZeroDivideException {
		if(denumerator == 0)
		{
			ZeroDivideException error = new ZeroDivideException();
			error.setInfo("U³amek nie mo¿e mieæ zera w mianowniku!");
			throw error;
		}
		
		this.denumerator = denumerator;
	}


	public Type getType() {
		return type;
	}


	public void setType(Type type) {
		this.type = type;
	}


	static public Type checkType(String type) throws BadTypeError
	{
		switch(type)
		{
			case "LiczbaNaturalna":
				return Type.LiczbaNaturalna;
			case "UlamekZwykly":
				return Type.UlamekZwykly;
			case "ZmiennaLogiczna":
				return Type.ZmiennaLogiczna;
			case "TypPusty":
				return Type.TypPusty;
			case "Tekst":
				return Type.Tekst;
			case "LiczbaZmiennoprzecinkowa":
				return Type.LiczbaZmiennoprzecinkowa;
			default:
				throw new BadTypeError();
		}
	}
}
