/*
 * Copyright 2006 Pentaho Corporation.  All rights reserved. 
 * This software was developed by Pentaho Corporation and is provided under the terms 
 * of the Mozilla Public License, Version 1.1, or any later version. You may not use 
 * this file except in compliance with the license. If you need a copy of the license, 
 * please go to http://www.mozilla.org/MPL/MPL-1.1.txt. The Original Code is the Pentaho 
 * BI Platform.  The Initial Developer is Pentaho Corporation.
 *
 * Software distributed under the Mozilla Public License is distributed on an "AS IS" 
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or  implied. Please refer to 
 * the license for the specific language governing your rights and limitations.
*/
 /**********************************************************************
 **                                                                   **
 **               This code belongs to the KETTLE project.            **
 **                                                                   **
 ** Kettle, from version 2.2 on, is released into the public domain   **
 ** under the Lesser GNU Public License (LGPL).                       **
 **                                                                   **
 ** For more details, please read the document LICENSE.txt, included  **
 ** in this project                                                   **
 **                                                                   **
 ** http://www.kettle.be                                              **
 ** info@kettle.be                                                    **
 **                                                                   **
 **********************************************************************/
 
package org.fasd.utils;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.olap.OLAPRelation;





/*
 * Created on 30-jan-04
 *
 */

public class Path
{
	private ArrayList<OLAPRelation> path;  // contains Relationship objects
	
	public Path()
	{
		path=new ArrayList<OLAPRelation>();
	}
	
	public List<OLAPRelation> getRelations(){
		return path;
	}
	public void addRelationship(OLAPRelation rel)
	{
		path.add(rel);
	}
	
	public void removeRelationship()
	{
		path.remove(size()-1);
	}
	
	public OLAPRelation getLastRelationship()
	{
		return (OLAPRelation)path.get(size()-1);
	}

	public int size()
	{
		return path.size();
	}
	
	public int nrTables()
	{
		return getUsedTables().length;
	}
	
	
	public OLAPRelation getRelationship(int i)
	{
		if (path.size() == 0 || i >= path.size() )
			return null;
		
		return (OLAPRelation)path.get(i);
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

	public boolean contains(OLAPRelation rel)
	{
		if (rel==null) return false;
		
		for (int i=0;i<size();i++)
		{
			OLAPRelation check = getRelationship(i);
            DataObject from = check.getLeftObject();
            DataObject to   = check.getRightObject();
			if ( ( rel.getLeftObject().equals(from) && rel.getRightObject().equals(to) ) ||
                 ( rel.getLeftObject().equals(to) && rel.getRightObject().equals(from) )
              ) return true;
		}
		return false;
	}

	public boolean contains(DataObject tab)
	{
		if (tab==null) return false;
		
		for (int i=0;i<size();i++)
		{
			OLAPRelation check = getRelationship(i);
			if (check.isUsingTable(tab)) return true;
		}
		return false;
	}

	public boolean contains(DataObject tabs[])
	{
		if (tabs==null) return false;
		
		boolean all=true;
		for (int i=0;i<tabs.length && all;i++)
		{
			if (!contains(tabs[i])) all=false;
		}
		return all;
	}

	public boolean contains(ArrayList<DataObject> tabs)
	{
		if (tabs==null) return false;
		
		boolean all=true;
		for (int i=0;i<tabs.size() && all;i++)
		{
			if (!contains((DataObject)tabs.get(i))) all=false;
		}
		return all;
	}

	public Object clone()
	{
		Path retval   = new Path();
		
		for (int i=0;i<size();i++)
		{
			OLAPRelation rel = getRelationship(i);
			retval.addRelationship(rel);
		}
		
		return retval;
	}
	
	public String toString()
	{
		String thisPath=""; //$NON-NLS-1$
		for (int i=0;i<size();i++)
		{
			OLAPRelation relationship = getRelationship(i);
            if (i>0) thisPath+=", "; //$NON-NLS-1$
            thisPath+="["+relationship.getLeftObject().getId()+"-"+relationship.getRightObject().getId()+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
	
	public DataObject[] getUsedTables()
	{
		Hashtable<DataObject, String> hash = new Hashtable<DataObject, String>();
		
		for (int i=0;i<size();i++)
		{
			OLAPRelation rel = getRelationship(i);
			hash.put(rel.getLeftObject(), "OK"); //$NON-NLS-1$
			hash.put(rel.getRightObject(), "OK"); //$NON-NLS-1$
		}
        return (DataObject[]) hash.keySet().toArray(new DataObject[hash.keySet().size()]);
	}
	
	public List<DataObjectItem> getUsedItems(){
		Hashtable<DataObjectItem, String> hash = new Hashtable<DataObjectItem, String>();
		
		for (int i=0;i<size();i++)
		{
			OLAPRelation rel = getRelationship(i);
			hash.put(rel.getLeftObjectItem(), "OK"); //$NON-NLS-1$
			hash.put(rel.getRightObjectItem(), "OK"); //$NON-NLS-1$
		}
		List<DataObjectItem> l = new ArrayList<DataObjectItem>();
		for(DataObjectItem i : hash.keySet())
			l.add(i);
        return l;

	}

	public OLAPRelation[] getUsedRelationships()
	{
		ArrayList<OLAPRelation> list = new ArrayList<OLAPRelation>();
		
		for (int i=0;i<size();i++)
		{
			OLAPRelation rel = getRelationship(i);
			boolean exists=false;
			for (int j=0;j<list.size() && !exists;j++)
			{
				OLAPRelation check = (OLAPRelation)list.get(j);
				if ( check.isUsingTable( rel.getLeftObject()) &&
				     check.isUsingTable( rel.getRightObject())
				   ) exists=true;
			}
			if (!exists) list.add(rel);
		}
		
		OLAPRelation rels[] = new OLAPRelation[list.size()];
		for (int i=0;i<list.size();i++) rels[i] = (OLAPRelation)list.get(i);

		return rels;
	}
}
