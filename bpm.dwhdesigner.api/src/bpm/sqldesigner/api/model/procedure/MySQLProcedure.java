package bpm.sqldesigner.api.model.procedure;

public class MySQLProcedure extends SQLProcedure {

	private static final int PROCEDURE_VALUE_COLUMN = 3;

	@Override
	public String getCreateStatement() {
		if (type == 0)
			return "SHOW CREATE PROCEDURE `" + schema.getCatalog().getName()
					+ "`.`" + name + "`";
		else if (type == 2)
			return "SHOW CREATE FUNCTION `" + schema.getCatalog().getName()
					+ "`.`" + name + "`";
		else
			return "";
	}

	@Override
	public int getProcedureValueColumn() {
		return PROCEDURE_VALUE_COLUMN;
	}

	@Override
	public void setValue(String value) {
		int index = -1;
		value = value.replaceAll(new String(new char[] { '\n' }), " ");
		value = value.replaceAll(new String(new char[] { '\r' }), "");
		value = value.replaceAll("  ", " ");
		if (type == 0) {
			index = value.indexOf("PROCEDURE");
		} else if (type == 2) {
			index = value.indexOf("FUNCTION");
		}
		if (index != -1) {
			value = value.substring(index, value.length());
			super.setValue(value);
		}
	}


	@Override
	public String getDropStatement() {
		if (type == 0)
			return "DROP PROCEDURE `" + schema.getCatalog().getName()
					+ "`.`" + name + "`";
		else if (type == 2)
			return "DROP FUNCTION `" + schema.getCatalog().getName()
					+ "`.`" + name + "`";
		else
			return "";
	}

}
