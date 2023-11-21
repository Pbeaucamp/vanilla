package bpm.fa.api.olap.xmla;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;


import org.apache.commons.io.IOUtils;
import org.fasd.datasource.IConnection;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.olap.ICubeView;
import org.fasd.xmla.XMLADataSourceConnection;
import org.xml.sax.SAXException;

import bpm.fa.api.connection.XMLAConnection;
import bpm.fa.api.item.Item;
import bpm.fa.api.item.ItemDependent;
import bpm.fa.api.item.ItemElement;
import bpm.fa.api.item.ItemNull;
import bpm.fa.api.item.ItemValue;
import bpm.fa.api.item.Parameter;
import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.Measure;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.olap.OLAPQuery;
import bpm.fa.api.olap.OLAPResult;
import bpm.fa.api.olap.OLAPStructure;
import bpm.fa.api.olap.helper.StructureHelper;
import bpm.fa.api.olap.xmla.parse.XMLAAxis;
import bpm.fa.api.olap.xmla.parse.XMLAResult;
import bpm.fa.api.olap.xmla.parse.XMLATuple;
import bpm.fa.api.utils.copy.DeepCopy;
import bpm.fa.api.utils.log.Log;
import bpm.united.olap.api.runtime.IRuntimeContext;

public class XMLAStructure implements OLAPStructure {

	private boolean isConnected = false;
	
	private ArrayList<Dimension> dims;
	private ArrayList<MeasureGroup> mes;
	private boolean showTotals;
	
	private static XMLAConnection sock;
	
	private static URL url;
	
	private List<String> unames;
	private String cubeName ;
	private FAModel fasdModel;
	/**
	 * DO NOT USE, singleton
	 * @param itm {@link RepositoryItem}
	 */
	public XMLAStructure(FAModel fasdModel, String cubeName ) {
		this.cubeName = cubeName;
		this.fasdModel = fasdModel;
	}
	
	

	public ArrayList<Dimension> getDimensions() {
		return dims;
	}

	public ArrayList<MeasureGroup> getMeasures() {
		return mes;
	}
	@Override
	public boolean addChilds(OLAPMember memb, IRuntimeContext ctx) throws Exception {
		return addMemberChilds(memb);
	}
	
	@Override
	public OLAPCube createCube(String path, IRuntimeContext ctx) throws Exception {
		DigesterFasd dig = new DigesterFasd(IOUtils.toInputStream(path,"UTF-8"));
		sock = new XMLAConnection();
		IConnection con = dig.getFAModel().getSchema().getICubes().get(0).getConnection();
		
		if (!(con instanceof XMLADataSourceConnection)) {
			throw new Exception("did'nt find an XMLA Cube definition");
		}
		
		XMLADataSourceConnection c = (XMLADataSourceConnection) con;
		sock.setUrl(c.getUrl());
		sock.setDatasource(c.getSchema()); //XXX normal, ere: ludo fais mal son travail :p
		sock.setSchema(c.getCatalog());
		sock.setUser(c.getUser());
		sock.setPass(c.getPass());
		sock.setProvider(c.getType());
		sock.setCube(getCubeName());
	
		url = new URL(sock.getUrl());
		
		if (!isConnected){
			exploreCube();
			isConnected = true;
		}
		
//		exploreCube();
		
		//XXX open view also needs to be done
		XMLACube cube = new XMLACube(this);
		
		return cube;
	}
	
	private void cleanUniqueName(OLAPMember mb){
		if (mb.getUniqueName().contains(".[")){
			if (((XMLAMember)mb).getParent() != null){
				((XMLAMember)mb).setUname(((XMLAMember)mb).getParent().getUniqueName() + "." +mb.getUniqueName().substring(mb.getUniqueName().lastIndexOf("[")));
			}
			 
		}
		
		for(Object m : mb.getMembers()){
			cleanUniqueName((OLAPMember)m);
		}
	}

