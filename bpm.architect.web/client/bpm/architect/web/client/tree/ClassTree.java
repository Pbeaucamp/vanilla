package bpm.architect.web.client.tree;

import bpm.architect.web.client.services.ArchitectService;
import bpm.gwt.commons.client.loading.GreyAbsolutePanel;
import bpm.gwt.commons.client.loading.IWait;
import bpm.gwt.commons.client.loading.WaitAbsolutePanel;
import bpm.gwt.commons.client.services.GwtCallbackWrapper;
import bpm.vanilla.platform.core.beans.resources.ClassDefinition;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ClassTree extends Composite implements IWait, SelectionHandler<TreeItem> {

	private static MetadataTreeUiBinder uiBinder = GWT.create(MetadataTreeUiBinder.class);

	interface MetadataTreeUiBinder extends UiBinder<Widget, ClassTree> {
	}

	@UiField
	HTMLPanel mainPanel;

	@UiField
	Tree tree;

	private boolean isCharging = false;
	private GreyAbsolutePanel greyPanel;
	private WaitAbsolutePanel waitPanel;

	private IRulesListener parent;

	public ClassTree(IRulesListener parent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;

		tree.addSelectionHandler(this);
	}

	public void loadRules(ClassDefinition selectedClass) {
		ArchitectService.Connect.getInstance().buildClassDefinition(selectedClass, new GwtCallbackWrapper<ClassDefinition>(this, true, true) {

			@Override
			public void onSuccess(ClassDefinition result) {
				tree.clear();

				if (result.getClasses() != null) {
					for (ClassDefinition classDef : result.getClasses()) {
						ClassTreeItem<ClassDefinition> item = new ClassTreeItem<ClassDefinition>(new TreeObjectWidget<ClassDefinition>(classDef));
						tree.addItem(item);
					}
				}
			}
		}.getAsyncCallback());
	}

	public Object getSelectedItem() {
		if (tree.getSelectedItem() != null) {
			ClassTreeItem<?> item = (ClassTreeItem<?>) tree.getSelectedItem();
			return item.getItem();
		}

		return null;
	}

	public void refreshSelectedItemLabel() {
		if (tree.getSelectedItem() != null) {
			ClassTreeItem<?> item = (ClassTreeItem<?>) tree.getSelectedItem();
			item.refreshLabel();
		}
	}

	@Override
	public void onSelection(SelectionEvent<TreeItem> event) {
		TreeItem item = event.getSelectedItem();
		if (item != null && item instanceof ClassTreeItem<?>) {
			if (parent != null) {
				parent.loadRules((ClassTreeItem<?>) item);
			}
		}
	}

	@Override
	public void showWaitPart(boolean visible) {
		if (visible && !isCharging) {
			isCharging = true;

			greyPanel = new GreyAbsolutePanel();
			waitPanel = new WaitAbsolutePanel();

			mainPanel.add(greyPanel);
			mainPanel.add(waitPanel);

			int width = mainPanel.getOffsetWidth();

			waitPanel.getElement().getStyle().setProperty("top", 50 + "px");
			if (width > 0) {
				waitPanel.getElement().getStyle().setProperty("left", ((width / 2) - 100) + "px");
			}
			else {
				waitPanel.getElement().getStyle().setProperty("left", 120 + "px");
			}
		}
		else if (!visible && isCharging) {
			isCharging = false;

			mainPanel.remove(greyPanel);
			mainPanel.remove(waitPanel);
		}
	}

	public interface IRulesListener {

		public void loadRules(ClassTreeItem<?> item);
	}
}
