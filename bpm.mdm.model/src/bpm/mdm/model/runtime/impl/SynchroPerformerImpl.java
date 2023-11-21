/**
 * <copyright>
 * </copyright>
 *
 * $Id$
 */
package bpm.mdm.model.runtime.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;
import org.eclipse.datatools.connectivity.oda.OdaException;
import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.EObjectImpl;

import bpm.dataprovider.odainput.consumer.QueryHelper;
import bpm.mdm.model.Attribute;
import bpm.mdm.model.Model;
import bpm.mdm.model.Rule;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.model.helper.DataSetDesignConverter;
import bpm.mdm.model.runtime.DiffResult;
import bpm.mdm.model.runtime.Row;
import bpm.mdm.model.runtime.RowState;
import bpm.mdm.model.runtime.RuntimePackage;
import bpm.mdm.model.runtime.SynchroPerformer;
import bpm.mdm.model.storage.IEntityStorage;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Synchro Performer</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * </p>
 *
 * @generated
 */
public class SynchroPerformerImpl extends EObjectImpl implements SynchroPerformer {
	
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected SynchroPerformerImpl() {
		super();
	}

	private IEntityStorage storage;
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	protected SynchroPerformerImpl(IEntityStorage storage) {
		super();
		this.storage = storage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return RuntimePackage.Literals.SYNCHRO_PERFORMER;
	}




	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 */
	public DiffResult performDiff(Synchronizer synchronizer){
		// TODO: implement this method
		DiffResult res = RuntimeFactoryImpl.eINSTANCE.createDiffResult();
		
		DataSetDesign dataSource = ((Model)synchronizer.eContainer()).getDataSource(synchronizer.getDataSourceName());
		
		IQuery query = null;
		
		try{
			query = QueryHelper.buildquery(DataSetDesignConverter.convert(dataSource));
			query.setProperty("rowFetchSize", 10000 + "");
		}catch(Exception ex){
			
		}
		

		IResultSet rs = null;
		try{
			
			rs = query.executeQuery();
			
			while(rs.next()){
				res.incrementRead();
				Row row = RuntimeFactoryImpl.eINSTANCE.createRow();
				RowState state = null;
				
				List<Rule> insatisfiedRules = new ArrayList<Rule>();
				
				for(Attribute a : synchronizer.getEntity().getAttributes()){
					int i = synchronizer.getDataSourceField(a);
					if (i >=0){
						
						Object value = extractValue(a, rs, i+1);
						row.setValue(a, value);
						for(Rule rule : a.getRules()){
							if (rule.isActive() && !rule.evaluate(value)){
								state = RowState.DISCARD;
								insatisfiedRules.add(rule);
							}
						}
					}
				}
				
				

					
				
				Row exists = null;
				if (state == null){
					
					exists = storage.lookup(row);
					
					
					if (exists != null){
						
						for(Attribute a : synchronizer.getEntity().getAttributes()){
							if (synchronizer.getDataSourceField(a) > 0){
								Object currentValue = exists.getValue(a);
								Object newValue = row.getValue(a);
								if (currentValue != null){
									if (!currentValue.equals(newValue)){
										state = RowState.UPDATE;
										break;
									}
								}
								else{
									if (newValue != null){
										state = RowState.UPDATE;
										break;
									}
								}
							}
						}
						
					}
					else{
						//check if there is a modification on the row
						
						state = RowState.NEW;
					}
				}
				
				
				
					
				
				if (state != null){
					
					res.addRow(row, state, insatisfiedRules, exists);
				}
				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if (rs != null){
				try {
					rs.close();
				} catch (OdaException e) {
					
					e.printStackTrace();
				}
			}
			if (query != null){
				try {
					query.close();
				} catch (OdaException e) {
					
					e.printStackTrace();
				}
				//QueryHelper.removeQuery(query);
			}
		}
		
		return res;
		
	}

	private Object extractValue(Attribute a, IResultSet rs, int index) throws Exception{
		
		switch (a.getDataType()) {
		case BOOLEAN:
			return rs.getBoolean(index);
		case DATE:
			return rs.getDate(index);
		case DOUBLE:
			return rs.getDouble(index);
		case LONG:
			return rs.getInt(index);
		default:
			return rs.getString(index);

		}
	}

} //SynchroPerformerImpl
