package jp.sourceforge.adjustimage.view;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;

public class FilesizeCellRenderer extends DefaultTableCellRenderer {
	private static final long serialVersionUID = 4661260875395263102L;

	static final Color LESS_COLOR = new Color(213, 188, 212);
	static final Color FULL_COLOR = new Color(197, 207, 227);
	static final Color FAIL_COLOR = new Color(232, 157, 151);	
	double filesizeRate = 0.0f;
	
	public FilesizeCellRenderer() {
		super();
		setHorizontalAlignment(SwingConstants.RIGHT);
	}

	public void setFilesizeRate(double filesizeRate) {
		this.filesizeRate = filesizeRate;
	}
	
	@Override
	protected void paintComponent(Graphics g) {

		// progress bar
		int width = (filesizeRate >= 1.0f ? getWidth() : (int)(getWidth() * filesizeRate));
		Color current = g.getColor();
		g.setColor(getBarColor());
		g.fillRect(0, 0, width, getHeight());
		g.setColor(current);
		
		super.paintComponent(g);
	}
	
	protected Color getBarColor() {
		if (filesizeRate >= 1.0f) return FAIL_COLOR;
		if (filesizeRate <= 0.0f) return Color.WHITE;
		int r = FULL_COLOR.getRed() + (int)((LESS_COLOR.getRed() - FULL_COLOR.getRed()) * filesizeRate);
		int g = FULL_COLOR.getGreen() + (int)((LESS_COLOR.getGreen() - FULL_COLOR.getGreen()) * filesizeRate);
		int b = FULL_COLOR.getBlue() + (int)((LESS_COLOR.getBlue() - FULL_COLOR.getBlue()) * filesizeRate);
		return new Color(r, g, b);
	}
}