	/**
	 * Explore the cube, filling dimensions and Measures
	 * @throws Exception
	 */
	private void exploreCube() throws Exception {
		dims = XMLADiscover.discoverDimensions(url, sock);
		
		HashMap<Dimension, ArrayList<OLAPMember>> allMembers = new HashMap<Dimension, ArrayList<OLAPMember>>();
		for(Dimension d : dims){
			allMembers.put(d, new ArrayList<OLAPMember>());
		}
		
		
		for (Dimension dim : dims) {
			ArrayList<Hierarchy> hieras = XMLADiscover.discoverHierarchy(url, sock, dim);
			
			for (Hierarchy hiera : hieras) {
				Level lvl = null;
				ArrayList<Level> l = XMLADiscover.discoverLevel(url, sock, dim, hiera);
				Collections.sort(l, new Comparator<Level>() {
					public int compare(Level o1, Level o2) {
						Integer i1 = new Integer(o1.getLevelNumber());
						Integer i2 = new Integer(o2.getLevelNumber());
						return  i1.compareTo(i2);
					};
				});
			
				for(Level _l : l){
					hiera.addLevel(_l);
				}
				
				lvl = l.get(0);
				//XXX we only read first lvl and first member, we're looking for "all member"
				
				ArrayList<OLAPMember> mb = XMLADiscover.discoverMember(url, sock, dim, hiera, lvl);
				
				for(OLAPMember m : mb){
					cleanUniqueName(m);
					lvl.addMember(m);
				}
				
				
				
				
				//setup default member
				if (mb.size() == 1){
					hiera.setDefaultMember(mb.get(0));
				}
				
				
				
				
				dim.addHierarchy(hiera);
			}
		}
		
		
		mes = XMLADiscover.discoverMeasureGroup(url, sock);

		if (mes.isEmpty()) {
			mes.add(new MeasureGroup("", ""));
		}
		
		for (MeasureGroup gr : mes) {
			ArrayList<Measure> measures = XMLADiscover.discoverMeasure(url, sock, gr);
			
			for (Measure m : measures) {
				gr.addElement(m);
			}
		}
		
		if (sock.getProvider().equals(XMLAConnection.HyperionProvider)){
			for(Dimension d : dims){
				organize(d, allMembers.get(d));
			}
		}
	}
	
	
	private void organize(Dimension d, List<OLAPMember> list){
		for(Hierarchy h : d.getHierarchies()){
			for(int i = 0; i < h.getLevel().size(); i++){
				if ( i != h.getLevel().size() - 1){
					h.getLevel().get(i).setNextLevel(h.getLevel().get(i+1));
				}
			}
		}
		
	}
	
	

	private OLAPMember find(OLAPMember m ){
		Hierarchy hi = null;
		for(Dimension d : dims){
			for(Hierarchy h : d.getHierarchies()){
				if (h.getUniqueName().equals(((XMLAMember)m).getHieraName())){
					hi = h;
					break;
				}
			}
		}
		
		OLAPMember first = hi.getFirstLevel().getMembers().get(0);
		
		if (m.getUniqueName().equals(first.getUniqueName())){
			return first;
		}
		
		for(Object mb : first.getMembers()){
			if (((OLAPMember)mb).getUniqueName().endsWith(m.getUniqueName())){
				return (OLAPMember)mb;
			}
			OLAPMember r = findMember((OLAPMember)mb, m);
			if (r!= null){
				return r;
			}
		}
		
		return null;
	}
	
	private OLAPMember findMember(OLAPMember source, OLAPMember look){
		for(Object mb : source.getMembers()){
			if (((OLAPMember)mb).getUniqueName().endsWith(look.getUniqueName())){
				return (OLAPMember)mb;
			}
			OLAPMember r = findMember((OLAPMember)mb, look);
			if (r!= null){
				return r;
			}
		}
		return null;
	}
	
	
	
