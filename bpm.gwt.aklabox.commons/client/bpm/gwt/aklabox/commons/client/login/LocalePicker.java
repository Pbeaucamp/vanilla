package bpm.gwt.aklabox.commons.client.login;

import java.util.ArrayList;
import java.util.List;

import bpm.gwt.aklabox.commons.client.I18N.LabelsConstants;

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

public class LocalePicker extends Composite {
	private static final String LOCALE = "locale";
	private static final String LOCALE_ENGLISH = "en";
	private static final String LOCALE_FRENCH = "fr";
	
	private static LocalePickerUiBinder uiBinder = GWT.create(LocalePickerUiBinder.class);

	interface LocalePickerUiBinder extends UiBinder<Widget, LocalePicker> {
	}

	@UiField
	ListBox listLocale;

	public LocalePicker() {
		initWidget(uiBinder.createAndBindUi(this));
		loadLocales();
	}

	private void loadLocales() {
		List<String> locales = new ArrayList<String>();
		
		locales.add(LOCALE_FRENCH);
		locales.add(LOCALE_ENGLISH);
		
		listLocale.addItem(LabelsConstants.lblCnst.French(), LOCALE_FRENCH);
		listLocale.addItem(LabelsConstants.lblCnst.English(), LOCALE_ENGLISH);
		

		String locale = Window.Location.getParameter(LOCALE);
		if (locale != null) {
			for (String value : locales) {
				if (value.equals(locale)) {
					listLocale.setSelectedIndex(locales.indexOf(value));
				}
			}
		}
		else {
			//UrlBuilder newUrl = Window.Location.createUrlBuilder();
			//newUrl.setParameter(LOCALE, LOCALE_FRENCH);
			//Window.Location.assign(newUrl.buildString());
		}

	}

	private void setLocales(int index) {
		String locale = listLocale.getValue(listLocale.getSelectedIndex());
		UrlBuilder builder = Location.createUrlBuilder().setParameter(LOCALE, locale);
		Window.Location.replace(builder.buildString());
	}

	@UiHandler("listLocale")
	void onChangeLocales(ChangeEvent e) {
		int index = listLocale.getSelectedIndex();
		setLocales(index);
	}
}
