package bpm.gateway.runtime2.transformation.norparena;

import bpm.gateway.core.Activator;
import bpm.gateway.core.transformations.vanillamaps.VanillaMapAddressOutput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IFactoryModelObject;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.platform.core.IRepositoryContext;

public class NorparenaInsertAdresses extends RuntimeStep{

	private IMapDefinitionService daoService;
	private IFactoryModelObject factory;
	
	
	
	public NorparenaInsertAdresses(IRepositoryContext repositoryContext, VanillaMapAddressOutput transformation,	int bufferSize) throws Exception{
		super(repositoryContext, transformation, bufferSize);
		if (getRepositoryContext() == null){
			throw new Exception("Cannot use a Norparena step without a VanillaContext. You must be connected to Vanilla.");
		}
	}

	@Override
	public void init(Object adapter) throws Exception {
		info("Looking for the norparena IMapDefinitionService...");
		
		try{
			daoService = Activator.getDefault().getNorparenaMapService();
		}catch(Exception ex){
			ex.printStackTrace();
			error("Unable to get the norparena IMapDefinitionService ", ex);
			throw ex;
		}
		
		if (daoService == null){
			error("Norparena IMapDefinitionService not found");
			throw new Exception("Norparena IMapDefinitionService not found");
			
		}
		info("Norparena IMapDefinitionService found");
		VanillaMapAddressOutput tr = (VanillaMapAddressOutput)getTransformation();
		
		daoService.configure(getRepositoryContext().getVanillaContext().getVanillaUrl());
		info("IMapDefinitionService configured with :" + getRepositoryContext().getVanillaContext().getVanillaUrl());
		//looking for the Map API FactoryModel
		info("Looking for the norparena IFactoryModelObject...");
		
		try{
			factory = Activator.getDefault().getNorparenaFactory();
		}catch(Exception ex){
			ex.printStackTrace();
			error("Unable to get the norparena IFactoryModelObject ", ex);
			throw ex;
		}
		
		if (factory == null){
			error("Norparena IFactoryModelObject not found");
			throw new Exception("Norparena IFactoryModelObject not found");
			
		}
		info("Norparena IFactoryModelObject found");
		
		//check the mappings
		
		
		
		if (!tr.getInputs().isEmpty()){
			
			int inputSize = tr.getInputs().get(0).getDescriptor(tr).getColumnCount();
			
			if (tr.getInputArrondissementIndex() != null && tr.getInputArrondissementIndex() >= inputSize){
				throw new Exception("The mapped input Field index on  Arrondissement Field is higher than input columns number");
			}
			if (tr.getInputBlocIndex() != null && tr.getInputBlocIndex() >= inputSize){
				throw new Exception("The mapped input Field index on  Bloc Field is higher than input columns number");
			}
			if (tr.getInputCityIndex() != null && tr.getInputCityIndex() >= inputSize){
				throw new Exception("The mapped input Field index on  City Field is higher than input columns number");
			}
			if (tr.getInputCountryIndex() != null && tr.getInputCountryIndex() >= inputSize){
				throw new Exception("The mapped input Field index on  Country Field is higher than input columns number");
			}
			if (tr.getInputInseeCodeIndex() != null && tr.getInputInseeCodeIndex() >= inputSize){
				throw new Exception("The mapped input Field index on  InseeCodeField is higher than input columns number");
			}
			if (tr.getInputLabelIndex() != null && tr.getInputLabelIndex() >= inputSize){
				throw new Exception("The mapped input Field index on  Label Field is higher than input columns number");
			}
			if (tr.getInputStreet1Index() != null && tr.getInputStreet1Index() >= inputSize){
				throw new Exception("The mapped input Field index on  Street1 Field is higher than input columns number");
			}
			if (tr.getInputStreet2Index() != null && tr.getInputStreet2Index() >= inputSize){
				throw new Exception("The mapped input Field index on  Street2 Field is higher than input columns number");
			}
			
			if (tr.getInputZipcodeIndex() != null && tr.getInputZipcodeIndex() >= inputSize){
				throw new Exception("The mapped input Field index on  Zipcode Field is higher than input columns number");
			}
		}
		
	}

