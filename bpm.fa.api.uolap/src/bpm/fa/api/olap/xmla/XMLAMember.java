package bpm.fa.api.olap.xmla;

import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.Element;
import bpm.fa.api.olap.Hierarchy;
import bpm.fa.api.olap.Level;
import bpm.fa.api.olap.OLAPMember;
import bpm.fa.api.utils.log.Log;

public class XMLAMember extends Element implements OLAPMember {
	private HashMap<String, OLAPMember> mbs = new HashMap<String, OLAPMember>();
	
	private Dimension dim;
	private Hierarchy hiera;
	private Level lvl;
	
	private String hieraName;
	
	private boolean sync;
	
	private int depth = 0;
	private String parentUName;
	
	private XMLAMember parent = null;
	
	public XMLAMember getParent(){
		return parent;
	}
	
	public XMLAMember() {
		
	}
	
	public XMLAMember(String name, String uname, String caption, Dimension dim, Hierarchy hiera, Level lvl) {
		super(name, uname, caption);
		this.hiera = hiera;
		this.dim = dim;
		this.lvl = lvl;
		
		Log.info("Loaded " + this.getClass().getName() + " uname = " + getUniqueName());
	}
	
	public XMLAMember(String name, String uname, String caption, Dimension dim, Hierarchy hiera, Level lvl, String depth) {
		this(name, uname, caption, dim, hiera, lvl);
		setLevelDepth(depth);
	}

	public XMLAMember(String name, String uname, String caption,
			Dimension dim, Hierarchy h, Level lvl, String depth,
			String parentUname) {
		this(name, uname, caption, dim, h, lvl, depth);
		this.parentUName = parentUname;
	}
	
	public String getParentUName(){
		return parentUName;
	}

	public void addMember(OLAPMember mb) {
		String parent = getUniqueName();
		//String parent = getParentUName();
		
		if (parent.endsWith(".members")){
			parent = parent.substring(0, parent.indexOf(".members"));
		}
		if(parent.endsWith(".children")){
			parent = parent.substring(0, parent.indexOf(".children"));
		}
		if (!mb.getUniqueName().startsWith(parent + ".") && ! mb.getUniqueName().startsWith(mb.getLevel().getUniqueName())){
			((Element)mb).setUname(parent + "." + mb.getUniqueName());
		}
		
		mbs.put(mb.getUniqueName(), (XMLAMember) mb);
		((XMLAMember) mb).parent = this;
	}
	
//	public void setUnameFromParent(){
//		String s = getUniqueName();
//		
//		if (parent != null){
//			
//		}
//	}
	
	
	public Collection getMembers() {
		return mbs.values();
	}
	
	public Vector<OLAPMember> getMembersVector() {
		return new Vector<OLAPMember>(mbs.values());
	}
	
	public OLAPMember findMember(OLAPMember mb) {
		return null;
	}
	
	public OLAPMember findMember(String uname) {
		return null;
	}

	public String getHierarchy() {
		return hiera.getUniqueName();
	}

	public int getLevelDepth() {
		return depth;
	}
	
	public void setLevelDepth(String dp) {
		depth = new Integer(dp);
	}

	public String[] getPropertiesName() {
		return null;
	}

	public String[] getPropertiesValue() {
		return null;
	}

	public boolean hasProperties() {
		return false;
	}

	/**
	 * Synchronization between the MondrianMember tree and the elements
	 * 
	 * @return
	 */
	public boolean isSynchro(){
		return sync;	
	}
	
	public void setSynchro(boolean synchro){
		this.sync =synchro;
		
	}

	public Dimension getDimension() {
		return dim;
	}

	public Hierarchy getHiera() {
		return hiera;
	}
	

	public Level getLevel() {
		return lvl;
	}

	public String getHieraName() {
		return hieraName;
	}

	public void setHieraName(String hieraName) {
		this.hieraName = hieraName;
	}

	@Override
	public void setLevel(Level lvl) {
		this.lvl = lvl;
		
	}
}
