package bpm.faweb.server.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.unitedolap.UnitedOlapMember;
import bpm.faweb.server.security.FaWebSession;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.faweb.shared.infoscube.ItemHier;
import bpm.faweb.shared.infoscube.ItemOlapMember;

public class RecupDim {
	
	public static List<ItemDim> recupDim(int keySession, OLAPCube cube, FaWebSession session){	
		Collection<Dimension> dimensions = cube.getDimensions();
		List<ItemDim> lroot = new ArrayList<ItemDim>();
	
		for(Dimension d : dimensions) {
			ItemDim dim = new ItemDim(d.getName(), d.getUniqueName(), "dim", d.isGeolocalisable());
			dim.setDate(d.isDate());
			
			Collection<Hierarchy> hierarchies = d.getHierarchies();	
			for(Hierarchy h : hierarchies) {
				String hiera = h.getUniqueName();
				
				ItemHier hier = new ItemHier(h.getName(), h.getUniqueName(), hiera, false);
				hier.setAllMember(h.getDefaultMember().getCaption());
	
				List<Level> levels = h.getLevel();
				for(Level l : levels) {
					List<OLAPMember> itmb = l.getMembers();
					
					for(OLAPMember currm : itmb) {
						hier.addOlapChild(fillMembers(keySession, currm, hiera, session));   					
					}
					
					ItemHier lv = new ItemHier(l.getName(), l.getUniqueName(), h.getUniqueName(), false);
					hier.addChild(lv);
					
				}
				
				dim.addChild(hier);
			}
			
			lroot.add(dim);
		}
	
		return lroot;		
	}//loadDimensionModel

/**
 * recursively fill members
 */
	@SuppressWarnings("unchecked")
	private static ItemOlapMember fillMembers(int keySession, OLAPMember curr, String hiera, FaWebSession session) {
		Collection<OLAPMember> olapMembers = curr.getMembers();  	
		
		ItemOlapMember olapMember = new ItemOlapMember(curr.getCaption(), curr.getUniqueName(), hiera, false);
		session.addOlapMember(keySession, curr);

		for(OLAPMember currm : olapMembers) {
			session.addOlapMember(keySession, currm);
			
			olapMember.addOlapMemberChild(fillMembers(keySession, currm, hiera, session));
		}
		
		return olapMember;
	}//fillMembers	
	
	
	public static List<ItemOlapMember> addChilds(int keySession, ItemDim itemdim, OLAPCube cube, FaWebSession session) {
		List<ItemOlapMember> childs = null;
		OLAPMember themb = null;
		
		List<OLAPMember> olapMembers = session.getOlapMembers(keySession);
		for(OLAPMember olapMember : olapMembers){
			if(olapMember.getUniqueName().equals(itemdim.getUname())){
				themb = olapMember;
			}
		}
		
		if(themb == null) {
			themb = new UnitedOlapMember(itemdim.getName(), itemdim.getUname(), itemdim.getName());
		}
		
		if(themb != null){
			session.getOlapMembers(keySession).remove(themb);		
			String hiera = itemdim.getHiera();
			
			Collection<Dimension> dimensions = cube.getDimensions();		
			for(Dimension d : dimensions) {
				Collection<Hierarchy> hierarchies = d.getHierarchies();	
				for(Hierarchy h : hierarchies) {
					if (h.getUniqueName().equals(hiera)){
						try {
							if(themb.getHiera() == null) {
								if(itemdim.getUname() != null) {
									String u = itemdim.getUname();
									int l = u.split("\\]\\.\\[").length;
									int ll = l - 3;
									Level lvl = null;
									if(ll >= 0) {
										lvl = h.getLevel().get(ll);
									}
									themb = new UnitedOlapMember(itemdim.getName(), itemdim.getUname(), itemdim.getName(), h, d, lvl);
								}
								else {
									themb = new UnitedOlapMember(itemdim.getName(), itemdim.getUname(), itemdim.getName(), h, d, null);
								}
							}
							cube.addChilds(themb, h);
						} catch (Exception e) {
							e.printStackTrace();
						}
						childs = getSons(keySession, themb, session);
					}
				}
			}
		}
		return childs;
	}
	
	@SuppressWarnings("unchecked")
	public static List<ItemOlapMember> getSons(int keySession, OLAPMember mb, FaWebSession session){
		List<ItemOlapMember> childs = new ArrayList<ItemOlapMember>();

		Collection<OLAPMember> sons = mb.getMembers();
		for(OLAPMember currm : sons){
			session.addOlapMember(keySession, currm);
			try {
				childs.add(new ItemOlapMember(currm.getCaption(), currm.getUniqueName(), currm.getHierarchy(), false));
			}
			catch(Exception e) {
				childs.add(new ItemOlapMember(currm.getCaption(), currm.getUniqueName(), null, false));
			}
		}
		
		return childs;
	}
	
}
