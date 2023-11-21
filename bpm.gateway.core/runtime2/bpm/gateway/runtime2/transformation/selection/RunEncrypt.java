package bpm.gateway.runtime2.transformation.selection;

import java.util.List;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;

import bpm.gateway.core.transformations.EncryptTransformation;
import bpm.gateway.runtime2.RuntimeStep;
import bpm.gateway.runtime2.internal.Row;

public class RunEncrypt extends RuntimeStep{
	
	private EncryptTransformation encryptTransfo;
	private int[] columnIndices;
	
	private StandardPBEStringEncryptor stringEncryptor;
	
	public RunEncrypt(EncryptTransformation encryptTransfo, int bufferSize) {
		super(null, encryptTransfo, bufferSize);
		this.encryptTransfo = encryptTransfo;
		
	}

	@Override
	public void init(Object adapter) throws Exception {
		if (encryptTransfo.getInputs().size() != 1){
			String message = " EncryptTransformation cannot have more or less than one Input at runtime";
			error(message);
			throw new Exception(message);
		}
		
		stringEncryptor = new StandardPBEStringEncryptor();
		try {
			stringEncryptor.setPassword(encryptTransfo.getPublicKey());
		} catch (Exception e) {
			String message = " EncryptTransformation need a public key to encrypt data";
			error(message);
			throw new Exception(message);
		}
		
		List<Integer> l = encryptTransfo.getOutputedFor(encryptTransfo.getInputs().get(0));
		columnIndices = new int[l.size()];
		
		for(int i = 0; i < columnIndices.length; i++){
			columnIndices[i] = l.get(i);
		}
		
		info(" inited");
		
	}

	@Override
	public void performRow() throws Exception {
		
		if (areInputStepAllProcessed()){
			if (inputEmpty()){
				setEnd();
			}
		}
		
		if (isEnd() && inputEmpty()){
			return;
		}
		
		if (!isEnd() && inputEmpty()){
			try{
				Thread.sleep(10);
				return;
			}catch(InterruptedException e){
				
			}
		}
		
		Row row = readRow();

		for(int i = 0; i < columnIndices.length; i++){
			row.set(columnIndices[i], anonymeValue(row.get(columnIndices[i])));
		}
		
		writeRow(row);
		
	}

	private Object anonymeValue(Object object) {
		if(encryptTransfo.isEncrypt()){
			return stringEncryptor.encrypt(String.valueOf(object));
		}
		else {
			return stringEncryptor.decrypt(String.valueOf(object));
		}
	}

	@Override
	public void releaseResources() {
		info( " resources released");
		
	}

}
