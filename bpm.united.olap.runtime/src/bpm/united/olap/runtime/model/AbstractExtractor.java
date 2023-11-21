package bpm.united.olap.runtime.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import bpm.united.olap.api.model.Hierarchy;
import bpm.united.olap.api.model.Level;
import bpm.united.olap.api.model.Member;
import bpm.united.olap.api.model.MemberProperty;
import bpm.united.olap.api.model.ModelFactory;
import bpm.united.olap.api.runtime.IRuntimeContext;
import bpm.united.olap.api.runtime.model.HierarchyExtractor;
import bpm.united.olap.api.tools.AlphanumComparator;
import bpm.united.olap.runtime.cache.ICacheServer;
import bpm.united.olap.runtime.data.cache.LevelDatasCache;
import bpm.united.olap.runtime.model.LevelDatas.LevelDataType;

public abstract class AbstractExtractor implements HierarchyExtractor{

	private AlphanumComparator comparator = new AlphanumComparator();
	
	protected Member rootMember;
	protected Hierarchy hierarchy;
	protected ICacheServer cacheServer;
	
	protected abstract LevelDatasCache getLevelDatas(Level lvl, IRuntimeContext runtimeContext) throws Exception;
//	protected abstract Member createChildMember(Member parent, LevelDatas datas);
	
	@Override
	public List<Member> getChilds(Member parent, IRuntimeContext runtimeContext) throws Exception {
		if (!parent.isChildsLoaded()){
			loadChildFromDatas(parent, runtimeContext);
			parent.setChildsLoaded(true);
		}
		
		return parent.getSubMembers();
	}
	
	protected void loadChildFromDatas(Member parent, IRuntimeContext runtimeContext) throws Exception {
		
		Level level = null;
		
		if (parent.getParentLevel() == null){
			level = getHierarchy().getLevels().get(0);
		}
		else{
			level = parent.getParentLevel().getSubLevel();
		}
		
		if (level == null){
			parent.setChildsLoaded(true);
			return;
		}
		
		LevelDatasCache datas = getLevelDatas(level, runtimeContext);
		
		Collections.sort(datas.getLevelDatas(), new Comparator<LevelDatas>() {
			@Override
			public int compare(LevelDatas o1, LevelDatas o2) {
				String order1 = (String) o1.getData(LevelDataType.MEMBER_ORDER);
				String order2 = (String) o2.getData(LevelDataType.MEMBER_ORDER);
				if(order1 != null && !order1.isEmpty() && order2 != null && !order2.isEmpty()) {
					return comparator.compare(order1, order2);
				}
				String name1 = (String) o1.getData(LevelDataType.MEMBER_NAME);
				String name2 = (String) o2.getData(LevelDataType.MEMBER_NAME);
				if(name1 != null && !name1.isEmpty() && name2 != null && !name2.isEmpty()) {
					return comparator.compare(name1, name2);
				}
				return 0;
			}
		});
		
		for(LevelDatas data : datas.getLevelDatas()) {
			if (parent.getName().equals(data.getData(LevelDataType.PARENT_NAME))){
				createChildMember(parent, data);
			}
		}
	}
	
	
	@Override
	public Hierarchy getHierarchy() {
		return hierarchy;
	}
	
	@Override
	public Member getMember(String uniqueName, IRuntimeContext runtimeContext) throws Exception {
		if (getRootMember().getUname().equals(uniqueName)){
			return getRootMember();
		}
		
		Member m = getMember(getRootMember(), uniqueName, runtimeContext); 
		return m;
	}
	
	protected Member getMember(Member parent, String uniqueName, IRuntimeContext runtimeContext) throws Exception {
		if(!parent.isChildsLoaded()) {
			getChilds(parent, runtimeContext);
		}
		
		for(Member m : parent.getSubMembers()){
			if (m.getUname().equals(uniqueName)){
				return m;
			}
			else if (uniqueName.startsWith(m.getUname())){
				return getMember(m, uniqueName, runtimeContext);
			}
		}
		Logger.getLogger(getClass()).error("Unable to find Member " + uniqueName );
		return null;
	}
	
