package bpm.profiling.runtime.core;

public class Function {
	public static final String[] Function = new String[]{"AVG", "COUNT", "MAX", "MIN", "SUM"};
	
	private String function = Function[1];
	
	private Column col;

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public Column getCol() {
		return col;
	}

	public void setCol(Column col) {
		this.col = col;
	}
	
	
	
}
