package bpm.vanilla.portal.client.wizard.map;

import bpm.gwt.commons.client.wizard.IGwtPage;
import bpm.gwt.commons.client.wizard.IGwtWizard;
import bpm.vanilla.map.core.design.MapVanilla;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class AddMapParamPage extends Composite implements IGwtPage {
	private static AddMapParamPageUiBinder uiBinder = GWT.create(AddMapParamPageUiBinder.class);

	interface AddMapParamPageUiBinder extends UiBinder<Widget, AddMapParamPage> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	Label lblZoom, lblOriginLat, lblOriginLong, lblBoundLeft, lblBoundBottom, lblBoundRight, lblBoundTop, lblProjection;

	@UiField
	IntegerBox txtZoom;

	@UiField
	TextBox txtOriginLat, txtOriginLong, txtBoundLeft, txtBoundBottom, txtBoundRight, txtBoundTop;

	@UiField
	TextBox txtProjection;

	@UiField
	MyStyle style;

	private IGwtWizard parent;
	private int index;

	public AddMapParamPage(IGwtWizard parent, int index, MapVanilla selectedMap) {
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;
		
		if (selectedMap != null) {
			txtZoom.setValue(selectedMap.getZoom());
			txtOriginLat.setValue(selectedMap.getOriginLat().toString());
			txtOriginLong.setValue(selectedMap.getOriginLong().toString());
			txtBoundLeft.setValue(selectedMap.getBoundLeft().toString());
			txtBoundBottom.setValue(selectedMap.getBoundBottom().toString());
			txtBoundRight.setValue(selectedMap.getBoundRight().toString());
			txtBoundTop.setValue(selectedMap.getBoundTop().toString());
			txtProjection.setText(selectedMap.getProjection().toString());
		}
	}

	@Override
	public boolean canGoBack() {
		return true;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete();
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		String zoom = txtZoom.getText();
		String originLat = txtOriginLat.getText();
		String originLong = txtOriginLong.getText();
		String boundLeft = txtBoundLeft.getText();
		String boundBottom = txtBoundBottom.getText();
		String boundRight = txtBoundRight.getText();
		String boundTop = txtBoundTop.getText();
		String projection = txtProjection.getText();
		
		boolean res = zoom.isEmpty() ? false : originLat.isEmpty() ? false : originLong.isEmpty() ? false : boundLeft.isEmpty() ? false : boundBottom.isEmpty() ? false : 
			boundRight.isEmpty() ? false : boundTop.isEmpty() ? false : projection.isEmpty() ? false : true;

		if (getOriginLat() == Double.MAX_VALUE || getOriginLong() == Double.MAX_VALUE || getBoundLeft() == Double.MAX_VALUE || getBoundBottom() == Double.MAX_VALUE 
				|| getBoundRight() == Double.MAX_VALUE || getBoundTop() == Double.MAX_VALUE) {
			res = false;
		}
		return res;
	}

	@UiHandler("txtZoom")
	public void onZoomChange(ValueChangeEvent<Integer> event) {
		parent.updateBtn();
	}

	@UiHandler("txtOriginLat")
	public void onOriginLatChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	@UiHandler("txtOriginLong")
	public void onOriginLongChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	@UiHandler("txtBoundLeft")
	public void onBoundLeftChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	@UiHandler("txtBoundBottom")
	public void onBoundBottomChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	@UiHandler("txtBoundRight")
	public void onBoundRightChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	@UiHandler("txtBoundTop")
	public void onBoundTopChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	@UiHandler("txtProjection")
	public void onProjectionChange(KeyUpEvent event) {
		parent.updateBtn();
	}

	public Integer getZoom() {
		return txtZoom.getValue();
	}

	public Double getOriginLat() {
		Double res;
		try { // not pretty but it works
			res = Double.parseDouble(txtOriginLat.getValue());
		} catch (NumberFormatException e) {
			// lblErrorOriginLat.setVisible(true);
			res = Double.MAX_VALUE;
		}
		return res;
	}

	public Double getOriginLong() {
		Double res;
		try { // not pretty but it works
			res = Double.parseDouble(txtOriginLong.getValue());
		} catch (NumberFormatException e) {
			// lblErrorOriginLong.setVisible(true);
			res = Double.MAX_VALUE;
		}
		return res;
	}

	public Double getBoundLeft() {
		Double res;
		try { // not pretty but it works
			res = Double.parseDouble(txtBoundLeft.getValue());
		} catch (NumberFormatException e) {
			// lblErrorBoundLeft.setVisible(true);
			res = Double.MAX_VALUE;
		}
		return res;
	}

	public Double getBoundBottom() {
		Double res;
		try { // not pretty but it works
			res = Double.parseDouble(txtBoundBottom.getValue());
		} catch (NumberFormatException e) {
			// lblErrorBoundBottom.setVisible(true);
			res = Double.MAX_VALUE;
		}
		return res;
	}

	public Double getBoundRight() {
		Double res;
		try { // not pretty but it works
			res = Double.parseDouble(txtBoundRight.getValue());
		} catch (NumberFormatException e) {
			// lblErrorBoundRight.setVisible(true);
			res = Double.MAX_VALUE;
		}
		return res;
	}

	public Double getBoundTop() {
		Double res;
		try { // not pretty but it works
			res = Double.parseDouble(txtBoundTop.getValue());
		} catch (NumberFormatException e) {
			// lblErrorBoundTop.setVisible(true);
			res = Double.MAX_VALUE;
		}
		return res;
	}

	public String getProjection() {
		return txtProjection.getText();
	}
}
