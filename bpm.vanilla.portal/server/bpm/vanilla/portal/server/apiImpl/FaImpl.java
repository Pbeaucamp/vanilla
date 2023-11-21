package bpm.vanilla.portal.server.apiImpl;

import java.io.IOException;
import java.util.List;

import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;



public class FaImpl {
	/**
	 * pourquoi sync'? parceque!
	 * @param group
	 * @param fasdItem
	 * @param location
	 * @param sock
	 * @return
	 * @throws IOException
	 */
	synchronized public static List<String> loadModel(RepositoryItem fasdItem, IRepositoryApi sock) throws Exception {
		return sock.getRepositoryService().getCubeNames(fasdItem);
	}
	
//		Vector<String> cubeNames = new Vector<String>();
//		
//		try{
//			String xml = sock.loadModel(fasdItem.getType(), fasdItem);
//			Document document = DocumentHelper.parseText(xml);
//			
//	        Element root = document.getRootElement();
//	        
//	        try {
//		        for (Object o : root.element("olap").element("Cube").elements("Cube-item")) {
//		        	Element e = (Element) o;
//		        	cubeNames.add(e.element("name").getText());
//		        }
//	        }
//	        catch (Exception e) {
//	        	
//			}
//	        try {
//	        	for (Object o : root.element("olap").element("VirtualCube").elements("VirtualCube-item")) {
//		        	Element e = (Element) o;
//		        	cubeNames.add(e.element("name").getText());
//		        }
//			} catch (Exception e) {
//			}
//	        
//			
//		}catch (Exception e) {
//			e.printStackTrace();
//		}
//	
//		return cubeNames;
//	}
	
//	synchronized public static Set<RepositoryItem> writeRepository(String group, int fasdId, String location, IRepositoryConnection sock){
//		location += File.separator + "resources";
//		
//		Set<RepositoryItem> cubes = null;		
//		
//		try{
//			RepositoryFactory.initialize();
//			Repository repmanu = RepositoryFactory.getInstance(location + File.separator, sock);
//						
//			cubes = repmanu.getCubes(group);
//			
//		}catch (MalformedURLException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return cubes;
//	}

	

}
