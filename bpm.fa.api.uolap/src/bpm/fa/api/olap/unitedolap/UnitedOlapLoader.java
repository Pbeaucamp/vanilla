package bpm.fa.api.olap.unitedolap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.fasd.inport.DigesterFasd;
import org.fasd.olap.Drill;
import org.fasd.olap.FAModel;
import org.fasd.olap.ICube;
import org.fasd.olap.OLAPDimension;

import bpm.fa.api.olap.Dimension;
import bpm.fa.api.olap.MeasureGroup;
import bpm.fa.api.olap.OLAPCube;
import bpm.fa.api.repository.StructureQueryLogger;
import bpm.united.olap.api.BadFasdSchemaModelTypeException;
import bpm.united.olap.api.model.Cube;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Measure;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.preload.PreloadConfig;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.impl.RuntimeContext;
import bpm.united.olap.api.tools.FasdModelConverter;
import bpm.vanilla.platform.core.IObjectIdentifier;
import bpm.vanilla.platform.core.IRepositoryApi;
import bpm.vanilla.platform.core.IRepositoryContext;
import bpm.vanilla.platform.core.IVanillaContext;
import bpm.vanilla.platform.core.beans.Group;
import bpm.vanilla.platform.core.beans.Repository;
import bpm.vanilla.platform.core.config.ConfigurationManager;
import bpm.vanilla.platform.core.config.VanillaConfiguration;
import bpm.vanilla.platform.core.exceptions.VanillaException;
import bpm.vanilla.platform.core.impl.BaseRepositoryContext;
import bpm.vanilla.platform.core.impl.BaseVanillaContext;
import bpm.vanilla.platform.core.remote.RemoteRepositoryApi;
import bpm.vanilla.platform.core.repository.RepositoryItem;

public class UnitedOlapLoader {
	/**
	 * unitedOlapSchemaId = key
	 * 
	 */
	private HashMap<String, List<UnitedOlapStructure>> loadedSchema = new HashMap<String, List<UnitedOlapStructure>>();

	private HashMap<FAModel, Schema> createdSchemas = new HashMap<FAModel, Schema>();

	public List<UnitedOlapStructure> getStructures(String unitedOlapSchemaId) {
		return loadedSchema.get(unitedOlapSchemaId);
	}

	public OLAPCube createCube(String unitedOalpSchemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		List<UnitedOlapStructure> structs = loadedSchema.get(unitedOalpSchemaId);

		if (structs == null) {
			throw new Exception("The United Schema with Id= " + unitedOalpSchemaId + " have not been loaded");
		}

		for (UnitedOlapStructure s : structs) {
			if (s.getCubeName().equals(cubeName)) {
				return s.createCube(ctx);
			}
		}

		throw new Exception("No cube named " + cubeName + " within the United Olap Schema");

	}

	public String reloadModel(IObjectIdentifier id, FAModel faModel, IRuntimeContext runtimeContext, StructureQueryLogger queryLogger) throws Exception {
		Schema s = createdSchemas.remove(faModel);
		UnitedOlapServiceProvider.getInstance().getModelService().unloadSchema(s.getId(), id);
		return loadModel(faModel, runtimeContext, queryLogger);
	}

