package monolith52.adjustimage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import monolith52.adjustimage.view.FileCell;
import monolith52.adjustimage.view.FilesizeCell;
import monolith52.adjustimage.view.PercentageCell;

public class ApplicationController {

	@FXML private MenuItem closeMenuItem;
	@FXML private TableView<FileTableRecord> tableView;
	@FXML private Button convertButton;
	@FXML private TableColumn<FileTableRecord, File> columnFile;
	@FXML private TableColumn<FileTableRecord, Long> columnOriginalSize;
	@FXML private TableColumn<FileTableRecord, Long> columnCompressedSize;
	@FXML private TableColumn<FileTableRecord, Double> columnProgress;
	

//	private Scene scene;
	@FXML
	public void initialize() {
		tableView.setSelectionModel(null);
		
		columnFile.setCellValueFactory(new PropertyValueFactory<FileTableRecord,File>("file"));
		columnOriginalSize.setCellValueFactory(new PropertyValueFactory<FileTableRecord,Long>("originalSize"));
		columnCompressedSize.setCellValueFactory(new PropertyValueFactory<FileTableRecord,Long>("compressedSize"));
		columnProgress.setCellValueFactory(new PropertyValueFactory<FileTableRecord,Double>("progress"));
		
		columnFile.setCellFactory((column) -> {return new FileCell<FileTableRecord>();});
		columnOriginalSize.setCellFactory((column) -> {return new FilesizeCell<FileTableRecord>();});
		columnCompressedSize.setCellFactory((column) -> {return new FilesizeCell<FileTableRecord>();});
		columnProgress.setCellFactory((column) -> {return new PercentageCell<FileTableRecord>();});
	}
	
	public void setScene(Scene scene) {
//		this.scene = scene;
		scene.setOnDragDropped(new DroppedHandler());
		scene.setOnDragOver(new DragOverHandler());
	}
	
	public void addFiles(List<File> files) {
		files.forEach((file) -> {
			FileTableRecord record = new FileTableRecord(file, file.length(), 0l, 0.0d);
			tableView.getItems().add(record);
		});
	}
	
	class DroppedHandler implements EventHandler<DragEvent> {
		@Override
		public void handle(DragEvent event) {
			Object content = event.getDragboard().getContent(DataFormat.FILES);
			@SuppressWarnings("unchecked")
			List<File> files = (content instanceof List) ? (List<File>)content : new ArrayList<File>();
			Platform.runLater(() -> {addFiles(files);});
		}
	}
	
	class DragOverHandler implements EventHandler<DragEvent> {
		@Override
		public void handle(DragEvent event) {
			Dragboard db = event.getDragboard();
			if (db.hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY);
			} else {
				event.consume();
			}
		}
	}
}
