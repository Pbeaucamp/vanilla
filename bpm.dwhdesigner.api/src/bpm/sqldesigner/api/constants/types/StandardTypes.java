package bpm.sqldesigner.api.constants.types;

import bpm.sqldesigner.api.model.Type;

public abstract class StandardTypes {

	public static final Type NONE = new Type(0, "");
	public static final Type VARCHAR = new Type(1, "VARCHAR");
	public static final Type NUMERIC = new Type(2, "NUMERIC");
	public static final Type INTEGER = new Type(3, "INTEGER");
	public static final Type DATE = new Type(4, "DATE");
	public static final Type DATETIME = new Type(5, "DATETIME");
	public static final Type TEXT = new Type(6, "TEXT");
	public static final Type BOOLEAN = new Type(7, "BOOLEAN");
	public static final Type BYTE = new Type(8, "BYTE");

}
