package bpm.vanilla.api.runtime.dto;

import bpm.fa.api.item.Item;

public class CubeNullCell extends CubeCell {
	public CubeNullCell(Item it) {
		super(it, null, "ItemNull");
	}
}
