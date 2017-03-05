package monolith52.adjustimage;

import java.io.File;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class FileTableRecord {

	public FileTableRecord(File file, Long originalSize, Long compressedSize, Double progress) {
		this.file 			= new SimpleObjectProperty<File>(this, "file", file);
		this.originalSize 	= new SimpleLongProperty(this, "originalSize", originalSize);
		this.compressedSize = new SimpleLongProperty(this, "compressedSize", compressedSize);
		this.progress 		= new SimpleDoubleProperty(this, "progress", progress);
		this.error			= new SimpleStringProperty(this, "error", null);
	}
	
	final private ObjectProperty<File> file;
	public void setFile(File file) { this.file.set(file); }
	public File getFile(){ return this.file.get(); }
	public ObjectProperty<File> fileProperty(){ return file; }
	
	final private LongProperty originalSize;
	public void setOriginalSize(Long filesize) { this.originalSize.set(filesize); }
	public Long getOriginalSize() { return this.originalSize.get(); }
	public LongProperty originalSizeProperty(){ return originalSize; }
	
	final private LongProperty compressedSize;
	public void setCompressedSize(Long filesize) { this.compressedSize.set(filesize); }
	public Long getCompressedSize() { return this.compressedSize.get(); }
	public LongProperty compressedSizeProperty(){ return compressedSize; }
	
	final private DoubleProperty progress;
	public void setProgress(Double progress) { this.progress.set(progress); }
	public Double getProgress() { return this.progress.get(); }
	public DoubleProperty progressProperty(){ return progress; }

	final private StringProperty error;
	public void setError(String error){ this.error.set(error); }
	public String getError(){ return error.get(); }
	public StringProperty errorProperty(){ return error; }
}
