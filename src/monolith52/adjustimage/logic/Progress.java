package monolith52.adjustimage.logic;

public class Progress {
	public int length = 0;
	public int current = 0;
	
	public String toString() {
		if (length == 0) return "0%";
		return String.format("%.2f%% (%d/%d)", (double)current * 100.0 / (double)length, current, length);
	}
}
