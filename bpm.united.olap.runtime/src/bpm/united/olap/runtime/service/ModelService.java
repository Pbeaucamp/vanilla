package bpm.united.olap.runtime.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.united.olap.api.communication.IModelService;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.Schema;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.preload.IPreloadConfig;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.HierarchyExtractor;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.api.tools.AlphanumComparator;
import bpm.united.olap.runtime.data.cache.CacheKeyGenerator;
import bpm.united.olap.runtime.engine.OlapRunner;
import bpm.united.olap.runtime.engine.UnitedOlapContentManager;
import bpm.united.olap.runtime.model.ProjectionHierarchyExtractor;
import bpm.united.olap.runtime.preload.Preloader;
import bpm.vanilla.platform.core.IObjectIdentifier;

public class ModelService extends UnitedOlapContentManager implements IModelService {
	


	@Override
	public List<Member> getSubMembers(String uname, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		ICubeInstance instance = getCubeInstance(schemaId, cubeName, ctx);
		
		Hierarchy hierarchy = null;
		
		for(bpm.united.olap.api.model.Dimension d : instance.getCube().getDimensions()){
			for(Hierarchy h : d.getHierarchies()){
				if (uname.startsWith(h.getUname())){
					hierarchy = h;
					break;
				}
			}
		}
		
		
		Member m = instance.getHierarchyExtractor(hierarchy).getMember(uname, ctx);
		instance.getHierarchyExtractor(hierarchy).getChilds(m, ctx);
		
		return m.getSubMembers();
	}



	@Override
	public List<Schema> getLoadedSchema() throws Exception {
		return super.getLoadedSchemas();
	}

	
	@Override
	public Schema getSchema(IObjectIdentifier identifier, IRuntimeContext ctx) {
		for(IObjectIdentifier id : identifierMap.keySet()){
			if (id.getDirectoryItemId() == identifier.getDirectoryItemId() && id.getRepositoryId() == identifier.getRepositoryId()){
				return identifierMap.get(id);
			}
		}
		
		return null;
	}



	@Override
	public String loadSchema(Schema schema, IPreloadConfig config, IRuntimeContext runtimeContext) throws Exception {
		
		if (config != null){
			try{
				Preloader p = new Preloader(this, schema, runtimeContext);
				p.preload(this, config);
			}catch(Exception ex){
				Logger.getLogger(getClass()).warn("preload failed - " + ex.getMessage(), ex);
			}

		}
		
		storeSchema(schema, runtimeContext);
		
		
		return schema.getId();
	}




	@Override
	public void refreshSchema(Schema schema, IObjectIdentifier id, IPreloadConfig preloadConf, IRuntimeContext runtimeContext) throws Exception{
		unloadSchema(schema.getId(),id);
		loadSchema(schema, preloadConf, runtimeContext);
		
	}




	@Override
	public List<List<String>> exploreDimension(String hieraName, String schemaId, String cubeName, IRuntimeContext ctx) throws Exception {
		ICubeInstance instance = getCubeInstance(schemaId, cubeName, ctx);
		
		List<List<String>> values = new ArrayList<List<String>>();
		
		if(hieraName.equals("[Measures]")) {
			throw new Exception("Browse of the fact table is not supported yet");
		}
		else {
			LOOK:for(Dimension dim : instance.getCube().getDimensions()) {
				for(Hierarchy hiera : dim.getHierarchies()) {
					if(hiera.getUname().equals(hieraName)) {
						HierarchyExtractor extr = instance.getHierarchyExtractor(hiera);
						
						values = getMemberTable(extr, extr.getRootMember(), ctx);			
						break LOOK;
					}
				}
			}
		}
		
		//sort the results in alphabetical order
		Collections.sort(values, new Comparator<List<String>>() {
			@Override
			public int compare(List<String> arg0, List<String> arg1) {
				if(arg0 != null && arg1 != null && !arg0.isEmpty() && ! arg1.isEmpty() && arg0.size() == arg1.size()) {
					for(int i = 0 ; i < arg0.size() ; i++) {
						int res = arg0.get(i).compareTo(arg1.get(i));
						if(res != 0) {
							return res;
						}
					}
				}
				return 0;
			}
		});
		
		return values;
	}




	private List<List<String>> getMemberTable(HierarchyExtractor extr, Member parent, IRuntimeContext ctx) throws Exception {
		List<List<String>> values = new ArrayList<List<String>>();
		
		List<Member> subs = extr.getChilds(parent, ctx);
		
		if(subs != null && !subs.isEmpty()) {
		
			for(Member mem : subs) {
				List<List<String>> tmp = getMemberTable(extr, mem, ctx);
				
				for(List<String> l : tmp) {
					l.add(0, mem.getName());
				}
				
				values.addAll(tmp);
			}
		}
		
		else {
			List<String> l = new ArrayList<String>();
			values.add(l);
		}
		return values;
	}




