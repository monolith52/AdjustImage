package jp.sourceforge.adjustimage.model;

public class Filesize {
	private long filesize;
	public Filesize(long filesize) {
		this.filesize = filesize;
	}
	public long get() {
		return this.filesize;
	}
	public String toString() {
		return String.format("%1$,3d KB", filesize / 1024);
	}
}
