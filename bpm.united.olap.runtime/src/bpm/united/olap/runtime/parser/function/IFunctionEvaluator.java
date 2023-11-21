package bpm.united.olap.runtime.parser.function;

import java.util.List;

import bpm.mdx.parser.result.NodeEvaluator;
import bpm.united.olap.api.runtime.MdxSet;

/**
 * Evaluation a node which represents a Mdx function
 * @author Marc Lanquetin
 *
 */
public interface IFunctionEvaluator {

	/**
	 * Return Sets which represent the result of the function
	 * @param node
	 * @return
	 * @throws Exception
	 */
	List<MdxSet> evaluate(NodeEvaluator node) throws Exception;
	
}
