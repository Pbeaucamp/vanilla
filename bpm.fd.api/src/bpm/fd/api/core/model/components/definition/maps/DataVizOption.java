package bpm.fd.api.core.model.components.definition.maps;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import bpm.fd.api.core.model.components.definition.IComponentOptions;

public class DataVizOption implements IComponentOptions {

	public static final int DATAPREP_ID_KEY = 0;
	public static final String DATAPREP_ID = "dataprepId";

	public static String[] standardKeys = new String[] { DATAPREP_ID };
	public static String[] i18nKeys = new String[] {};

	private int dataprepId;

	@Override
	public IComponentOptions getAdapter(Object type) {
		return this;
	}

	@Override
	public String getDefaultLabelValue(String key) {
		return null;
	}

	@Override
	public Element getElement() {
		Element e = DocumentHelper.createElement("options");
		e.addAttribute("class", DataVizOption.class.getName());

		for(int i = 0; i < standardKeys.length; i++) {
			try {
				e.addElement(standardKeys[i]).setText(getValue(standardKeys[i]));
			} catch(Exception e1) {
				System.out.println("No value for " + standardKeys[i]);
			}
		}
		return e;
	}

	@Override
	public String[] getInternationalizationKeys() {
		return i18nKeys;
	}

	@Override
	public String[] getNonInternationalizationKeys() {
		return standardKeys;
	}

	@Override
	public String getValue(String key) {
		int index = -1;

		for(int i = 0; i < standardKeys.length; i++) {
			if(standardKeys[i].equals(key)) {
				index = i;
				break;
			}
		}
		switch(index) {
			case DATAPREP_ID_KEY:
				return dataprepId + "";
			default:
				break;
		}
		return null;
	}

	@Override
	public IComponentOptions copy() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getDataprepId() {
		return dataprepId;
	}

	public void setDataprepId(int dataprepId) {
		this.dataprepId = dataprepId;
	}

}