	/**
	 * Load an already built FAModel if it has not been loaded, otherwise the
	 * old Schema Id is returned
	 * 
	 * @param faModel
	 * @param runtimeContext
	 * @param queryLogger
	 * @return
	 */
	public String loadModel(FAModel faModel, IRuntimeContext runtimeContext, StructureQueryLogger queryLogger) throws Exception {
		Schema utdSchema = createdSchemas.get(faModel);

		if (utdSchema == null) {
			FasdModelConverter converter = new FasdModelConverter();
			utdSchema = converter.convert(faModel);

			PreloadConfig conf = null;
			if (faModel.getPreloadConfig() != null) {
				conf = new PreloadConfig();
				for (bpm.united.olap.api.model.Dimension d : utdSchema.getDimensions()) {
					for (Hierarchy h : d.getHierarchies()) {
						if (faModel.getPreloadConfig().getLevelNumber(h.getUname()) != null) {
							conf.setHierarchyLevel(h.getUname(), faModel.getPreloadConfig().getLevelNumber(h.getUname()));
						}
					}
				}

			}

			try {
				utdSchema.setId(UnitedOlapServiceProvider.getInstance().getModelService().loadSchema(utdSchema, conf, runtimeContext));

			} catch (VanillaException ex) {
				throw ex;
			} catch (Exception ex) {
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new Exception("Unable to load FasdModel within UnitedOlap Runtime - " + ex.getMessage(), ex);
			}

			List<UnitedOlapStructure> structures = new ArrayList<UnitedOlapStructure>();

			// create the fa model
			for (Cube cube : utdSchema.getCubes()) {
				ArrayList<Dimension> dimensions = new ArrayList<Dimension>();
				ArrayList<MeasureGroup> measures = new ArrayList<MeasureGroup>();

				for (bpm.united.olap.api.model.Dimension utdDim : cube.getDimensions()) {

					Dimension faDim = new Dimension(utdDim.getName(), utdDim.getUname(), utdDim.getCaption());
					for (OLAPDimension dim : faModel.getOLAPSchema().getDimensions()) {
						if (dim.getName().replace(".", "_").equals(utdDim.getName())) {
							faDim.setGeolocalisable(dim.isGeolocalisable());
							faDim.setDate(utdDim.isDate());
							break;
						}
					}

					for (Hierarchy utdHiera : utdDim.getHierarchies()) {

						bpm.fa.api.olap.Hierarchy faHiera = new bpm.fa.api.olap.Hierarchy(utdHiera.getName(), utdHiera.getUname(), utdHiera.getCaption(), null);
						faHiera.setParentChild(utdHiera.isIsClosureHierarchy());
						UnitedOlapMember defMem = null;
						if(utdHiera.getAllMember() != null && !utdHiera.getAllMember().isEmpty()) {
							defMem = new UnitedOlapMember("All " + faDim.getName(), faHiera.getUniqueName() + ".[All " + faDim.getName() + "]", utdHiera.getAllMember(), faHiera);
						}
						else {
							defMem = new UnitedOlapMember("All " + faDim.getName(), faHiera.getUniqueName() + ".[All " + faDim.getName() + "]", "All " + faDim.getName(), faHiera);
						}
						faHiera.setDefaultMember(defMem);

						int levelNb = 0;
						for (Level utdLevel : utdHiera.getLevels()) {

							bpm.fa.api.olap.Level faLevel = new bpm.fa.api.olap.Level(utdLevel.getName(), utdLevel.getUname(), utdLevel.getCaption(), levelNb);

							try {
								faHiera.addLevel(faLevel);
							} catch (Exception ex) {
								Logger.getLogger(getClass()).error(ex.getMessage(), ex);
							}
							levelNb++;
						}

						faDim.addHierarchy(faHiera);
					}

					dimensions.add(faDim);
				}

				MeasureGroup mesGrp = new MeasureGroup("All measures", "All measures");
				for (Measure utdMes : cube.getMeasures()) {

					bpm.fa.api.olap.Measure faMes = new bpm.fa.api.olap.Measure(utdMes.getName(), utdMes.getUname(), utdMes.getCaption(), utdMes.getCalculationType());

					mesGrp.addElement(faMes);
				}

				measures.add(mesGrp);

				// find drills
				List<Drill> drills = new ArrayList<Drill>();
				for (ICube c : faModel.getCubes()) {
					if (c.getName().equals(cube.getName())) {
						drills = c.getDrills();
					}
				}

				structures.add(new UnitedOlapStructure(queryLogger, cube.getName(), utdSchema, dimensions, measures, drills));
			}

			loadedSchema.put(utdSchema.getId(), structures);

			createdSchemas.put(faModel, utdSchema);

		}
		return utdSchema.getId();
	}

