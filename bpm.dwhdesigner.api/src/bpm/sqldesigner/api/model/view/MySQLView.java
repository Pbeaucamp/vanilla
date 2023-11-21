package bpm.sqldesigner.api.model.view;

public class MySQLView extends SQLView {

	private static final int VIEW_VALUE_COLUMN = 2;
	private String extras;

	@Override
	public String getStatementString() {
		return "SHOW CREATE VIEW `" + schema.getCatalog().getName() + "`.`" + name + "`";
	}

	@Override
	public int getViewValueColumn() {
		return VIEW_VALUE_COLUMN;
	}

	@Override
	public void setValue(String value) {
		String extract = "`" + schema.getCatalog().getName() + "`.`" + name + "` AS ";
		int index = value.indexOf(extract);
		extras = value.substring(0, index + extract.length());
		value = value.substring(index + extract.length(), value.length());
		super.setValue(value);
	}

}