	@Override
	public List<String> getLevelValues(String levelUname, String schemaId, String cubeName, IRuntimeContext runtimeContext) throws Exception {
		ICubeInstance instance = getCubeInstance(schemaId, cubeName, runtimeContext);
		
		List<String> values = new ArrayList<String>();
		
		//search the level
		Level lvl = null;
		LOOK:for(Dimension dim : instance.getCube().getDimensions()) {
			for(Hierarchy hiera : dim.getHierarchies()) {
				if(levelUname.startsWith(hiera.getUname())) {
					for(Level l : hiera.getLevels()) {
						if(l.getUname().equals(levelUname)) {
							lvl = l;
							break LOOK;
						}
					}
				}
			}
		}
		
		HierarchyExtractor extr = instance.getHierarchyExtractor(lvl.getParentHierarchy());
		
		Level actual = lvl.getParentHierarchy().getLevels().get(0);
		List<Member> previousMembers = extr.getChilds(extr.getRootMember(), runtimeContext);
		List<Member> actualMembers = new ArrayList<Member>();
		while(actual != lvl) {
			actualMembers = new ArrayList<Member>();
			for(Member mem : previousMembers) {
				actualMembers.addAll(extr.getChilds(mem, runtimeContext));
			}
			previousMembers = actualMembers;
			actual = actual.getSubLevel();
		}
		
		for(Member mem : previousMembers) {
			if(!values.contains(mem.getUname())) {
				values.add(mem.getUname());
			}
		}
		
		AlphanumComparator comparator = new AlphanumComparator();
		Collections.sort(values, comparator);
		
		return values;
	}



	@Override
	public IObjectIdentifier getSchemaObjectIdentifier(String schemaIdMd5Encoded)
			throws Exception {
		
		
		for(IObjectIdentifier i : identifierMap.keySet()){
			Schema s = identifierMap.get(i);
			if (s != null && CacheKeyGenerator.md5(s.getId()).equals(schemaIdMd5Encoded) || schemaIdMd5Encoded.equals(s.getId())){
				return i;
			}
		}
		
		return null;
	}



	@Override
	public Member refreshTimeDimension(String utdSchemaId, String cubeName, IRuntimeContext ctx, Projection projection) throws Exception {
		ICubeInstance instance = getCubeInstance(utdSchemaId, cubeName, ctx);
		
		Hierarchy timeHiera=  null;
		for(Dimension dim : instance.getSchema().getDimensions()) {
			if(dim.isDate()) {
				timeHiera= dim.getHierarchies().get(0);
				break;
			}
		}
		
		instance.getHierarchyExtractor(timeHiera).getRootMember();
		HierarchyExtractor extr = instance.getHierarchyExtractor(timeHiera);
		HierarchyExtractor projExtr = new ProjectionHierarchyExtractor(timeHiera, projection, getCacheServer());
		
		for(Level lvl : timeHiera.getLevels()) {
			
			extr.getLevelMembers(lvl, ctx);
			projExtr.getLevelMembers(lvl, ctx);
			
			if(lvl.getUname().equals(projection.getProjectionLevel())) {
				break;
			}
		}
		
		return instance.getHierarchyExtractor(timeHiera).getRootMember();
	}



	@Override
	public void removeCache(String schemaId, String cubeName, IRuntimeContext ctx, boolean removeCacheDisk, boolean removeMemCached) throws Exception {
		ICubeInstance instance = getCubeInstance(schemaId, cubeName, ctx);
		
		if(removeMemCached) {
			getCacheServer().unloadObject(instance.getSchema());
		}
		if(removeCacheDisk) {
			getCacheServer().getCacheDisk().removeForSchema(schemaId);
		}
	}



	@Override
	public void restoreReloadCache(String schemaId, String cubename, IPreloadConfig preloadConfig, IRuntimeContext ctx, List<String> mdxQueries) throws Exception {
		ICubeInstance instance = getCubeInstance(schemaId, cubename, ctx);
		
		if(preloadConfig != null) {
			Preloader preloader = new Preloader(this, instance.getSchema(), ctx);
			preloader.preload(this, preloadConfig);
		}
		
		if(mdxQueries != null && mdxQueries.size() > 0) {
			OlapRunner runner = new OlapRunner(null, instance, logger.getLogger(ModelService.class.getName()), getCacheServer(), ctx);
			
			Logger.getLogger(getClass()).info("preloading " + mdxQueries.size() + " queries for the cube " + cubename);
			
			for(String query : mdxQueries) {
				runner.executeQuery(query, true);
			}
		}
	}

}
