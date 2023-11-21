package bpm.mdx.parser.result;

import java.util.List;

public class TermItem extends NodeEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8423771812247294886L;
	private String uname;
	
	@Override
	public List<ICubeItem> evaluate() {
		
		return null;
	}

	public void setUname(String uname) {
		this.uname = uname;
	}

	public String getUname() {
		return uname;
	}

}
