package bpm.fa.api.olap;

import java.util.Collection;
import java.util.Vector;

/**
 * 
 * @author ereynard
 *
 */
public interface OLAPMember {

	/**
	 * 
	 * @return
	 */
	public String getName();
	
	/**
	 * 
	 * @return
	 */
	public String getUniqueName();
	
	/**
	 * 
	 * @return
	 */
	public String getCaption();
	
	public void setCaption(String caption);
	
	public Dimension getDimension();
	public Hierarchy getHiera();
	public Level getLevel();
	public void setLevel(Level lvl);
	
	public int getLevelDepth();
	public String getHierarchy();
	
	public void addMember(OLAPMember mb);
	
	public Collection getMembers();
	public Vector<OLAPMember> getMembersVector();
	
	public OLAPMember findMember(OLAPMember mb);
	public OLAPMember findMember(String uname);
	
	public OLAPMember getParent();
	
	
	//properties
	public boolean hasProperties();
	public String[] getPropertiesName();
	public String[] getPropertiesValue();
	
	//ludo stuff synchro
	public void setSynchro(boolean sync);
	public boolean isSynchro();
	
}
