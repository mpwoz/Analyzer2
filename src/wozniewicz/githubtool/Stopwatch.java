package wozniewicz.githubtool;

/**
 * Simple class for timing things.
 * @author wozniew1
 *
 */
public class Stopwatch {
	private long starttime, endtime;
	
	public void Start() {
		starttime = System.currentTimeMillis();
	}
	
	public void Stop() {
		End();
		Report();
	}
	
	public void End() {
		endtime = System.currentTimeMillis();
	}
	
	public void Report() {
		long difference = (endtime - starttime) / 1000; // Difference (in seconds)
		System.out.println(difference + "s");
	}
}
