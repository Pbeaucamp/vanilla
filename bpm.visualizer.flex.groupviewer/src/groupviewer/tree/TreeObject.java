package groupviewer.tree;


public class TreeObject implements ITreeObject{
		
		private String name;
		private String fullName;
		private TreeParent parent;
		private Object datas;
		/*
		 * Constructors
		 */
		public TreeObject(Object data){
			this.datas = data;
		}
		public TreeObject(String name) {
			setName(name);
		}
		
		public TreeObject(String name, Object data) {
			setName(name);
			this.datas = data;
		}
		/*
		 * GETTER & SETTERS
		 */
		
		/* (non-Javadoc)
		 * @see groupviewer.tree.ITreeObject#getData()
		 */
		public Object getData(){
			return datas;
		}
		
		public void setData(Object datas) {
			this.datas = datas;
		}
		
		/* (non-Javadoc)
		 * @see groupviewer.tree.ITreeObject#setParent(groupviewer.tree.TreeParent)
		 */
		public void setParent(TreeParent parent) {
			this.parent = parent;
			if (parent == null){
				fullName = name;
			}
			else{
				fullName = parent.getName() + "/" + name;
			}
			
		}
		/* (non-Javadoc)
		 * @see groupviewer.tree.ITreeObject#getParent()
		 */
		public TreeParent getParent() {
			return parent;
		}
		public String toString() {
			return getName();
		}
		
		/* (non-Javadoc)
		 * @see groupviewer.tree.ITreeObject#getName()
		 */
		public String getName() {
			return name;
		}
		/* (non-Javadoc)
		 * @see groupviewer.tree.ITreeObject#setName(java.lang.String)
		 */
		public void setName (String name) {
			this.name = name;
		}
		
		protected void setFullName(String full){
			this.fullName = full;
		}
		
		public String getFullName(){
			return fullName;
		}	
}
