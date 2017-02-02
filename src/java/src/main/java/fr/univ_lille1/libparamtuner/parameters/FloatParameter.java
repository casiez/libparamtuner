package fr.univ_lille1.libparamtuner.parameters;

import org.w3c.dom.Element;

public class FloatParameter extends Parameter {
	
	public FloatParameter(String n, double v, double m, double M) {
		super(n, m, M);
		setValue(v);
	}
	
	public FloatParameter(String n, double v) {
		this(n, v, 0, 0);
	}
	
	/* package */ FloatParameter(Element el) {
		super(el);
	}
	
	public void setValue(double v) {
		value = Double.toString(v);
	}
	
	public double getValue() {
		return Double.parseDouble(value);
	}
	
}
