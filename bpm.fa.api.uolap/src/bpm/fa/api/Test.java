package bpm.fa.api;

import java.util.ArrayList;

import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.utils.log.Log;

public class Test {

	/**
	 * @param args
	 * @throws Exception 
	 * @throws Exception 
	 */
	public static void main(String[] args) {
		try{
			
			
			Log.setup();
			Log.info("Entered Main class");
//			
//			FactoryRepositoryServicesStub.setAxisModulesPath("C:/BPM/eclipse/eclipse/" + "resources" + File.separator);
//			
//			IRepositoryConnection sock = FactoryRepository.getInstance().getConnection(FactoryRepository.AXIS_CLIENT, "http://localhost:8080/BIRepository", "system", "system", null, 1);

//			//IRepository ir =sock.getRepository(IRepositoryConnection.FASD_TYPE);
//			String dir = System.getProperty("user.dir");
//			RepositoryFactory.initialize();
//			Repository rep = RepositoryFactory.getInstance(dir, sock);
//			
//			System.out.println("**********************************************");
//			OLAPCube faCube = null;
			
//			for(RepositoryItem d : rep.getCubes("System")){
////				{
////					
////				}
////				if (d.getProvider().toLowerCase().contains("ic")){// && d.getProvider().toLowerCase().contains("xmla")){
//				if (d.getRepositoryItemId() == 131){
//					faCube = rep.getCubeUsingAlternateDataSource(d, "Default");
//					break;
//				}
//					
////				}
//				
//			}
			
			
			
//			String mdx = "SELECT {Hierarchize({[Location].[All  COUNTRY]})} on columns, {Hierarchize(Crossjoin (Union (Union ({[Time].[All  YEAR_ID]}, [Time].[All  YEAR_ID].[2003].children), [Time].[All  YEAR_ID].children), {[Measures].[PRICEEACH]}))} on rows	FROM [zouzouzou]";
//			String mdx2 = "SELECT {Hierarchize({[Location].[All  COUNTRY]})} on columns, {Hierarchize(Crossjoin (Union (Union ({[Time].[All  YEAR_ID]}, [Time].[All  YEAR_ID].[2003].children), [Time].[All  YEAR_ID].children), {[Measures].[QUANTITYORDERED]}))} on rows	FROM [zouzouzou]";

//			String mdxQuery = "SELECT {Hierarchize(Union ({[chiefs].[All  EMPLOYEENUMBER]}, [chiefs].[All  EMPLOYEENUMBER].children))} on columns, {Hierarchize(Crossjoin ({[territory].[All  COUNTRY]}, {[Measures].[OFFICECODE]}))} on rows FROM [officess2]";
//			
//			String mdxQuery2 = "SELECT {Hierarchize(Union (Union ({[chiefs].[All  EMPLOYEENUMBER]}, [chiefs].[All  EMPLOYEENUMBER].[1002].children), [chiefs].[All  EMPLOYEENUMBER].children))} on columns, {Hierarchize(Crossjoin ({[territory].[All  COUNTRY]}, {[Measures].[OFFICECODE]}))} on rows FROM [offices2]";
//			String mdxQuery3 = "SELECT {Hierarchize(Union (Union (Union ({[chiefs].[All  EMPLOYEENUMBER]}, [chiefs].[All  EMPLOYEENUMBER].[1002].[1056].children), [chiefs].[All  EMPLOYEENUMBER].[1002].children), Union ([chiefs].[All  EMPLOYEENUMBER].children, [chiefs].[All  EMPLOYEENUMBER].[1002].[1056].children)))} on columns,{Hierarchize(Crossjoin ({[territory].[All  COUNTRY]}, {[Measures].[OFFICECODE]}))} on rows FROM [offices2]";

//			String mdx = "SELECT {Hierarchize({[Ligne].[All  Ligne]})} on columns,{Hierarchize(Crossjoin (Union (Union (Union ({[Date_Year].[All  Date_Year]}, [Date_Year].[All  Date_Year].[2010.0].[2.0].children), [Date_Year].[All  Date_Year].[2010.0].children), Union ([Date_Year].[All  Date_Year].children, [Date_Year].[All  Date_Year].[2010.0].[2.0].children)), Union ({[Measures].[Vente_last]}, {[Measures].[Vente_sum]})))} on rows FROM [olap_last]";
//			
//			OLAPResult res = null;// faCube.doQuery();
//			
//			String mdx = "SELECT"+
//						"{Hierarchize(Union ({[Product.ProductHierarchy].[All Product]}, [Product.ProductHierarchy].[All Product].children))} on columns,"+
//						"{Hierarchize(Crossjoin (Union (Union ({[Time.TimeHierarchy].[All Time]}, [Time.TimeHierarchy].[All Time].[2003].children), [Time.TimeHierarchy].[All Time].children), {[Measures].[T"+
//						"OTALPRICE]}))} on rows"+
//						" FROM [unCube]";
//			
//			Date deb = new Date();
//			
//			res = faCube.doQuery(mdx);
//			
//			Date fin = new Date();
//			
//			System.out.println("fa execution time : " + (fin.getTime() - deb.getTime()));
			
//			for(Dimension d : ((MondrianCube)faCube).getDimensions()){
//				for(Hierarchy h : d.getHierarchies()){
//					System.out.println(h.getDefaultMember().getUniqueName());
//					
//					((MondrianCube)faCube).addChilds(h.getDefaultMember(), h);
//					
//					for(Object o : h.getDefaultMember().getMembers()){
//						System.out.println(((OLAPMember)o).getUniqueName());
//						((MondrianCube)faCube).addChilds((OLAPMember)o, h);
//						
//						
//						
//					}
//				}
//			}
//			res = faCube.doQuery();
////			res = faCube.doQuery(mdx);
//			dump(res.getRaw());
////			res = faCube.doQuery("SELECT {Hierarchize({[Product].[Prod]})} on columns,{Hierarchize(Crossjoin (Union (Union ({[Geography].[Economy]}, [Geography].[Economy].[Partnership].&amp;[NAFTA].children), [Geography].[Economy].[ALL].children), {[Measures].[Amount]}))} on rows FROM [Sales]");
////			res = faCube.doQuery("SELECT{Hierarchize({[Product].[Prod]})} on columns,{Hierarchize(Crossjoin ({[Geography].[Economy]}, {[Measures].[Amount]}))} on rows FROM [Sales]");
////			dump(res.getRaw());
//			
//			Dimension dim = null;
//			for(Dimension d : faCube.getDimensions()){
//				if (d.getName().toLowerCase().contains("time")){
////					dump(faCube, d);
//					dim = d;
//				}
//				
//			}
//			ItemElement ie = new ItemElement(dim.getHierarchies().iterator().next().getUniqueName(), false, false);
//			faCube.add(ie);
//			
//			
//			res = faCube.doQuery();
//			dump(res.getRaw());
//			
//			faCube.drilldown(ie);
//			res = faCube.doQuery();
//			dump(res.getRaw());
//			
//			
//			faCube.remove(ie);
//			res = faCube.doQuery();
//			dump(res.getRaw());
			
			
//			res = faCube.doQuery(mdx);
//			dump(res.getRaw());
//			
//			res = faCube.doQuery(mdx2);
			
//			((MondrianCube)faCube).getMdx().measureAsLast(true, "QUANTITYORDERED", "([Measures].[QUANTITYORDERED],ClosingPeriod ( [Time].[TIME_ID], [Time].CurrentMember))");
//			faCube.setMeasureUseOnlyLastLevelMember("QUANTITYORDERED", null, "Time");
//			res = faCube.doQuery();
//			dump(res.getRaw());
			
//			((MondrianCube)faCube).getMdx().measureAsLast(false, "QUANTITYORDERED", "([Measures].[QUANTITYORDERED],ClosingPeriod ( [Time].[TIME_ID], [Time].CurrentMember))");
//			/dump(res.getRaw());
//			ItemElement i = getElement(res, "[chiefs].[All  EMPLOYEENUMBER]");
//			faCube.drilldown(i);
//			res = faCube.doQuery(mdxQuery3);
//			dump(res.getRaw());
			
//			res = faCube.doQuery(mdxQuery2);
//			dump(res.getRaw());
//			i = getElement(res, "[chiefs].[All  EMPLOYEENUMBER].[1002]");
//			faCube.drilldown(i);
//			res = faCube.doQuery();
//			
//			i = getElement(res, "[chiefs].[All  EMPLOYEENUMBER].[1002].[1056]");
//			faCube.drilldown(i);
//			String mdx = ((MondrianCube)faCube).getMdx().getMDX();
//			res = faCube.doQuery();
//			dump(res.getRaw());
//			
//			System.out.println(mdx.equals(mdxQuery3));
//			
//			res = faCube.doQuery(mdxQuery3);
//			dump(res.getRaw());
//			res = faCube.doQuery(mdxQuery3);
//			dump(res.getRaw());
//			res = faCube.doQuery(mdxQuery3);
//			dump(res.getRaw());
			

		}catch(Exception e){
		e.printStackTrace();
		}
//		String mdx = "SELECT "+
//		"{Hierarchize(Union ({[Customer Dimension].[All  Customers]}, [Customer Dimension].[All  Customers].children))} on columns,"+
//		"{Hierarchize(Crossjoin (Union ({[Time Dimension].[All  Times]}, TopCount([Time Dimension].[All  Times].children,1,[Measures].[Sales])), {[Measures].[Sales]}))} on rows"+
//		" FROM [Sales]";
//		
//		String mdx2 = "SELECT "+
//		"{Hierarchize(Union ({[Customer Dimension].[All  Customers]}, [Customer Dimension].[All  Customers].children))} on columns,"+
//		"{Hierarchize(Crossjoin (Union ({[Time Dimension].[All  Times]}, TopCount([Time Dimension].[All  Times].children,2,[Measures].[Sales])), {[Measures].[Sales]}))} on rows"+
//		" FROM [Sales]";
////		String mdx2 = "SELECT {Hierarchize({[Location].[All  COUNTRY]})} on columns, {Hierarchize(Crossjoin (Union (Union ({[Time].[All  YEAR_ID]}, [Time].[All  YEAR_ID].[2003].children), [Time].[All  YEAR_ID].children), {[Measures].[QUANTITYORDERED]}))} on rows	FROM [zouzouzou]";
//
////		String mdxQuery = "SELECT {Hierarchize(Union ({[chiefs].[All  EMPLOYEENUMBER]}, [chiefs].[All  EMPLOYEENUMBER].children))} on columns, {Hierarchize(Crossjoin ({[territory].[All  COUNTRY]}, {[Measures].[OFFICECODE]}))} on rows FROM [officess2]";
////		
////		String mdxQuery2 = "SELECT {Hierarchize(Union (Union ({[chiefs].[All  EMPLOYEENUMBER]}, [chiefs].[All  EMPLOYEENUMBER].[1002].children), [chiefs].[All  EMPLOYEENUMBER].children))} on columns, {Hierarchize(Crossjoin ({[territory].[All  COUNTRY]}, {[Measures].[OFFICECODE]}))} on rows FROM [offices2]";
////		String mdxQuery3 = "SELECT {Hierarchize(Union (Union (Union ({[chiefs].[All  EMPLOYEENUMBER]}, [chiefs].[All  EMPLOYEENUMBER].[1002].[1056].children), [chiefs].[All  EMPLOYEENUMBER].[1002].children), Union ([chiefs].[All  EMPLOYEENUMBER].children, [chiefs].[All  EMPLOYEENUMBER].[1002].[1056].children)))} on columns,{Hierarchize(Crossjoin ({[territory].[All  COUNTRY]}, {[Measures].[OFFICECODE]}))} on rows FROM [offices2]";
//		
//		
//		
//		OLAPResult res = null;// faCube.doQuery();
//		
////		res = faCube.doQuery();
////		res = faCube.doQuery(mdx);
////		dump(res.getRaw());
//		
//		
//		System.out.println("-------------------------------executing the first request------------------------------");
//		
//		res = faCube.doQuery(mdx);
//		dump(res.getRaw());
//		
//		System.out.println("-------------------------------executing the second request------------------------------");
//		
//		res = faCube.doQuery(mdx2);
//		dump(res.getRaw());

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
