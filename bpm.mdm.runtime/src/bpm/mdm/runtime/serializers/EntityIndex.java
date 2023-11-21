package bpm.mdm.runtime.serializers;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.serialisation.DatasSerializer;
import bpm.mdm.runtime.serializers.index.IndexNode;
import bpm.mdm.runtime.serializers.index.IndexWalker;

public class EntityIndex {
	private Entity entity;
	private File folder;
	private File indexFile;
	private long maxFileSize;
	private IndexNode treeIndex;
	public EntityIndex(Entity entity,File folder, long maxFileSize){
		this.entity = entity;
		this.folder = folder;
		this.maxFileSize = maxFileSize;
		this.indexFile = new File(folder, entity.getUuid() + ".index");
		reloadIndex();
		Logger.getLogger(getClass()).info("EntityIndex created for " + entity.getName());
	}
	
	private void reloadIndex(){
		if (indexFile.exists()){
			Logger.getLogger(getClass()).info("Loading " + entity.getName() + " index");
			try{
				ObjectInputStream ois = new ObjectInputStream(new FileInputStream(this.indexFile));
				Object o = ois.readObject();
				ois.close();
				treeIndex = (IndexNode)o;
			}catch(Exception ex){
				throw new RuntimeException("Unable to reload entity Index " + entity.getName() + " " + ex.getMessage(), ex);
			}
			
		}
		else{
			treeIndex = new IndexNode(entity.getUuid(), -1);
		}
		IndexWalker.checkTree(treeIndex);
	}
	
	public IndexNode getTreeIndex(){
		return treeIndex;
	}
	
	
	public File getIndexFile(){
		return this.indexFile;
	}
	
	public File getFile(int chunkNumber) {
		for(String s : folder.list()){
			if (s.equals(entity.getUuid() + ".store." + chunkNumber)){
				return new File(folder, s);
			}
		}
		return null;
	}
	
	public File getFile4PrimaryKey(Row row) throws Exception{
		List<Attribute> pkAttributes = entity.getAttributesId();
		//XXX : improve this by sorting files, keeping an updated index, binary-tree for the index, and so on,...
		for(String s : folder.list()){
			File f = new File(folder, s);
			DatasSerializer ser = new DatasSerializer(null);
			List<HashMap<String, Serializable>> datas = ser.loadDatas(entity, new FileInputStream(f));
			 
			 
			 for(HashMap<String, Serializable> r : datas){
				 boolean match = true;
				 for(Attribute a : pkAttributes){
					 if (!row.getValue(a).equals(r.get(a.getUuid()))){
						 match = false;
						 break;
					 }
				 } 
				 if (match){
					 return f;
				 }
			 }
		}
		return null;
	}

	
	public File getAvailableFile() {
		int maxChunkNumber = -1;
		
		for(String s : folder.list()){
			if (s.startsWith(entity.getUuid() + ".store." )){
				String n = s.replace(entity.getUuid() + ".store.", "");
				int i = Integer.parseInt(n);
				if (i > maxChunkNumber){
					maxChunkNumber = i;
				}
			}
		}
		
		File lastFile = null;
		
		if (maxChunkNumber == -1){
			lastFile = new File(folder, entity.getUuid() + ".store.0" );
		}
		else{
			lastFile = new File(folder, entity.getUuid() + ".store."  + maxChunkNumber);
			if (lastFile.exists() && lastFile.length() >= maxFileSize){
				maxChunkNumber++;
				lastFile = new File(folder, entity.getUuid() + ".store."  + maxChunkNumber);
				Logger.getLogger(getClass()).info("File Chunk " + lastFile.getName() +" created for " +entity.getName());
			}
		}
		return lastFile;
	}
}
