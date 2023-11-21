package bpm.faweb.client.tree;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.listeners.MeasureTreeListener;
import bpm.faweb.shared.infoscube.ItemMes;
import bpm.faweb.shared.infoscube.ItemMesGroup;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class MeasureTreePanel extends Composite {

	private static MeasureTreePanelUiBinder uiBinder = GWT.create(MeasureTreePanelUiBinder.class);

	interface MeasureTreePanelUiBinder extends UiBinder<Widget, MeasureTreePanel> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	Tree treeMeasure;
	
	private MainPanel mainPanel;
	private String[] unames;

	public MeasureTreePanel(MainPanel mainPanel, String[] unames) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.unames=unames;
	}

	public void refresh() {
		loadMeasure();
	}

	private void loadMeasure() {
		treeMeasure.clear();

		List<ItemMesGroup> mes = mainPanel.getInfosReport().getMeasuresGroup();
		
		if (unames!=null){
			List<ItemMesGroup> measuregroups= new ArrayList<ItemMesGroup>();
			for (ItemMesGroup group : mes) {
				boolean find=false;
				List<ItemMes> currMes = group.getChilds();
				for (ItemMes ffocus : currMes) {
					
					for(int i=0; i<unames.length; i++){
						if (unames[i].equals(ffocus.getUname()))
						{
							measuregroups.add(group);
							find=true;
							break;
						}
					}
					if(find)
						break;
				}			
			}
			if (!measuregroups.isEmpty())
				mes=measuregroups;
		}
		
		
		for (ItemMesGroup measureGr : mes) {
			CheckBox cb = new CheckBox();
			cb.setName(measureGr.getUname());
			HorizontalPanel hp = new HorizontalPanel();
			Label img = new HTML(new Image(FaWebImage.INSTANCE.obj_folder()) + " ");
			final Label txt = new Label(" " + measureGr.getName());
			hp.add(cb);
			hp.add(img);
			hp.add(txt);
			hp.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);

			cb.addClickHandler(new MeasureTreeListener(mainPanel));
			FaWebTreeItem root = new FaWebTreeItem(hp, measureGr.getUname());

			List<ItemMes> currMes = measureGr.getChilds();
			
			if (unames!=null){
				List<ItemMes> measure= new ArrayList<ItemMes>();
				for (ItemMes mesfocus : currMes) {
					for(int i=0; i<unames.length; i++){
						if (unames[i].equals(mesfocus.getUname()))
						{
							measure.add(mesfocus);
							break;
						}
					}
				}
				if (!measure.isEmpty())
					currMes=measure;
			}
			
			for (ItemMes ffocus : currMes) {
				CheckBox cbmes = new CheckBox();
				cbmes.setName(ffocus.getUname());
				HorizontalPanel hpmes = new HorizontalPanel();
				Label imgmes = new HTML(new Image(FaWebImage.INSTANCE.obj_measure()) + "&nbsp;&nbsp;&nbsp;");
				imgmes.addStyleName("pointer");

				DraggableTreeItem txtmes = new DraggableTreeItem(mainPanel, ffocus.getName(), ffocus.getUname());
				hpmes.add(cbmes);
				hpmes.add(imgmes);
				hpmes.add(txtmes);
				hpmes.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);

				cbmes.addClickHandler(new MeasureTreeListener(mainPanel));
				FaWebTreeItem lb = new FaWebTreeItem(hpmes, ffocus.getUname());

				root.addItem(lb);
			}
			treeMeasure.addItem(root);
		}
	}

	public Tree getTreeMes() {
		return treeMeasure;
	}

}
