package jp.sourceforge.adjustimage.view;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import jp.sourceforge.adjustimage.logic.ImageArchive;

public class Application extends JFrame {
	private static final long serialVersionUID = 1L;
	
	List<ImageArchive> imageArchives;

	FileListTable fileListTable;
	JButton saveButton;
	
	public static void main(String[] args) {
		System.out.println("application is started");
		
		Application app = new Application();
		app.setVisible(true);
	}
	
	public Application() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("AdjustImage");
		setSize(700, 500);
		setLayout(new BorderLayout());
		setTransferHandler(new DropSupport());
		
		fileListTable = new FileListTable();
		JScrollPane scrollPane = new JScrollPane(fileListTable);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		saveButton = new JButton("•Û‘¶");
		saveButton.addActionListener(new SaveAction());
		getContentPane().add(saveButton,  BorderLayout.SOUTH);
	}

	class DropSupport extends TransferHandler {
		private static final long serialVersionUID = 1L;

		@Override
		public boolean canImport(TransferSupport info) {
			if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }
			
			return true;
		}

		@SuppressWarnings("unchecked")
		@Override
		public boolean importData(TransferSupport info) {
			if (!info.isDrop()) {
                return false;
            }
			
			if (!info.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
                return false;
            }
			
			List<File> fileList;
			try {
				fileList = (List<File>)info.getTransferable().getTransferData(DataFlavor.javaFileListFlavor);
			} catch (UnsupportedFlavorException | IOException e) {
				e.printStackTrace();
				return false;
			}
			imageArchives = new ArrayList<ImageArchive>();
			fileListTable.clearEntry();
			for (File file: fileList) {
				System.out.println("import: " + file.toString());
				ImageArchive imageArchive = new ImageArchive(file);
				imageArchive.setFileListTable(fileListTable);
				imageArchives.add(imageArchive);
				try {
					imageArchive.scan();
					fileListTable.addEntry(file.getName(), file.length());
				} catch (IOException e) {
					fileListTable.addErrorEntry(file.getName(), file.length(), e.getMessage());
					e.printStackTrace();
				} catch (RuntimeException e) {
					fileListTable.addErrorEntry(file.getName(), file.length(), e.getMessage());
					e.printStackTrace();
				}
			}
			return true;
		}
		
	}
	
	
	class SaveAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			saveButton.setEnabled(false);
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						int index = 0;
						for (ImageArchive imageArchive: imageArchives) {
							fileListTable.setCurrentIndex(index++);
							imageArchive.save();
						}
						SwingUtilities.invokeLater(new Runnable(){
							public void run() {
								saveButton.setEnabled(true);
								saveButton.repaint();
							}
						});
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (RuntimeException e2) {
						e2.printStackTrace();				
					}
				}
			}).start();
		}
		
	}
}
