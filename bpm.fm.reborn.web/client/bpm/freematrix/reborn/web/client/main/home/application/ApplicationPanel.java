package bpm.freematrix.reborn.web.client.main.home.application;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bpm.fm.api.model.Axis;
import bpm.fm.api.model.FactTable;
import bpm.fm.api.model.FactTableAxis;
import bpm.fm.api.model.Level;
import bpm.fm.api.model.Observatory;
import bpm.fm.api.model.Theme;
import bpm.freematrix.reborn.web.client.i18n.LabelConstants;
import bpm.freematrix.reborn.web.client.images.Images;
import bpm.freematrix.reborn.web.client.main.header.FreeMatrixHeader;
import bpm.freematrix.reborn.web.client.main.home.HomeView;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ApplicationPanel extends Composite {

	private static ApplicationPanelUiBinder uiBinder = GWT
			.create(ApplicationPanelUiBinder.class);

	interface ApplicationPanelUiBinder extends
			UiBinder<Widget, ApplicationPanel> {
	}
	
	private static ApplicationPanel instance;

	@UiField Tree applicationTree;
	
	@UiField
	Image imgCollapse, imgExpand;
	
	@UiField
	Label lblAxis;
	
	private List<Axis> listAxis ;
	private HomeView homeView;
	
	public static ApplicationPanel getInstance() {
		return instance;
	}
	
	public ApplicationPanel(HomeView homeView) {
		initWidget(uiBinder.createAndBindUi(this));
		
		this.homeView = homeView;
		
		instance = this;
		
		imgExpand.setVisible(false);
	}
	
	public void createAxisTree(){
		
		Set<Axis> setAxis = new HashSet<Axis>();
		if(FreeMatrixHeader.getInstance().getSelectedTheme() != -1) {
			setAxis.addAll(FreeMatrixHeader.getInstance().getSelectedThemeObject().getAxis());
		}
		else {
			if(FreeMatrixHeader.getInstance().getSelectedObs() != -1) {
				for(Theme th : FreeMatrixHeader.getInstance().getSelectedObsObject().getThemes()) {
					setAxis.addAll(th.getAxis());
				}
			}
			else {
				for(Observatory obs : FreeMatrixHeader.getInstance().getObservatoriesForSelection()) {
					for(Theme th : obs.getThemes()) {
						setAxis.addAll(th.getAxis());
					}
				}
			}
		}
		
		listAxis = new ArrayList<Axis>(setAxis);
		
		applicationTree.clear();
		for(Axis axis : listAxis){
			AxeTreeItem axeItem = new AxeTreeItem(axis);
			applicationTree.addItem(axeItem);
			applicationTree.addItem(axeItem);
			for(Level level : axis.getChildren()) {
				LevelTreeItem levelItem = new LevelTreeItem(level);
				axeItem.addItem(levelItem);
			}
		}
	}

	
	class AxeTreeItem extends TreeItem {
		private Axis axis;
		
		public AxeTreeItem(Axis axis) {
			super(new HTML(new Image(Images.INSTANCE.dimension()) + " " + axis.getName()));
			this.axis = axis;
			
		}
		
		public Axis getAxe() {
			return axis;
		}
	}
	
	class LevelTreeItem extends TreeItem{
		private Level level;
		
		public LevelTreeItem(Level level) {
			super(new HTML(new Image(Images.INSTANCE.level()) + " " + level.getName()));
//			super(new SafeHtmlBuilder().appendEscaped(new Image(Images.INSTANCE.level()) + " " + level.getName()).toSafeHtml() );
//			super(new Image(Images.INSTANCE.level()) + " " + level.getName());
			this.level = level;
			
		}
		
		public Level getLevel() {
			return level;
		}

	}

	public Level getSelectedLevel() {
		TreeItem item = applicationTree.getSelectedItem();
		if(item instanceof LevelTreeItem) {
			return ((LevelTreeItem)item).getLevel();
		}
		return null;
	}
	
	public Axis getSelectedAxe() {
		TreeItem item = applicationTree.getSelectedItem();
		if(item instanceof AxeTreeItem) {
			return ((AxeTreeItem)item).getAxe();
		}
		else if(item instanceof LevelTreeItem) {
			return ((AxeTreeItem)item.getParentItem()).getAxe();
		}
		return null;
	}
	
	@UiHandler("applicationTree")
	void onSelectionApplication(SelectionEvent<TreeItem> e){
		if(e.getSelectedItem() instanceof AxeTreeItem){
			
			if(homeView.getMetricValue().getMetric().getFactTable() instanceof FactTable) {
				for(FactTableAxis a : ((FactTable)homeView.getMetricValue().getMetric().getFactTable()).getFactTableAxis()) {
					if(a.getAxis().equals(getSelectedAxe())) {
						homeView.generateInformationPanel(true, getSelectedAxe(), homeView.getMetricValue());
						return;
					}
				}
			}
			Window.alert(LabelConstants.lblCnst.metricAxisNotLinked());
		}else{
			if(homeView.getMetricValue().getMetric().getFactTable() instanceof FactTable) {
				for(FactTableAxis a : ((FactTable)homeView.getMetricValue().getMetric().getFactTable()).getFactTableAxis()) {
					if(a.getAxis().equals(getSelectedAxe())) {
						homeView.generateInformationPanel(true, getSelectedLevel(), homeView.getMetricValue());
						return;
					}
				}
			}
			Window.alert(LabelConstants.lblCnst.metricAxisNotLinked());
		}
	}
	
	@UiHandler("imgCollapse")
	public void onCollapseClick(ClickEvent event) {
		homeView.collapseApplicationPanel(false);
	}

	@UiHandler("imgExpand")
	public void onExpandClick(ClickEvent event) {
		homeView.collapseApplicationPanel(true);
	}

	public void changeSize(boolean isCollapse) {
		if(isCollapse){
			this.getParent().setWidth("300px");
			applicationTree.setVisible(true);
			imgCollapse.setVisible(true);
			lblAxis.setVisible(true);
			imgExpand.setVisible(false);
			
		} else {
			this.getParent().setWidth("40px");
			applicationTree.setVisible(false);
			imgCollapse.setVisible(false);
			lblAxis.setVisible(false);
			imgExpand.setVisible(true);
		}
		
	}
}
