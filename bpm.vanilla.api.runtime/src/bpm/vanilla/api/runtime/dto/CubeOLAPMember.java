package bpm.vanilla.api.runtime.dto;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import java.util.List;
import java.util.ArrayList;

public class CubeOLAPMember extends CubeTreeComponent {

	private String dimID;
	private CubeLevel cubeLvl;
	private int depth;

	public CubeOLAPMember(String dimId, OLAPMember mem, boolean showChildren) {
		super(mem.getUniqueName(), mem.getName(), "OLAPMember", null);

		dimID = dimId;
		if (showChildren) {
			children = loadChildren(mem);
		}

		if (mem.getLevel() != null) {
			cubeLvl = new CubeLevel(mem.getLevel());
			depth = mem.getLevelDepth() + 1;
		}
		else {
			cubeLvl = null;
			depth = mem.getLevelDepth();
		}

	}

	public CubeLevel getCubeLvl() {
		return cubeLvl;
	}

	public String getDimID() {
		return dimID;
	}

	public int getDepth() {
		return depth;
	}

	public static CubeOLAPMember loadCubeOLAPMember(OLAPCube cube, OLAPMember mem, boolean showChildren) throws Exception {
		for (Dimension dim : cube.getDimensions()) {
			for (Hierarchy hiera : dim.getHierarchies()) {
				if(mem.getHiera() == hiera) {
					return new CubeOLAPMember(dim.getUniqueName(),mem,showChildren);
				}
			}
		}
		
		return null;
	}
	
	public static CubeOLAPMember findCubeOLAPMember(OLAPCube cube, String memUname, boolean showChildren) throws Exception {

		for (Dimension dim : cube.getDimensions()) {
			for (Hierarchy hiera : dim.getHierarchies()) {
				OLAPMember defMember = hiera.getDefaultMember();
				if (memUname.startsWith(defMember.getUniqueName())) {
					String dimId = dim.getUniqueName();
					OLAPMember mem = findMemberRecursively(cube, defMember, memUname);
					if (mem != null) {
						return new CubeOLAPMember(dimId, mem, showChildren);
					}
					else {
						return null;
					}
				}
			}
		}

		return null;
	}

	public static List<CubeOLAPMember> findChildrenCubeOLAPMember(OLAPCube cube, String memUname, boolean showChildren) throws Exception {

		for (Dimension dim : cube.getDimensions()) {
			for (Hierarchy hiera : dim.getHierarchies()) {
				OLAPMember defMember = hiera.getDefaultMember();
				if (memUname.startsWith(defMember.getUniqueName())) {
					String dimId = dim.getUniqueName();
					OLAPMember mem = findMemberRecursively(cube, defMember, memUname);
					if (mem != null) {
						cube.addChilds(mem, hiera);

						if (mem.getMembers() != null) {
							List<CubeOLAPMember> cubememArray = new ArrayList<>();
							for (OLAPMember memChild : mem.getMembersVector()) {
								cubememArray.add(new CubeOLAPMember(dimId, memChild, showChildren));
							}
							return cubememArray;
						}

					}
					else {
						return null;
					}
				}
			}
		}

		return null;
	}

	private static OLAPMember findMemberRecursively(OLAPCube cube, OLAPMember currentMember, String memUname) throws Exception {

		if (memUname.equals(currentMember.getUniqueName())) {
			return currentMember;
		}

		if ((currentMember.getMembers() == null) || (currentMember.getMembers().isEmpty())) {
			cube.addChilds(currentMember, currentMember.getHiera());
		}

		if (currentMember.getMembers() != null) {
			for (OLAPMember childMember : currentMember.getMembersVector()) {
				if (memUname.startsWith(childMember.getUniqueName())) {
					return findMemberRecursively(cube, childMember, memUname);
				}
			}
		}

		return null;
	}

	public List<CubeTreeComponent> loadChildren(Object memObj) {

		if ((memObj != null) && (memObj instanceof OLAPMember)) {
			OLAPMember mem = (OLAPMember) memObj;

			if ((mem.getMembers() != null) && (mem.getMembers().size() > 0)) {
				List<CubeTreeComponent> memChilds = new ArrayList<>();

				for (OLAPMember child : mem.getMembersVector()) {
					memChilds.add(new CubeOLAPMember(dimID, child, true));
				}

				return memChilds;
			}

		}

		return null;

	}
}
