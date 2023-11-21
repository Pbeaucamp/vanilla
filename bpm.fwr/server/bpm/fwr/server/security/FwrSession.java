package bpm.fwr.server.security;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import bpm.fwr.api.beans.components.IReportComponent;
import bpm.fwr.api.beans.dataset.DataSet;
import bpm.fwr.api.beans.dataset.FWRFilter;
import bpm.fwr.server.FwrServiceMetadataImpl;
import bpm.gwt.commons.server.security.CommonSession;
import bpm.metadata.layer.business.IBusinessModel;
import bpm.metadata.layer.business.IBusinessPackage;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.beans.VanillaSetup;

public class FwrSession extends CommonSession {

	private String location;

	private VanillaSetup vanillaSetup;

	private Collection<IBusinessModel> businessModel;
	private Collection<IBusinessModel> models = new ArrayList<IBusinessModel>();
	private Collection<IBusinessPackage> businessPackage;
	private IBusinessPackage selectedPackage;
	private IBusinessPackage pack;

	private HashMap<String, FWRFilter> fwrFilters;
	private HashMap<String, IDataStreamElement> olapExtrem;

	private HashMap<String, Color> colors = new HashMap<String, Color>();
	private FwrServiceMetadataImpl services;

	// For Servlet purposes we need to save somes parameters
	private DataSet dataset;
	private List<DataSet> datasets;
	private IReportComponent component;

	public FwrSession() {

	}

	public IBusinessPackage getPack() {
		return pack;
	}

	public void setPack(IBusinessPackage pack) {
		this.pack = pack;
	}

	public Collection<IBusinessModel> getModels() {
		return models;
	}

	public void setModels(Collection<IBusinessModel> models) {
		this.models = models;
	}

	public IBusinessPackage getSelectedPackage() {
		return selectedPackage;
	}

	public void setSelectedPackage(IBusinessPackage selectedPackage) {
		this.selectedPackage = selectedPackage;
	}

	public Collection<IBusinessPackage> getBusinessPackage() {
		return businessPackage;
	}

	public void setBusinessPackage(Collection<IBusinessPackage> businessPackage) {
		this.businessPackage = businessPackage;
	}

	public Collection<IBusinessModel> getBusinessModel() {
		return businessModel;
	}

	public void setBusinessModel(Collection<IBusinessModel> businessModel) {
		this.businessModel = businessModel;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public HashMap<String, IDataStreamElement> getOlapExtrem() {
		return olapExtrem;
	}

	public void setOlapExtrem(HashMap<String, IDataStreamElement> olapExtrem) {
		this.olapExtrem = olapExtrem;
	}

	public VanillaSetup getVanillaSetup() {
		return vanillaSetup;
	}

	public void setVanillaSetup(VanillaSetup vanillaSetup) {
		this.vanillaSetup = vanillaSetup;
	}

	public void setColors(HashMap<String, Color> colors) {
		this.colors = colors;
	}

	public HashMap<String, Color> getColors() {
		return colors;
	}

	public void addColor(String name, Color color) {
		this.colors.put(name, color);
	}

	public void setServices(FwrServiceMetadataImpl services) {
		this.services = services;
	}

	public FwrServiceMetadataImpl getServices() {
		return services;
	}

	public void add(FWRFilter f) {
		if (fwrFilters == null) {
			fwrFilters = new HashMap<String, FWRFilter>();
		}
		fwrFilters.put(f.getName(), f);
	}

	public HashMap<String, FWRFilter> getFwrFilters() {
		return fwrFilters;
	}

	public DataSet getSavedDataset() {
		return dataset;
	}

	public void saveDataset(DataSet ds) {
		this.dataset = ds;
	}

	public List<DataSet> getSavedDatasets() {
		return datasets;
	}

	public void saveDatasets(List<DataSet> dss) {
		this.datasets = dss;
	}

	public IReportComponent getSavedComponent() {
		return component;
	}

	public void saveComponent(IReportComponent component) {
		this.component = component;
	}

	@Override
	public String getApplicationId() {
		return IRepositoryApi.FWR;
	}
}
