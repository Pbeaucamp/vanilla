package bpm.birep.admin.client.trees;

import adminbirep.Messages;


public class TreeObject<T> {
		private String name;
		private TreeParent parent;
		protected String fullName = ""; //$NON-NLS-1$
		private T datas;
		
		public TreeObject(T data){
			this.datas = data;
		}
		public TreeObject(String name) {
			setName(name);
			
		}
		
		public TreeObject(String name, T data) {
			setName(name);
			this.datas = data;
		}
		
		public T getData(){
			return datas;
		}
		
		public String getName() {
			return name;
		}
		public void setParent(TreeParent parent) {
			this.parent = parent;
			if (parent == null){
				fullName = name;
			}
			else{
				fullName = parent.getName() + "/" + name; //$NON-NLS-1$
			}
			
		}
		public TreeParent getParent() {
			return parent;
		}
		public String toString() {
			return getName();
		}
		public void setName (String name) {
			this.name = name;
		}
		public String getFullName(){
			return fullName;
		}
}
