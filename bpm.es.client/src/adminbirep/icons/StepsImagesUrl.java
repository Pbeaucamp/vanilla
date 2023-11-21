package adminbirep.icons;

import java.net.URL;
import java.util.HashMap;

import bpm.vanilla.platform.core.IRepositoryApi;

public class StepsImagesUrl {
	private static final HashMap<Integer, URL> urls = new HashMap<Integer, URL>();
	
	public static HashMap<Integer,URL> getImages(){
		
		if (!urls.isEmpty()){
			return urls;
		}
		urls.put(IRepositoryApi.CUST_TYPE, Icons.class.getResource(Icons.BIRT.endsWith(".png") ? Icons.BIRT : Icons.BIRT + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.FASD_TYPE, Icons.class.getResource(Icons.CUBE.endsWith(".gif") ? Icons.CUBE : Icons.CUBE + ".gif")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.FAV_TYPE, Icons.class.getResource(Icons.FAV.endsWith(".png") ? Icons.FAV : Icons.FAV + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.FD_TYPE, Icons.class.getResource(Icons.FD.endsWith(".png") ? Icons.FD : Icons.FD + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.FMDT_TYPE, Icons.class.getResource(Icons.FMDT.endsWith(".png") ? Icons.FMDT : Icons.FMDT + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.FWR_TYPE, Icons.class.getResource(Icons.FWR.endsWith(".png") ? Icons.FWR : Icons.FWR + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.GED_TYPE, Icons.class.getResource(Icons.GED_ENTRY.endsWith(".png") ? Icons.GED_ENTRY : Icons.GED_ENTRY + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.FD_DICO_TYPE, Icons.class.getResource(Icons.DICTIONNARY.endsWith(".png") ? Icons.DICTIONNARY : Icons.DICTIONNARY + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.BIW_TYPE, Icons.class.getResource(Icons.BIW.endsWith(".gif") ? Icons.BIW : Icons.BIW + ".gif")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.MAP_TYPE, Icons.class.getResource(Icons.MD.endsWith(".png") ? Icons.MD : Icons.MD + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.EXTERNAL_DOCUMENT, Icons.class.getResource(Icons.DOC.endsWith(".png") ? Icons.DOC : Icons.DOC + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.DISCONNECTED_PCKG, Icons.class.getResource(Icons.PACKAGE.endsWith(".png") ? Icons.PACKAGE : Icons.PACKAGE + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.GTW_TYPE, Icons.class.getResource(Icons.BIG.endsWith(".png") ? Icons.BIG : Icons.BIG + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.REPORTS_GROUP, Icons.class.getResource(Icons.REPORTS_GROUP.endsWith(".png") ? Icons.REPORTS_GROUP : Icons.REPORTS_GROUP + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		urls.put(IRepositoryApi.KPI_THEME, Icons.class.getResource(Icons.KPI_THEME.endsWith(".png") ? Icons.KPI_THEME : Icons.KPI_THEME + ".png")); //$NON-NLS-1$ //$NON-NLS-2$
		
		urls.put(-1, Icons.class.getResource(Icons.DIRECTORY.endsWith(".png") ? Icons.DIRECTORY : Icons.DIRECTORY + ".png")); //$NON-NLS-1$ //$NON-NLS-2$

		return urls;
	}
}
