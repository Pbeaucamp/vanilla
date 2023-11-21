package bpm.document.management.core.model;

import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MailAttachementList implements Serializable {

	private List<InputStream> inputStreams = new ArrayList<>();
	private List<String> names = new ArrayList<>();
	private List<Integer> sizes = new ArrayList<>();

	public List<InputStream> getInputStreams() {
		return inputStreams;
	}

	public void setInputStreams(List<InputStream> inputStreams) {
		this.inputStreams = inputStreams;
	}

	public List<String> getNames() {
		return names;
	}

	public void setNames(List<String> names) {
		this.names = names;
	}

	public List<Integer> getSizes() {
		return sizes;
	}

	public void setSizes(List<Integer> sizes) {
		this.sizes = sizes;
	}

}
