package monolith52.adjustimage.logic;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;

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

	private int scan(InputStream input) throws IOException {
		int count = 0;
		try (ZipArchiveInputStream zipIn = new ZipArchiveInputStream(input)) {
			ZipArchiveEntry entry;
			while ((entry = zipIn.getNextZipEntry()) != null) {
				count++;
				System.out.println("scan: " + entry.getName());
			}
		}
		return count;
	}

	public void process() {
		process(getSaveFile());
	}
	
	public void process(File outputFile) {
		if (isFileError) {
			System.out.println("archive skipped");
			return;
		}
		
		System.out.println("archive action");
		
		boolean failed = false;
		
		ByteArrayOutputStream inBuffer = new ByteArrayOutputStream((int)inputFile.length());
		try (FileInputStream input = new FileInputStream(inputFile)) {
			IOUtils.copy(input, inBuffer);
		} catch (IOException e) {
			if (listener != null) listener.failed(inputFile, e.getMessage());
			return;
		}
		
		ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
		try (
				ZipArchiveInputStream zipIn = new ZipArchiveInputStream(new ByteArrayInputStream(inBuffer.toByteArray()));
				ZipArchiveOutputStream zipOut = new ZipArchiveOutputStream(outBuffer);
		) {
			Progress progress = new Progress();
			progress.length = scan(new ByteArrayInputStream(inBuffer.toByteArray()));
			
			ZipArchiveEntry entry;
			while ((entry = zipIn.getNextZipEntry()) != null) {
				progress.current += 1;
				
				String entryName = adjuster.getTargetFilename(entry.getName());
				ZipArchiveEntry newEntry = new ZipArchiveEntry(entryName);
				
				zipOut.putArchiveEntry(newEntry);
				if (!entry.isDirectory()) adjuster.adjust(entryName, zipIn, zipOut);				
				zipOut.closeArchiveEntry();
				
				if (listener != null) listener.update(inputFile, progress);
				System.out.println("archive: " + entry.getName());
			}
			zipIn.close();
			zipOut.close();
			
			long inputFilesize = inBuffer.toByteArray().length;
			long outputFilesize = outBuffer.toByteArray().length;
			
			if (!ResourceUtil.getBoolean("remove.ineffectiveFile") || (inputFilesize > outputFilesize)) {
				try (FileOutputStream out = new FileOutputStream(outputFile)) {
					IOUtils.copy(new ByteArrayInputStream(outBuffer.toByteArray()), out);
					System.out.println("saved filesize [" + outputFilesize + "]");
				}
			}
			
			if (ResourceUtil.getBoolean("rename.effectiveOriginalFile")) {
				if (inputFilesize > outputFilesize) {
					String renameFilename = inputFile.getParent() + File.separator + ResourceUtil.getString("rename.prefix") + inputFile.getName();
					inputFile.renameTo(new File(renameFilename));
					System.out.println("original file renamed");
				}
			}
			
			if (listener != null) listener.success(inputFile, outputFilesize);
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
}
