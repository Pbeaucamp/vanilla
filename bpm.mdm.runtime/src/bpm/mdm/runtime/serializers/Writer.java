package bpm.mdm.runtime.serializers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.mdm.model.Attribute;
import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.serialisation.DatasSerializer;
import bpm.mdm.model.serialisation.MdmConfiguration;
import bpm.mdm.model.util.RowUtil;
import bpm.mdm.runtime.serializers.index.IndexNode;
import bpm.mdm.runtime.serializers.index.IndexWalker;

public class Writer {
	private Entity entity;
	private MdmConfiguration conf;
	private EntityIndex fileIndex;
	private IndexNode tree;
	
	public Writer(Entity entity, MdmConfiguration conf, EntityIndex fileIndex,IndexNode tree){
		this.conf = conf;
		this.entity = entity;
		this.fileIndex = fileIndex;
		this.tree = tree;
	}
	
	public void writeRows(List<Row> rows) throws Exception{
		if (rows.isEmpty()){
			return;
		}
		File f = fileIndex.getAvailableFile();
		
		DatasSerializer serial = new DatasSerializer(conf);
		
		List<HashMap<String, Serializable>>  fileDatas = new ArrayList<HashMap<String,Serializable>>();
		
		
		if (f.exists()){
			// read the actual content
			fileDatas.addAll(serial.loadDatas(entity, new FileInputStream(f)));
		}
		
		//add the rows
		fileDatas.addAll(serial.convertToSerializable(entity, rows));
		
		//write fully the file
		serial.saveDatas(entity, fileDatas, new FileOutputStream(f));
		
		
		String chunkName = f.getName().substring(f.getName().lastIndexOf(".") + 1);
		Long chunkNum = Long.parseLong(chunkName);
		
		for(Row r : rows){
			tree.addNode(RowUtil.generateUUID(entity, r), chunkNum);
		}
		IndexWalker.checkTree(tree);
		//Logger.getLogger(getClass()).info(tree.dump(""));
	}
	
	public void replaceRows(HashMap<File, List<Row>> rows) throws Exception{
		DatasSerializer s = new DatasSerializer(conf);
		List<Attribute> ids = entity.getAttributesId();
		
		for(File f : rows.keySet()){
			List<HashMap<String, Serializable>> datas = s.loadDatas(entity, new FileInputStream(f));
			if (rows.get(f) == null || rows.get(f).isEmpty()){
				continue;
			}
			for(Row r : rows.get(f)){
				update(ids, r, datas);
			}
			s.saveDatas(entity, datas, new FileOutputStream(f));
			Logger.getLogger(getClass()).info("Entity "+ entity.getName() + " chunk file " + f.getName() + " updated.") ;
		}
	}
	
	public void replaceRowsIndexed(HashMap<IndexNode, List<Row>> rows) throws Exception{
		DatasSerializer s = new DatasSerializer(conf);
		List<Attribute> ids = entity.getAttributesId();
		
		for(IndexNode i : rows.keySet()){
			List<HashMap<String, Serializable>> datas = s.loadDatas(entity, new FileInputStream(conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".store." + i.getChunkFileNumber()));
			if (rows.get(i) == null || rows.get(i).isEmpty()){
				continue;
			}
			for(Row r : rows.get(i)){
				update(ids, r, datas);
			}
			s.saveDatas(entity, datas, new FileOutputStream(conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".store." + i.getChunkFileNumber()));
			Logger.getLogger(getClass()).info("Entity "+ entity.getName() + " chunk file " + entity.getUuid() + ".store." + i.getChunkFileNumber() + " updated.") ;
		}
	}

	private void update(List<Attribute> pkAttrbutes, Row r, List<HashMap<String, Serializable>> datas) {
		for(HashMap<String, Serializable> l : datas){
			boolean macth = !pkAttrbutes.isEmpty();
			for(Attribute a : pkAttrbutes){
				if (!l.get(a.getUuid()).equals(r.getValue(a))){
					macth = false;
					break;
				}
			}
			if (macth){
				for(Attribute a : entity.getAttributes()){
					l.put(a.getUuid(), (Serializable)r.getValue(a));
				}
			}
		}
		
	}
	private void remove(List<Attribute> pkAttrbutes, Row r, List<HashMap<String, Serializable>> datas) {
		
		List toRemove = new ArrayList();
		for(HashMap<String, Serializable> l : datas){
			boolean macth = !pkAttrbutes.isEmpty();
			for(Attribute a : pkAttrbutes){
				if (!l.get(a.getUuid()).equals(r.getValue(a))){
					macth = false;
					break;
				}
			}
			if (macth){
				toRemove.add(l);
			}
		}
		
		for(Object o : toRemove){
			datas.remove(o);
		}
	}


	public EntityIndex getIndex(){
		return fileIndex;
	}

	public void deleteRows(HashMap<File, List<Row>> toDelete) throws Exception{
		DatasSerializer s = new DatasSerializer(conf);
		List<Attribute> ids = entity.getAttributesId();
		
		for(File f : toDelete.keySet()){
			List<HashMap<String, Serializable>> datas = s.loadDatas(entity, new FileInputStream(f));
			if (toDelete.get(f) == null || toDelete.get(f).isEmpty()){
				continue;
			}
			for(Row r : toDelete.get(f)){
				remove(ids, r, datas);
				
				tree.removeNode(RowUtil.generateUUID(entity, r));
				
			}
			s.saveDatas(entity, datas, new FileOutputStream(f));
			Logger.getLogger(getClass()).info("Entity "+ entity.getName() + " chunk file " + f.getName() + " updated.") ;
		}
		IndexWalker.checkTree(tree);
	}
	
	public void deleteRowsTree(HashMap<IndexNode, List<Row>> toDelete) throws Exception{
		DatasSerializer s = new DatasSerializer(conf);
		List<Attribute> ids = entity.getAttributesId();
		
		for(IndexNode f : toDelete.keySet()){
			List<HashMap<String, Serializable>> datas = s.loadDatas(entity, new FileInputStream(conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".store." + f.getChunkFileNumber()));
			if (toDelete.get(f) == null || toDelete.get(f).isEmpty()){
				continue;
			}
			for(Row r : toDelete.get(f)){
				remove(ids, r, datas);
				
				tree.removeNode(RowUtil.generateUUID(entity, r));
				
			}
			s.saveDatas(entity, datas, new FileOutputStream(conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".store." + f.getChunkFileNumber()));
			Logger.getLogger(getClass()).info("Entity "+ entity.getName() + " chunk file " + entity.getUuid() + ".store." + f.getChunkFileNumber() + " updated.") ;
		}
		
	}
}
