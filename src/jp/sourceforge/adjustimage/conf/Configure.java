package jp.sourceforge.adjustimage.conf;

import java.nio.charset.Charset;

public class Configure {

	public static final String SAVEFILE_PREFIX = "!!AdjustImage!!";
	public static final String RENAME_PREFIX = "__AdjustOrigin__";
	
	public static final float ADJUST_COMPRESSRATE = 0.5f;
	public static final boolean REMOVE_INCOMPLETE_FILE = true;
	public static final boolean REMOVE_INEFFECTIVE_FILE = true;
	public static final boolean RENAME_EFFECTIVE_ORIGINAL_FILE = true;
	
	public static final Charset ZIPFILE_CHARSET = Charset.forName("MS932");
//	public static final Charset ZIPFILE_CHARSET = StandardCharsets.UTF_8;
	
}
