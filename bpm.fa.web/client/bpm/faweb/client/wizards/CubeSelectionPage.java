package bpm.faweb.client.wizards;

import java.util.List;

import bpm.faweb.client.MainPanel;
import bpm.faweb.client.services.FaWebService;
import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

public class CubeSelectionPage extends Composite implements IGwtPage {
	
	private static CubeSelectionPageUiBinder uiBinder = GWT.create(CubeSelectionPageUiBinder.class);

	interface CubeSelectionPageUiBinder extends UiBinder<Widget, CubeSelectionPage> {}

	interface MyStyle extends CssResource {
		
	}

	@UiField
	SimplePanel panelContent;

	@UiField
	MyStyle style;
	
	@UiField
	ListBox lstCubes;

	private IGwtWizard parent;
	private int index;

	private MainPanel mainCompParent;
	private String selectedCubeName;
	
	public CubeSelectionPage(IGwtWizard parent, int index, MainPanel mainCompParent) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		this.mainCompParent = mainCompParent;
	}

	public void browseFASDModel(int fasdId) {
		parent.showWaitPart(true);

		FaWebService.Connect.getInstance().browseFASDModel(mainCompParent.getKeySession(), fasdId, new AsyncCallback<List<String>>() {
			
			@Override
			public void onSuccess(List<String> result) {
				parent.showWaitPart(false);
				
				if (result != null) {
					fillCubeId(result);
				}
			}

			@Override
			public void onFailure(Throwable ex) {
				ex.printStackTrace();
				
				parent.showWaitPart(false);
			}
		});
	}

	public void fillCubeId(List<String> cubes) {
		lstCubes.clear();
		
		if (cubes.isEmpty()) {
			lstCubes.addItem("No Cube", "No Cube");
		}
		else {
			for (String currName : cubes) {
				lstCubes.addItem(currName, currName);
			}
		}
		lstCubes.setVisibleItemCount(cubes.size() + 1);


		selectedCubeName = lstCubes.getValue(lstCubes.getSelectedIndex());
		
		parent.updateBtn();
	}
	
	@UiHandler("lstCubes")
	public void onCubeChange(ChangeEvent event) {
		selectedCubeName = ((ListBox) event.getSource()).getValue(((ListBox) event.getSource()).getSelectedIndex());
		
		parent.updateBtn();
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return selectedCubeName != null && !selectedCubeName.equals("");
	}

	public String getSelectedCubeName() {
		return selectedCubeName;
	}
}
