package wozniewicz.githubtool.downloader;
import static org.apache.commons.io.FileUtils.copyURLToFile;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class DownloadUnzipWorker implements Runnable {

	private final URL zipFile;
	private final String directory;
	private final String fileName = "src.zip";
	final int BUFFER = 2048;

	public DownloadUnzipWorker(URL zipFile, String username, String projectName, String directoryroot) {
		this.zipFile = zipFile;
		System.out.println("downloading "+projectName+"...");
		directory = directoryroot + projectName + "/";
		File file = new File(directory);
		file.mkdir();

	}

	@Override
	public void run() {
		download();
	}

	public void download() {
		try {
			File file = new File(directory + fileName);
			copyURLToFile(zipFile, file);
			uncompress();
		} catch (IOException e) {
			System.out
					.println("***Error! Couldn't find zip-file:"
							+ zipFile);
		}

	}

	public void uncompress() {
		try {
			// Initiate ZipFile object with the path/name of the zip file.
			ZipFile zipFile = new ZipFile(directory + fileName);

			// Extracts all files to the path specified
			zipFile.extractAll(directory);

		} catch (ZipException e) {
			System.out.println(zipFile);
			e.printStackTrace();
		}

	}
}
