package org.fasd.utils.trees;

import java.io.FileNotFoundException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.fasd.datasource.DataSourceConnection;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;

import xmldesigner.internal.DimensionTree;
import xmldesigner.parse.XMLParser;
import xmldesigner.parse.item.DataXML;
import xmldesigner.xpath.Xpath;
import bpm.connection.manager.connection.ConnectionManager;
import bpm.connection.manager.connection.jdbc.VanillaJdbcConnection;
import bpm.connection.manager.connection.jdbc.VanillaPreparedStatement;

public class TreeMember extends TreeParent {
	private OLAPLevel lvl;

	public TreeMember(String name, OLAPLevel lvl) {
		super(name);
		this.lvl = lvl;
	}
	
	public OLAPLevel getLevel(){
		return lvl;
	}
	
	
	public String getWhere(){
		if (lvl.getItem().getParent().getDataSource().getDriver().getType().equals("XML")){
			String s = "where ($i//"+ lvl.getItem().getOrigin() + "=\"" + getName() +"\")";
		
			TreeParent tp = getParent();

			while (tp instanceof TreeMember){
				s += " and ($i//" + ((TreeMember)tp).getLevel().getItem().getOrigin();
				s += "=\"" + tp.getName() + "\")";
				tp = tp.getParent();
			}
			
			return s+"\n";
		}
		
		String s = " WHERE " + lvl.getItem().getOrigin() + "='" + getName().replace("'", "''") + "'" ;
		
		TreeParent tp = getParent();

		while (tp instanceof TreeMember){
			s += " AND " + ((TreeMember)tp).getLevel().getItem().getOrigin();
			s += "='" + tp.getName() + "'";
			tp = tp.getParent();
		}
		
		return s;
	}


	public List<String> getValues(String sql){
		System.out.println(sql);
		List<String> result = new ArrayList<String>();
		
		try {
			if (lvl.getItem().getParent().getDataSource().getDriver().getType().equals("XML")){
				DataSourceConnection sock = lvl.getItem().getParent().getDataSource().getDriver();
				
				String url = "file:///" + sock.getTransUrl();
				
				XMLParser parser = new XMLParser(url);
				parser.parser();
				DataXML dtd = parser.getDataXML();
				Xpath xpath = new Xpath(sock.getTransUrl(), dtd.getRoot().getElement(0).getName());
							
				DimensionTree model = new DimensionTree(dtd);
				xmldesigner.internal.TreeParent rr = model.createModel();
				xpath.setListHiera(createHiera(rr));
				
				for(OLAPLevel l : lvl.getParent().getLevels()){
					if (l.getNb()<= lvl.getNb() + 1){
						xpath.addCol(l.getItem().getOrigin());
					}
				}
				
				
				
				try {
					System.out.println("TREEMEMBER :\n" + sql);
					xpath.executeXquery(sql);
					xpath.modifieSortie();

					XMLParser pars = new XMLParser("Temp/sortie.xml");
					pars.parser();
					DataXML dtd2 = pars.getDataXML();
					List<String> buf = xpath.listXquery(dtd2.getRoot());
					
					for(int i = 1; i<=buf.size(); i++){
						if (i % (lvl.getNb() + 2) == 0){
							boolean contained = false;
							for(String s : result){
								if (s.equals(buf.get(i - 1))){
									contained = true;
									break;
								}
							}
							if (!contained)
								result.add(buf.get(i - 1));
						}
					}
					
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}

			}
			else{
				lvl.getItem().getParent().getDataSource().getDriver().connectAll();
				
				VanillaJdbcConnection con = lvl.getItem().getParent().getDataSource().getDriver().getConnection().getConnection();
				VanillaPreparedStatement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery(sql);
				
				while(rs.next()){
					if( rs.getString(1) != null)
						result.add(rs.getString(1));
				}
				rs.close();
				stmt.close();
				ConnectionManager.getInstance().returnJdbcConnection(con);
			}
			
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		} catch (Exception e) {
			
			e.printStackTrace();
		}

		return result;
	}