	private void clean(XMLAResult res){
		for(XMLAAxis ax : res.getAxes()){
			for(XMLATuple tu : ax.getTuples()){
				List<OLAPMember> map = new ArrayList<OLAPMember>();
				
				for(OLAPMember m : tu.getMembers()){
					map.add(find(m));
				}
				tu.getMembers().clear();
				tu.getMembers().addAll(map);
			}
		}
	}
	@Override
	public OLAPResult executeQuery(OLAPCube cube, String str, boolean showProps, IRuntimeContext ctx) {
		XMLAResult res = null;
		try {
			res = XMLAExecute.executeMDX(url, sock, str);
		} catch (Exception e) {
			Log.error("Error while executing XMLA request : \n" + str, e);
			return null;
		}
		
		
		if (sock.getProvider().equals(XMLAConnection.HyperionProvider)){
			clean(res);
		}
		
		
		XMLAAxis col = res.getAxes().get(0);
		XMLAAxis row = res.getAxes().get(1);

		//titles
		int trow = row.getNbTuple();

		//x -> col, y -> row
		int xfixed = row.getMaxMembers();
		int yfixed = col.getMaxMembers();

		int x = col.getNbTuple() + row.getMaxMembers();
		int y = row.getNbTuple() + col.getMaxMembers(); 

		ItemNull empty = new ItemNull();

		ArrayList<ArrayList<Item>> xtable = new ArrayList<ArrayList<Item>>();

		String last = "";

		//XXX fucked up result set!!!!
		res.brasseMerde();

		for (int i=0; i < yfixed; i++){
			ArrayList<Item> ytable = new ArrayList<Item>();

			//draw empty top left ones
			for (int k=0 ; k <  xfixed; k++) {
				ytable.add(empty);
			}

			for (int j = 0; j <  col.getNbTuple(); j++) {
				
				if (last.compareTo(col.getTuples().get(j).getMembers().get(i).getName()) != 0) {
					
					ItemElement item = new ItemElement(col.getTuples().get(j).getMembers().get(i), true, false);
					ytable.add(item);
				}
				else {
					ItemElement item = new ItemElement(col.getTuples().get(j).getMembers().get(i), true, true);
					ytable.add(item);
				}

				last = col.getTuples().get(j).getMembers().get(i).getName();
			}
			xtable.add(ytable);
		}

		String[] lasted = new String[row.getMaxMembers()];
		for (int i=0; i < lasted.length; i++) {
			lasted[i] = "";
		}
		//rows
		int tupleJ =-1;
		for (int i=0; i < row.getNbTuple(); i++) {
			tupleJ++;
			ArrayList<Item> ytable = new ArrayList<Item>();

			for (int j = 0; j <  row.getTuples().get(i).getNbMembers(); j++) {
				if (lasted[j].compareTo(row.getTuples().get(i).getMembers().get(j).getName()) != 0) {
					ItemElement item = new ItemElement(row.getTuples().get(i).getMembers().get(j), false, false);
					ytable.add(item);
				}
				else {
					ItemElement item = new ItemElement(row.getTuples().get(i).getMembers().get(j), false, true);
					ytable.add(item);
				}

				lasted[j] = row.getTuples().get(i).getMembers().get(j).getName();

			}

			//XXX latent bug or not? (i * col.getNbTuple()) // fixed i believed
			int tupleI =0;
			for (int k=0; k < col.getNbTuple(); k++) {
				try {
					if (k + (i * col.getNbTuple())>= res.getCells().size() || res.getCells().get(k + (i * col.getNbTuple())).getValue() == null){
//						ItemNu item = new ItemValue(res.getCells().get(k + (i * col.getNbTuple())).getFormattedValue(), res.getCells().get(k + (i * col.getNbTuple())).getFormattedValue(),null);
						
//						ytable.add(new ItemNull());
						ytable.add(new ItemValue("", null, ""));
					}
					else{
						ItemValue item = new ItemValue(res.getCells().get(k + (i * col.getNbTuple())).getFormattedValue(), res.getCells().get(k + (i * col.getNbTuple())).getValue(),null);
						item.addMemberDep(col.getTuples().get(k).getMembers().get(0));
						item.addMemberDep(row.getTuples().get(i).getMembers().get(0));
						item.addMemberDep(res.getAxes().get(2).getTuples().get(0).getMembers().get(0));
						ytable.add(item);
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			xtable.add(ytable);
		}

		return parseModel(xtable, xfixed, yfixed);
	}

	private OLAPResult parseModel(ArrayList<ArrayList<Item>> xtable, int xfix, int yfix) {

		for (int i=0; i < xtable.size(); i++) {//row
			ArrayList<Item> ytable = xtable.get(i);

			for (int j=0; j < ytable.size(); j++) {
				//COLS
				if (ytable.get(j) instanceof ItemElement && ((ItemElement)ytable.get(j)).isCol()) {
					ItemElement curr = (ItemElement) ytable.get(j);


					//exists, same hiera, < depth
					if (j + 1 < ytable.size() && isSubject(curr, ytable.get(j+1))) {
//							curr.getHiera().equals(((ItemFixed) ytable.get(j+1)).getHiera()) &&
//							curr.getDepth() < ((ItemFixed) ytable.get(j+1)).getDepth()) {
						//System.out.println("");
						//add sameline after us
						//List<ItemBase> tmp = ytable.subList(0, j);

						curr.setDrilled(true);

						ArrayList<Item> buf = new ArrayList<Item>();

						//fill empty
						for (int k=0; k <= j; k++) {
							//buf.add(new ItemValue("", null));
							buf.add(new ItemNull());
						}
						boolean aaa_left = false;
						//fill w/ rest of the line && clear line-1
						for (int k=j+1; k < ytable.size(); k++) {
							if (/*k + 1 < ytable.size() && */isSubject(curr, ytable.get(k))) {
								buf.add(ytable.get(k));
								//we set here the parent as drilled
								//lookup rule : row - 1 (== ytable), col - i (==k - i)
								//condition to setIsDrilled name != null
								int par;
								for (par = 1; ytable.get(k - par).getLabel().equals(""); par++) {
								}
								//((ItemFixed) ytable.get(k - par)).setIsDrilled(true);
								if (ytable.get(k - par) instanceof ItemElement)
									((ItemElement) ytable.get(k - par)).setDrilled(true);
//								ytable.set(k, new ItemValue("", null));
								ytable.set(k, new ItemNull());
								aaa_left = true;
							}
//							else if (k + 1 == ytable.size()) {
//								buf.add(ytable.get(k));
//								ytable.set(k, new ItemValue("", "", 0));
//							}
							else {
//								buf.add(new ItemValue("", null));
								buf.add(new ItemNull());
								if (aaa_left) {
									//((ItemFixed) ytable.get(k)).setIsDrilled(true);
									aaa_left = false;
								}
							}
						}

						xtable.add(i+1, buf);
						yfix++;
					}
				}//end if col
				//if curr fixed, if next exists, if next fixed if next isRow
				else if (ytable.get(j) instanceof ItemElement &&
						i + 1 < xtable.size() && xtable.get(i + 1).get(j) instanceof ItemElement &&
						((ItemElement)xtable.get(i + 1).get(j)).isRow()) {

					ItemElement curr = (ItemElement) ytable.get(j);

					if (isSubject(curr, xtable.get(i + 1).get(j))) {
						curr.setDrilled(true);

						for (int k=0; k < i; k++) {
//							xtable.get(k).add(j+1, new ItemNull("", null));
							xtable.get(k).add(j+1, new ItemNull());
						}
						boolean bbb_after = false;
						boolean aaa_under = false; //no use

						for (int k=i; k < xtable.size(); k++) {
							if (isSubject(curr, xtable.get(k).get(j))) {
								xtable.get(k).add(j+1, xtable.get(k).get(j));
//								xtable.get(k).set(j, new ItemValue("", null)); //AAA
								xtable.get(k).set(j, new ItemNull()); //AAA
								if (bbb_after) {
									//we set here the parent as drilled
									//lookup rule : row - i (== k - i), col - 1 (==j)
									//condition to setIsDrilled name != null
									int par;
									for (par = 1; xtable.get(k - par).get(j).getLabel().equals(""); par++) {
									}

									((ItemElement) xtable.get(k - par).get(j)).setDrilled(true);
									bbb_after = false;
								}
							}
							else {
								xtable.get(k).add(j+1, new ItemNull());
//								xtable.get(k).add(j+1, new ItemValue("", null)); //BBB
								bbb_after = true;
							}
						}
						xfix++;
					}
				}//end if row
			}
		}
		
		if(!showTotals) {
			StructureHelper.removeTotals(xtable);
		}

		OLAPResult res = new OLAPResult(xtable, xfix, yfix);
		return res;
	}

	private boolean isSubject(ItemElement curr, Item next) {
		if (next instanceof ItemElement) {
			//FIXME !enough, need compare on same hiera too?
			return curr.getLevelDepth() < ((ItemElement) next).getLevelDepth();
		}
		else
			return false;
	}
	
	protected static boolean addMemberChilds(OLAPMember mb) throws XMLAException {
		assert(mb instanceof XMLAMember);
		
		mb.setSynchro(true);
		
		try {
			if(mb.getLevel() != null) {
				XMLADiscover.discoverMemberSons(url, sock, mb.getDimension(), mb.getHiera(), mb.getLevel(), mb);
			}
			else {
				XMLADiscover.discoverMember(url, sock, mb.getDimension(), mb.getHiera(), mb.getLevel());
			}
		} catch (Exception e) {
			throw new XMLAException("", e);
		}
		
		return true;
	}
	
	public OLAPResult drillthrough(ItemValue curr, OLAPQuery mdx) {
		ArrayList<ItemValue> vals = new ArrayList<ItemValue>();

		OLAPQuery temp = (OLAPQuery) DeepCopy.copy(mdx);
		//SchemaReader rd = ((RolapCube) cube).getSchemaReader();

		temp.clearAxes();

		temp.addcol(curr.getDependent().get(0).getUname());

		//we start at 1 to skip measures
		for (int i=1; i < curr.getDependent().size(); i++) {
			if (mdx.hasHiera(curr.getDependent().get(i).getHierarchy())) {
				ItemDependent item = curr.getDependent().get(i);
				String[] splited = item.getUname().split("]");
				for (int j=0; j < splited.length; j++) {
					splited[j] = splited[j].replace("[", "").replace(".", "");
				}

				//Id.Segment id = new Id.Segment();
//				mondrian.olap.Member mb = rd.getMemberByUniqueName(Id.Segment.toList(splited), true);
//
//				if (mdx.colHasHiera(curr.getDependent().get(i).getHierarchy())) {
//					temp.addcol(mb.getUniqueName() + "");
//				}
//				else {
//					temp.addrow(mb.getUniqueName() + "");
//				}
//
//				recDrill(rd, mb, temp, mdx.colHasHiera(curr.getDependent().get(i).getHierarchy()), 0);
			}
		}

//		String buf = temp.getMDX();
//		System.out.println("Drill Through query = " + buf);
//		Query query = socket.parseQuery(buf);
//		Result res = socket.execute(query);

//		vals.add(curr);
//		String buf = temp.getMDX();
//		String s = 	"drillthrough select {[Measures].[Store Cost]} ON COLUMNS,"+
//				"{[Time].[All Time].[1997]} ON ROWS" + 
//				"from [Sales]" + 
//				"where ([Store].[All Stores].[Canada])";
//		
//		System.out.println("Drill Through query = " + buf);
//		Query query = sock.parseQuery(s);
//		Result res = sock.execute(query);
//
//		vals.add(curr);
//
//		return null;
//		return getDTResult(res);
		return null;
		//return getDTResult(res);
	}

	public OLAPResult executeDrillThrough(String s) throws XMLAException, SAXException, IOException, ParserConfigurationException {
		return XMLADrillThrough.executeMDX(url, sock, s);
	}

	public LinkedHashMap<String, LinkedHashMap<String, String>> getLevels() {
		LinkedHashMap<String, LinkedHashMap<String, String>> levels = new LinkedHashMap<String, LinkedHashMap<String, String>>();
		
		for(Dimension dim :  dims) {
			LinkedHashMap<String, String> dimLvl = new LinkedHashMap<String, String>();
			
			for(Hierarchy hiera : dim.getHierarchies()) {
				
				for(Level lvl : hiera.getLevel()) {
					
					dimLvl.put(lvl.getUniqueName(), lvl.getName());
				}
			}
			levels.put(dim.getName(), dimLvl);
		}
		
		return levels;
	}

	public void setShowTotals(boolean showTotals) {
		this.showTotals = showTotals;
	}

	public List<String> searchOnDimensions(String word, String level) {
		unames = new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		word = word.toLowerCase();
		
		for(Dimension dim :  dims) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				for(Level lvl : hiera.getLevel()) {
					if(level != null) {
						if(lvl.getUniqueName().equals(level)) {
							List<OLAPMember> members = null;
							try {
								members = XMLADiscover.discoverMember(url, sock, dim, hiera, lvl);
							} catch (Exception e) {
								e.printStackTrace();
							}
							for(OLAPMember m : members) {
								if(!unames.contains(m.getUniqueName())) {
									unames.add(m.getUniqueName());
									if(m.getLevel().getNextLevel() != null) {
										getSubMembers(result, m, word);
									}
									if(m.getUniqueName().toLowerCase().contains(word)) {
										result.add(m.getUniqueName());
									}
								}
							}
						}
					}
					else {
						List<OLAPMember> members = null;
						try {
							members = XMLADiscover.discoverMember(url, sock, dim, hiera, lvl);
							
						} catch (Exception e) {
							e.printStackTrace();
						}
						for(OLAPMember m : members) {
							if(!unames.contains(m.getUniqueName())) {
								unames.add(m.getUniqueName());
								if(m.getLevel().getNextLevel() != null) {
									getSubMembers(result, m, word);
								}
								if(m.getUniqueName().toLowerCase().contains(word)) {
									result.add(m.getUniqueName());
								}
							}
						}
					}
				}
			}
		}
		unames = null;
		
		return result;
	}

	private void getSubMembers(List<String> result, OLAPMember member, String word) {
		try {
			XMLADiscover.discoverMemberSons(url, sock, member.getDimension(), member.getHiera(), member.getLevel(), member);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Collection<OLAPMember> members = member.getMembers();
		
		for(OLAPMember m : members) {
			if(!unames.contains(m.getUniqueName())) {
				unames.add(m.getUniqueName());
				if(m.getLevel().getNextLevel() != null) {
					getSubMembers(result, m, word);
				}
				if(m.getUniqueName().toLowerCase().contains(word)) {
					result.add(m.getUniqueName());
				}
			}
		}
	}
	
	public HashMap<String, String> findChildsForReporter(String uname) {
		LinkedHashMap<String, String> results = new LinkedHashMap<String, String>();
		
		for(Dimension dim :  dims) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				if(hiera.getUniqueName().equals(uname)) {
					try {
						List<OLAPMember> members = XMLADiscover.discoverMember(url, sock, dim, hiera, hiera.getLevel().get(0));
						for(OLAPMember memb : members) {
							results.put(memb.getUniqueName(), memb.getHiera().getUniqueName());
						}
						return results;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				for(Level lvl : hiera.getLevel()) {
					try {
						List<OLAPMember> members = XMLADiscover.discoverMember(url, sock, dim, hiera, lvl);
						for(OLAPMember m : members) {
							if(m.getUniqueName().equals(uname)) {
								try {
									XMLADiscover.discoverMemberSons(url, sock, m.getDimension(), m.getHiera(), m.getLevel(), m);
									Collection<OLAPMember> submembers = m.getMembers();
									for(OLAPMember memb : submembers) {
										results.put(memb.getUniqueName(), memb.getHiera().getUniqueName());
									}
									return results;
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							else {
								if(getSubMembersForReporter(m,results,uname)) {
									return results;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		
		return results;
	}

	private boolean getSubMembersForReporter(OLAPMember me, LinkedHashMap<String, String> results, String uname) {
		try {
			XMLADiscover.discoverMemberSons(url, sock, me.getDimension(), me.getHiera(), me.getLevel(), me);
			Collection<OLAPMember> sumembers = me.getMembers();
			for(OLAPMember m : sumembers) {
				if(m.getUniqueName().equals(uname)) {
					try {
						XMLADiscover.discoverMemberSons(url, sock, m.getDimension(), m.getHiera(), m.getLevel(), m);
						Collection<OLAPMember> submembers = m.getMembers();
						for(OLAPMember memb : submembers) {
							results.put(memb.getUniqueName(), memb.getHiera().getUniqueName());
						}
						return true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				else {
					if(getSubMembersForReporter(m,results,uname)) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void getSubMembersForReporter() {
		
	}

	public List<String> getParametersValues(String level) {
		List<String> values = new ArrayList<String>();
		for(Dimension dim :  dims) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				for(Level lvl : hiera.getLevel()) {
					
					if(lvl.getUniqueName().equals(level)) {
						try {
							List<OLAPMember> members = XMLADiscover.discoverMember(url, sock, dim, hiera, lvl);
							for(OLAPMember memb : members) {
								values.add(memb.getName());
							}
							return values;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					
				}
			}
		}
		return values;
	}

	@Override
	public OLAPCube createCube(String path,
			List<ICubeView> lstCubeViews, IRuntimeContext ctx) throws Exception {
		return null;
	}

	@Override
	public OLAPMember getOLAPMember(String uname) {
		for(Dimension d : dims){
			if (uname.startsWith(d.getUniqueName())){
				
				for(Hierarchy h : d.getHierarchies()){
					if (uname.startsWith(h.getUniqueName())){
						
						return h.getDefaultMember().findMember(uname);
						
					}
				}
				
				
			}
		}
		
		
		return null;
	}

	@Override
	public List<Measure> getAllMeasures() {
		List<Measure> l = new ArrayList<Measure>();
		for(MeasureGroup m : getMeasures()){
			l.addAll(m.getMeasures());
		}
		return l;
	}

	@Override
	public OLAPCube createCube(IRuntimeContext ctx) throws Exception{
		sock = new XMLAConnection();
		IConnection con = null;
		for(ICube c : fasdModel.getSchema().getICubes()){
			if (c.getName().endsWith(cubeName)){
				con = c.getConnection();
				break;
			}
		}
		
		
		
		if (!(con instanceof XMLADataSourceConnection)) {
			throw new RuntimeException("did'nt find an XMLA Cube definition");
		}
		
		XMLADataSourceConnection c = (XMLADataSourceConnection) con;
		sock.setUrl(c.getUrl());
		sock.setDatasource(c.getSchema()); //XXX normal, ere: ludo fais mal son travail :p
		sock.setSchema(c.getCatalog());
		sock.setUser(c.getUser());
		sock.setPass(c.getPass());
		sock.setProvider(c.getType());
		sock.setCube(getCubeName());
	
		url = new URL(sock.getUrl());
		
		if (!isConnected){
			exploreCube();
			isConnected = true;
		}
		
//		exploreCube();
		
		//XXX open view also needs to be done
		XMLACube cube = new XMLACube(this);
		
		return cube;
	}
	
	public String getCubeName(){
		return cubeName;
	}
}
