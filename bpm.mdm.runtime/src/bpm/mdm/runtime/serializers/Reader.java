package bpm.mdm.runtime.serializers;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

import bpm.mdm.model.Entity;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.exception.NonExistingChunkFileException;
import bpm.mdm.model.serialisation.DatasSerializer;
import bpm.mdm.model.serialisation.MdmConfiguration;
import bpm.mdm.runtime.serializers.index.IndexNode;

public class Reader {
	private Entity entity;
	private MdmConfiguration conf;
	private EntityIndex fileIndex;
	
	public Reader(Entity entity, MdmConfiguration conf, EntityIndex fileIndex){
		this.conf = conf;
		this.entity = entity;
		this.fileIndex = fileIndex;
	}
	
	public List<Row> readStreamRows(int chunkNumber) throws NonExistingChunkFileException, Exception{
		DatasSerializer s = new DatasSerializer(conf);
		File f = fileIndex.getFile(chunkNumber);
		if (f == null){
			throw new NonExistingChunkFileException("No file for the chunk " + chunkNumber + " entity " + entity.getName());
		}
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(f);
			List<HashMap<String, Serializable>> l = s.loadDatas(entity, fis);
			return s.convert(entity, l);
		}finally{
			
			try{
				if (fis != null){
					fis.close();
				}
			}catch(Exception ex){
				
			}
		}
	
	}
	
	public List<Row> readStreamRows(IndexNode node) throws NonExistingChunkFileException, Exception{
		DatasSerializer s = new DatasSerializer(conf);
		File f = new File(conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".store." + node.getChunkFileNumber());
		if (f == null){
			throw new NonExistingChunkFileException("Bad indexNode");//"No file for the chunk " + chunkNumber + " entity " + entity.getName());
		}
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(f);
			List<HashMap<String, Serializable>> l = s.loadDatas(entity, fis);
			return s.convert(entity, l);
		}finally{
			
			try{
				if (fis != null){
					fis.close();
				}
			}catch(Exception ex){
				
			}
		}
	
	}
	
	public List<HashMap<String, Serializable>> readStreamDatas(int chunkNumber) throws NonExistingChunkFileException, Exception{
		DatasSerializer s = new DatasSerializer(conf);
		File f = fileIndex.getFile(chunkNumber);
		if (f == null){
			throw new NonExistingChunkFileException("No file for the chunk " + chunkNumber + " entity " + entity.getName());
		}
		FileInputStream fis = null;
		try{
			fis = new FileInputStream(f);
			List<HashMap<String, Serializable>> l = s.loadDatas(entity, fis);
			return l;
		}finally{
			
			try{
				if (fis != null){
					fis.close();
				}
			}catch(Exception ex){
				
			}
		}
	
	}


	public EntityIndex getIndex(){
		return fileIndex;
	}
}
