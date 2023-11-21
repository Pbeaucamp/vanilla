package bpm.fmloader.client.table;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bpm.fm.api.model.utils.AxisInfo;
import bpm.fm.api.model.utils.LevelMember;
import bpm.fm.api.model.utils.LoaderDataContainer;
import bpm.fm.api.model.utils.LoaderMetricValue;
import bpm.fmloader.client.constante.Constantes;
import bpm.fmloader.client.images.ImageResources;
import bpm.fmloader.client.panel.MetricNewValuePanel;
import bpm.fmloader.client.tools.AlphanumComparator;
import bpm.gwt.commons.client.utils.CustomResources;

import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.view.client.ListDataProvider;

public class MetricDataPanel extends HTMLPanel {

	private static AlphanumComparator comparator = new AlphanumComparator();
	
	private DataGrid<LoaderMetricValue> dataGrid;
	private ListDataProvider<LoaderMetricValue> dataProvider = new ListDataProvider<LoaderMetricValue>();

	private LoaderDataContainer data;
	private MetricNewValuePanel newValuePanel;
	
	public MetricDataPanel(List<LoaderDataContainer> dataContainer, boolean canModify) {
		super("");
		
		DataGrid.Resources resources = new CustomResources();
		dataGrid = new DataGrid<LoaderMetricValue>(50, resources);
		dataGrid.setWidth("auto");
//		dataGrid.setWidth("100%");
		if (canModify) {
			dataGrid.setHeight("80%");
		}
		else {
			dataGrid.setHeight("92%");
		}
		
		//we only handle one metric for now
		this.data = dataContainer != null ? dataContainer.get(0) : null;
		
		Column<LoaderMetricValue, ImageResource> colStatus = new Column<LoaderMetricValue, ImageResource>(new ImageResourceCell()) {		
			@Override
			public ImageResource getValue(LoaderMetricValue object) {
				if(object.isDeleted()) {
					return ImageResources.INSTANCE.delete_16();
				}
				else if(object.isNew()) {
					return ImageResources.INSTANCE.newadd_16();
				}
				else if(object.isUpdated()) {
					return ImageResources.INSTANCE.update_16();
				}

				return null;
			}
		};
		
		Column<LoaderMetricValue, String> colValue = new Column<LoaderMetricValue, String>(new EditTextCell()) {
			@Override
			public String getValue(LoaderMetricValue object) {
				return String.valueOf(object.getValue());
			}
		};
		colValue.setFieldUpdater(new FieldUpdater<LoaderMetricValue, String>() {	
			@Override
			public void update(int index, LoaderMetricValue object, String value) {
				if(Double.parseDouble(value) != object.getValue()) {
					object.setUpdated(true);
					dataProvider.refresh();
				}
				object.setValue(Double.parseDouble(value));
			}
		});
		
		Column<LoaderMetricValue, String> colObj = new Column<LoaderMetricValue, String>(new EditTextCell()) {
			@Override
			public String getValue(LoaderMetricValue object) {
				return String.valueOf(object.getObjective());
			}
		};
		colObj.setFieldUpdater(new FieldUpdater<LoaderMetricValue, String>() {	
			@Override
			public void update(int index, LoaderMetricValue object, String value) {
				if(Double.parseDouble(value) != object.getObjective()) {
					object.setUpdated(true);
					dataProvider.refresh();
				}
				object.setObjective(Double.parseDouble(value));
			}
		});
		
		Column<LoaderMetricValue, String> colMin = new Column<LoaderMetricValue, String>(new EditTextCell()) {
			@Override
			public String getValue(LoaderMetricValue object) {
				return String.valueOf(object.getMinimum());
			}
		};
		colMin.setFieldUpdater(new FieldUpdater<LoaderMetricValue, String>() {	
			@Override
			public void update(int index, LoaderMetricValue object, String value) {
				if(Double.parseDouble(value) != object.getMinimum()) {
					object.setUpdated(true);
					dataProvider.refresh();
				}
				object.setMinimum(Double.parseDouble(value));
			}
		});
		
		Column<LoaderMetricValue, String> colMax = new Column<LoaderMetricValue, String>(new EditTextCell()) {
			@Override
			public String getValue(LoaderMetricValue object) {
				return String.valueOf(object.getMaximum());
			}
		};
		colMax.setFieldUpdater(new FieldUpdater<LoaderMetricValue, String>() {	
			@Override
			public void update(int index, LoaderMetricValue object, String value) {
				if(Double.parseDouble(value) != object.getMaximum()) {
					object.setUpdated(true);
					dataProvider.refresh();
				}
				object.setMaximum(Double.parseDouble(value));
			}
		});
		
		dataGrid.addColumn(colStatus, "");
		
		dataGrid.addColumn(colValue, Constantes.LBL.value());
		dataGrid.addColumn(colObj,  Constantes.LBL.objectives());
		dataGrid.addColumn(colMin, "Minimum");
		dataGrid.addColumn(colMax, "Maximum");
		
		dataGrid.setColumnWidth(colStatus, "35px");
		dataGrid.setColumnWidth(colValue, "100px");
		dataGrid.setColumnWidth(colObj, "100px");
		dataGrid.setColumnWidth(colMin, "100px");
		dataGrid.setColumnWidth(colMax, "100px");
		
		dataGrid.setEmptyTableWidget(new Label("No value for the selection"));
		
		//add the axis columns
		int i = 0;
		for(final AxisInfo axis : dataContainer.get(0).getAxisInfos()) {
			final int lineIndex = i;
			
			MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
			
			List<String> axisValues = getAxisValuesAsString(axis);
			oracle.addAll(axisValues);
			
			Column<LoaderMetricValue, String> axisCol = new Column<LoaderMetricValue, String>(new SuggestBoxCell(oracle)) {
				@Override
				public String getValue(LoaderMetricValue object) {
					int index = lineIndex;
					return object.getMembers().get(index).getLabel() + " : " + object.getMembers().get(index).getValue();
				}
			};
			axisCol.setFieldUpdater(new FieldUpdater<LoaderMetricValue, String>() {	
				@Override
				public void update(int index, LoaderMetricValue object, String value) {
					
					try {
						String val = value.split(" : ")[1];
//						int val = Integer.parseInt(v);
						LevelMember mem = axis.getMemberKeys().get(val);
						
						if(mem != object.getMembers().get(lineIndex) && mem != null) {
							object.getMembers().set(lineIndex, mem);
							object.setUpdated(true);
							dataProvider.refresh();
						}
					} catch (Exception e) {
						//e.printStackTrace();
					}
				}
			});
			i++;
			dataGrid.addColumn(axisCol, axis.getAxis().getName());
			dataGrid.setColumnWidth(axisCol, "250px");
		}
		
		Column<LoaderMetricValue, ImageResource> colDetail = new Column<LoaderMetricValue, ImageResource>(new ClickableImageResourceCell()) {
			@Override
			public ImageResource getValue(LoaderMetricValue object) {
				return null;
			}
		};
		colDetail.setFieldUpdater(new FieldUpdater<LoaderMetricValue, ImageResource>() {		
			@Override
			public void update(int index, LoaderMetricValue object, ImageResource value) {
//				Window.alert("It works !");
			}
		});
		
		Column<LoaderMetricValue, ImageResource> colDelete = new Column<LoaderMetricValue, ImageResource>(new ClickableImageResourceCell()) {
			@Override
			public ImageResource getValue(LoaderMetricValue object) {
				if(object.isDeleted()) {
					return ImageResources.INSTANCE.fmloader_line_restore_16_color();
				}
				return ImageResources.INSTANCE.fmloader_line_delete_16_color();
			}
		};
		colDelete.setFieldUpdater(new FieldUpdater<LoaderMetricValue, ImageResource>() {		
			@Override
			public void update(int index, LoaderMetricValue object, ImageResource value) {
				if(object.isDeleted()) {
					object.setDeleted(false);
					dataProvider.refresh();	
				}else {
					object.setDeleted(true);
					dataProvider.refresh();
				}
			}
		});
		
		if (canModify) {
			dataGrid.addColumn(colDetail, "");
			dataGrid.setColumnWidth(colDetail, 50, Unit.PX);
			dataGrid.addColumn(colDelete, "");
			dataGrid.setColumnWidth(colDelete, 50, Unit.PX);
		}
		
		dataProvider = new ListDataProvider<LoaderMetricValue>();
		dataProvider.addDataDisplay(dataGrid);
		
	    SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
	    SimplePager pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
	    pager.addStyleName("pageGrid");
	    pager.setDisplay(dataGrid);
	    
	    newValuePanel = new MetricNewValuePanel(data, this);
	    this.add(dataGrid);
	    this.add(pager);
	    if (canModify) {
	    	this.add(newValuePanel);
	    }
	    
    	loadValues(dataContainer);
	}
	