	@Override
	public void performRow() throws Exception {
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			Thread.sleep(10);
			return;
		}
		
		Row row = readRow();
		
		
		
		IAddress adress = null;
		
		try{
			adress = getAdress(row);
		}catch(Exception ex){
			String errorMessage = "Error when converting row " + row.dump() + " into an IAddress:\n" + ex.getMessage(); 
			error(errorMessage, ex);
			throw new Exception(errorMessage, ex);
		}
		
		try{
			daoService.saveAddress(adress);
		}catch(Exception ex){
			String errorMessage = "Unable to save Adress created from row " + row.dump() + ":\n" + ex.getMessage();
			error(errorMessage, ex);
			throw new Exception(errorMessage, ex);
		}
		
		
		writeRow(row);
		
	}

	
	
	private IAddress getAdress(Row row) throws Exception{
		VanillaMapAddressOutput tr = (VanillaMapAddressOutput)getTransformation();
		
		
		IAddress adress = factory.createAdress();
		
		Integer i = tr.getInputArrondissementIndex();
		if (i != null && i >= 0 && row.get(i) != null){
			adress.setArrondissement(row.get(i).toString());
		}
		i= tr.getInputBlocIndex();
		if (i != null && i >= 0 && row.get(i) != null){
			adress.setBloc(row.get(i).toString());
		}
		i= tr.getInputCityIndex();
		if (i != null && i >= 0 && row.get(i) != null){
			adress.setCity(row.get(i).toString());
		}
		i= tr.getInputCountryIndex();
		if (i != null && i >= 0 && row.get(i) != null){
			adress.setCountry(row.get(i).toString());
		}
		i= tr.getInputInseeCodeIndex();
		if (i != null && i >= 0 && row.get(i) != null){
			try{
				adress.setINSEECode(Integer.parseInt(row.get(i).toString()));
			}catch(NumberFormatException ex){
				
				try{
					adress.setINSEECode(new Double(Double.parseDouble(row.get(i).toString())).intValue());
				}catch(NumberFormatException ex2){
					throw new Exception("Error when setting INSEECODE incoming value " + row.get(i) + " is not an Integer neither a Double.");
				}
			}

		}
		i= tr.getInputLabelIndex();
		if (i != null && i >= 0 && row.get(i) != null){
			adress.setLabel(row.get(i).toString());
		}
		i= tr.getInputStreet1Index();
		if (i != null && i >= 0 && row.get(i) != null){
			adress.setStreet1(row.get(i).toString());
		}
		
		i= tr.getInputStreet2Index();
		if (i != null && i >= 0 && row.get(i) != null){
			adress.setStreet2(row.get(i).toString());
		}
		
		i= tr.getInputZipcodeIndex();
		if (i != null && i >= 0 && row.get(i) != null){
			try{
				adress.setZipCode(Integer.parseInt(row.get(i).toString()));
			}catch(NumberFormatException ex){
				
				try{
					adress.setZipCode(new Double(Double.parseDouble(row.get(i).toString())).intValue());
				}catch(NumberFormatException ex2){
					throw new Exception("Error when setting ZipCode.The incoming value " + row.get(i) + " is not an Integer neither a Double.");
				}
			}
		}
		
		return adress;
		
	}
	
	@Override
	public void releaseResources() {
		info("Releasing Norparena Dao ... ");
		if (Activator.getDefault().releaseNorparenaDao()){
			info("Norparena Dao released");
		}
		daoService = null;
		
		info("Releasing Norparena Factory ... ");
		
		if (Activator.getDefault().releaseNorparenaFactory()){
			info("Norparena Factory released");
		}
		factory = null;
	}

}
