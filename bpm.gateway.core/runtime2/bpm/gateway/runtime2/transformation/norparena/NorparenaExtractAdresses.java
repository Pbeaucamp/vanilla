package bpm.gateway.runtime2.transformation.norparena;

import java.util.Iterator;
import java.util.List;

import bpm.gateway.core.Activator;
import bpm.gateway.core.transformations.vanillamaps.VanillaMapAddressInput;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;
import bpm.gateway.runtime2.internal.RowFactory;
import bpm.vanilla.map.core.design.IAddress;
import bpm.vanilla.map.core.design.IMapDefinitionService;
import bpm.vanilla.platform.core.IRepositoryContext;

public class NorparenaExtractAdresses extends RuntimeStep{

	private IMapDefinitionService daoService;
	private List<IAddress> adresses;
	private Iterator<IAddress> iterator;

	
	
	public NorparenaExtractAdresses(IRepositoryContext repositoryContext, VanillaMapAddressInput transformation,	int bufferSize) throws Exception{
		super(repositoryContext, transformation, bufferSize);
		if (getRepositoryContext() == null){
			throw new Exception("Cannot use a VanillaSecurity step without a VanillaContext. You must be connected to Vanilla.");
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
		
		
		VanillaMapAddressInput tr = (VanillaMapAddressInput)getTransformation();
		
		
		
		daoService.configure(getRepositoryContext().getVanillaContext().getVanillaUrl());
		info("IMapDefinitionService configured with :" + getRepositoryContext().getVanillaContext().getVanillaUrl());
		
		
		
		adresses = daoService.getAllAddress();
		iterator = adresses.iterator();
		
	}

	@Override
	public void performRow() throws Exception {
		if (iterator == null){
			throw new Exception("No ResultSet defined");
		}
		
		if (iterator.hasNext()){
			
			IAddress adress = iterator.next();
			
			
			Row row = RowFactory.createRow(this);
			
			row.set(0, adress.getArrondissement());
			row.set(1, adress.getBloc());
			row.set(2, adress.getCity());
			row.set(3, adress.getCountry());
			row.set(4, adress.getId());
			row.set(5, adress.getINSEECode());
			row.set(6, adress.getLabel());
			row.set(7, adress.getStreet1());
			row.set(8, adress.getStreet2());
			row.set(9, adress.getZipCode());
			
			writeRow(row);
		}
		else{
			if (!areInputsAlive()){
				if (areInputStepAllProcessed()){
					if (inputEmpty()){
						setEnd();
					}
				}
			}
			
		
			
		}
		
	}

	@Override
	public void releaseResources() {
		iterator = null;
		if (adresses != null){
			adresses.clear();
		}
		adresses = null;
		
		info("Releasing Norparena Dao ... ");
		if (Activator.getDefault().releaseNorparenaDao()){
			info("Norparena Dao released");
		}
		daoService = null;
		
		
		
	}

}