	public void loadValues(List<LoaderDataContainer> dataContainer) {
		List<LoaderMetricValue> values = new ArrayList<LoaderMetricValue>();
		if (dataContainer != null && !dataContainer.isEmpty()) {
			values = dataContainer.get(0).getValues();
		}
	    dataProvider.setList(values);
	}
	
	private List<String> getAxisValuesAsString(AxisInfo axis) {
		List<LevelMember> members = new ArrayList<LevelMember>(axis.getMemberKeys().values());
		Collections.sort(members, new Comparator<LevelMember>() {
			@Override
			public int compare(LevelMember o1, LevelMember o2) {
				return comparator.compare(o1.getLabel(), o2.getLabel());
			}
		});
		
		List<String> result = new ArrayList<String>();
		for(LevelMember member : members) {
			result.add(member.getLabel() + " : " + member.getValue());
		}
		return result;
	}

	private class SuggestBoxCell extends EditTextCell {
		
		private SuggestOracle oracle;

		public SuggestBoxCell(SuggestOracle oracle) {
			super();
			this.oracle = oracle;
		}
		
		@Override
		public void onBrowserEvent(com.google.gwt.cell.client.Cell.Context context, Element parent, String value, NativeEvent event, ValueUpdater<String> valueUpdater) {
	        InputElement input = parent.getFirstChild().<InputElement> cast();
	       
	        if ("keydown".equals(event.getType())) {
	            TextBox textBox = new CustomTextBox(input);
	            SuggestBoxWithPublicMethod suggestBox = new SuggestBoxWithPublicMethod(oracle, textBox);
	            suggestBox.onAttach();
	        }
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
		}
		
	}
	
	private class CustomTextBox extends TextBox {
		
		public CustomTextBox(Element element) {
			super(element);
		}
		
	}
	
	private class SuggestBoxWithPublicMethod extends SuggestBox {
		
		public SuggestBoxWithPublicMethod(SuggestOracle oracle, TextBox textBox) {
			super(oracle, textBox);
		}

		@Override
		public void onAttach() {
			super.onAttach();
		}
	}

	public void addLine(LoaderMetricValue value) {
		
		//XXX check if exists
		boolean exists = false;
		LOOP:for(LoaderMetricValue val : data.getValues()) {
			for(int i = 0 ; i < value.getMembers().size() ; i++) {
				if(val.getMembers().get(i).equals(value.getMembers().get(i))) {
					exists = true;
				}
				else {
					exists = false;
					break;
				}
//				if(exists) {
//					break LOOP;
//				}
			}
		}
		if(!exists) {
			data.addValue(value);
		}
		
		dataProvider.setList(data.getValues());
	}

	public LoaderDataContainer getValues() {
		data.setValues(new ArrayList<LoaderMetricValue>(dataProvider.getList()) );
		return data;
	}
	
	public String getWidth() {
		return newValuePanel.getWidth();
	}
}
