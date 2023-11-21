package bpm.fm.designer.web.client.panel;

import java.util.List;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.Level;
import bpm.fm.designer.web.client.ClientSession;
import bpm.fm.designer.web.client.Messages;
import bpm.fm.designer.web.client.images.Images;
import bpm.fm.designer.web.client.services.MetricService;
import bpm.gwt.commons.client.InformationsDialog;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class AxisPanel extends Composite {

	private static AxePanelUiBinder uiBinder = GWT.create(AxePanelUiBinder.class);

	interface AxePanelUiBinder extends UiBinder<Widget, AxisPanel> {
	}
	
	@UiField
	Tree treeAxes;
	
	@UiField(provided=true)
	AxisMetricsToolbar toolbar;
	
	private List<Axis> axes;

	
	public @UiConstructor AxisPanel() {
		toolbar = new AxisMetricsToolbar(AxisMetricsToolbar.AXE_TOOLBAR, this);
		
		initWidget(uiBinder.createAndBindUi(this));
		
		fillTree();		
	}

	private void fillTree() {
		
		axes = ClientSession.getInstance().getAxis();
		
		treeAxes.clear();
		
		for(Axis axe : axes) {
			AxeTreeItem axeItem = new AxeTreeItem(axe);
			treeAxes.addItem(axeItem);
			for(Level level : axe.getChildren()) {
				LevelTreeItem levelItem = new LevelTreeItem(level);
				axeItem.addItem(levelItem);
			}
		}
		
	}
	
	class AxeTreeItem extends TreeItem {
		private Axis axe;
		
		public AxeTreeItem(Axis axe) {
			super(new HTML(new Image(Images.INSTANCE.dimension()) + " " + axe.getName()));
			this.axe = axe;
			
		}
		
		public Axis getAxe() {
			return axe;
		}
	}
	
	class LevelTreeItem extends TreeItem {
		private Level level;
		
		public LevelTreeItem(Level level) {
			super(new HTML(new Image(Images.INSTANCE.level()) + " " + level.getName()));
			this.level = level;
			
		}
		
		public Level getLevel() {
			return level;
		}
	}

	public Axis getSelectedAxe() {
		TreeItem item = treeAxes.getSelectedItem();
		if(item instanceof AxeTreeItem) {
			return ((AxeTreeItem)item).getAxe();
		}
		return null;
	}

	public void refresh() {
		int themeId = -1;
		try {
			themeId = MainPanel.getInstance().getSelectedTheme();
		} catch(Exception e) {
			
		}
		int obsId = -1;
		try {
			obsId = MainPanel.getInstance().getSelectedObservatory();
		} catch(Exception e) {
			
		}
		MetricService.Connection.getInstance().getAxes(obsId, themeId,new AsyncCallback<List<Axis>>() {
			@Override
			public void onFailure(Throwable caught) {
				InformationsDialog dial = new InformationsDialog(Messages.lbl.Error(), Messages.lbl.Ok(), Messages.lbl.GetAxisError(), caught.getMessage(), caught);
				dial.center();
			}
			@Override
			public void onSuccess(List<Axis> result) {
				ClientSession.getInstance().setAxis(result);
				fillTree();
				
//				MainPanel.getInstance().reloadObservatories();
			}
		});
		
	}
}
