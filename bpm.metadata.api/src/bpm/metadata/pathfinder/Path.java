package bpm.metadata.pathfinder;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;





/*
 * Created on 30-jan-04
 *
 */

public class Path
{
	private ArrayList<Relation> path;  // contains Relationship objects
	
	public Path()
	{
		path=new ArrayList<Relation>();
	}
	
	public List<Relation> getRelations(){
		return path;
	}
	public void addRelationship(Relation rel)
	{
		if (!path.contains(rel))
		path.add(rel);
	}
	
	public void removeRelationship()
	{
		path.remove(size()-1);
	}
	
	public Relation getLastRelationship()
	{
		return (Relation)path.get(size()-1);
	}

	public int size()
	{
		return path.size();
	}
	
	public int nrTables()
	{
		return getUsedTables().length;
	}
	
	
	public Relation getRelationship(int i)
	{
		if (path.size() == 0 || i >= path.size() )
			return null;
		
		return (Relation)path.get(i);
	}
		
	public boolean contains(Path in)
	{
		if (in.size()==0) return false;
		
		for (int i=0;i<size();i++)
		{
			int nr=0;
			while (getRelationship(i+nr).equals(in.getRelationship(nr)) && 
			       nr<in.size() &&
			       i+nr<size()
			      )
			{
				nr++;
			}
			if (nr==in.size()) return true;
		}
		return false;
	}

	public boolean contains(Relation rel)
	{
		if (rel==null) return false;
		
		for (int i=0;i<size();i++)
		{
			Relation check = getRelationship(i);
            IDataStream from = check.getLeftTable();
            IDataStream to   = check.getRightTable();
			if ( ( rel.getLeftTable().equals(from) && rel.getRightTable().equals(to) ) ||
                 ( rel.getLeftTable().equals(to) && rel.getRightTable().equals(from) )
              ) return true;
		}
		return false;
	}

	public boolean contains(IDataStream tab)
	{
		if (tab==null) return false;
		
		for (int i=0;i<size();i++)
		{
			Relation check = getRelationship(i);
			if (check.isUsingTable(tab)) return true;
		}
		return false;
	}

	public boolean contains(IDataStream tabs[])
	{
		if (tabs==null) return false;
		
		boolean all=true;
		for (int i=0;i<tabs.length && all;i++)
		{
			if (!contains(tabs[i])) all=false;
		}
		return all;
	}

	public boolean contains(ArrayList<IDataStream> tabs)
	{
		if (tabs==null) return false;
		
		boolean all=true;
		for (int i=0;i<tabs.size() && all;i++)
		{
			if (!contains((IDataStream)tabs.get(i))) all=false;
		}
		return all;
	}

	public Object clone()
	{
		Path retval   = new Path();
		
		for (int i=0;i<size();i++)
		{
			Relation rel = getRelationship(i);
			retval.addRelationship(rel);
		}
		
		return retval;
	}
	
	public String toString()
	{
		String thisPath=""; //$NON-NLS-1$
		for (int i=0;i<size();i++)
		{
			Relation relationship = getRelationship(i);
            if (i>0) thisPath+=", "; //$NON-NLS-1$
            
            thisPath+="["+(relationship.getLeftTable()==null?relationship.getLeftTableName():relationship.getLeftTable().getName())+"-"+(relationship.getRightTable()==null?relationship.getRightTableName():relationship.getRightTable().getName())+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
		return thisPath;
	}
	
	// Compare two paths: first on the number of tables used!!!
	public int compare(Path thisPath)
	{
		int diff=size()-thisPath.size();
		if (diff<0) return -1;
		else if (diff>0) return 1;
		else return 0;
	}
	
	public IDataStream[] getUsedTables()
	{
		Hashtable<IDataStream, String> hash = new Hashtable<IDataStream, String>();
		
		for (int i=0;i<size();i++)
		{
			Relation rel = getRelationship(i);
			hash.put(rel.getLeftTable(), "OK"); //$NON-NLS-1$
			hash.put(rel.getRightTable(), "OK"); //$NON-NLS-1$
		}
        return (IDataStream[]) hash.keySet().toArray(new IDataStream[hash.keySet().size()]);
	}
	
	public List<IDataStreamElement> getUsedItems(){
		Hashtable<IDataStreamElement, String> hash = new Hashtable<IDataStreamElement, String>();
		
		for (int i=0;i<size();i++)
		{
			Relation rel = getRelationship(i);
			hash.put(rel.getJoins().get(0).getLeftElement(), "OK"); //$NON-NLS-1$
			hash.put(rel.getJoins().get(0).getRightElement(), "OK"); //$NON-NLS-1$
		}
		List<IDataStreamElement> l = new ArrayList<IDataStreamElement>();
		for(IDataStreamElement i : hash.keySet())
			l.add(i);
        return l;

	}

	public Relation[] getUsedRelationships()
	{
		ArrayList<Relation> list = new ArrayList<Relation>();
		
		for (int i=0;i<size();i++)
		{
			Relation rel = getRelationship(i);
			boolean exists=false;
			for (int j=0;j<list.size() && !exists;j++)
			{
				Relation check = (Relation)list.get(j);
				if ( check.isUsingTable( rel.getLeftTable()) &&
				     check.isUsingTable( rel.getRightTable())
				   ) exists=true;
			}
			if (!exists) list.add(rel);
		}
		
		Relation rels[] = new Relation[list.size()];
		for (int i=0;i<list.size();i++) rels[i] = (Relation)list.get(i);

		return rels;
	}
}
