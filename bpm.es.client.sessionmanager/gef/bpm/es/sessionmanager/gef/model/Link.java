package bpm.es.sessionmanager.gef.model;


public class Link {
	private FieldModel source;
	private FieldModel target;
	private boolean isConnected = false;
	
	public Link(FieldModel source, FieldModel target) throws Exception{
		this.source = source;
		this.target = target;
		reconnect(source, target);
	}
	
	public FieldModel getSource(){
		return source;
	}
	
	public FieldModel getTarget(){
		return target;
	}
	
	public void reconnect()  throws Exception{
		if (!isConnected) {
			
			try{
				source.addLink(this);
				target.addLink(this);
				isConnected = true;
			}catch(Exception e){
				source.getSourceLink().remove(this);
				target.getSourceLink().remove(this);
				isConnected = false;
				
				throw e;
			}
			
		}
	}
	
	public void reconnect(FieldModel newSource, FieldModel newTarget) throws Exception{
		if (newSource == null || newTarget == null || newSource == newTarget) {
			throw new IllegalArgumentException();
		}
		//disconnect();
		this.source = newSource;
		this.target = newTarget;
		reconnect();
	}
	
	public void disconnect() {
		try {
		//if (isConnected) {
			source.getSourceLink().remove(this);
			target.getSourceLink().remove(this);
			source.removeLink(this);
			target.removeLink(this);
			
			//FIXME, callback to disconnect things
			//TableMapping mappingSource = (TableMapping) source.getModel().getDBTable().getPrivateObject();
			
			String toDelete = target.getFieldName();
			
			//mappingSource.removeMapping(toDelete);
			
			isConnected = false;
		//}
		} catch (Exception e) {
			//disregard
			System.out.println("");
		}
	}
}
