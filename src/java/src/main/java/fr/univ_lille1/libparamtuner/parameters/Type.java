package fr.univ_lille1.libparamtuner.parameters;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum Type {
	
	INTEGER(IntegerParameter.class, "int", "integer", "long"),
	FLOAT(FloatParameter.class, "double", "float"),
	BOOLEAN(BooleanParameter.class, "bool", "boolean"),
	STRING(StringParameter.class, "string");
	

	/*
	 * Static values
	 */
	private static Map<Class<?>, Type> javaTypeToType = new HashMap<>();
	private static Map<Class<?>, Function<? extends Parameter, ?>> javaTypeToGetter = new HashMap<>();
	
	static {
		javaTypeToType.put(Integer.class, INTEGER);
		javaTypeToGetter.put(Integer.class, (IntegerParameter p) -> (int)p.getValue());
		
		javaTypeToType.put(Long.class, INTEGER);
		javaTypeToGetter.put(Long.class, (IntegerParameter p) -> p.getValue());
		
		javaTypeToType.put(Float.class, FLOAT);
		javaTypeToGetter.put(Float.class, (FloatParameter p) -> (float)p.getValue());
		
		javaTypeToType.put(Double.class, FLOAT);
		javaTypeToGetter.put(Double.class, (FloatParameter p) -> p.getValue());
		
		javaTypeToType.put(Boolean.class, BOOLEAN);
		javaTypeToGetter.put(Boolean.class, (BooleanParameter p) -> p.getValue());
		
		javaTypeToType.put(String.class, STRING);
		javaTypeToGetter.put(String.class, (StringParameter p) -> p.getValue());
		
	}
	
	

	/*
	 * Static methods
	 */
	
	public static Type getType(String typeAttributeValue) {
		for (Type t : values()) {
			for (String v : t.typeAttrValues) {
				if (v.equalsIgnoreCase(typeAttributeValue))
					return t;
			}
		}
		return null;
	}
	
	public static Type getTypeFromParamInstance(Class<? extends Parameter> pC) {
		for (Type t : values()) {
			if (t.parameterClass.equals(pC))
				return t;
		}
		return null;
	}
	
	public static Type getTypeFromJavaType(Class<?> c) {
		return javaTypeToType.get(c);
	}
	
	@SuppressWarnings("unchecked")
	public static <T, P extends Parameter> Function<P, T> getFunctionGetterFromJavaType(Class<T> c, @SuppressWarnings("unused") Class<P> pC) {
		return (Function<P, T>) javaTypeToGetter.get(c);
	}
	
	
	
	
	
	
	
	
	
	
	public final Class<? extends Parameter> parameterClass;
	private final String[] typeAttrValues;
	
	public String[] getTypeAttributesValues() {
		return Arrays.copyOf(typeAttrValues, typeAttrValues.length);
	}
	
	private <T extends Parameter> Type(Class<T> pC, String... attr) {
		parameterClass = pC;
		typeAttrValues = attr;
	}
	
	
}
