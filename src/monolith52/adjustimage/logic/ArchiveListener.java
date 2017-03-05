package monolith52.adjustimage.logic;

import java.io.File;

public interface ArchiveListener {

	public void update(File inputFile, Progress progress);
	public void success(File inputFile, File outputFile);
	public void failed(File inputFile, String msg);
	
}
