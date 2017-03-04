package jp.sourceforge.adjustimage.logic;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.apache.commons.io.IOUtils;

import jp.sourceforge.adjustimage.model.Filesize;
import jp.sourceforge.adjustimage.view.FileListTable;
import monolith52.adjustimage.util.ResourceUtil;

public class ImageArchive {
	
	File targetFile;
	FileListTable fileListTable;
	boolean isFileError = false;
	
	public ImageArchive(File targetFile) {
		this.targetFile = targetFile;
	}

	public void setFileListTable(FileListTable obj) {
		this.fileListTable = obj;
	}

	public int scan() throws ZipException, IOException {
		ZipFile zip = null;
		int index = 0;
		try {
			zip = new ZipFile(targetFile, Charset.forName(ResourceUtil.getString("zipfileCharset")));
			Enumeration<? extends ZipEntry> entries = zip.entries();
			while (entries.hasMoreElements()) {
				index += 1;
				ZipEntry entry = entries.nextElement();
				System.out.println("scan: " + entry.getName());
			}
		} catch (IOException e) {
			isFileError = true;
			throw e;
		} finally {
			if (zip != null) zip.close();
		}
		return index;
	}
	
	public void analyze() throws IOException {
		archive(null);
	}
	
	public void save() throws IOException {
		archive(getSaveFile());
	}
	
	public void archive(File outputFile) throws IOException {
		if (isFileError) {
			System.out.println("archive skipped");
			return;
		}
		
		System.out.println("archive action");
		
		OutputStream outputStream = (outputFile == null) ?
				new IdleOutputStream() :
				new FileOutputStream(outputFile);
		boolean failed = false;
		
		ZipFile zip = new ZipFile(targetFile, Charset.forName(ResourceUtil.getString("zipfileCharset")));
		ZipOutputStream zipOut = new ZipOutputStream(outputStream);
		
		try {
			Enumeration<? extends ZipEntry> entries = zip.entries();
			Progress progress = new Progress();
			progress.length = scan();
			progress.current = 0;
			while (entries.hasMoreElements()) {
				progress.current += 1;
				ZipEntry entry = entries.nextElement();
	
				JPEGImageWriteParam jiparam = new JPEGImageWriteParam(Locale.getDefault());
				jiparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				jiparam.setCompressionQuality(ResourceUtil.getFloat("adjust.compressRate"));
				
				if (isImage(entry.getName())) {
					
					BufferedImage image = ImageIO.read(zip.getInputStream(entry));
					ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
					
					String entryName = entry.getName();
					if (entryName.endsWith(".png") || entryName.endsWith(".PNG")) {
						entryName = entryName.substring(0, entryName.length() - 4) + ".jpg";
					}
					
					ZipEntry saveEntry = new ZipEntry(entryName);
					zipOut.putNextEntry(saveEntry);
					writer.setOutput(ImageIO.createImageOutputStream(zipOut));
					writer.write(null, new IIOImage(image, null, null), jiparam);
					zipOut.closeEntry();
					
					fileListTable.update(progress);
					System.out.println("archive: [" + new Filesize(saveEntry.getSize()) + "] " + entryName);
				} else {
					ZipEntry saveEntry = new ZipEntry(entry.getName());
					zipOut.putNextEntry(saveEntry);
					IOUtils.copy(zip.getInputStream(entry), zipOut);
					
					fileListTable.update(progress);
					System.out.println("archive: " + entry.getName());
				}
			}
			
			if (outputFile != null) {
				zip.close();
				zip = null;
				zipOut.close();
				zipOut = null;
				
				long inputFilesize = targetFile.length();
				long outputFilesize = outputFile.length();
				fileListTable.complete(outputFilesize);
				System.out.println("saved filesize [" + outputFilesize + "]");
				
				// �t�@�C���T�C�Y���������Ȃ�Ȃ������ꍇ�͏o�̓t�@�C�����폜����
				if (ResourceUtil.getBoolean("remove.ineffectiveFile")) {
					if (inputFilesize <= outputFilesize) {
						outputFile.delete();
						outputFile = null;
						System.out.println("ineffective file removed");
					}
				}
				
				// �t�@�C���T�C�Y���������Ȃ�����I���W�i���t�@�C�������l�[������
				if (ResourceUtil.getBoolean("rename.effectiveOriginalFile")) {
					if (inputFilesize > outputFilesize) {
						String renameFilename = targetFile.getParent() + File.separator + ResourceUtil.getString("rename.prefix") + targetFile.getName();
						targetFile.renameTo(new File(renameFilename));
						System.out.println("original file renamed");
					}
				}
			}
			
			System.out.println("archive complete");
		} catch (IOException e) {
			fileListTable.failed(e.getMessage());
			failed = true;
		} catch (RuntimeException e) {
			fileListTable.failed(e.getMessage());
			failed = true;
		} finally {
			if (zip != null) zip.close();
			if (zipOut != null) zipOut.close();
		}
		
		// ���s�����ꍇ�͍폜����
		if (ResourceUtil.getBoolean("remove.incompleteFile")) {
			if (failed && outputFile != null) {
				outputFile.delete();
				outputFile = null;
			}
		}
	}
	
	public File getSaveFile() {
		String filename = targetFile.getAbsolutePath();
		String[] parts = filename.split(File.separator.replace("\\", "\\\\"));
		parts[parts.length-1] = ResourceUtil.getString("savefile.prefix") + parts[parts.length-1];
		filename = String.join(File.separator, parts);
		
		return new File(filename);
	}
	
	protected boolean isImage(String filename) {
		return filename.endsWith("jpg") || filename.endsWith("JPG") ||
				filename.endsWith("jpeg") || filename.endsWith("JPEG") ||
				filename.endsWith("png") || filename.endsWith("PNG");
	}
	
	private class IdleOutputStream extends OutputStream {
		public void write(int b) throws IOException {
		}
	}
}