	public void createChilds(){
		if (lvl.getNb() < lvl.getParent().getLevels().size() - 1){
			
			
			OLAPLevel next = lvl.getParent().getLevels().get(lvl.getNb() + 1);
			String where = this.getWhere();
			String sql = "";
			if (next.getItem() != null){
				if (next.getItem().getParent().getDataSource().getDriver().getType().equals("XML")){
					DataSourceConnection sock = next.getItem().getParent().getDataSource().getDriver();
					
					String url = "file:///" + sock.getTransUrl();
					
					XMLParser parser = new XMLParser(url);
					parser.parser();
					DataXML dtd = parser.getDataXML();

					sql = "for $i in doc('" + sock.getTransUrl().replace("\\", "/") + "')//" + dtd.getRoot().getElement(0).getName() + "\n";
					sql += getWhere();
					sql += "return\n";
					sql += "<" + dtd.getRoot().getElement(0).getName() + ">\n";
					sql += "{$i//" + next.getItem().getOrigin()  +"}\n";
					sql += "</" + dtd.getRoot().getElement(0).getName() + ">\n";

				}
				else{
					sql = "SELECT DISTINCT " + next.getItem().getOrigin() + " FROM " + next.getItem().getParent().getPhysicalName();
					if (!where.endsWith(" WHERE ")){
						sql += where;
					}
				}
			}
				
			else{
				if (next.getItem().getParent().getDataSource().getDriver().getType().equals("XML")){
					DataSourceConnection sock = next.getItem().getParent().getDataSource().getDriver();
					
					String url = "file:///" + sock.getTransUrl();
					
					XMLParser parser = new XMLParser(url);
					parser.parser();
					DataXML dtd = parser.getDataXML();

					sql = "for $i in doc('" + sock.getTransUrl().replace("\\", "/") + "')//" + next.getItem().getOrigin() + "\n";
					sql += "return\n";
					sql += "<" + dtd.getRoot().getElement(0).getName() + ">\n";
					sql += "{$i}\n";
					sql += "</" + dtd.getRoot().getElement(0).getName() + ">\n";

				}
				else{
					sql = "SELECT DISTINCT " + next.getSql() + " FROM " + next.getTableId();
				}
				
			}
				
			
			
			for(String s : getValues(sql)){
				System.out.println(s);
				TreeMember tm = new TreeMember(s, next);
				this.addChild(tm);
				tm.createChilds();
			}
			
		}
		else if (lvl.isClosureNeeded()){
			String sql = "SELECT DISTINCT " + lvl.getItem().getOrigin() + " FROM " + lvl.getItem().getParent().getPhysicalName();
			sql += " WHERE " + lvl.getClosureParentCol().getName() +"=" + getName();
			
			for(String s : getValues(sql)){
				System.out.println(s);
				TreeMember tm = new TreeMember(s, lvl);
				this.addChild(tm);
				tm.createChilds();
			}
		}
		
	}

	public static TreeParent createModel(OLAPDimension dim) {
		TreeParent root = new TreeParent("");
		
		TreeDim td = new TreeDim(dim);
		
		for(OLAPHierarchy h : dim.getHierarchies()){
			TreeHierarchy th = new TreeHierarchy(h);
			
			if (h.getLevels().size()>0){
				OLAPLevel l0 = h.getLevels().get(0);
				List<String> list = new ArrayList<String>();
				
				try {
					l0.getItem().getParent().getDataSource().getDriver().connectAll();
				
					VanillaJdbcConnection con = l0.getItem().getParent().getDataSource().getDriver().getConnection().getConnection();
					VanillaPreparedStatement stmt = con.createStatement();
					ResultSet rs = stmt.executeQuery("SELECT DISTINCT " + l0.getItem().getOrigin() + " FROM " + l0.getItem().getParent().getPhysicalName());
					
					while(rs.next()){
						if( rs.getString(1) != null)
							list.add(rs.getString(1));
					}
					rs.close();
					stmt.close();
					ConnectionManager.getInstance().returnJdbcConnection(con);
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
				} catch (Exception e) {
					
					e.printStackTrace();
				}
				for(String s : list){
					TreeMember tm = new TreeMember(s, l0);
					th.addChild(tm);
					tm.createChilds();
				}

			}

			td.addChild(th);
		}
		root.addChild(td);
		
		return root;
	}

	/**
	 * Creation a list hierarchic of tags of file xml
	 * @param t
	 * @return
	 */
	private ArrayList<String> createHiera(xmldesigner.internal.TreeParent t)
	{
		ArrayList<String> list = new ArrayList<String>();
		list.add(t.getName());
		for(int i=0;i<t.getChildren().length;i++)
		{
			add(list,createHiera((xmldesigner.internal.TreeParent)t.getChildren(i)));
		}
		return list;
	}
	
	private List<String> add(List<String> list1,List<String> list2)
	{
		for(int i=0;i<list2.size();i++)
		{
			list1.add(list2.get(i));
		}
		return list1;
	}
}
