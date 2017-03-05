package monolith52.adjustimage.logic;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import monolith52.adjustimage.util.ResourceUtil;

public class ImageArchive {
	
	Adjuster adjuster = new Adjuster();
	File inputFile;
	ArchiveListener listener;
	boolean isFileError = false;
	
	public ImageArchive(File inputFile, ArchiveListener listener) {
		this.inputFile = inputFile;
		this.listener = listener;
	}

	public int scan() throws ZipException, IOException {
		int index = 0;
		try (ZipFile zip = new ZipFile(inputFile, Charset.forName(ResourceUtil.getString("zipfile.charset")))) {
			
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				index += 1;
				ZipEntry entry = entries.nextElement();
				System.out.println("scan: " + entry.getName());
			}
		} catch (IOException e) {
			isFileError = true;
			throw e;
		}
		return index;
	}
	
	public void analyze() {
		try {
			archive(null);
		} catch (IOException e) {
			if (listener != null) listener.failed(inputFile, e.getMessage());
		}
	}
	
	public void process() {
		try {
			archive(getSaveFile());
		} catch (IOException e) {
			if (listener != null) listener.failed(inputFile, e.getMessage());
		}
	}
	
	protected void archive(File outputFile) throws IOException {
		if (isFileError) {
			System.out.println("archive skipped");
			return;
		}
		
		System.out.println("archive action");
		
		boolean failed = false;
		
		try (
				ZipFile zip = new ZipFile(inputFile, Charset.forName(ResourceUtil.getString("zipfile.charset")));
				OutputStream outputStream = (outputFile == null) ?
						new IdleOutputStream() :
						new FileOutputStream(outputFile);
				ZipOutputStream zipOut = new ZipOutputStream(outputStream);
		) {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			Progress progress = new Progress();
			progress.length = scan();
			progress.current = 0;
			while (entries.hasMoreElements()) {
				progress.current += 1;
				ZipEntry entry = entries.nextElement();
				String entryName = adjuster.getTargetFilename(entry.getName());
				
				ZipEntry saveEntry = new ZipEntry(entryName);
				zipOut.putNextEntry(saveEntry);
				adjuster.adjust(entryName, zip.getInputStream(entry), zipOut);
				zipOut.closeEntry();
				
				if (listener != null) listener.update(inputFile, progress);
				System.out.println("archive: " + entry.getName());
			}
			zip.close();
			zipOut.close();
			
			if (outputFile != null) {
				long inputFilesize = inputFile.length();
				long outputFilesize = outputFile.length();
				if (listener != null) listener.success(inputFile, outputFile);
				System.out.println("saved filesize [" + outputFilesize + "]");
				
				if (ResourceUtil.getBoolean("remove.ineffectiveFile")) {
					if (inputFilesize <= outputFilesize) {
						outputFile.delete();
						outputFile = null;
						System.out.println("ineffective file removed");
					}
				}
				
				if (ResourceUtil.getBoolean("rename.effectiveOriginalFile")) {
					if (inputFilesize > outputFilesize) {
						String renameFilename = inputFile.getParent() + File.separator + ResourceUtil.getString("rename.prefix") + inputFile.getName();
						inputFile.renameTo(new File(renameFilename));
						System.out.println("original file renamed");
					}
				}
			}
			
			System.out.println("archive complete");
		} catch (IOException e) {
			if (listener != null) listener.failed(inputFile, e.getMessage());
			failed = true;
		} catch (RuntimeException e) {
			if (listener != null) listener.failed(inputFile, e.getMessage());
			failed = true;
		}
		
		if (ResourceUtil.getBoolean("remove.incompleteFile")) {
			if (failed && outputFile != null) {
				outputFile.delete();
				outputFile = null;
			}
		}
	}
	
	public File getSaveFile() {
		String filename = inputFile.getAbsolutePath();
		String[] parts = filename.split(File.separator.replace("\\", "\\\\"));
		parts[parts.length-1] = ResourceUtil.getString("savefile.prefix") + parts[parts.length-1];
		filename = String.join(File.separator, parts);
		
		return new File(filename);
	}
	
	private class IdleOutputStream extends OutputStream implements AutoCloseable {
		public void write(int b) throws IOException {
		}
	}
}
