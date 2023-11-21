package bpm.fwr.server.actions;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.io.IOUtils;

import bpm.fwr.server.security.FwrSession;
import bpm.fwr.shared.models.IDirectoryDTO;
import bpm.fwr.shared.models.IDirectoryItemDTO;
import bpm.fwr.shared.models.TreeParentDTO;
import bpm.metadata.MetaDataReader;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class FwrServerActions {
	
	public static TreeParentDTO getContentsByType(FwrSession session) {
		IRepository repository = session.getRepository();
		
		TreeParentDTO res = new TreeParentDTO();
		
		List<RepositoryDirectory> list = new ArrayList<RepositoryDirectory>();
		
		try {
			list = repository.getRootDirectories();
			for(RepositoryDirectory d : list){
				
				IDirectoryDTO dir = new IDirectoryDTO(d.getName());
				dir.setId(d.getId());
				
				res.addChild(dir);
				buildChildsByType(dir, d, session,repository);
				//XXX
				for(RepositoryItem di : repository.getItems(d)){
					
						try {
							IDirectoryItemDTO ti = new IDirectoryItemDTO(di.getItemName());
							ti.setType(IRepositoryApi.TYPES_NAMES[di.getType()]);
							ti.setId(di.getId());
							ti.setParent(dir);
							dir.addChild(ti);
							
							if(di.getType() == IRepositoryApi.FMDT_TYPE ) {
							
								String result = session.getRepositoryConnection().getRepositoryService().loadModel(di);
								InputStream input = IOUtils.toInputStream(result);
								Collection<IBusinessModel> bmodels = MetaDataReader.read(session.getCurrentGroup().getName(), input, session.getRepositoryConnection(), true);
								Iterator<IBusinessModel> mod = bmodels.iterator();
								for(Locale l : mod.next().getLocales()) {
									ti.addLocales(l.getLanguage(), l.getDisplayLanguage(l));
								}
							
							}
							
							
						} catch  (Exception e) {
							e.printStackTrace();
						}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
			
		return res;
	
	}

	private static void buildChildsByType(IDirectoryDTO parentTree, RepositoryDirectory parent, FwrSession session, IRepository rep){
		
		List<RepositoryDirectory> dirs = null;
		try {
			dirs = rep.getChildDirectories(parent);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		for(RepositoryDirectory d : dirs){
			IDirectoryDTO td = new IDirectoryDTO(d.getName());
			td.setId(d.getId());
			parentTree.addChild(td);

			List<RepositoryItem> items = null;
			try {
				items = rep.getItems(d);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			for(RepositoryItem di : items){
			
					IDirectoryItemDTO ti = new IDirectoryItemDTO(di.getItemName());
					ti.setType(IRepositoryApi.TYPES_NAMES[di.getType()]);
					ti.setId(di.getId());
					ti.setParent(td);
					td.addChild(ti);
					
					if(di.getType() == IRepositoryApi.FMDT_TYPE ) {
					
						try {
							String result = session.getRepositoryConnection().getRepositoryService().loadModel(di);
							InputStream input = IOUtils.toInputStream(result);
							Collection<IBusinessModel> bmodels = MetaDataReader.read(session.getCurrentGroup().getName(), input, session.getRepositoryConnection(), true);
							Iterator<IBusinessModel> mod = bmodels.iterator();
							for(Locale l : mod.next().getLocales()) {
								ti.addLocales(l.getLanguage(), l.getDisplayLanguage(l));
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
				
					}
			}
			buildChildsByType(td, d,session, rep);
		}
		
	}
}
