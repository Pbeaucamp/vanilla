package org.fasd.utils;

import org.fasd.datasource.DataObject;
import org.fasd.datasource.DataObjectItem;
import org.fasd.datasource.DataSource;
import org.fasd.datasource.DataSourceConnection;
import org.fasd.olap.OLAPCube;
import org.fasd.olap.OLAPDimension;
import org.fasd.olap.OLAPDimensionGroup;
import org.fasd.olap.OLAPHierarchy;
import org.fasd.olap.OLAPLevel;
import org.fasd.olap.OLAPMeasure;
import org.fasd.olap.OLAPMeasureGroup;
import org.fasd.olap.OLAPRelation;
import org.fasd.olap.Property;
import org.fasd.olap.SecurityProvider;
import org.fasd.olap.ServerConnection;
import org.fasd.olap.aggregate.AggregateTable;

public class ResetCounterFactory {
	private static ResetCounterFactory reseter = null;
	
	private ResetCounterFactory(){}
	
	public static ResetCounterFactory getInstance(){
		if (reseter == null){
			reseter = new ResetCounterFactory();
			
		}
		return reseter;
	}
	
	public void resetAllCounter(){
		DataSource.resetCounter();
		DataObject.resetCounter();
		DataObjectItem.resetCounter();
		DataSourceConnection.resetCounter();
		
		OLAPCube.resetCounter();
		OLAPDimension.resetCounter();
		OLAPHierarchy.resetCounter();
		OLAPLevel.resetCounter();
		
		OLAPDimensionGroup.resetCounter();
		OLAPMeasureGroup.resetCounter();
		OLAPRelation.resetCounter();
		OLAPMeasure.resetCounter();
		SecurityProvider.resetCounter();
		ServerConnection.resetCounter();
		AggregateTable.resetCounter();
		Property.resetCounter();
	}

	@Override
	protected void finalize() throws Throwable {
		reseter = null;
		super.finalize();
	}
	
	

}
