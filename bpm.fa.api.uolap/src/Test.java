

import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataSourceConnection;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.olap.ICubeView;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.OLAPStructure;

public class Test {
	public static void main(String[] args) throws Exception{
		FAModel faModel = new DigesterFasd("C:\\Users\\Slhoka\\Desktop\\Test_Plugin_Visualisateur\\fasd_lca.fasd").getFAModel();
		System.out.println("**********************************************");
		OLAPCube faCube = null;
		DataSourceConnection con = null;
		List<ICubeView> lstCubeViews = null;
		String path = "C:\\Users\\Slhoka\\Desktop\\";
		
		for(ICube c : faModel.getCubes()){
			System.out.println(c.getName());
			lstCubeViews = c.getCubeViews();
			if (c instanceof org.fasd.olap.OLAPCube){
				for(DataSourceConnection lstCon : ((org.fasd.olap.OLAPCube)c).getDataSource().getDrivers()){
					System.out.println(lstCon.getId());
					if(lstCon.getId().equals("a6")){
						con = lstCon;
					}
				}
			}
		}
		
		OLAPStructure struct = null;//new MondrianStructure(faModel, new ArrayList<Drill>(), con);
//		boolean bl = false;
//		if(faModel.getOLAPSchema().findCubeNamed("alternate") != null){
//			bl = true;
//		}
//		System.out.println("FAModel Id : " + bl);
//		System.out.println("xmlMandrian : " + xmlMandrian);
//		System.out.println("FaModel : " + faModel.getOLAPSchema().getXML());
		System.out.println("**********************************************");

		try {
			faCube = null;//struct.createCube(path, "alternate", lstCubeViews);
		} catch (Exception e1) {
			
			e1.printStackTrace();
		}
		
		OLAPResult res = null;
		
		res = faCube.doQuery();
		
		dump(res.getRaw());
		
		Dimension dim = null;
		for(Dimension d : faCube.getDimensions()){
			if (d.getName().toLowerCase().contains("time")){
//				dump(faCube, d);
				dim = d;
			}
			
		}
		
	}
	
	private static ItemElement getElement(OLAPResult result, String uname){
		for(ArrayList<Item> row : result.getRaw()){
			for(Item i : row){
				if (i instanceof ItemElement){
					if (uname.equals(((ItemElement)i).getDataMember().getUniqueName())){
						return (ItemElement)i;
					}
				}
			}
		}
		return null;
	}

	private static void dump(ArrayList<ArrayList<Item>> raw){
		for(ArrayList<Item> row : raw){
			for(Item i : row){
				System.out.print("\t|" + i.getLabel() + "\t|");
			}
			System.out.println();
		}
	
	}

	public static void dump(OLAPCube cube, Dimension dim) {
		StringBuffer buf = new StringBuffer();
		buf.append("Dimension : " + dim.getName() + "\n");
		for(Hierarchy h : dim.getHierarchies()){
			buf.append("\tHierarchy : " + h.getName() + "\n");
			
			OLAPMember curMember = h.getDefaultMember();
			
			if (curMember != null){
				dumpMember(cube, h, curMember, buf);
			}
			else{
				for(OLAPMember m :  h.getFirstLevel().getMembers()){
					dumpMember(cube, h, m, buf);
				}
			}
			
		}
		System.out.println(buf.toString());
	}

	private static void dumpMember(OLAPCube cube, Hierarchy h, OLAPMember mb, StringBuffer buf){
		buf.append(mb.getUniqueName() + "\n");
		if (!mb.isSynchro()){
			try {
				cube.addChilds(mb, h);
			} catch (Exception e) {
				
				e.printStackTrace();
			}
		}
		for(Object m : mb.getMembers()){
			dumpMember(cube, h, (OLAPMember)m, buf);
		}
	}

}
