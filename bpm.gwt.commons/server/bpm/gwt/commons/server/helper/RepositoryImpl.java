package bpm.gwt.commons.server.helper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.services.exception.ServiceException;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.gwt.commons.shared.repository.PortailItemCube;
import bpm.gwt.commons.shared.repository.PortailItemCubeView;
import bpm.gwt.commons.shared.repository.PortailItemFasd;
import bpm.gwt.commons.shared.repository.PortailItemFmdt;
import bpm.gwt.commons.shared.repository.PortailItemKpiTheme;
import bpm.gwt.commons.shared.repository.PortailItemReportsGroup;
import bpm.gwt.commons.shared.repository.PortailRepositoryDirectory;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.components.ReportHistoricComponent;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;
import bpm.vanilla.platform.core.remote.impl.components.RemoteHistoricReportComponent;
import bpm.vanilla.platform.core.repository.IRepository;
import bpm.vanilla.platform.core.repository.IRepositoryObject;
import bpm.vanilla.platform.core.repository.KpiTheme;
import bpm.vanilla.platform.core.repository.ReportsGroup;
import bpm.vanilla.platform.core.repository.Repository;
import bpm.vanilla.platform.core.repository.RepositoryDirectory;
import bpm.vanilla.platform.core.repository.RepositoryItem;

import com.thoughtworks.xstream.XStream;

public class RepositoryImpl {

	private CommonSession session;

	public RepositoryImpl(CommonSession session) throws Exception {
		this.session = session;
	}

