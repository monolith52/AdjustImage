package monolith52.adjustimage.logic;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;

import org.apache.commons.io.IOUtils;

import monolith52.adjustimage.util.ResourceUtil;

public class Adjuster {
	private static final String writeFormatName = "jpg";
	private static final List<String> extensions = Arrays.asList(new String[]{".png", ".PNG", ".jpg", ".JPG", ".jpeg", ".JPEG"});
	private static final List<String> pngExtensions = Arrays.asList(new String[]{".png", ".PNG"});

	
	public void adjust(String filename, InputStream in, OutputStream out) throws IOException {
		if (isTargetImage(filename)) {
			BufferedImage image = ImageIO.read(in);
			ImageWriter writer = ImageIO.getImageWritersByFormatName(writeFormatName).next();
			
			JPEGImageWriteParam jiparam = new JPEGImageWriteParam(Locale.getDefault());
			jiparam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			jiparam.setCompressionQuality(ResourceUtil.getFloat("adjust.compressrate"));
			
			writer.setOutput(ImageIO.createImageOutputStream(out));
			writer.write(null, new IIOImage(image, null, null), jiparam);
		} else {
			IOUtils.copy(in, out);
		}
	}
	
	public boolean isTargetImage(String filename) {
		return extensions.stream().anyMatch((ext) -> filename.endsWith(ext));
	}
	
	public String getTargetFilename(String filename) {
		if (pngExtensions.stream().anyMatch((ext) -> filename.endsWith(ext))) {
			return filename.substring(0, filename.length() - 4) + ".jpg";
		}
		return filename;
	}
}
