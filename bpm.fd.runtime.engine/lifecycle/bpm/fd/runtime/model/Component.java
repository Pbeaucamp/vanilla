package bpm.fd.runtime.model;

import java.awt.Rectangle;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.eclipse.datatools.connectivity.oda.IParameterMetaData;
import org.eclipse.datatools.connectivity.oda.IQuery;
import org.eclipse.datatools.connectivity.oda.IResultSet;

import bpm.fd.api.core.model.components.definition.ComponentConfig;
import bpm.fd.api.core.model.components.definition.ComponentParameter;
import bpm.fd.api.core.model.components.definition.IComponentDefinition;
import bpm.fd.api.core.model.components.definition.slicer.ComponentSlicer;
import bpm.fd.api.core.model.components.definition.slicer.SlicerData;
import bpm.fd.api.core.model.datas.DataSet;
import bpm.fd.runtime.model.datas.FilteredResultSet;

/**
 * Its the runtime implementation of the iComponentDefinition elemnt of a dashboard.
 * Those instances will be responsibleof preparing the IQuery of a component
 * @author ludo
 *
 */
public class Component extends ComponentRuntime{
//	private List<Component> target = new ArrayList<Component>();
	
	/**
	 * to apply paramaters values to the queries
	 * 
	 * the key is the name of parameter provide
	 * the value is the parameter index within the query
	 */
//	private HashMap<String, Integer> datasParameterMapping = new HashMap<String, Integer>();
	private HashMap<Integer, String> datasParameterMapping = new HashMap<Integer, String>();
//	private HashMap<String, String> datasParameterDefaultValue = new HashMap<String, String>();
	private HashMap<Integer, String> datasParameterDefaultValue = new HashMap<Integer, String>();
	private Rectangle layout;
//	private IComponentDefinition fdComponentDef;

	/**
	 * @param : freeLayout : use to know if we need to generate absolute div position or not
	 */
	public Component(IComponentDefinition fdComponentDef, ComponentConfig config, boolean freeLayout){
		super(fdComponentDef);
		if (freeLayout){
			layout = new Rectangle(config.getCell().getPosition().x, config.getCell().getPosition().y,
					config.getCell().getSize().x,config.getCell().getSize().y );
		}
		
		
		//build parameters mapping
		for(ComponentParameter p : config.getParameters()){
//			datasParameterMapping.put(config.getComponentNameFor(p), p.getIndice());
//			datasParameterDefaultValue.put(config.getComponentNameFor(p), p.getDefaultValue());
			
			datasParameterMapping.put(p.getIndice(), config.getComponentNameFor(p));
			datasParameterDefaultValue.put(p.getIndice(), p.getDefaultValue());
		}
	}
	
	public Rectangle getLayout(){
		return layout;
	}
	
//	public String getName(){
//		return fdComponentDef.getName();
//	}
	
//	public void addTarget(Component target){
//		this.target.add(target);
//	}
	
//	public List<Component> getTargets(boolean cascade){
//		List<Component> l = new ArrayList<Component>(target);
//		return l;
//	}
	
	public DataSet getDataSet(){
		if (getComponentDefinition().getDatas() == null){
			return null;
		}
		return getComponentDefinition().getDatas().getDataSet();
	}
	
	public boolean prepareQuery(IQuery query, DashState state) throws Exception{
		IParameterMetaData pMd = query.getParameterMetaData();
		boolean prepared = true;
		for(Integer indexPos : datasParameterMapping.keySet()){
			String value = state.getComponentValue(datasParameterMapping.get(indexPos));
			if (value == null){
				value = datasParameterDefaultValue.get(indexPos);
			}
			
			if (value == null){
				try{
					int parameterNonNull = 0;
					try {
						parameterNonNull = pMd.isNullable(indexPos);
					} catch(Exception e) {
						//Because of the mysql driver which won't want to get the parameter metadata sometimes.
						//So we say the parameter can't be null.
						parameterNonNull = IParameterMetaData.parameterNoNulls;
					}
					if (parameterNonNull == IParameterMetaData.parameterNoNulls){
						prepared = false;
					}
					else{
						return false;
//						query.setNull(indexPos);
//						Logger.getLogger(getClass()).debug("Component " + getName() + " parameterQuery" + datasParameterMapping.get(indexPos) + "=null");	
					}
				}catch(Exception ex){
					ex.printStackTrace();
				}
				
				
			}
			else{
				query.setString(indexPos, value);
				Logger.getLogger(getClass()).debug("Component " + getName() + " parameterQuery" + datasParameterMapping.get(indexPos) + "=value");
			}
			
		}
		
		return prepared;
		
		
	}
	
	public IComponentDefinition getComponentDefinition(){
		return (IComponentDefinition)getElement();
	}
	
	/**
	 * used to convert base ODA ResultSet in others
	 * 
	 * Used to add additional behavior on IResultSet like filtering from the dashInstance slicer's states
	 * 
	 * @param result
	 * @return

	 */
	public IResultSet adapt(DashState state, IResultSet result) throws Exception{
		IResultSet finalResultSet = result;
		
		for(ComponentRuntime s : getSources()){
			if (s.getElement() instanceof ComponentSlicer){
				finalResultSet = new FilteredResultSet((SlicerData)((ComponentSlicer)s.getElement()).getDatas(), state.getSlicerState(s.getName()), finalResultSet); 
			}
		}
		return finalResultSet;
	}
}
