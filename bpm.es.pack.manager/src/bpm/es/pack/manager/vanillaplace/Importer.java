package bpm.es.pack.manager.vanillaplace;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.dom4j.DocumentException;
import org.eclipse.core.runtime.IProgressMonitor;

import adminbirep.Activator;
import bpm.es.pack.manager.I18N.Messages;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;
import bpm.vanilla.workplace.api.datasource.replacement.BIGDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIRTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.BIWDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FASDDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FAVDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FDDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FDDicoDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FMDTDatasourceReplacement;
import bpm.vanilla.workplace.api.datasource.replacement.FWRDatasourceReplacement;
import bpm.vanilla.workplace.core.IPackage;
import bpm.vanilla.workplace.core.datasource.IDatasource;
import bpm.vanilla.workplace.core.datasource.IDatasourceReplacement;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceFD;
import bpm.vanilla.workplace.core.datasource.ModelDatasourceRepository;
import bpm.vanilla.workplace.core.model.PlaceImportDirectory;
import bpm.vanilla.workplace.core.model.PlaceImportItem;

public class Importer {
	private static final String PACKAGE_FOLDER = Messages.Importer_0;
	
	private static HashMap<Integer, Integer> groupsMap = new HashMap<Integer, Integer>();

	private static HashMap<PlaceImportItem, List<Integer>> newlyCreatedDirsId = new HashMap<PlaceImportItem, List<Integer>>();

	public static synchronized void importPackage(IProgressMonitor monitor, String packPath, String pathSelectedDir, IPackage pack, IRepositoryApi sock) throws Exception{
//		logs = new StringBuffer();
		groupsMap.clear();
		newlyCreatedDirsId.clear();
		
		//We update the path for each items according to the selected item
//		for(PlaceImportItem item : pack.getImportItems()){
//			String path = item.getPath();
//			path = pathSelectedDir + path;
//			item.setPath(path);
//		}
		
		
		//push them with apply options
//		createItems(monitor, packPath, sock, pack.getImportItems(), new HashMap<PlaceImportItem, Integer>(), 
//				new HashMap<Integer, Integer>(), pack);
	}
	
	
	/*
	 * return the newId for each items
	 */
	private static HashMap<PlaceImportItem, Integer> createItems(IProgressMonitor monitor, String packPath, 
			IRepositoryApi sock, List<PlaceImportItem> importItems, HashMap<PlaceImportItem, 
			Integer> itemsId, HashMap<Integer, Integer> matching, IPackage pack) throws Exception {

		for(PlaceImportItem i : importItems){
			//create the directory if it doesnt exists
			if (itemsId.get(i) != null){
				continue;
			}
			
			//we first create the needed object
			for(Integer depId : i.getNeeded()){
				for(PlaceImportItem ii : importItems){
					if (ii.getItem().getId() == depId){
						if (!ii.getNeeded().isEmpty()) {
							List<PlaceImportItem> dep = new ArrayList<PlaceImportItem>();
							for (Integer inte : ii.getNeeded()) {
								for (PlaceImportItem _ii : importItems) {
									if (_ii.getItem().getId() == inte) {
										dep.add(_ii);
									}
								}
							}
							createItems(monitor, packPath, sock, dep, itemsId, matching, pack);
						}
						
						if(itemsId.get(ii) == null){
							createItem(monitor, packPath, ii, sock, itemsId, matching, pack);
						}
					}
				}
			}
 
			//here we had the importItem
			createItem(monitor, packPath, i, sock, itemsId, matching, pack);
				
		}
		
		return itemsId;
	}