	@Override
	public String getMemberName(Object factForeignKey, int levelIndex, IRuntimeContext runtimeContext) throws Exception {
		Level level = hierarchy.getLevels().get(levelIndex);
		String memberName = null;
		LevelDatasCache levelDatas = getLevelDatas(level, runtimeContext);

		Integer key = levelDatas.getForeignKeyIndexMap().get(factForeignKey);
		try{
			LevelDatas data = levelDatas.getLevelDatas().get(key);
		
			memberName = data.getData(LevelDataType.MEMBER_NAME).toString();
			
			return memberName;
		}catch(Exception ex){
//			String message = "Unable to find a value for the level " + level.getParentHierarchy().getUname() + "." + level.getUname() + " matching with the fact Table Key value=" + factForeignKey;
//			Logger.getLogger(getClass()).warn(message);
//			throw new Exception(message, ex);
			return "";
		}
	}
	
	
	@Override
	public List<String> getMembersWith(String word, String level, IRuntimeContext ctx) throws Exception {
		
		List<String> results = new ArrayList<String>();
		
		if(level == null && getRootMember().getUname().toLowerCase().contains(word.toLowerCase())) {
			results.add(getRootMember().getUname());
		}
		
		results.addAll(getSubMembersWith(getChilds(getRootMember(), ctx), level, word, ctx));
		
		return results;
	}
	
	private List<String> getSubMembersWith(List<Member> childs, String level, String word, IRuntimeContext ctx) throws Exception {
		List<String> results = new ArrayList<String>();
		
		for(Member mem : childs) {
			if(level != null) {
				if(mem.getParentLevel().getUname().equals(level) && mem.getUname().toLowerCase().contains(word.toLowerCase())) {
					results.add(mem.getUname());
				}
				if(mem.getParentLevel().getSubLevel() != null) {
					results.addAll(getSubMembersWith(getChilds(mem, ctx), level, word, ctx));
				}
			}
			else {
				if(mem.getUname().toLowerCase().contains(word.toLowerCase())) {
					results.add(mem.getUname());
					if(mem.getParentLevel().getSubLevel() != null) {
						results.addAll(getSubMembersWith(getChilds(mem, ctx), level, word, ctx));
					}
				}
			}
		}
		
		return results;
	}

	@Override
	public Member getRootMember() {
		if (rootMember == null){
			Member mem = ModelFactory.eINSTANCE.createMember();
			if(getHierarchy().getAllMember() != null && !getHierarchy().getAllMember().isEmpty()) {
				mem.setCaption(getHierarchy().getAllMember());
			}
			else {
				mem.setCaption("All " + getHierarchy().getParentDimension().getName());
			}
			mem.setName("All " + getHierarchy().getParentDimension().getName());
			mem.setUname(getHierarchy().getUname()+".[All " + getHierarchy().getParentDimension().getName() + "]");
			mem.setOrderUname(getHierarchy().getUname()+".[All " + getHierarchy().getParentDimension().getName() + "]");
			mem.setMemberRelationsUname(hierarchy.getUname()+".[All " + getHierarchy().getParentDimension().getName() + "]");
			mem.setParentHierarchy(hierarchy);
			rootMember = mem;
		}
		
		return rootMember;
	}
	
	@Override
	public boolean validate(Member member, Object factForeigNkey, IRuntimeContext runtimeContext) throws Exception {
		boolean isValid = false;
		
		LevelDatasCache levelDatas = getLevelDatas(member.getParentLevel(), runtimeContext);
		
		Integer key = levelDatas.getForeignKeyIndexMap().get(factForeigNkey);
		
		LevelDatas data = levelDatas.getLevelDatas().get(key);
		
		String memberName = data.getData(LevelDataType.MEMBER_NAME).toString();
		
		return memberName.equals(member.getName());
	}
	
