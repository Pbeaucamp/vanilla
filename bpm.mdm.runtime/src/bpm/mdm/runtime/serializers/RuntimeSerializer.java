package bpm.mdm.runtime.serializers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.concurrent.Semaphore;

import org.apache.log4j.Logger;

import bpm.mdm.model.Entity;
import bpm.mdm.model.serialisation.MdmConfiguration;
import bpm.mdm.runtime.serializers.index.IndexNode;

public class RuntimeSerializer {
	private HashMap<String, Writer> writers = new HashMap<String, Writer>();
	private HashMap<String, Reader> readers = new HashMap<String, Reader>();
	private HashMap<String, IndexNode> index = new HashMap<String, IndexNode>();
	
	private Semaphore locker = new Semaphore(1);
	private MdmConfiguration conf;
	private long maxFileSize = 1024 * 10 *100;
	
	
	
	
	public RuntimeSerializer(MdmConfiguration conf){
		this.conf = conf;
	}
	
	
	private void initEntity(Entity entity) throws InterruptedException{
	
		try{
			locker.acquire();
			EntityIndex index = new EntityIndex(entity, new File(conf.getMdmPersistanceFolderName()), maxFileSize);
			if (this.index.get(entity.getUuid()) == null){
				this.index.put(entity.getUuid(), index.getTreeIndex());	
			}
			readers.put(entity.getUuid(), new Reader(entity, conf, index));
			writers.put(entity.getUuid(), new Writer(entity, conf, index, index.getTreeIndex()));
		}finally{
			locker.release();	
		}
		
		
	}
	
	public Writer getWriter(Entity entity) throws InterruptedException{
		Writer w = writers.get(entity.getUuid());
		if (w == null){
			initEntity(entity);
		}
		
		return writers.get(entity.getUuid());
	}
	
	
	public Reader getReader(Entity entity) throws InterruptedException{
		Reader r = readers.get(entity.getUuid());
		if (r == null){
			initEntity(entity);
		}
		
		return readers.get(entity.getUuid());
	}
	
	public IndexNode getIndexTree(Entity entity) throws InterruptedException{
		IndexNode node = index.get(entity.getUuid());
		if (node == null){
			initEntity(entity);
		}
		return index.get(entity.getUuid());
	}

	public void serializeIndex(Entity entity) throws Exception{
		IndexNode node = getIndexTree(entity);
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".index"));
		oos.writeObject(node);
		oos.close();
	}


	public void resetIndexTree(Entity entity, IndexNode indexNode) throws Exception{
		try{
			locker.acquire();
			
			String f = conf.getMdmPersistanceFolderName() + "/" + entity.getUuid() + ".index";
			Logger.getLogger(getClass()).info("Save the rebuilt index for " + entity.getName() + " in " + f);
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(indexNode);
			oos.close();
			
			this.index.remove(entity.getUuid());
			this.readers.remove(entity.getUuid());
			this.writers.remove(entity.getUuid());

			Logger.getLogger(getClass()).info("Removed old version of index");
		}finally{
			locker.release();	
		}		
	}
}
