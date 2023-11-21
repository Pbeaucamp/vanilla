package bpm.united.olap.runtime.projection;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.logging.IVanillaLogger;

public class ProjectionSerializationHelper {

	public static final String PROJECTION_FILE_PREFIX = "forecastFile";
	
	public static ProjectionDescriptor serialize(List<CrossMembersValues> data, Projection projection, IVanillaLogger logger, int nbFile, String projectionMeasureName) throws Exception {
		
		logger.debug("create the fileName");
		
		String key = PROJECTION_FILE_PREFIX + "_" + projectionMeasureName + "_" + CacheKeyGenerator.generateKey(projection);
		String destFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.united.olap.runtime.projection.fileLocation");
		File file = new File(destFolder + File.separator + key + "_" + nbFile);
		
		logger.debug("filename = " + file.getAbsolutePath());
		
		FileOutputStream fos = new FileOutputStream(file);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(data);
		out.close();
		
		logger.debug("file writed");
		
		ProjectionDescriptor desc = new ProjectionDescriptor();
		desc.setFileName(destFolder + File.separator + key);
		projection.setNewFactName(destFolder + File.separator + key);
		
		return desc;
	}
	
	public static List<CrossMembersValues> deserialize(Projection projection, int nbFile, String projectionMeasureName) throws Exception {
		
		String key = PROJECTION_FILE_PREFIX + "_" + projectionMeasureName + "_" + CacheKeyGenerator.generateKey(projection);
		String destFolder = ConfigurationManager.getInstance().getVanillaConfiguration().getProperty("bpm.united.olap.runtime.projection.fileLocation");
		File file = new File(destFolder + File.separator + key + "_" + nbFile);
		
		if(!file.exists()) {
			Logger.getLogger(ProjectionSerializationHelper.class).debug("filename = " + file.getAbsolutePath() + " doesn't exists");
			return null;
		}
		
		Logger.getLogger(ProjectionSerializationHelper.class).debug("filename = " + file.getAbsolutePath());
		
		FileInputStream in = new FileInputStream(file);
		ObjectInputStream oin = new ObjectInputStream(in);
		List<CrossMembersValues> result = (List<CrossMembersValues>) oin.readObject();
		oin.close();
		
		return result;
	}
}
