package bpm.architect.web.server.utils;

import java.util.List;
import java.util.Stack;

import com.thoughtworks.xstream.XStream;

import bpm.data.viz.core.preparation.PreparationRule;

public class UndoRedoPreparation {

	private static XStream xStream = new XStream();
	
	private Stack<String> previous = new Stack<>();
	private Stack<String> nexts = new Stack<>();
	
	public List<PreparationRule> undo() {
		String res = previous.pop();
		nexts.push(res);
		
		return (List<PreparationRule>) xStream.fromXML(previous.pop());
	}
	
	public List<PreparationRule> redo() {
		return (List<PreparationRule>) xStream.fromXML(nexts.pop());
	}
	
	public void historize(List<PreparationRule> rules) {
		String res = xStream.toXML(rules);
		previous.push(res);
	}
}
