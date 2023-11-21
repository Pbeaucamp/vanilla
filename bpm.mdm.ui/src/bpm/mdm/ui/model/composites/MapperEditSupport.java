package bpm.mdm.ui.model.composites;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.datatools.connectivity.oda.design.DataSetDesign;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.swt.widgets.Composite;

import bpm.dataprovider.odainput.consumer.OdaHelper;
import bpm.mdm.model.Attribute;
import bpm.mdm.model.Mapper;
import bpm.mdm.model.Model;
import bpm.mdm.model.Synchronizer;
import bpm.mdm.model.helper.DataSetDesignConverter;
import bpm.mdm.ui.i18n.Messages;

public class MapperEditSupport extends EditingSupport{

	private List<String> fieldNames;
	private Mapper mapper;
	private ComboBoxCellEditor editor;
	
	public MapperEditSupport(ColumnViewer viewer, Synchronizer synchronizer) throws Exception{
		super(viewer);
		this.mapper = synchronizer;

		createEditor(((Model)synchronizer.eContainer()).getDataSource(synchronizer.getDataSourceName()));
	}

	
	private void createEditor(DataSetDesign dataSetDesign)throws Exception{
		
		fieldNames = new ArrayList<String>();	
		fieldNames.add(Messages.MapperEditSupport_0);
		fieldNames.addAll(OdaHelper.createDescriptor(	DataSetDesignConverter.convert(dataSetDesign)));
		
		editor = new ComboBoxCellEditor((Composite)getViewer().getControl(), fieldNames.toArray(new String[fieldNames.size()]));
	}
	
	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		int fieldNum = mapper.getDataSourceField((Attribute)element);
		return fieldNum + 1;
	}

	@Override
	protected void setValue(Object element, Object value) {
		Integer v = (Integer)value;
		if (v == null || v <= 0){
			mapper.mapAttributeWithField((Attribute)element, -1);
		}
		else{
			mapper.mapAttributeWithField((Attribute)element, v - 1);
		}
		
		getViewer().refresh();
	}

	public String getFieldName(Integer i){
		try{
			return fieldNames.get(i + 1);
		}catch(IndexOutOfBoundsException e){
			return fieldNames.get(0);
		}
	}
}
