package bpm.mdx.parser.result;

import java.util.List;

import antlr.CommonAST;

public abstract class NodeEvaluator extends CommonAST {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8609663358441657821L;

	List<ICubeItem> evaluate(){return null;}
	
}
