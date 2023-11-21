package bpm.metadata.resource.complex;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bpm.metadata.MetaData;
import bpm.metadata.layer.logical.IDataStream;
import bpm.metadata.layer.logical.IDataStreamElement;
import bpm.metadata.layer.logical.Relation;
import bpm.metadata.layer.logical.sql.SQLDataSource;
import bpm.metadata.resource.IResource;

public class FmdtDimension implements IResource{
	private String name = "newDimension";
	private boolean geolocalisable = false;
	private SQLDataSource dataSource;
	private List<IDataStreamElement> levels = new ArrayList<IDataStreamElement>();
	
	private class _Lvl{
		String streamName;
		String fieldName;
	}
	
	private List<_Lvl> _lvls;
	private String dataSourceName;
	
	
	
	
	
	/**
	 * @return the geolocalisable
	 */
	public boolean isGeolocalisable() {
		return geolocalisable;
	}

	/**
	 * @param geolocalisable the geolocalisable to set
	 */
	public void setGeolocalisable(boolean geolocalisable) {
		this.geolocalisable = geolocalisable;
	}

	public void setGeolocalisable(String geolocalisable) {
		this.geolocalisable = Boolean.parseBoolean(geolocalisable);
	}

	
	public String getName() {
		return name;
	}
	
	public void setDataSourceName(String dataSourceName){
		this.dataSourceName = dataSourceName;
	}

	public void build(MetaData model){
		try{
			setDataSource((SQLDataSource)model.getDataSource(dataSourceName));
			dataSourceName = null;
			for(_Lvl l : _lvls){
				IDataStream e = dataSource.getDataStreamNamed(l.streamName);
				IDataStreamElement c = e.getElementNamed(l.fieldName);
				try {
					addLevel(c);
				} catch (Exception e1) {
					
					e1.printStackTrace();
				}
			}
			_lvls = null;
		}catch(Exception ex){
			ex.printStackTrace();
		}
		
	}
	/**
	 * used only by digester
	 * @param streamName
	 * @param colName
	 */
	public void addLevel(String streamName, String colName){
		if (_lvls == null){
			_lvls = new ArrayList<_Lvl>();
		}
		_Lvl l = new _Lvl();
		l.fieldName = colName;
		l.streamName = streamName;
		_lvls.add(l);
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public boolean levelCanBeAdded(IDataStreamElement element){
		if (levels.isEmpty()){
			return true;
		}

		for(IDataStreamElement col : levels){
			if (col.getDataStream() == element.getDataStream()){
				return true;
			}
		}
		List<IDataStream> dataStreams = new ArrayList<IDataStream>();
		dataStreams.add(element.getDataStream());
		
		for(IDataStreamElement e : levels){
			if (!dataStreams.contains(e.getDataStream())){
				dataStreams.add(e.getDataStream());
				List<Relation> rels = dataSource.getRelations(dataStreams);
				if (!rels.isEmpty()){
					return true;
				}
				dataStreams.remove(e.getDataStream());
			}
			else{
				return true;
			}
		}
		return false;
	}
	
	public void addLevel(IDataStreamElement element) throws Exception{
		if (element == null){
			return ;
		}
		if (!levelCanBeAdded(element) ){
			throw new Exception("Missing relations to add this DataStreamElement in the dimension" );
		}
		else{
			levels.add(element);
		}
	}
	
	public void removeLevel(IDataStreamElement element){
		levels.remove(element);
	}
	
	public List<IDataStreamElement> getLevels(){
		return new ArrayList<IDataStreamElement>(levels);
	}
	
	public void swapLevels(IDataStreamElement lvl1, IDataStreamElement l2){
		int i1 = levels.indexOf(lvl1);
		int i2 = levels.indexOf(l2);
		
		if (i1 == -1 || i2 == -1){
			return;
		}
		
		levels.set(i1, l2);
		levels.set(i2, lvl1);
	}

	public String getXml() {
		StringBuffer buf = new StringBuffer();
		buf.append("    <dimension>\n");
		buf.append("        <name>" + getName() + "</name>\n");
		buf.append("        <dataSource>" + dataSource.getName() + "</dataSource>\n");
		buf.append("        <geolocalizable>" + isGeolocalisable() + "</geolocalizable>\n");
		for(IDataStreamElement col : levels){
			buf.append("        <level>\n");
			buf.append("            <dataStream>" + col.getDataStream().getName() + "</dataStream>\n");
			buf.append("            <dataStreamElement>" + col.getName() + "</dataStreamElement>\n");
			buf.append("        </level>\n");
		}
		buf.append("    </dimension>\n");
		return buf.toString();
	}
	
	public void setDataSource(SQLDataSource dataSource){
		this.dataSource = dataSource;
	}
	
	public SQLDataSource getDataSource(){
		return dataSource;
	}

	public boolean isGrantedFor(String groupName) {
		
		return true;
	}

	public String getOutputName() {
		
		return getName();
	}

	public String getOutputName(Locale l) {
		
		return getName();
	}
	
}
