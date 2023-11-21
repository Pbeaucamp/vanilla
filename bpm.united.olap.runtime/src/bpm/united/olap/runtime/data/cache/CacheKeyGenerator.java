package bpm.united.olap.runtime.data.cache;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.runtime.DataCellIdentifier2;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.model.SnowFlakesTable;

public class CacheKeyGenerator {

//	public static String generateKey(Hierarchy hierarchy){
//		StringBuilder s = new StringBuilder();
//		s.append(hierarchy.getUname());
//		
//		return generateKey(hierarchy.getParentDimension().getParentSchema() ) + "/" + md5(s.toString());
//	}
//	
	
	
	public static String md5(String value){
		byte[] uniqueKey = value.getBytes();
		byte[] hash = null;

		try {

		// on récupère un objet qui permettra de crypter la chaine

			hash = MessageDigest.getInstance("MD5").digest(uniqueKey);

		}catch (NoSuchAlgorithmException e) {
			throw new Error("no MD5 support in this VM");

		}
		StringBuffer hashString = new StringBuffer();

		for (int i = 0; i < hash.length; ++i) {

			String hex = Integer.toHexString(hash[i]);
			if (hex.length() == 1) {
				hashString.append('0');
				hashString.append(hex.charAt(hex.length() - 1));
			}
			else {
				hashString.append(hex.substring(hex.length() - 2));
			}

		}

		return hashString.toString();
	}

	public static String generateKey(ICubeInstance cubeInstance, DataCellIdentifier2 identifier, String effectiveQuery) {
		StringBuilder s= new StringBuilder();
		s.append(generateKey(cubeInstance));
		s.append("-");
		s.append(md5(identifier.getKey()));
		if (effectiveQuery != null){
			s.append("-");
			s.append(effectiveQuery);
		}
		return s.toString();
	}

	public static String generateKey(Schema schema) {
		StringBuilder s = new StringBuilder();
		s.append(schema.getId());
		
		return s.toString();
	}
	
	public static String generateKey(LevelDatasCache levelDatas) {
		StringBuilder s = new StringBuilder();
		s.append(levelDatas.getSchemaId());
		s.append("-");
		
		
		try {
			s.append(URLEncoder.encode(levelDatas.getLevelUname(), "UTF-8"));
//			s.append(md5(levelDatas.getLevelUname()));
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		if (levelDatas.getEffectiveQuery() != null){
			s.append("-" + md5(levelDatas.getEffectiveQuery()));
		}
		
		return md5(s.toString());//md5(s.toString());
	}

	public static String generateKey(ICubeInstance cubeInstance) {
		StringBuilder s = new StringBuilder();
		s.append(generateKey(cubeInstance.getSchema()));
		s.append("-");
		
		s.append(cubeInstance.getGroupId());
		s.append("-");
		try {
			s.append(URLEncoder.encode(cubeInstance.getCube().getName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s.toString();
	}

	public static String generateKey(SnowFlakesTable snowFlakesTable) {
		StringBuilder s = new StringBuilder();
		s.append(snowFlakesTable.getObjectName());
		return s.toString();
	}
	
	
	public static String generateKey(Cube cube) {
		StringBuilder s = new StringBuilder();
		s.append(generateKey(cube.getParentSchema()));
		s.append("-");
		
		try {
			s.append(URLEncoder.encode(cube.getName(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return s.toString();
	}
	

	public static String generateKey(Level lvl) {
		StringBuilder s = new StringBuilder();
		s.append(lvl.getParentHierarchy().getParentDimension().getParentSchema().getId());
		s.append("-");
		
		
		try {
			s.append(URLEncoder.encode(lvl.getUname(), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			
			e.printStackTrace();
		}
		
		return md5(s.toString());
	}
	
	public static String generateKey(Projection projection) {
		return md5(projection.getKey());
	}
}
