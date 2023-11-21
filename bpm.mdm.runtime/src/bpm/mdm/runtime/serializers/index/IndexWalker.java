package bpm.mdm.runtime.serializers.index;

import bpm.mdm.model.storage.EntityStorageStatistics;

public class IndexWalker {

	public static void checkTree(IndexNode node) {
		
		if (node.getLeftNode() != null){
			if (node.getKey().compareTo(node.getLeftNode().getKey()) <= 0){
				throw new RuntimeException("Wrong tree");
			}
			checkTree(node.getLeftNode());
		}
		
		if (node.getRightNode() != null){
			if (node.getKey().compareTo(node.getRightNode().getKey()) >= 0){
				throw new RuntimeException("Wrong tree");
			}
			checkTree(node.getRightNode());
		}
	}
	public static IndexNode lookup(IndexNode node, String key){
		try{
			if (node == null){
				return null;
			}
			
			int v = node.getKey().compareTo(key);
			
			
			if (v == 0){
				return node;
			}
			else if (v < 1){
				return lookup(node.getRightNode(), key);
			}
			else{
				return lookup(node.getLeftNode(), key);
			}
			
	/*		IndexNode n = lookup(node.getRightNode(), key);
			if (n != null){
				return n;
			}
			
			n = lookup(node.getLeftNode(), key);
			if (n != null){
				return n;
			}
			

			
			return null;*/
		}catch(Throwable t){
			t.printStackTrace();
			throw new RuntimeException(t.getMessage(), t);
		}
		
	}

	public static IndexNode getMinNode(IndexNode node) {
		if (node.getLeftNode() == null && node.getRightNode() == null){
			return node;
		}
		
		else if (node.getLeftNode() != null){
			return getMinNode(node.getLeftNode());
		}
		else{
			return getMinNode(node.getRightNode());
		}
		
	}
	public static EntityStorageStatistics gatherStatistics(IndexNode root) {
		EntityStorageStatistics stat = new EntityStorageStatistics();
		
		getNodeInfos(root, stat);
		
		return stat;
	}
	
	/**
	 * 
	 * @param node
	 * @return a Point : p.x=size of the node, p.y=number of different chunk of the node
	 */
	private static void getNodeInfos(IndexNode node, EntityStorageStatistics stat){
		
		
		boolean chunkUsed = node.getChunkFileNumber() < 0;
		if (!chunkUsed){
			stat.setRowsNumberForChunk(node.getChunkFileNumber(), stat.getRowsNumberForChunk(node.getChunkFileNumber())+1 );
		}
		
		if (node.getLeftNode() != null){
			getNodeInfos(node.getLeftNode(), stat);

		}
		if (node.getRightNode() != null){
			getNodeInfos(node.getRightNode(), stat);
		}
		
	}
}
