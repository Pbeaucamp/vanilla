package bpm.fmloader.client.constante;

import java.util.LinkedHashMap;

import bpm.fmloader.client.ConnectionServices;
import bpm.fmloader.client.ConnectionServicesAsync;
import bpm.fmloader.client.DatasServices;
import bpm.fmloader.client.DatasServicesAsync;
import bpm.fmloader.client.I18N.LabelsConstants;
import bpm.fmloader.client.images.Images;

import com.google.gwt.core.client.GWT;

public class Constantes {

	public final static Images images = (Images) GWT.create(Images.class);
	
	public final static LabelsConstants LBL = (LabelsConstants) GWT.create(LabelsConstants.class);
	
	public final static ConnectionServicesAsync CONNECTION_SERVICES = (ConnectionServicesAsync) GWT.create(ConnectionServices.class);
	
	public final static DatasServicesAsync DATAS_SERVICES = (DatasServicesAsync) GWT.create(DatasServices.class);
	
	public static LinkedHashMap<String, String> periodes = null;
	
	public static LinkedHashMap<Integer, String> mois = null;
	
	public static void init() {
		periodes = new LinkedHashMap<String, String>();
		periodes.put("YEAR", LBL.year());
		periodes.put("BIANNUAL", LBL.biannual());
		periodes.put("QUARTER", LBL.quarter());
		periodes.put("MONTH", LBL.month());
		periodes.put("WEEK", LBL.week());
		periodes.put("DAY", LBL.day());
		
		mois = new LinkedHashMap<Integer, String>();
		mois.put(1, LBL.january());
		mois.put(2, LBL.february());
		mois.put(3, LBL.march());
		mois.put(4, LBL.april());
		mois.put(5, LBL.may());
		mois.put(6, LBL.june());
		mois.put(7, LBL.july());
		mois.put(8, LBL.august());
		mois.put(9, LBL.september());
		mois.put(10, LBL.october());
		mois.put(11, LBL.november());
		mois.put(12, LBL.december());
	}
}
