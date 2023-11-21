package bpm.mdm.model.provider;


public class FileSystemModelProvider{}/* implements IMdmProvider{
	
	public static final String MODEL_FILE_NAME = "mdm.xml";
	
	private Model model;
	private HashMap<Entity, IEntityStore> stores = new  HashMap<Entity, IEntityStore>();
	
	
	public FileSystemModelProvider(String folderFile) throws Exception{
		
		
		
		
		
		try{
			model = Serializer.eInstance.loadModel();
			
			
			
		}catch(Throwable t){
			t.printStackTrace();
			model = MdmFactory.eINSTANCE.createModel();
		
			
		}
	}
	
	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public IEntityStore getStore(Entity entity) {
		IEntityStore store = stores.get(entity);
		if (store == null){
			store = StorageFactory.eINSTANCE.createEntityStore();
			stores.put(entity, store);
		}
		return store;
	}

	public void persistModel(){
		try {
			Serializer.eInstance.save(model);
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	@Override
	public void updateModel(Model model) {
		persistModel();
		
	}
}*/
