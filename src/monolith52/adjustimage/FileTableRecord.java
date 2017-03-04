package monolith52.adjustimage;

import java.io.File;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import jp.sourceforge.adjustimage.logic.Progress;
import jp.sourceforge.adjustimage.model.Filesize;

public class FileTableRecord {

	public FileTableRecord(File file, Filesize originalSize, Filesize compressedSize, Progress progress) {
		this.file 			= new SimpleObjectProperty<File>(this, "file", file);
		this.originalSize 	= new SimpleObjectProperty<Filesize>(this, "originalSize", originalSize);
		this.compressedSize = new SimpleObjectProperty<Filesize>(this, "compressedSize", compressedSize);
		this.progress 		= new SimpleObjectProperty<Progress>(this, "progress", progress);
	}
	
	final ObjectProperty<File> file;
	public void setFile(File file) { this.file.set(file); }
	public File getFile(){ return this.file.get(); }
	public ObjectProperty<File> fileProperty(){ return file; }
	
	final ObjectProperty<Filesize> originalSize;
	public void setOriginalSize(Filesize filesize) { this.originalSize.set(filesize); }
	public Filesize getOriginalSize() { return this.originalSize.get(); }
	public ObjectProperty<Filesize> originalSizeProperty(){ return originalSize; }
	
	final ObjectProperty<Filesize> compressedSize;
	public void setCompressedSize(Filesize filesize) { this.compressedSize.set(filesize); }
	public Filesize getCompressedSize() { return this.compressedSize.get(); }
	public ObjectProperty<Filesize> compressedSizeProperty(){ return compressedSize; }
	
	final ObjectProperty<Progress> progress;
	public void setProgress(Progress progress) { this.progress.set(progress); }
	public Progress getProgress() { return this.progress.get(); }
	public ObjectProperty<Progress> progressProperty(){ return progress; }

}
