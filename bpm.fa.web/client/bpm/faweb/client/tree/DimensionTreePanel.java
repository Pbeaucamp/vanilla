package bpm.faweb.client.tree;

import java.util.ArrayList;
import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.dnd.DraggableTreeItem;
import bpm.faweb.client.images.FaWebImage;
import bpm.faweb.client.listeners.DimensionTreeListener;
import bpm.faweb.client.services.FaWebService;
import bpm.faweb.shared.infoscube.ItemDim;
import bpm.faweb.shared.infoscube.ItemHier;
import bpm.faweb.shared.infoscube.ItemOlapMember;
import bpm.gwt.commons.client.loading.IWait;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.Widget;

public class DimensionTreePanel extends Composite {

	private static DimensionTreePanelUiBinder uiBinder = GWT.create(DimensionTreePanelUiBinder.class);

	interface DimensionTreePanelUiBinder extends UiBinder<Widget, DimensionTreePanel> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	MyStyle style;

	@UiField
	Tree treeDimension;

	private MainPanel mainPanel;
	private IWait waitPanel;
	private String[] unames;

	private List<FaWebTreeItem> extremite = new ArrayList<FaWebTreeItem>();

	private boolean selected = false;

	public DimensionTreePanel(MainPanel mainPanel, IWait waitPanel, String[] unames) {
		initWidget(uiBinder.createAndBindUi(this));
		this.mainPanel = mainPanel;
		this.waitPanel = waitPanel;
		this.unames = unames;
	}

	public void refresh() {
		loadDimensionTree();
	}
	
	private void loadDimensionTree() {
		treeDimension.clear();

		List<ItemDim> dimensions = mainPanel.getInfosReport().getDims();
		
		if (unames!=null){
			List<ItemDim> dims= new ArrayList<ItemDim>();
			for (ItemDim dimfocus : dimensions) {
				for(int i=0; i<unames.length; i++){
					if (unames[i].equals(dimfocus.getUname()))
					{
						dims.add(dimfocus);
						break;
					}
				}
			}
			if (!dims.isEmpty())
				dimensions=dims;
		}
		
		for (ItemDim dimfocus : dimensions) {
			CheckBox cbdim = new CheckBox();
			cbdim.setName(dimfocus.getUname());

			HorizontalPanel hpdim = new HorizontalPanel();
			Label lbldim = new HTML(new Image(FaWebImage.INSTANCE.dimension()) + " " + dimfocus.getName());
			hpdim.add(cbdim);
			hpdim.add(lbldim);
			cbdim.addClickHandler(new DimensionTreeListener(mainPanel));
			FaWebTreeItem treedim = new FaWebTreeItem(hpdim, dimfocus);

			for (ItemHier hierafocus : dimfocus.getChilds()) {
				CheckBox cbhiera = new CheckBox();
				cbhiera.setName(hierafocus.getUname());
				HorizontalPanel hphiera = new HorizontalPanel();
				Label lblhiera = new HTML(new Image(FaWebImage.INSTANCE.obj_folder()) + " " + hierafocus.getName());
				hphiera.add(cbhiera);
				hphiera.add(lblhiera);
				cbhiera.addClickHandler(new DimensionTreeListener(mainPanel));
				FaWebTreeItem treehiera = new FaWebTreeItem(hphiera, hierafocus);
				lblhiera.addClickHandler(new ItemClickHandler(waitPanel, mainPanel, treehiera));

				ItemDim focus = null;

				if (hierafocus.getOlapChilds() != null && hierafocus.getOlapChilds().size() > 0) {
					focus = new ItemDim(hierafocus.getOlapChilds().get(0).getName(), hierafocus.getOlapChilds().get(0).getUname(), hierafocus.getHiera(), hierafocus.isGeolocalisable());
				}
				else {
					focus = new ItemDim(hierafocus.getAllMember(), hierafocus.getUname() + ".[All " + dimfocus.getName() + "]", hierafocus.getHiera(), hierafocus.isGeolocalisable());
				}

				CheckBox cbn = new CheckBox();
				cbn.setName(focus.getUname());
				HorizontalPanel hpn = new HorizontalPanel();
				Label imgn = new HTML(new Image(FaWebImage.INSTANCE.obj_file()) + "&nbsp;&nbsp;&nbsp;");
				imgn.addStyleName("pointer");

				DraggableTreeItem txtn = new DraggableTreeItem(mainPanel, focus.getName(), focus.getUname());
				hpn.add(cbn);
				hpn.add(imgn);
				hpn.add(txtn);

				cbn.addClickHandler(new DimensionTreeListener(mainPanel));

				FaWebTreeItem lvl = new FaWebTreeItem(hpn, focus);

				imgn.addClickHandler(new ItemClickHandler(waitPanel, mainPanel, lvl));

				extremite.add(lvl);
				treehiera.addItem(lvl);

				treedim.addItem(treehiera);
			}
			treeDimension.addItem(treedim);
		}
	}