	protected Member createChildMember(Member parent, LevelDatas datas) {
		Level memberLevel = null;
		if (parent.getParentLevel() == null){
			memberLevel = getHierarchy().getLevels().get(0);
		}
		else{
			memberLevel = parent.getParentLevel().getSubLevel();
		}
		Member member = ModelFactory.eINSTANCE.createMember();
		member.setName(datas.getData(LevelDataType.MEMBER_NAME).toString());
		if(datas.getData(LevelDataType.MEMBER_ORDER) != null) {
			member.setOrderValue(datas.getData(LevelDataType.MEMBER_ORDER).toString());
		}
		else{
			member.setOrderValue(datas.getData(LevelDataType.MEMBER_NAME).toString());
		}
		
		if(datas.getData(LevelDataType.MEMBER_LABEL) != null) {
			member.setCaption(datas.getData(LevelDataType.MEMBER_LABEL).toString());
		}
		else {
			member.setCaption(datas.getData(LevelDataType.MEMBER_NAME).toString());
		}
		
		if(memberLevel.getMemberProperties() != null) {
			for(int i = 0 ; i < memberLevel.getMemberProperties().size() ; i++) {
				MemberProperty prop = memberLevel.getMemberProperties().get(i);
				MemberProperty memProp = ModelFactory.eINSTANCE.createMemberProperty();
				memProp.setName(prop.getName());
				memProp.setType(prop.getType());
				memProp.setValueItem(prop.getValueItem());
				memProp.setValue(((List)datas.getData(LevelDataType.MEMBER_PROPERTIES)).get(i).toString());
				member.getProperties().add(memProp);
			}
		}
		
		member.setUname(parent.getUname() + ".[" + member.getName() + "]");
		member.setMemberRelationsUname(member.getUname());
		if(member.getOrderValue() != null) {
			member.setOrderUname(parent.getOrderUname() + ".[" + member.getOrderValue() + "]");
		}
		else {
			member.setOrderUname(parent.getOrderUname() + ".[" + member.getName() + "]");
		}
		
		parent.getSubMembers().add(member);
		member.setParentLevel(memberLevel);
		member.setParentMember(parent);
		member.setParentHierarchy(hierarchy);
		return member;
	}

	@Override
	public String getMemberOrderValue(Object factForeignKey, int levelIndex, IRuntimeContext runtimeContext) throws Exception {
		Level level = hierarchy.getLevels().get(levelIndex);
		String memberValue = null;
		LevelDatasCache levelDatas = getLevelDatas(level, runtimeContext);

		Integer key = levelDatas.getForeignKeyIndexMap().get(factForeignKey);
		try{
			LevelDatas data = levelDatas.getLevelDatas().get(key);
		
			memberValue = data.getData(LevelDataType.MEMBER_ORDER).toString();
			
			return memberValue;
		}catch(Exception ex){
//			ex.printStackTrace();
			throw ex;
		}
	}

	@Override
	public void preloadLevelDatas(int currentLevel, int lastLevel, IRuntimeContext ctx) throws Exception {
		for(int i = currentLevel ; i <= lastLevel ; i++) {
			getLevelDatas(hierarchy.getLevels().get(i), ctx);
		}		
	}

	@Override
	public List<Object> getMemberForeignKeys(Member elem, IRuntimeContext runtimeContext) throws Exception {
		
		LevelDatasCache levelDatas = getLevelDatas(elem.getParentLevel(), runtimeContext);
		for(LevelDatas lvlData : levelDatas.getLevelDatas()) {
			if(lvlData.getData(LevelDataType.MEMBER_NAME).equals(elem.getName())) {
				return (List<Object>) lvlData.getData(LevelDataType.FOREIGN_KEY);
			}
		}
		
		return null;
	}

	@Override
	public List<Object> getAllForeignKeys(IRuntimeContext runtimeContext) throws Exception {
		
		List<Object> foreignKeys = new ArrayList<Object>();
		LevelDatasCache levelDatas = getLevelDatas(hierarchy.getLevels().get(0), runtimeContext);
		for(LevelDatas lvlData : levelDatas.getLevelDatas()) {
			foreignKeys.addAll((List<Object>) lvlData.getData(LevelDataType.FOREIGN_KEY));
		}
		
		return foreignKeys;
	}
	
	@Override
	public List<Member> getLevelMembers(Level lookedLevel, IRuntimeContext runtimeCtx) throws Exception {
		
		int levelIndex = hierarchy.getLevels().indexOf(lookedLevel);
		
		List<Member> parentMembers = this.getChilds(getRootMember(), runtimeCtx);
		
		if(levelIndex == 0) {
			return parentMembers;
		}
		
		List<Member> actualMembers = new ArrayList<Member>();
		
		for(int i = 0 ; i < levelIndex ; i++) {
			actualMembers = new ArrayList<Member>();
			for(Member parent : parentMembers) {
				actualMembers.addAll(this.getChilds(parent, runtimeCtx));
			}
			parentMembers = actualMembers;
		}
		
		return actualMembers;
	}
}

