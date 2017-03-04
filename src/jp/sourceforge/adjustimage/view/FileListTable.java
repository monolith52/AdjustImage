package jp.sourceforge.adjustimage.view;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import jp.sourceforge.adjustimage.logic.Progress;
import jp.sourceforge.adjustimage.model.Filesize;

public class FileListTable extends JTable {
	private static final long serialVersionUID = 1L;

	private String[] columnNames = {"ファイル名", "サイズ", "圧縮後", "進捗"};
	int currentIndex = 0;
	FilesizeCellRenderer filesizeRenderer = new FilesizeCellRenderer();
	DefaultTableCellRenderer progressRenderer = new ProgressCellRenderer();
	
	public FileListTable() {
		TableModel model = new DefaultTableModel(columnNames, 0);
		setModel(model);
		
		getColumnModel().getColumn(0).setPreferredWidth(360);
		getColumnModel().getColumn(1).setPreferredWidth(80);
		getColumnModel().getColumn(2).setPreferredWidth(80);
		getColumnModel().getColumn(3).setPreferredWidth(120);
		
		DefaultTableCellRenderer right = new DefaultTableCellRenderer();
		right.setHorizontalAlignment(SwingConstants.RIGHT);
		getColumnModel().getColumn(1).setCellRenderer(right);
		getColumnModel().getColumn(2).setCellRenderer(filesizeRenderer);
		getColumnModel().getColumn(3).setCellRenderer(progressRenderer);
	}
	
	public void clearEntry() {
		DefaultTableModel model = (DefaultTableModel)getModel();
		model.setRowCount(0);
		setModel(model);
	}
	
	public void addEntry(String filename, long filesize) {
		DefaultTableModel model = (DefaultTableModel)getModel();
		model.addRow(new Object[]{filename, new Filesize(filesize), new Filesize(0), new Progress()});
		setModel(model);
	}
	
	public void addErrorEntry(String filename, long filesize, String errormsg) {
		DefaultTableModel model = (DefaultTableModel)getModel();
		model.addRow(new Object[]{filename, new Filesize(filesize), errormsg, "ERR"});
		setModel(model);
	}

	public void setCurrentIndex(int i) {
		this.currentIndex = i;
	}
	
	public void update(Progress progress) {
		setValueAt(progress, currentIndex, 3);
	}
	
	public void complete(long filesize) {
		setValueAt(new Filesize(filesize), currentIndex, 2);
	}
	
	public void failed(String msg) {
		setValueAt(msg, currentIndex, 2);
		repaint();
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		
		if (getValueAt(row, 1) instanceof String || getValueAt(row, 2) instanceof String) {
			c.setBackground(Color.RED);
			c.setForeground(Color.WHITE);
		} else {
			c.setBackground(Color.WHITE);
			c.setForeground(Color.BLACK);
		}
		
		if (getValueAt(row, 1) instanceof Filesize && getValueAt(row, 2) instanceof Filesize) {
			Filesize pre = (Filesize)getValueAt(row, 1);
			Filesize post = (Filesize)getValueAt(row, 2);
			filesizeRenderer.setFilesizeRate((double)post.get() / (double)pre.get());
		}
		
		return c;
	}
}
