package bpm.mdx.parser.result;

public class BasicItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8838549558413040392L;

	private Object value;

	public void setValue(Object value) {
		this.value = value;
	}

	public Object getValue() {
		return value;
	}
	
	
	
}
