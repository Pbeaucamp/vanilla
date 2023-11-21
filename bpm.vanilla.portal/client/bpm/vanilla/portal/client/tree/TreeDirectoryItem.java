package bpm.vanilla.portal.client.tree;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.commons.client.custom.CustomHTML;
import bpm.gwt.commons.client.images.CommonImages;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.gwt.commons.shared.repository.PortailItemCube;
import bpm.gwt.commons.shared.repository.PortailItemFasd;
import bpm.gwt.commons.shared.repository.PortailItemFmdt;
import bpm.gwt.commons.shared.repository.PortailItemFmdtChart;
import bpm.gwt.commons.shared.repository.PortailItemFmdtDriller;
import bpm.gwt.commons.shared.repository.PortailItemReportsGroup;
import bpm.gwt.commons.shared.repository.PortailRepositoryItem;
import bpm.vanilla.portal.client.Listeners.DirectoryItemContextMenuHandler;
import bpm.vanilla.portal.client.Listeners.DirectoryItemDoubleClickHandler;
import bpm.vanilla.portal.client.images.PortalImage;
import bpm.vanilla.portal.client.panels.ContentDisplayPanel;
import bpm.vanilla.portal.client.panels.navigation.TypeViewer;
import bpm.vanilla.portal.client.services.BiPortalService;
import bpm.vanilla.portal.client.utils.ToolsGWT;

import com.google.gwt.user.client.ui.Image;

public class TreeDirectoryItem extends TreeItemOk {

	private ContentDisplayPanel contentDisplayPanel;
	private PortailRepositoryItem item;

	public TreeDirectoryItem(ContentDisplayPanel contentDisplayPanel, PortailRepositoryItem item, TypeViewer typeViewer, String search) {
		super(new CustomHTML(new Image(ToolsGWT.getImageForObject(item, false)) + item.getName()), typeViewer);
		this.contentDisplayPanel = contentDisplayPanel;
		this.item = item;

		if (item instanceof PortailItemFasd) {
			List<PortailItemCube> cubes = new ArrayList<PortailItemCube>();
			for (PortailItemCube itemCube : ((PortailItemFasd) item).getCubes()) {
				if (search.isEmpty() || itemCube.getName().toLowerCase().contains(search.toLowerCase())) {
					cubes.add(itemCube);
				}
			}
			addCubes(cubes);
		}
		else if (item instanceof PortailItemFmdt) {
			this.addItem(new TreeFmdtDriller(contentDisplayPanel, new CustomHTML("..."), null, typeViewer));
			// List<PortailItemFmdtDriller> drillers = new
			// ArrayList<PortailItemFmdtDriller>();
			// for (PortailItemFmdtDriller itemDriller : ((PortailItemFmdt)
			// item).getDrillers()) {
			// if (search.isEmpty() ||
			// itemDriller.getName().toLowerCase().contains(search.toLowerCase()))
			// {
			// drillers.add(itemDriller);
			// }
			// }
			// addDrillers(drillers);
		}
		else if (item instanceof PortailItemReportsGroup) {
			List<PortailRepositoryItem> reports = new ArrayList<PortailRepositoryItem>();
			for (PortailRepositoryItem report : ((PortailItemReportsGroup) item).getReports()) {
				if (search.isEmpty() || report.getName().toLowerCase().contains(search.toLowerCase())) {
					reports.add(report);
				}
			}
			addReports(reports);
		}

		addContextMenuHandler(new DirectoryItemContextMenuHandler(contentDisplayPanel, this, typeViewer));
		addDoubleClickHandler(new DirectoryItemDoubleClickHandler(contentDisplayPanel, this));
	}

	public int getType() {
		return item.getType();
	}

	public PortailRepositoryItem getItem() {
		return item;
	}

	public void setDirectory(PortailRepositoryItem item) {
		this.item = item;
	}

	public int getSubType() {
		return item.getSubType();
	}

	private void addCubes(List<PortailItemCube> cubes) {
		for (PortailItemCube cube : cubes) {
			TreeCube tc = new TreeCube(contentDisplayPanel, new CustomHTML(new Image(PortalImage.INSTANCE.Analysis()) + " " + cube.getName()), cube, getTypeViewer());
			tc.addViews(cube.getViews());
			tc.setParent(this);

			this.addItem(tc);
		}
	}

	private void addDrillers(List<PortailItemFmdtDriller> drillers) {
		for (PortailItemFmdtDriller driller : drillers) {
			TreeFmdtDriller tc = new TreeFmdtDriller(contentDisplayPanel, new CustomHTML(new Image(PortalImage.INSTANCE.view()) + " " + driller.getName()), driller, getTypeViewer());
			tc.setParent(this);

			this.addItem(tc);

			addCharts(tc, driller.getCharts());
		}
	}

	private void addCharts(TreeFmdtDriller item, List<PortailItemFmdtChart> charts) {
		for (PortailItemFmdtChart chart : charts) {
			TreeFmdtChart tc = new TreeFmdtChart(contentDisplayPanel, new CustomHTML(new Image(CommonImages.INSTANCE.chart_item()) + " " + chart.getName()), chart, getTypeViewer());
			tc.setParent(item);

			item.addItem(tc);
		}
	}

	private void addReports(List<PortailRepositoryItem> reports) {
		for (PortailRepositoryItem report : reports) {
			TreeDirectoryItem tc = new TreeDirectoryItem(contentDisplayPanel, report, getTypeViewer(), "");
			tc.setParent(this);

			this.addItem(tc);
		}
	}

	public void open() {
		if (item instanceof PortailItemFmdt) {
			if (this.getChildCount() == 1 && this.getChild(0) instanceof TreeFmdtDriller && ((TreeFmdtDriller) this.getChild(0)).getDriller() == null) {
				BiPortalService.Connect.getInstance().loadFmdtQueries(item.getId(), new GwtCallbackWrapper<List<PortailItemFmdtDriller>>(null, false, false) {
					@Override
					public void onSuccess(List<PortailItemFmdtDriller> result) {
						TreeDirectoryItem.this.removeItems();
						List<PortailItemFmdtDriller> drillers = new ArrayList<PortailItemFmdtDriller>();
						for (PortailItemFmdtDriller itemDriller : result) {
							drillers.add(itemDriller);
						}
						addDrillers(drillers);
					}
				}.getAsyncCallback());
			}
		}
	}
}
