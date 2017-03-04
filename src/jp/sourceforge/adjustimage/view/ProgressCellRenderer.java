package jp.sourceforge.adjustimage.view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

import jp.sourceforge.adjustimage.logic.Progress;

public class ProgressCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 8328671711817095749L;

	private static final Color BAR_COLOR = new Color(187, 225, 208);
	private Progress progress = new Progress();
	
	public ProgressCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.CENTER);
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column) {
		
		if (value instanceof Progress) {
			this.progress = (Progress)value;
		}
		
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}

	@Override
	protected void paintComponent(Graphics g) {
		
		// progress bar
		int width = (progress.length <= 0 ? 0 : getWidth() * progress.current / progress.length);
		Color current = g.getColor();
		g.setColor(BAR_COLOR);
		g.fillRect(0, 0, width, getHeight());
		g.setColor(current);
		
		super.paintComponent(g);
	}
}
