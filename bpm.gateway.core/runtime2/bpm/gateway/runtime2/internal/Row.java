package bpm.gateway.runtime2.internal;

import java.util.Iterator;

import bpm.gateway.core.Transformation;

public class Row implements Iterable<Object>{
	private Object[] values;
	private RowMeta meta;
	private Transformation transformation;
	
	private class RowIterator<T> implements Iterator<Object>{

		private int currentPos;
		private Row row;
		private int size;
		
		public RowIterator(Row row){
			this.row = row;
			this.size = row.getMeta().getSize();
			this.currentPos = -1;
		}
		
		public boolean hasNext() {
			
			return currentPos + 1 <size;
		}

		public Object next() {
			
			return row.values[++currentPos];
		}

		public void remove() {
			
			
		}
		
	}
	
	protected Row(RowMeta meta){
		this.meta = meta;
		values = new Object[meta.getSize()];
		
		// init the values with null values
		for(int i = 0; i <meta.getSize(); i++ ){
			values[i] = null;
		}
	}

	public Iterator<Object> iterator() {
		return new RowIterator<Object>(this);
	}
	
	public void set(int pos, Object o) throws IndexOutOfBoundsException{
		if (pos >= meta.getSize()){
			throw new IndexOutOfBoundsException("cannot set row value at position " + pos + " for row.size=" + meta.getSize() );
		}
		values[pos] = o;
	}
	
	public Object get(int pos) throws IndexOutOfBoundsException{
		if (pos >= meta.getSize()){
			throw new IndexOutOfBoundsException("cannot get row value at position " + pos + " for row.size=" + meta.getSize() );
		}
		
		return values[pos];
	}

	public RowMeta getMeta() {
		return meta;
	}

	public String dump() {
		StringBuffer buf = new StringBuffer();
		buf.append("{");
		for(Object o : values){
			if (o == null){
				buf.append("[null]");
			}else{
				buf.append("[" + o.toString() + "]");
			}
			
		}
		buf.append("}");
		return buf.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( this == obj){
			return true;
		}
		
		if (!(obj instanceof Row)){
			return false;
		}
		
		Row row = (Row)obj;
		if (meta.getSize() != row.getMeta().getSize()){
			return false;
		}
		
		for(int i = 0; i < meta.getSize(); i++){
			if (get(i) == null){
				if (row.get(i) != null){
					return false;
				}
			}
			else{
				if (row.get(i) == null){
					return false;
				}
				else{
					if (!get(i).toString().equals(row.get(i).toString())){
						return false;
					}
				}
			}
		}
		
		
		return true;
	}

	public void setTransformation(Transformation transformation) {
		this.transformation = transformation;
	}

	public Transformation getTransformation() {
		return transformation;
	}
}
