package code;

import interpretator.Interpretator;

public class OutFunction extends Instruction {

	private SpecyficValue outValue;

	@Override
	public void execute() {
		
		outValue = Interpretator.getVariable("arg").getValue();
		
		switch(outValue.getType())
		{
			case LiczbaNaturalna:
				Interpretator.printLine(outValue.getIntValue().toString(), false);
				break;
			case UlamekZwykly:
				Interpretator.printLine(outValue.getNumerator() + "/" + outValue.getDenumerator(), false);
				break;
			case ZmiennaLogiczna:
				if(outValue.getBooleanValue())
					Interpretator.printLine("Prawda", false);
				else
					Interpretator.printLine("Fa³sz", false);
				break;
			case Tekst:
				Interpretator.printLine(outValue.getStringValue(), false);
				break;
			case LiczbaZmiennoprzecinkowa:
				Interpretator.printLine(outValue.getDoubleValue().toString(), false);
				break;
			default:
				break;
		}
	}

	@Override
	public SpecyficValue getValue() {
		return null;
	}

	public SpecyficValue getOutValue() {
		return outValue;
	}

	public void setOutValue(SpecyficValue outValue) {
		this.outValue = outValue;
	}

}
