package bpm.gateway.core.transformations.gid;

import java.util.HashMap;

import bpm.gateway.core.StreamElement;
import bpm.gateway.core.Transformation;
import bpm.gateway.core.transformations.SimpleMappingTransformation;

public class MappingNode extends GidNode<SimpleMappingTransformation> {

	public MappingNode(SimpleMappingTransformation transformation) {
		super(transformation);
	}

	@Override
	public Query evaluteQuery() {
		Query q1 = null;
		Query q2 = null;

		boolean naturalOrder = false;
		if(getTransformation().isMaster(getChilds().get(0).getTransformation())) {
			q1 = getChilds().get(0).evaluteQuery();
			q2 = getChilds().get(1).evaluteQuery();
			naturalOrder = true;
		}
		else {
			q1 = getChilds().get(1).evaluteQuery();
			q2 = getChilds().get(0).evaluteQuery();
			naturalOrder = false;
		}

		Query query = new Query(q1, q2);

		/*
		 * create the where condition from the mapping transfo
		 */
		HashMap<String, String> maps = getTransformation().getMappings();
		for(String input : maps.keySet()) {
			// XXX : check if q1 match to p.x
			// TODO: Check if it works
			String where = "";
			try {

				//
				// if(naturalOrder) {
				// if(getChilds().get(0) instanceof MappingNode) {
				//
				// }
				// }
				// else {
				//
				// }
				// if(where == null) {
				String from1 = q1.getFrom().get(0);
				String from2 = q2.getFrom().get(0);
				String input1 = input;
				String input2 = maps.get(input);
				if(q1.getFrom().size() > 1) {
					if(naturalOrder) {
						int i = 0;
						for(Transformation t : ((MappingNode) getChilds().get(0)).getTransformation().getInputs()) {
							for(StreamElement e : t.getDescriptor(null).getStreamElements()) {
								if(e.name.equals(maps.get(input))) {
									from1 = q1.getFrom().get(i);
									for(String s : q1.getSelect()) {
										if(s.contains(e.name) && s.contains(" as ")) {
											input1 = s.split(" as")[0];
											input1 = input1.replace(from1 + ".", "");
											break;
										}
									}
								}
							}
							i++;
						}
					}
					else {
						int i = 0;
						for(Transformation t : ((MappingNode) getChilds().get(1)).getTransformation().getInputs()) {
							for(StreamElement e : t.getDescriptor(null).getStreamElements()) {
								if(e.name.equals(maps.get(input))) {
									from1 = q2.getFrom().get(i);
									for(String s : q2.getSelect()) {
										if(s.contains(e.name) && s.contains(" as ")) {
											input1 = s.split(" as")[0];
											input1 = input1.replace(from1 + ".", "");
											break;
										}
									}
								}
							}
							i++;
						}
					}
				}
				else {

					for(String s : q1.getSelect()) {
						if(s.contains(input1) && s.contains(" as ")) {
							input1 = s.split(" as")[0];
							input1 = input1.replace(from1 + ".", "");
							break;
						}
					}

				}
				if(q2.getFrom().size() > 1) {
					if(naturalOrder) {
						int i = 0;
						for(Transformation t : ((MappingNode) getChilds().get(1)).getTransformation().getInputs()) {
							for(StreamElement e : t.getDescriptor(null).getStreamElements()) {
								if(e.name.equals(maps.get(input))) {
									from2 = q2.getFrom().get(i);
									for(String s : q2.getSelect()) {
										if(s.contains(e.name) && s.contains(" as ")) {
											input2 = s.split(" as")[0];
											input2 = input2.replace(from2 + ".", "");
											break;
										}
									}
								}
							}
							i++;
						}
					}
					else {
						int i = 0;
						for(Transformation t : ((MappingNode) getChilds().get(0)).getTransformation().getInputs()) {
							for(StreamElement e : t.getDescriptor(null).getStreamElements()) {
								if(e.name.equals(maps.get(input))) {
									from2 = q1.getFrom().get(i);
									for(String s : q1.getSelect()) {
										if(s.contains(e.name) && s.contains(" as ")) {
											input2 = s.split(" as")[0];
											input2 = input2.replace(from2 + ".", "");
											break;
										}
									}
								}
							}
							i++;
						}
					}
				}
				else {

					for(String s : q2.getSelect()) {
						if(s.contains(input2) && s.contains(" as ")) {
							input2 = s.split(" as")[0];
							input2 = input2.replace(from2 + ".", "");
							break;
						}
					}

				}

				where = from1 + "." + input1 + "=" + from2 + "." + input2;
				// }
			} catch(Exception e) {
				e.printStackTrace();
				// throw e;
			}

			query.addWhere(where);
		}

		return query;
	}

}