	/**
	 * Get the Repository Content
	 * 
	 * @param sock
	 * @param showAllRepository
	 * @return
	 */
	public PortailRepositoryDirectory getRepositoryDirectories(String repositoryName, IRepositoryApi sock, boolean showAllRepository) {
		PortailRepositoryDirectory res = new PortailRepositoryDirectory(repositoryName);

		try {
			int groupId = sock.getContext().getGroup().getId();
			if (showAllRepository) {
				sock.getContext().getGroup().setId(-1);
			}

			List<RepositoryDirectory> rootDirectories = new Repository(sock).getRootDirectories();
			for (RepositoryDirectory d : rootDirectories) {
				if (d.isShowed()) {
					PortailRepositoryDirectory dir = new PortailRepositoryDirectory(d);
					res.addItem(dir);
				}
			}

			sock.getContext().getGroup().setId(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public PortailRepositoryDirectory searchRepository(String currentRepositoryName, IRepositoryApi sock, String search) {
		PortailRepositoryDirectory res = new PortailRepositoryDirectory(currentRepositoryName);

		try {
			List<RepositoryItem> items = new Repository(sock).getItems(search);
			if (items != null) {
				for (RepositoryItem di : items) {
					if (di.isDisplay()) {
						PortailRepositoryItem item = buildItem(sock, res, di);
						if (item != null) {
							res.addItem(item);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	/**
	 * Renvoi un directory et son contenu
	 * 
	 * @param sock
	 *            connection au repository
	 * @param dir
	 * @return
	 */
	public List<IRepositoryObject> getDirectoryContent(IRepositoryApi sock, PortailRepositoryDirectory dir, boolean showAllRepository) {
		List<IRepositoryObject> items = new ArrayList<IRepositoryObject>();
		try {
			int groupId = sock.getContext().getGroup().getId();

			if (showAllRepository) {
				sock.getContext().getGroup().setId(-1);
			}

			IRepository rep = new Repository(sock);
			RepositoryDirectory d = rep.getDirectory(dir.getDirectory().getId());

			for (RepositoryDirectory dirTmp : rep.getChildDirectories(d)) {

				if (showAllRepository || dirTmp.isShowed()) {
					PortailRepositoryDirectory dirItem = new PortailRepositoryDirectory(dirTmp);
					items.add(dirItem);
				}
			}

			for (RepositoryItem di : rep.getItems(d)) {
				if (showAllRepository || di.isDisplay()) {
					PortailRepositoryItem item = buildItem(sock, dir, di);
					if (item != null) {
						items.add(item);
					}
				}
			}

			sock.getContext().getGroup().setId(groupId);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return items;
	}

	public PortailRepositoryDirectory getContentsByType(IRepositoryApi sock, int typeRepository) throws Exception {
		PortailRepositoryDirectory res = new PortailRepositoryDirectory();

		IRepository rep = new Repository(sock, typeRepository);
		List<RepositoryDirectory> rootDirs = rep.getRootDirectories();

		if (rootDirs != null) {
			for (RepositoryDirectory d : rootDirs) {

				if (d.isShowed()) {

					PortailRepositoryDirectory dir = new PortailRepositoryDirectory(d);
					res.addItem(dir);

					buildChildsByType(dir, sock, rep);

					for (RepositoryItem di : rep.getItems(d)) {
						if (di.isDisplay()) {
							PortailRepositoryItem item = buildItem(sock, dir, di);
							if (item != null) {
								dir.addItem(item);
							}
						}
					}
				}
			}
		}

		return res;
	}

	private void buildChildsByType(PortailRepositoryDirectory parent, IRepositoryApi sock, IRepository rep) throws Exception {

		for (RepositoryDirectory directory : rep.getChildDirectories(parent.getDirectory())) {
			if (directory.isShowed()) {

				PortailRepositoryDirectory td = new PortailRepositoryDirectory(directory);
				parent.addItem(td);

				for (RepositoryItem di : rep.getItems(directory)) {
					if (di.isDisplay()) {
						PortailRepositoryItem item = buildItem(sock, td, di);
						if (item != null && item.getType() != IRepositoryApi.FAV_TYPE) {
							td.addItem(item);
						}
					}
				}
				buildChildsByType(td, sock, rep);
			}
		}
	}

	public List<Integer> getGroupsAvailableForHisto(int itemId) throws ServiceException {
		String vanillaUrl = "";
		try {
			vanillaUrl = session.getVanillaRuntimeUrl();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get Vanilla URL: " + e.getMessage());
		}

		IVanillaContext context = new BaseVanillaContext(vanillaUrl, session.getUser().getLogin(), session.getUser().getPassword());

		ReportHistoricComponent comp = new RemoteHistoricReportComponent(context);

		try {
			IObjectIdentifier identifier = new ObjectIdentifier(session.getCurrentRepository().getId(), itemId);
			return comp.getGroupsAuthorizedByItemId(identifier);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceException("Unable to get the available groups for historic of item with id = " + itemId);
		}
	}

	public PortailRepositoryItem buildItem(IRepositoryApi sock, IRepositoryObject parent, RepositoryItem di) throws Exception {
		if (di.getType() == IRepositoryApi.GED_TYPE || di.getType() == IRepositoryApi.FD_DICO_TYPE || di.getType() == IRepositoryApi.FAV_TYPE 
				|| di.getType() == IRepositoryApi.FMDT_DRILLER_TYPE || di.getType() == IRepositoryApi.FMDT_CHART_TYPE || di.getType() == IRepositoryApi.PROJECTION_TYPE || di.getType() == -1) {
			// ignore it, and dump it
		}
		else {

			PortailRepositoryItem ti = null;
			if (di.getType() == IRepositoryApi.FASD_TYPE) {
				List<String> v = RepositoryImpl.loadModel(di, sock);

				ti = new PortailItemFasd(di, IRepositoryApi.TYPES_NAMES[di.getType()]);
				buildCubes((PortailItemFasd) ti, v, sock);
			}
			else if (di.getType() == IRepositoryApi.REPORTS_GROUP) {
				List<Integer> v = loadGroupModel(di, sock);

				ti = new PortailItemReportsGroup(di, IRepositoryApi.TYPES_NAMES[di.getType()]);
				buildReports((PortailItemReportsGroup) ti, v, sock);
			}
			else if (di.getType() == IRepositoryApi.FMDT_TYPE) {
				ti = new PortailItemFmdt(di, IRepositoryApi.TYPES_NAMES[di.getType()]);
				buildFmdt((PortailItemFmdt) ti, sock);
			}
			else if (di.getType() == IRepositoryApi.KPI_THEME) {
				ti = new PortailItemKpiTheme(di, IRepositoryApi.TYPES_NAMES[di.getType()]);
				buildKpiTheme((PortailItemKpiTheme) ti, sock);
			}
			else {
				String typeName = IRepositoryApi.TYPES_NAMES[di.getType()];
				if(typeName.equals(IRepositoryApi.TYPES_NAMES[IRepositoryApi.CUST_TYPE])) {
					typeName = IRepositoryApi.SUBTYPES_NAMES[di.getSubtype()];
				}
				
				ti = new PortailRepositoryItem(di, typeName);
			}

			ti.setOwned(di.getOwnerId().equals(session.getUser().getId()));

			try {
				List<Integer> groupAvailableForHisto = getGroupsAvailableForHisto(di.getId());
				if (groupAvailableForHisto != null) {
					for (Integer grId : groupAvailableForHisto) {
						if (grId.equals(session.getCurrentGroup().getId())) {
							ti.setViewable(true);
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			return ti;
		}

		return null;
	}
	
	/**
	 * pourquoi sync'? parceque!
	 * @param group
	 * @param fasdItem
	 * @param location
	 * @param sock
	 * @return
	 * @throws IOException
	 */
	synchronized private static List<String> loadModel(RepositoryItem fasdItem, IRepositoryApi sock) throws Exception {
		return sock.getRepositoryService().getCubeNames(fasdItem);
	}

	private List<Integer> loadGroupModel(RepositoryItem di, IRepositoryApi sock) {
		String itemXML = null;
		try {
			itemXML = session.getRepositoryConnection().getRepositoryService().loadModel(di);
		} catch (Exception e) {
			e.printStackTrace();
		}

		XStream xstream = new XStream();
		ReportsGroup report = (ReportsGroup) xstream.fromXML(itemXML);
		
		return report.getReports();
	}

	private void buildReports(PortailItemReportsGroup ti, List<Integer> reports, IRepositoryApi sock) {
		for (Integer reportId : reports) {
			try {
				RepositoryItem report = sock.getRepositoryService().getDirectoryItem(reportId);;

				String typeName = IRepositoryApi.TYPES_NAMES[report.getType()];
				if(typeName.equals(IRepositoryApi.TYPES_NAMES[IRepositoryApi.CUST_TYPE])) {
					typeName = IRepositoryApi.SUBTYPES_NAMES[report.getSubtype()];
				}
				
				PortailRepositoryItem itemReport = new PortailRepositoryItem(report, typeName);
				ti.addReport(itemReport);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void buildCubes(PortailItemFasd itemFasd, List<String> cubes, IRepositoryApi sock) {
		for (String name : cubes) {
			try {
				List<RepositoryItem> views = sock.getRepositoryService().getCubeViews(name, itemFasd.getItem());

				RepositoryItem itemCube = new RepositoryItem();
				itemCube.setItemName(name);

				PortailItemCube cube = new PortailItemCube(itemCube, IRepositoryApi.TYPES_NAMES[IRepositoryApi.FASD_TYPE]);
				for (RepositoryItem view : views) {
					cube.addView(new PortailItemCubeView(view, IRepositoryApi.TYPES_NAMES[IRepositoryApi.FAV_TYPE]));
				}

				itemFasd.addCube(cube);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void buildFmdt(PortailItemFmdt itemFmdt, IRepositoryApi sock) {
//		try {
//			List<RepositoryItem> drillers = sock.getRepositoryService().getFmdtDrillers(itemFmdt.getItem());
//			
//
//			RepositoryItem i = null;
//			try {
//				i = sock.getRepositoryService().getDirectoryItem(itemFmdt.getId());
//			} catch (Exception e) {
//				e.printStackTrace();
//				return ;
//			}
//
//			String result = null;
//			try {
//				result = sock.getRepositoryService().loadModel(i);
//			} catch (Exception e) {
//				e.printStackTrace();
//				return ;
//			}
//
//			List<IBusinessModel> bModels = null;
//			try {
//				bModels = MetaDataReader.read(session.getCurrentGroup().getName(), IOUtils.toInputStream(result, "UTF-8"), sock, false);
//			} catch (Exception e) {
//				e.printStackTrace();
//				return ;
//			}
//
//			
//			if (bModels != null) {
//				for (IBusinessModel bmod : bModels) {
//					FmdtModel modelDTO = new FmdtModel();
//					modelDTO.setName(bmod.getName());
//
//					List<IBusinessPackage> packages = bmod.getBusinessPackages(session.getCurrentGroup().getName());
//					for (IBusinessPackage pack : packages) {
//						if (pack.isExplorable()) {
//							for(SavedQuery query: pack.getSavedQueries()){
//								RepositoryItem item = new RepositoryItem();
//								item.setId(itemFmdt.getId());
//								item.setItemName(query.getName());
//								item.setInternalVersion(pack.getName());
//								item.setPublicVersion(bmod.getName());
//								itemFmdt.addDriller(new PortailItemFmdtDriller(item, IRepositoryApi.TYPES_NAMES[IRepositoryApi.FMDT_DRILLER_TYPE]));
//							}
//						}
//					}
//				}
//			}
//			
//			/*
//			for (RepositoryItem driller : drillers) {
//				itemFmdt.addDriller(new PortailItemFmdtDriller(driller, IRepositoryApi.TYPES_NAMES[driller.getType()]));
//			}
//			*/
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
	}

	public void buildKpiTheme(PortailItemKpiTheme item, IRepositoryApi sock) {
		RepositoryItem i = null;
		try {
			i = sock.getRepositoryService().getDirectoryItem(item.getId());
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}

		String itemXml = null;
		try {
			itemXml = sock.getRepositoryService().loadModel(i);
		} catch (Exception e) {
			e.printStackTrace();
			return ;
		}


		XStream xstream = new XStream();
		KpiTheme report = (KpiTheme) xstream.fromXML(itemXml);
		
		item.setThemeId(report.getThemeId());
	}
}
