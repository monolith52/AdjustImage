package monolith52.adjustimage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import monolith52.adjustimage.logic.ArchiveListener;
import monolith52.adjustimage.logic.ImageArchive;
import monolith52.adjustimage.logic.Progress;
import monolith52.adjustimage.view.FileCell;
import monolith52.adjustimage.view.FilesizeCell;
import monolith52.adjustimage.view.PercentageCell;
import monolith52.adjustimage.view.RecordRow;

public class ApplicationController {

	@FXML
	private MenuItem closeMenuItem;
	@FXML
	private TableView<FileTableRecord> tableView;
	@FXML
	private Label messageLabel;
	@FXML
	private Button convertButton;
	@FXML
	private TableColumn<FileTableRecord, File> columnFile;
	@FXML
	private TableColumn<FileTableRecord, Long> columnOriginalSize;
	@FXML
	private TableColumn<FileTableRecord, Long> columnCompressedSize;
	@FXML
	private TableColumn<FileTableRecord, Double> columnProgress;
	
	@FXML
	public void initialize() {
		tableView.setSelectionModel(null);
		tableView.setRowFactory((tv) -> { return new RecordRow(); });

		columnFile.setCellValueFactory(new PropertyValueFactory<FileTableRecord, File>("file"));
		columnOriginalSize.setCellValueFactory(new PropertyValueFactory<FileTableRecord, Long>("originalSize"));
		columnCompressedSize.setCellValueFactory(new PropertyValueFactory<FileTableRecord, Long>("compressedSize"));
		columnProgress.setCellValueFactory(new PropertyValueFactory<FileTableRecord, Double>("progress"));

		columnFile.setCellFactory((column) -> {	return new FileCell(); });
		columnOriginalSize.setCellFactory((column) -> {	return new FilesizeCell<FileTableRecord>(); });
		columnCompressedSize.setCellFactory((column) -> { return new FilesizeCell<FileTableRecord>(); });
		columnProgress.setCellFactory((column) -> {	return new PercentageCell<FileTableRecord>(); });
		
//		columnFile.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
//			return tableView.getWidth() - columnOriginalSize.getWidth() - columnCompressedSize.getWidth() - columnProgress.getWidth() - 2;
//		}, tableView.width, columnOriginalSize.widthProperty(), columnCompressedSize.widthProperty(), columnProgress.widthProperty()));
	}

	@FXML
	public void onConvertButtonAction(ActionEvent event) {
		convertButton.setDisable(true);
		new Thread(() -> {
			convert();
		}).start();
	}

	public void convert() {
		tableView.getItems().forEach((record) -> {
			System.out.println(String.format("start processing %s", record.getFile().getName()));
			ImageArchive ia = new ImageArchive(record.getFile(), new ArchiveHandler());
			ia.process();
		});
	}

	public void setScene(Scene scene) {
		scene.setOnDragDropped(new DroppedHandler());
		scene.setOnDragOver(new DragOverHandler());
	}

	public void addFiles(List<File> files) {
		int[] count = new int[] { 0 };
		tableView.getItems().clear();
		files.forEach((file) -> {
			FileTableRecord record = new FileTableRecord(file, file.length(), 0l, 0.0d);
			tableView.getItems().add(record);
			count[0]++;
		});
		messageLabel.setText(String.format("%d files detected.", count[0]));
	}
	
	private FileTableRecord findRecordByFile(File file) {
		Optional<FileTableRecord> record = tableView.getItems().stream().filter((r) -> r.getFile() == file).findFirst();
		if (!record.isPresent()) throw new RuntimeException("record does not found in tableView");
		return record.get();
	}

	class ArchiveHandler implements ArchiveListener {
		@Override
		public void update(File file, Progress progress) {
			Platform.runLater(() -> {
				messageLabel.setText(String.format("Progress %s in %s", file.getName(), progress.toString()));
			});
		}
		@Override
		public void success(File inputFile, File outputFile) {
			Platform.runLater(() -> {
				FileTableRecord record = findRecordByFile(inputFile);
				System.out.println(String.format("done processing %s", inputFile.getName()));
				record.setCompressedSize(outputFile.length());
				record.setProgress((double)outputFile.length() / record.getOriginalSize()); 
			});
		}
		@Override
		public void failed(File inputFile, String msg) {
			Platform.runLater(() -> {
				FileTableRecord record = findRecordByFile(inputFile);
				System.out.println(String.format("failed processing %s for %s", inputFile.getName(), msg));
				record.setError(msg);
				tableView.refresh();
			});
		}
	}

	class DroppedHandler implements EventHandler<DragEvent> {
		@Override
		public void handle(DragEvent event) {
			Object content = event.getDragboard().getContent(DataFormat.FILES);
			@SuppressWarnings("unchecked")
			List<File> files = (content instanceof List) ? (List<File>) content : new ArrayList<File>();
			Platform.runLater(() -> {
				addFiles(files);
				convertButton.setDisable(false);
			});
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
