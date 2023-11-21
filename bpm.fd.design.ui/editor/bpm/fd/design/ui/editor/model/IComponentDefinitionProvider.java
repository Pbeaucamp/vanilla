package bpm.fd.design.ui.editor.model;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

/**
 * a convenient interface to be implement by the COmponent EditPart
 * to allow to get the IComponentDefinition from an EditPart whatever
 * its class is (old COmponentPart or new One)
 * @author ludo
 *
 */
public interface IComponentDefinitionProvider {

	public IComponentDefinition getComponent();
}
