package bpm.gwt.commons.client.wizard;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;

import bpm.gwt.commons.client.custom.ListBoxWithButton;
import bpm.gwt.commons.client.custom.TimeBox;
import bpm.gwt.commons.shared.utils.CommonConstants;
import bpm.gwt.commons.shared.viewer.LaunchReportInformations;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.Group;

public class PublicUrlDefinitionPage extends Composite implements IGwtPage {
	private static AddTaskDefinitionPageUiBinder uiBinder = GWT.create(AddTaskDefinitionPageUiBinder.class);

	interface AddTaskDefinitionPageUiBinder extends UiBinder<Widget, PublicUrlDefinitionPage> {
	}

	interface MyStyle extends CssResource {

	}

	@UiField
	DateBox dateBoxStop;

	@UiField(provided = true)
	TimeBox timeStop;

	@UiField
	ListBoxWithButton<Group> lstGroups;
	
	@UiField
	ListBoxWithButton<String> lstFormats;

	@UiField
	MyStyle style;

	private CreatePublicUrlWizard parent;
	private int index;

	public PublicUrlDefinitionPage(CreatePublicUrlWizard parent, int index, List<Group> availableGroups, LaunchReportInformations itemInfos) {
		Date d = new Date();
		d.setYear(d.getYear() + 10);
		timeStop = new TimeBox(d);
		initWidget(uiBinder.createAndBindUi(this));
		this.parent = parent;
		this.index = index;

		DateTimeFormat dateFormat = DateTimeFormat.getFormat(PredefinedFormat.DATE_LONG);
		dateBoxStop.setFormat(new DateBox.DefaultFormat(dateFormat));
		dateBoxStop.setValue(d);
		
		if (availableGroups != null) {
			for (Group group : availableGroups) {
				lstGroups.addItem(group.getName(), group);
			}
		}
		
		if (itemInfos != null) {
			for (int i = 0; i < CommonConstants.FORMAT_DISPLAY.length; i++) {
				if (i < 2 || (!(itemInfos.getItem().getType() == IRepositoryApi.CUST_TYPE && itemInfos.getItem().getSubType() == IRepositoryApi.JASPER_REPORT_SUBTYPE))) {
					lstFormats.addItem(CommonConstants.FORMAT_DISPLAY[i], CommonConstants.FORMAT_VALUE[i]);
				}
			}
		}
		else {
			lstFormats.setVisible(false);
		}
	}

	@Override
	public boolean canGoBack() {
		return false;
	}

	@Override
	public boolean canGoFurther() {
		return isComplete() && parent.hasParameters() ? true : false;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public boolean isComplete() {
		return true;
	}

	public Date getEndDate() {
		Date stopDate = dateBoxStop.getValue();
		int hours = timeStop.getSelectedHours();
		int minutes = timeStop.getSelectedMinutes();

		stopDate.setTime(stopDate.getTime() + hours * TimeBox.HOURS + minutes * TimeBox.MINUTES);
		return stopDate;
	}
	
	public String getSelectedFormat() {
		return lstFormats.getSelectedItem();
	}
	
	public int getSelectedGroup() {
		Group group = lstGroups.getSelectedObject();
		return group.getId();
	}
}
