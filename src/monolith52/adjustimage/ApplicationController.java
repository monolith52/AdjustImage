package monolith52.adjustimage;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.DataFormat;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import jp.sourceforge.adjustimage.logic.Progress;
import jp.sourceforge.adjustimage.model.Filesize;

public class ApplicationController {

	@FXML private MenuItem closeMenuItem;
	@FXML private TableView<FileTableRecord> tableView;
	@FXML private Button convertButton;
	@FXML private TableColumn<FileTableRecord, File> columnFile;
	@FXML private TableColumn<FileTableRecord, Filesize> columnOriginalSize;
	@FXML private TableColumn<FileTableRecord, Filesize> columnCompressedSize;
	@FXML private TableColumn<FileTableRecord, Progress> columnProgress;
	

//	private Scene scene;
	@FXML
	public void initialize() {
		tableView.setSelectionModel(null);
		
		columnFile.setCellValueFactory(new PropertyValueFactory<FileTableRecord,File>("file"));
		columnOriginalSize.setCellValueFactory(new PropertyValueFactory<FileTableRecord,Filesize>("originalSize"));
		columnCompressedSize.setCellValueFactory(new PropertyValueFactory<FileTableRecord,Filesize>("compressedSize"));
		columnProgress.setCellValueFactory(new PropertyValueFactory<FileTableRecord,Progress>("progress"));
		
		columnFile.setCellFactory((column) -> {return new FileCell();});
		columnOriginalSize.setCellFactory((column) -> {return new OriginalSizeCell();});
		columnCompressedSize.setCellFactory((column) -> {return new CompressedSizeCell();});
		columnProgress.setCellFactory((column) -> {return new ProgressCell();});
	}
	
	public void setScene(Scene scene) {
//		this.scene = scene;
		scene.setOnDragDropped(new DroppedHandler());
		scene.setOnDragOver(new DragOverHandler());
	}
	
	public void addFiles(List<File> files) {
		files.forEach((file) -> {
			FileTableRecord record = new FileTableRecord(new File(file.getAbsolutePath()), new Filesize(file.length()), new Filesize(0), new Progress());
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
	
	class FileCell extends TableCell<FileTableRecord, File> {
		@Override
		protected void updateItem(File item, boolean empty) {
			super.updateItem(item, empty);
			
			setText((item != null) ? item.getName() : "");
		}
	}
	
	class OriginalSizeCell extends TableCell<FileTableRecord, Filesize> {
		@Override
		protected void updateItem(Filesize item, boolean empty) {
			super.updateItem(item, empty);
			
			setText((item != null) ? item.toString() : "");
			setAlignment(Pos.BASELINE_RIGHT);
		}
	}
	
	class CompressedSizeCell extends TableCell<FileTableRecord, Filesize> {
		@Override
		protected void updateItem(Filesize item, boolean empty) {
			super.updateItem(item, empty);
			
			setText((item != null) ? item.toString() : "");
			setAlignment(Pos.BASELINE_RIGHT);
		}
	}
	
	class ProgressCell extends TableCell<FileTableRecord, Progress> {
		Pane root = new Pane();
		Rectangle rect = new Rectangle();
		Label label = new Label();
		public ProgressCell() {
			root.prefWidthProperty().bind(Bindings.createDoubleBinding(() -> {
				return getWidth() - getInsets().getLeft() - getInsets().getRight();
			}, widthProperty(), insetsProperty()));
			root.prefHeightProperty().bind(Bindings.createDoubleBinding(() -> {
				return getHeight() - getInsets().getTop() - getInsets().getBottom();
			}, heightProperty(), insetsProperty()));
			
			label.prefWidthProperty().bind(root.widthProperty());
			label.prefHeightProperty().bind(root.heightProperty());
			label.setAlignment(Pos.BASELINE_CENTER);
//			label.setBackground(new Background(new BackgroundFill(Color.RED, null, null)));
			rect.heightProperty().bind(root.heightProperty());
			rect.setFill(new Color(202.0d/255, 225.0d/255, 223.0d/255, 1.0d));
			root.getChildren().addAll(rect, label);
		}
		@Override
		protected void updateItem(Progress item, boolean empty) {
			super.updateItem(item, empty);
			
			setPadding(new Insets(0, 0, 0, 0));
			rect.setVisible(!empty);
			label.setText((item != null) ? item.toString() : "");
			setGraphic(root);
		}
	}
}
