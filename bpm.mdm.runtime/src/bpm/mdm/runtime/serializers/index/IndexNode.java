package bpm.mdm.runtime.serializers.index;

import java.io.File;
import java.io.Serializable;

import bpm.mdm.model.util.RowUtil;



public class IndexNode implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -5914418832434751458L;
	private String key;
	private long chunkFileNumber;
	private IndexNode leftNode;
	private IndexNode rightNode;
	private IndexNode parent;
	

	public IndexNode(String key, long chunkFileNumber){
		this.key = key;
		this.chunkFileNumber = chunkFileNumber;
	}

	/**
	 * @return the leftNode
	 */
	public IndexNode getLeftNode() {
		return leftNode;
	}

	

	/**
	 * @return the rightNode
	 */
	public IndexNode getRightNode() {
		return rightNode;
	}


	/**
	 * @return the key
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the fileName
	 */
	public long getChunkFileNumber() {
		return chunkFileNumber;
	}
	
	public void addNode(String key, long chunkNumber){
		int v = getKey().compareTo(key);
		if (v == 0){
			throw new RuntimeException("node uuid " + key + " already exists");
		}
		if (v < 0){
			if (getRightNode() != null){
				getRightNode().addNode(key, chunkNumber);
			}
			else{
				rightNode = new IndexNode(key, chunkNumber);
				rightNode.parent = this;
			}
		}
		else{
			if (getLeftNode() != null){
				getLeftNode().addNode(key, chunkNumber);
			}
			else{
				leftNode = new IndexNode(key, chunkNumber);
				leftNode.parent = this;
				
			}
		}
	}

	public void removeNode(String key) {
		IndexNode node = IndexWalker.lookup(this, key);
		if (node == null){
			return;
		}
		
		//leaf
		if (node.getLeftNode() == null && node.getRightNode() == null){
			node.parent.removeChild(node);
		}
		else if (node.getLeftNode() != null && node.getRightNode() != null){
			//get the from the right subtree
			IndexNode min = IndexWalker.getMinNode(node.getRightNode());
			//replace the actual node values by the min ones
			node.key = min.key;
			node.chunkFileNumber = min.chunkFileNumber;
			//remove the min node because it is doublon
			min.parent.removeChild(min);
			
		}
		else{
			if (node.getLeftNode() != null ){
				node.parent.replaceChild(node, node.getLeftNode());
			}
			else{
				node.parent.replaceChild(node, node.getRightNode());
			}
		}
		
		
		
		
		}

	private void replaceChild(IndexNode node, IndexNode leftNode2) {
		if (leftNode == node){
			leftNode = leftNode2;
			
		}
		if (rightNode == node){
			rightNode = leftNode2;

			
		}
		leftNode2.parent = this;
		
	}

	private void removeChild(IndexNode node) {
		if (getLeftNode() != null && getLeftNode().getKey().equals(node.getKey())){
			leftNode = null;
		}
		else if (getRightNode() != null && getRightNode().getKey().equals(node.getKey())){
			rightNode = null;
		}
		
	}

	public String dump(String spaces) {
		
		StringBuilder b = new StringBuilder();
		b.append(spaces + "-" + key + "\n");
		if (getLeftNode() != null){
			b.append(getLeftNode().dump(spaces + " "));
		}
		if (getRightNode() != null){
			b.append(getRightNode().dump(spaces + " "));
		}
		return b.toString();
	}
}