	private static void createItem(IProgressMonitor monitor, String packPath, PlaceImportItem i, 
			IRepositoryApi sock, HashMap<PlaceImportItem, Integer> itemsId, 
			HashMap<Integer, Integer> matching, IPackage pack){

		try {		
			RepositoryDirectory dir = getDirectory(monitor, i, sock, pack);
			//we modify the xml for the dependancies
			String xml = modifyXml(i, itemsId, matching, sock);
			
			int id = -1;
			if (i.getItem().getType() == IRepositoryApi.CUST_TYPE){
				Integer subtype = null;
				if(i.getItem().getSubtype() == IRepositoryApi.BIRT_REPORT_SUBTYPE){
					subtype = IRepositoryApi.BIRT_REPORT_SUBTYPE;
				}
				else if(i.getItem().getSubtype() == IRepositoryApi.XACTION_SUBTYPE){
					subtype = IRepositoryApi.XACTION_SUBTYPE;
				}
				else if(i.getItem().getSubtype() == IRepositoryApi.JASPER_REPORT_SUBTYPE){
					subtype = IRepositoryApi.JASPER_REPORT_SUBTYPE;
				}
				
				
				id = sock.getRepositoryService().addDirectoryItemWithDisplay(
						IRepositoryApi.CUST_TYPE,
						subtype, dir, i.getItem().getItemName(), "", "", "", xml, true).getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			
				monitor.worked(1);
			}
			else if (i.getItem().getType() == IRepositoryApi.EXTERNAL_DOCUMENT){
				String filePath = writeExternalFile(packPath, String.valueOf(i.getItem().getId()));
				File file = new File(filePath);
				
				String format = file.getName().lastIndexOf("." + 1) != -1 ? file.getName().substring(file.getName().lastIndexOf("." + 1)) : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				if(format.isEmpty()){
					format = i.getItem().getItemName().lastIndexOf("." + 1) != -1 ? i.getItem().getItemName().substring(i.getItem().getItemName().lastIndexOf("." + 1)) : ""; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
				}
				
				id = sock.getRepositoryService().addExternalDocumentWithDisplay(
						dir,  i.getItem().getItemName(), "", "", "",  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						new FileInputStream(file), false, format).getId(); //$NON-NLS-1$
				
				try {
					file.delete();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				monitor.worked(1);
			}
			else if(i.getItem().getType() == IRepositoryApi.FAV_TYPE){
											
				id = sock.getRepositoryService().addDirectoryItemWithDisplay(
						IRepositoryApi.FAV_TYPE,
						-1,
						dir, 
						i.getItem().getItemName(), 
						"", "", "", xml,  //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
						true).getId(); //$NON-NLS-1$
				
				monitor.worked(1);
			}
			else{
				if(i.getItem().getType() == IRepositoryApi.FD_TYPE){
					if(i.isMainModel()){
						id = sock.getRepositoryService().addDirectoryItemWithDisplay(i.getItem().getType(), -1, dir, i.getItem().getItemName(), "", "", "", xml, true).getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
					else {
						id = sock.getRepositoryService().addDirectoryItemWithDisplay(i.getItem().getType(), -1, dir, i.getItem().getItemName(), "", "", "", xml, false).getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
					}
				}
				else {
					id = sock.getRepositoryService().addDirectoryItemWithDisplay(i.getItem().getType(), -1, dir, i.getItem().getItemName(), "", "", "", xml, true).getId(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
				
				monitor.worked(1);
			}

			for(Integer groupId : i.getAvailableGroupsId()){
				sock.getAdminService().addGroupForItem(groupId, id);
			}
			
			if(i.getRunnableGroupsId() != null && !i.getRunnableGroupsId().isEmpty()){
				for(Integer groupId : i.getRunnableGroupsId()){
					Group gr = new Group();
					gr.setId(groupId);
					
					RepositoryItem it = new RepositoryItem();
					it.setId(id);
					sock.getAdminService().setObjectRunnableForGroup(gr.getId(), it);
				}
			}
			
			monitor.subTask("Succeed : created " + IRepositoryApi.TYPES_NAMES[i.getItem().getType()] + " " + i.getItem().getItemName() + Messages.Importer_31); //$NON-NLS-1$ //$NON-NLS-2$
			itemsId.put(i, id);
			
		} catch (Exception e) {
			monitor.subTask(Messages.Importer_32 + i.getItem().getId() + Messages.Importer_33);
			e.printStackTrace();
		}
	}

	private static String modifyXml(PlaceImportItem item, HashMap<PlaceImportItem, Integer> itemsId, 
			HashMap<Integer, Integer> matching, IRepositoryApi sock) throws DocumentException{

		//We get the new ID
		for(Integer id : item.getNeeded()){
			
			for(PlaceImportItem ii : itemsId.keySet()){
				if (ii.getItem().getId() == id){
					matching.put(ii.getItem().getId(), itemsId.get(ii));
					break;
				}
			}
		}
		
		IDatasourceReplacement remplacement = null;
		
		int type = item.getItem().getType();
		Integer subtype = item.getItem().getSubtype();
		switch(type){
		case IRepositoryApi.FASD_TYPE:
			remplacement = new FASDDatasourceReplacement();
			break;
		case IRepositoryApi.FD_TYPE:
			remplacement = new FDDatasourceReplacement();
			break;
		case IRepositoryApi.FD_DICO_TYPE:
			remplacement = new FDDicoDatasourceReplacement();
			break;
		case IRepositoryApi.FMDT_TYPE:
			remplacement = new FMDTDatasourceReplacement();
			break;
		case IRepositoryApi.GTW_TYPE:
			remplacement = new BIGDatasourceReplacement();
			break;
		case IRepositoryApi.GED_TYPE:
			break;
		case IRepositoryApi.BIW_TYPE:
			remplacement = new BIWDatasourceReplacement();
			break;
		case IRepositoryApi.EXTERNAL_DOCUMENT:
			break;
		case IRepositoryApi.FAV_TYPE:
			remplacement = new FAVDatasourceReplacement();
			break;
		case IRepositoryApi.FWR_TYPE:
			remplacement = new FWRDatasourceReplacement();
			break;
		case IRepositoryApi.CUST_TYPE:
			if(subtype != null && subtype == IRepositoryApi.BIRT_REPORT_SUBTYPE){
				remplacement = new BIRTDatasourceReplacement();
				break;
			}
		}
		
		if(remplacement != null){
			String user = sock.getContext().getVanillaContext().getLogin();
			String password = sock.getContext().getVanillaContext().getPassword();
			int groupId = 1;
			String groupName = ""; //$NON-NLS-1$
			try {
				groupName = Activator.getDefault().getVanillaApi().getVanillaSecurityManager().getGroupById(groupId).getName();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			String vanillaUrl = ""; //$NON-NLS-1$
			try {
				vanillaUrl = sock.getContext().getVanillaContext().getVanillaUrl();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			
			String repositoryUrl = Activator.getDefault().getCurrentRepository().getUrl();
			String repositoryId = String.valueOf(Activator.getDefault().getCurrentRepository().getId());
			
			for(IDatasource ds : item.getDatasources()){
				if(ds instanceof ModelDatasourceRepository){
					ModelDatasourceRepository dsRepo = (ModelDatasourceRepository)ds;
					if(dsRepo.getDirId() != null){
						int itemId = Integer.parseInt(dsRepo.getDirId());
						if(matching.get(itemId) != null){
							dsRepo.setDirId(String.valueOf(matching.get(itemId)));
						}
					}
					
					dsRepo.setGroupId(String.valueOf(groupId));
					dsRepo.setGroupName(groupName);
					dsRepo.setUser(user);
					dsRepo.setPassword(password);
					dsRepo.setRepositoryId(repositoryId);
					dsRepo.setRepositoryUrl(repositoryUrl);
					dsRepo.setVanillaRuntimeUrl(vanillaUrl);
					
					try {
						String xml = item.getXml();
						item.setXml(remplacement.replaceElement(xml, ds));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else if(ds instanceof ModelDatasourceFD){
					ModelDatasourceFD dsFD = (ModelDatasourceFD)ds;
					
					if(dsFD.getDictionaryRepositoryItemId() != null){
						String dicoId = String.valueOf(matching.get(Integer.parseInt(dsFD.getDictionaryRepositoryItemId())));
						dsFD.setDictionaryRepositoryItemId(dicoId);
					}
					
					HashMap<Integer, Integer> dependancies = new HashMap<Integer, Integer>();
					for(Integer key : dsFD.getDependancies().keySet()){
						Integer id = matching.get(key);
						dependancies.put(key, id);
					}
					dsFD.setDependancies(dependancies);
					
					try {
						String xml = item.getXml();
						item.setXml(remplacement.replaceElement(xml, ds));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}

		return item.getXml();
	}
	
	private static RepositoryDirectory getDirectory(IProgressMonitor monitor, PlaceImportItem importItem, 
			IRepositoryApi sock, IPackage pack) throws Exception {
		IRepository rep = new Repository(sock, importItem.getItem().getType());
		
		String p = importItem.getPath().replace("/", "]"); //$NON-NLS-1$ //$NON-NLS-2$
		Integer i = 0;
		String[] names = p.split("]"); //$NON-NLS-1$
		
		boolean found = false;
		RepositoryDirectory target = null;
		for(RepositoryDirectory d : rep.getRootDirectories()){
			if (d.getName().equals(names[i])){
				found = true;
				i++;
			}
			
			if (found && i < names.length){
				target = lookInto(rep, monitor, importItem, sock, d, names, i, pack);
				break;
			}
			else if (found){
				target = d;
				break;
			}
		}
		
		if (target == null){
			int id = createMissings(monitor, importItem, sock, null, names, i, pack);
			return sock.getRepositoryService().getDirectory(id);
		}
		else{
			return target;
		}
		
	}
	
	private static RepositoryDirectory lookInto(IRepository rep, IProgressMonitor monitor, PlaceImportItem importItem, IRepositoryApi sock, 
			RepositoryDirectory dir, String[] names, int pos, IPackage pack)throws Exception{
		for(RepositoryDirectory d : rep.getChildDirectories(dir)){
			if (d.getName().equals(names[pos])){
				if (pos < names.length - 1){
					pos = pos + 1;
					return lookInto(rep, monitor, importItem, sock, d, names, pos, pack);
				}
				
				return d;
			}
			
		}
		
		//create from pos
		int id = createMissings(monitor, importItem, sock, dir, names, pos, pack);
		
		return sock.getRepositoryService().getDirectory(id);
	}
	
	
	
	private static int createMissings(IProgressMonitor monitor, PlaceImportItem importItem, IRepositoryApi sock, RepositoryDirectory parent, 
			String[] names, int pos, IPackage pack) throws Exception{
		
		int dirId = -1;
		for(int i = pos; i<names.length; i++){
			RepositoryDirectory d = new RepositoryDirectory();
			d.setName(names[i]);
			if (parent == null){
				d.setParentId(0); //$NON-NLS-1$
			}else{
				d.setParentId(parent.getId()); //$NON-NLS-1$
			}
		
			
			try {
				d = sock.getRepositoryService().addDirectory(
						d.getName(), "", parent); //$NON-NLS-1$
				dirId = d.getId();

				IRepositoryApi repApi = Activator.getDefault().getRepositoryApi();
				PlaceImportDirectory dir = foundDir(d.getName(), pack.getRootDirectory().getChildsDir());
				if(dir != null){
					for(Integer grId : dir.getAvailableGroupsId()){
						repApi.getAdminService().addGroupForDirectory(grId, dirId);
					}
				}
			
				if (parent != null){
					monitor.subTask(Messages.Importer_45 + d.getName() + Messages.Importer_46 + parent.getName() + "\r\n"); //$NON-NLS-1$
				}
				else{
					monitor.subTask(Messages.Importer_48 + d.getName() + "\r\n"); //$NON-NLS-1$
				}
				parent = d;
				if (newlyCreatedDirsId.get(importItem) == null){
					newlyCreatedDirsId.put(importItem, new ArrayList<Integer>());
					
				}
				newlyCreatedDirsId.get(importItem).add(dirId);
				
			} catch (Exception e) {
				e.printStackTrace();
				monitor.subTask(Messages.Importer_50 + d.getName() + Messages.Importer_51);
				throw e;
			}
			
		}
		
		return dirId;
	}
	
	private static PlaceImportDirectory foundDir(String dirName, List<PlaceImportDirectory> directories){
		if(directories != null){
			for(PlaceImportDirectory dir : directories){
				if(dirName.equals(dir.getName())){
					return dir;
				}
				else {
					PlaceImportDirectory dirTmp = foundDir(dirName, dir.getChildsDir());
					if(dirTmp != null){
						return dirTmp;
					}
				}
			}
		}
		
		return null;
	}
	
	private static String writeExternalFile(String packPath, String fileName) throws Exception{
		ZipFile zipFile = new ZipFile(packPath);
		
		ZipEntry zipEntry = zipFile.getEntry(fileName);
		if(zipEntry != null){
			InputStream is = zipFile.getInputStream(zipEntry);
			
			String path = PACKAGE_FOLDER + File.separator + fileName;
			
			FileOutputStream outputStream = new FileOutputStream(path);
			byte[] buffer = new byte[2048];
		    int n;
		    while ((n = is.read(buffer, 0, 2048)) > -1) {
		    	outputStream.write(buffer, 0, n);
		    }
			    
		    outputStream.close();
		    is.close();
		    
		    return path;
		}
		
		return ""; //$NON-NLS-1$
	}
}
