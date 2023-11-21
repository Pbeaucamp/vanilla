package bpm.fd.web.client.utils;

import java.util.List;

import bpm.fd.web.client.widgets.DashboardWidget;

public class WidgetAlignHelper {

	public static enum AlignmentType {
		TOP, BOTTOM, LEFT, RIGHT
	}
	
	public static enum ResizeType {
		WIDTH, HEIGHT
	}
	
	public static void resize(List<DashboardWidget> widgets, ResizeType type) {
		int max = 0;
		for(DashboardWidget widget : widgets) {
			if(type == ResizeType.WIDTH) {
				if(widget.getWidth() > max) {
					max = widget.getWidth();
				}
			}
			else {
				if(widget.getHeight() > max) {
					max = widget.getHeight();
				}
			}
		}
		for(DashboardWidget widget : widgets) {
			if(type == ResizeType.WIDTH) {
				widget.resize(max, widget.getHeight());
			}
			else {
				widget.resize(widget.getWidth(), max);
			}
		}
	}
	
	public static void align(List<DashboardWidget> widgets, AlignmentType type) {
		if(type == AlignmentType.TOP) {
			int minTop = Integer.MAX_VALUE;
			for(DashboardWidget widget : widgets) {
				if(widget.getTop() < minTop) {
					minTop = widget.getTop();
				}
			}
			for(DashboardWidget widget : widgets) {
				widget.updatePosition(widget.getLeft(), minTop);
			}
		}
		else if(type == AlignmentType.BOTTOM) {
			int maxBottom = 0;
			for(DashboardWidget widget : widgets) {
				if(widget.getTop() + widget.getHeight() > maxBottom) {
					maxBottom = widget.getTop() + widget.getHeight();
				}
			}
			for(DashboardWidget widget : widgets) {
				widget.updatePosition(widget.getLeft(), maxBottom - widget.getHeight());
			}
		}
		else if(type == AlignmentType.LEFT) {
			int minLeft = Integer.MAX_VALUE;
			for(DashboardWidget widget : widgets) {
				if(widget.getLeft() < minLeft) {
					minLeft = widget.getLeft();
				}
			}
			for(DashboardWidget widget : widgets) {
				widget.updatePosition(minLeft, widget.getTop());
			}
		}
		else if(type == AlignmentType.RIGHT) {
			int maxRight = 0;
			for(DashboardWidget widget : widgets) {
				if(widget.getLeft() + widget.getWidth() > maxRight) {
					maxRight = widget.getLeft() + widget.getWidth();
				}
			}
			for(DashboardWidget widget : widgets) {
				widget.updatePosition(maxRight - widget.getWidth(), widget.getTop());
			}
		}
	}
	
}
