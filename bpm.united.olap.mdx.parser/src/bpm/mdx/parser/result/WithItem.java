package bpm.mdx.parser.result;

public class WithItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6340150774454024152L;
	private String uname;
	private NodeEvaluator value;
	private String format;
	
	public void setUname(String uname) {
		this.uname = uname;
	}
	
	public String getUname() {
		return uname;
	}

	public void setValue(NodeEvaluator value) {
		this.value = value;
	}

	public NodeEvaluator getValue() {
		return value;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getFormat() {
		return format;
	}
	
}
