package bpm.freematrix.reborn.web.client.i18n;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Window.Location;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;

public class LocalePicker extends Composite{

	private static LocalePickerUiBinder uiBinder = GWT
			.create(LocalePickerUiBinder.class);

	interface LocalePickerUiBinder extends UiBinder<Widget, LocalePicker> {
	}
	
	@UiField ListBox listLocale;

	public LocalePicker() {
		initWidget(uiBinder.createAndBindUi(this));
		loadLocales();
	}

	private void loadLocales() {
		List<String> locales = new ArrayList<String>();

		locales.add(LabelConstants.LOCALE_ENGLISH);
		locales.add(LabelConstants.LOCALE_FRENCH);

		listLocale.addItem(LabelConstants.lblCnst.English());
		listLocale.addItem(LabelConstants.lblCnst.French());

		String locale = Window.Location.getParameter(LabelConstants.LOCALE);
		if (locale != null) {
			for (String value : locales) {
				if (value.equals(locale)) {
					listLocale.setSelectedIndex(locales.indexOf(value));
				}
			}
			
		} else {
			UrlBuilder builder = Location.createUrlBuilder().setParameter(
					LabelConstants.LOCALE, LabelConstants.LOCALE_ENGLISH);
			Window.Location.replace(builder.buildString());
		}
		
	}
	
	private void setLocales(int index) {
		String locale = null;
		if (index == 0) {
			locale = LabelConstants.LOCALE_ENGLISH;
		} else if (index == 1) {
			locale = LabelConstants.LOCALE_FRENCH;
		}
		UrlBuilder builder = Location.createUrlBuilder().setParameter(
				LabelConstants.LOCALE, locale);
		Window.Location.replace(builder.buildString());
	}
	
	@UiHandler("listLocale")
	void onChangeLocales(ChangeEvent e) {
		int index = listLocale.getSelectedIndex();
		setLocales(index);
	}	
}