	/**
	 * Load a FAModel from the repository if it hasnt been loaded
	 * 
	 * @param faModel
	 * @return the unitedOlapSchema Id
	 */
	public String loadModel(IObjectIdentifier objectIdentifier, IRuntimeContext runtimeContext, StructureQueryLogger queryLogger) throws BadFasdSchemaModelTypeException, Exception {

		Schema utdSchema = null;
		try {
			utdSchema = UnitedOlapServiceProvider.getInstance().getModelService().getSchema(objectIdentifier, runtimeContext);
		} catch (VanillaException ex) {
			throw ex;
		} catch (Exception ex) {
			Logger.getLogger(getClass()).error(ex.getMessage(), ex);
		}

		if (utdSchema == null) {

			try {
				String shcemaId = UnitedOlapServiceProvider.getInstance().getModelService().loadSchema(objectIdentifier, runtimeContext);

				utdSchema = UnitedOlapServiceProvider.getInstance().getModelService().getSchema(shcemaId);

			} catch (Exception ex) {
				Logger.getLogger(getClass()).error(ex.getMessage(), ex);
				throw new Exception("Unable to load FasdModel from repository within UnitedOlap Runtime - " + ex.getMessage(), ex);
			}

		}

		List<UnitedOlapStructure> structures = new ArrayList<UnitedOlapStructure>();

		// create the fa model
		for (Cube cube : utdSchema.getCubes()) {
			ArrayList<Dimension> dimensions = new ArrayList<Dimension>();
			ArrayList<MeasureGroup> measures = new ArrayList<MeasureGroup>();

			for (bpm.united.olap.api.model.Dimension utdDim : cube.getDimensions()) {

				Dimension faDim = new Dimension(utdDim.getName(), utdDim.getUname(), utdDim.getCaption());
				faDim.setGeolocalisable(utdDim.isGeolocalizable());
				faDim.setDate(utdDim.isDate());

				for (Hierarchy utdHiera : utdDim.getHierarchies()) {

					bpm.fa.api.olap.Hierarchy faHiera = new bpm.fa.api.olap.Hierarchy(utdHiera.getName(), utdHiera.getUname(), utdHiera.getCaption(), null);
					faHiera.setParentChild(utdHiera.isIsClosureHierarchy());
					UnitedOlapMember defMem = null;
					if(utdHiera.getAllMember() != null && !utdHiera.getAllMember().isEmpty()) {
						defMem = new UnitedOlapMember("All " + faDim.getName(), faHiera.getUniqueName() + ".[All " + faDim.getName() + "]", utdHiera.getAllMember(), faHiera);
					}
					else {
						defMem = new UnitedOlapMember("All " + faDim.getName(), faHiera.getUniqueName() + ".[All " + faDim.getName() + "]", "All " + faDim.getName(), faHiera);
					}
					faHiera.setDefaultMember(defMem);

					int levelNb = 0;
					for (Level utdLevel : utdHiera.getLevels()) {

						bpm.fa.api.olap.Level faLevel = new bpm.fa.api.olap.Level(utdLevel.getName(), utdLevel.getUname(), utdLevel.getCaption(), levelNb);

						try {
							faHiera.addLevel(faLevel);
						} catch (Exception ex) {
							Logger.getLogger(getClass()).error(ex.getMessage(), ex);
						}
						levelNb++;
					}

					faDim.addHierarchy(faHiera);
				}

				dimensions.add(faDim);
			}

			MeasureGroup mesGrp = new MeasureGroup("All measures", "All measures");
			for (Measure utdMes : cube.getMeasures()) {

				bpm.fa.api.olap.Measure faMes = new bpm.fa.api.olap.Measure(utdMes.getName(), utdMes.getUname(), utdMes.getCaption(), utdMes.getCalculationType());

				mesGrp.addElement(faMes);
			}

			measures.add(mesGrp);

			// find drills

			// need to load the fasd model.... It's a little shitty...

			IVanillaContext vanillaContext;
			try {
				vanillaContext = new BaseVanillaContext(ConfigurationManager.getInstance().getVanillaConfiguration().getProperty(VanillaConfiguration.P_VANILLA_URL), runtimeContext.getLogin(), runtimeContext.getPassword());
			} catch(Exception e) {
				vanillaContext = new BaseVanillaContext(System.getProperty(VanillaConfiguration.P_VANILLA_URL), runtimeContext.getLogin(), runtimeContext.getPassword());
			}
			Group g = new Group();
			g.setId(runtimeContext.getGroupId());
			Repository repository = new Repository();
			repository.setId(objectIdentifier.getRepositoryId());
			IRepositoryContext ctx = new BaseRepositoryContext(vanillaContext, g, repository);
			IRepositoryApi api = new RemoteRepositoryApi(ctx);

			RepositoryItem repItem = api.getRepositoryService().getDirectoryItem(objectIdentifier.getDirectoryItemId());
			String model = api.getRepositoryService().loadModel(repItem);

			FAModel faModel = new DigesterFasd(IOUtils.toInputStream(model, "UTF-8")).getFAModel();

			List<Drill> drills = new ArrayList<Drill>();
			for (ICube c : faModel.getCubes()) {
				if (c.getName().equals(cube.getName())) {
					drills = c.getDrills();
				}
			}

			structures.add(new UnitedOlapStructure(queryLogger, cube.getName(), utdSchema, dimensions, measures, drills));
		}

		loadedSchema.put(utdSchema.getId(), structures);
		createdSchemas.put(new FAModel(), utdSchema);

		return utdSchema.getId();
	}

	public String getSchemaId(FAModel model) {
		if (createdSchemas.get(model) != null) {
			return createdSchemas.get(model).getId();
		}
		return null;
	}

	public Schema getSchemaByModel(FAModel model) {
		return createdSchemas.get(model);
	}

	public Collection<Schema> loadedSchemas() {
		return createdSchemas.values();
	}

	public void unloadSchema(String schemaId, IObjectIdentifier id) throws Exception {
		UnitedOlapServiceProvider.getInstance().getModelService().unloadSchema(schemaId, id);

		for (FAModel k : createdSchemas.keySet()) {
			if (createdSchemas.get(k).equals(schemaId)) {
				createdSchemas.remove(k);
				break;
			}
		}

		for (String k : loadedSchema.keySet()) {
			if (k.equals(schemaId)) {
				loadedSchema.remove(k);
				break;
			}
		}
	}
}
