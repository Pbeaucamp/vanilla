package bpm.faweb.server.tools;

import java.util.Collection;
import java.util.List;

import org.fasd.olap.DrillCube;

import bpm.fa.api.olap.OLAPCube;
import bpm.faweb.server.FaWebServiceImpl;
import bpm.faweb.server.beans.InfosOlap;
import bpm.faweb.server.security.FaWebSession;
import bpm.faweb.shared.infoscube.InfosReport;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.GroupProjection;
import bpm.vanilla.platform.core.impl.ObjectIdentifier;

public class RecupInfosCube {

	public static InfosReport openNew(int keySession, String cubeName, String group, FaWebSession session, FaWebServiceImpl parent, InfosOlap infos) {

		try {
			loadCube(keySession, cubeName, group, session, infos);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		InfosReport curr = infos.getInfosReport();
		curr.setCubeName(cubeName);

		curr.setMeasuresGroup(RecupMes.recupMes(infos.getCube(), session, infos));
		curr.setGrid(parent.getTable(infos));
		curr.setDims(RecupDim.recupDim(keySession, infos.getCube(), session));

		return curr;
	}

	public static InfosReport openCubeFromPortal(int keySession, FaWebSession session, int fasdItemId, String cubeName, String location, FaWebServiceImpl parent, InfosOlap infos, String dimName, String memberName) throws Exception {

		infos.setSelectedFasd(session.getRepository().getItem(fasdItemId));
		infos.setCubeName(cubeName);

		FaWebActions.writeRepository(keySession, session.getCurrentGroup(), fasdItemId, session, location, infos);
		FaWebActions.setCube(keySession, cubeName, session, infos, dimName, memberName);

		InfosReport curr = infos.getInfosReport();
		curr.setCubeName(cubeName);
		curr.setMeasuresGroup(RecupMes.recupMes(infos.getCube(), session, infos));
		curr.setGrid(parent.getTable(infos));
		curr.setDims(RecupDim.recupDim(keySession, infos.getCube(), session));

		return curr;
	}

	private static boolean loadCube(int keySession, String cubeName, String group, FaWebSession session, InfosOlap infos) throws Exception {
		if (infos != null && infos.getCube() != null) {
			try {
				infos.getCube().close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		Group gr = session.getCurrentGroup();

		IRuntimeContext ctx = new RuntimeContext(session.getUser().getLogin(), session.getUser().getPassword(), gr.getName(), gr.getId());

		IObjectIdentifier identifier = new ObjectIdentifier(session.getCurrentRepository().getId(), session.getInfosOlap(keySession).getSelectedFasd().getId());

		OLAPCube temp_cube = session.getFaApiHelper(keySession).getCube(identifier, ctx, cubeName);

		//XXX a trick to create drills on cubes from the same schema
		try {
			Collection<String> names = session.getFaApiHelper(keySession).getCubeNames(identifier, ctx);
			for(String n : names) {
				if(!n.equals(cubeName)) {
					DrillCube d = new DrillCube(identifier.getDirectoryItemId(), n);
					d.setDrillName(n);
					temp_cube.getDrills().add(d);
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		boolean projectionAllowed = isProjectionAllowed(session, infos);

		infos.setProjectionAllowed(projectionAllowed);
		infos.setCube(temp_cube);
		infos.setCubeName(cubeName);
		infos.setRes(temp_cube.doQuery());

		return false;

	}

	public static boolean isProjectionAllowed(FaWebSession session, InfosOlap infos) throws Exception {
		List<GroupProjection> grpProjs = session.getVanillaApi().getVanillaSecurityManager().getGroupProjectionsByFasdId(infos.getSelectedFasd().getId());
		for (GroupProjection grp : grpProjs) {
			if (grp.getGroupId() == session.getCurrentGroup().getId()) {
				return true;
			}
		}

		return false;
	}
}
