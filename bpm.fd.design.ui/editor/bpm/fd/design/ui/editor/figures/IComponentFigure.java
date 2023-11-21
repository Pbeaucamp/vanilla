package bpm.fd.design.ui.editor.figures;

import java.awt.Point;

import org.eclipse.draw2d.IFigure;

import bpm.fd.api.core.model.components.definition.IComponentDefinition;

public interface IComponentFigure extends IFigure{
	public void update(IComponentDefinition def, Point preferedSize);
}
