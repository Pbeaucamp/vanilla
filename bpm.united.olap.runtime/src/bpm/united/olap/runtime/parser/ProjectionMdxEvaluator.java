package bpm.united.olap.runtime.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import bpm.mdx.parser.result.RootItem;
import bpm.mdx.parser.result.TermItem;
import bpm.united.olap.api.constant.MemberFunctionNames;
import bpm.united.olap.api.model.Dimension;
import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.impl.Projection;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.MdxSet;
import bpm.united.olap.api.runtime.model.ICubeInstance;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.model.ProjectionHierarchyExtractor;
import bpm.vanilla.platform.logging.IVanillaLogger;


/**
 * An evaluator for the projection
 * Will merge members for the date dimension
 * 
 * @author Marc Lanquetin
 *
 */
public class ProjectionMdxEvaluator extends MdxEvaluator {

	private Projection projection;
	private ICacheServer cacheServer;
	
	public ProjectionMdxEvaluator(RootItem tree, ICubeInstance cubeInstance, IVanillaLogger logger, IRuntimeContext runtimeCtx, Projection projection, ICacheServer server) {
		super(tree, cubeInstance, logger, runtimeCtx);
		this.projection = projection;
		this.cacheServer = server;
	}
	

	private long findMemberTime = 0;
	private int findMemberCall = 0;
	
	/**
	 * find the members for this node
	 * if it's a date member, try to merge projection and original members
	 * @param node
	 * @param hiera
	 * @return
	 * @throws Exception 
	 */
	@Override
	protected List<MdxSet> findMembers(TermItem node, Hierarchy hiera) throws Exception {
		
		//Look if it's the time dimension
		boolean isDateDimension = hiera.getParentDimension().isDate();
		ProjectionHierarchyExtractor extr = new ProjectionHierarchyExtractor(hiera, projection, cacheServer);
		
		Date d = new Date();
		findMemberCall++;
		
		List<MdxSet> results = new ArrayList<MdxSet>();
		
		String[] unameParts = node.getUname().replace("[","").replace("]","").split("\\.");
		
		if(unameParts.length <= 1) {
			for(Dimension dim : cubeInstance.getCube().getDimensions()) {
				if(dim.getName().equals(unameParts[0])) {
					MdxSet set = factory.createMdxSet();
				
					set.getElements().add(dim);
					results.add(set);
					findMemberTime += new Date().getTime() - d.getTime();
					return results;
				}
			}
		}

		boolean isFunction = false;
		if(Arrays.asList(MemberFunctionNames.NAMES).contains(unameParts[unameParts.length - 1].toUpperCase())) {
			isFunction = true;
		}
		
		List<Member> resMembers = null;
 		
		
		if (isFunction){
			//find member children
			if (unameParts[unameParts.length - 1].toUpperCase().equals(MemberFunctionNames.CHILDREN)) {
				Member m = cubeInstance.getHierarchyExtractor(hiera).getMember(node.getUname().replace(".children", ""), runtimeCtx);
				
				resMembers = cubeInstance.getHierarchyExtractor(hiera).getChilds(m, runtimeCtx);
				if(isDateDimension) {
					List<Member> mems = extr.getChilds(m, runtimeCtx);
					if(mems != null && mems.size() > 0) {
						resMembers.addAll(mems);
					}
				}
			}
			
			//find all the members for the level
			else if(unameParts[unameParts.length - 1].toUpperCase().equals(MemberFunctionNames.MEMBERS)) {

				String lvlUname = node.getUname().substring(0, (node.getUname().length() - 1) - MemberFunctionNames.MEMBERS.length());
				
				Level lookedLevel = null;
				for(Level lvl : hiera.getLevels()) {
					if(lvl.getUname().equals(lvlUname)) {
						lookedLevel = lvl;
						break;
					}
				}
				
				if(lookedLevel == null) {
					throw new Exception("Can't find level " + lvlUname + " in hierarchy " + hiera.getUname());
				}
							
				resMembers = cubeInstance.getHierarchyExtractor(hiera).getLevelMembers(lookedLevel, runtimeCtx);
				if(isDateDimension) {
					List<Member> mems = extr.getLevelMembers(lookedLevel, runtimeCtx);
					if(mems != null && mems.size() > 0) {
						resMembers.addAll(mems);
					}
				}
			}
			
			//find this member
			else{
				Member m = cubeInstance.getHierarchyExtractor(hiera).getMember(node.getUname(), runtimeCtx);
				if (m == null){
					throw new Exception("Member " + node.getUname() + " not found");
				}
			}
			
		}
		else{
			if (resMembers == null){
			resMembers = new ArrayList<Member>();
			}
			resMembers.add(cubeInstance.getHierarchyExtractor(hiera).getMember(node.getUname(), runtimeCtx));
		}
		
		for(Member mem : resMembers) {
			MdxSet set = factory.createMdxSet();
			set.getElements().add(mem);
			results.add(set);
		}
		findMemberTime += new Date().getTime() - d.getTime();
		return results;
	}
	
}