	public void addNext(List<ItemOlapMember> l, FaWebTreeItem lb) {
		if (lb == null) {
			return;
		}
		extremite.remove(lb);
		for (ItemOlapMember focus : l) {
			CheckBox cb = new CheckBox();
			cb.setName(focus.getUname());
			HorizontalPanel hp = new HorizontalPanel();
			Label imgmb = new HTML(new Image(FaWebImage.INSTANCE.obj_file()) + "&nbsp;&nbsp;&nbsp;");
			imgmb.addStyleName("pointer");
			DraggableTreeItem txtmb = new DraggableTreeItem(mainPanel, focus.getName(), focus.getUname());
			hp.add(cb);
			hp.add(imgmb);
			hp.add(txtmb);

			cb.addClickHandler(new DimensionTreeListener(mainPanel));
			FaWebTreeItem curr = new FaWebTreeItem(hp, focus);
			extremite.add(curr);
			try {
				lb.addItem(curr);
			} catch (Exception e) {
				e.printStackTrace();
			}

			imgmb.addClickHandler(new ItemClickHandler(waitPanel, mainPanel, curr));
		}
	}

	public List<FaWebTreeItem> getExtremite() {
		return extremite;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public Tree getTreeDim() {
		return treeDimension;
	}

	public FaWebTreeItem findRootItem(String currMb) {

		for (int i = 0; i < treeDimension.getItemCount(); i++) {
			for (int j = 0; j < treeDimension.getItem(i).getChildCount(); j++) {
				for (int k = 0; k < treeDimension.getItem(i).getChild(j).getChildCount(); k++) {
					FaWebTreeItem item = (FaWebTreeItem) treeDimension.getItem(i).getChild(j).getChild(k);
					if (currMb.equals(item.getItemDim().getUname()) && item.getChildCount() <= 0) {
						return item;
					}
				}
			}
		}

		return null;
	}
	
	public FaWebTreeItem findRootItem2(String currMb) {

		for (int i = 0; i < treeDimension.getItemCount(); i++) {
			for (int j = 0; j < treeDimension.getItem(i).getChildCount(); j++) {
				for (int k = 0; k < treeDimension.getItem(i).getChild(j).getChildCount(); k++) {
					FaWebTreeItem item = (FaWebTreeItem) treeDimension.getItem(i).getChild(j).getChild(k);
					if (currMb.startsWith(item.getItemDim().getUname())) {
						return item;
					}
				}
			}
		}

		return null;
	}

	public class ItemClickHandler implements ClickHandler {

		private IWait waitPanel;
		private MainPanel mainPanel;

		private FaWebTreeItem item;
		private FaWebTreeItem otherItem;

		private boolean onChart = false;

		public ItemClickHandler(IWait waitPanel, MainPanel mainPanel, FaWebTreeItem i) {
			this.waitPanel = waitPanel;
			this.mainPanel = mainPanel;
			this.item = i;
		}

		public void onClick(ClickEvent arg0) {
			boolean isExtremite = false;
			for (FaWebTreeItem it : extremite) {
				if (it.getItemDim().getUname().equals(item.getItemDim().getUname())) {
					isExtremite = true;
					break;
				}
			}

			if (isExtremite) {

				waitPanel.showWaitPart(true);

				FaWebService.Connect.getInstance().addChildsService(mainPanel.getKeySession(), item.getItemDim(), new AsyncCallback<List<ItemOlapMember>>() {
					@Override
					public void onSuccess(List<ItemOlapMember> result) {
						if (result != null) {
							if (onChart) {
								addNext(result, otherItem);
								addNext(result, item);

							}
							else {
								addNext(result, item);
							}

							waitPanel.showWaitPart(false);
						}
					}

					@Override
					public void onFailure(Throwable ex) {
						ex.printStackTrace();

						waitPanel.showWaitPart(false);
					}
				});
			}
		}
	}
}
