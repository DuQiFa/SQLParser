package sqlparser;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class for reading a directory
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class DirectoryReader {
	private static final Logger logger = LogManager.getLogger(DirectoryReader.class);
	private List<File> fileList;
	
	public DirectoryReader(File dir, final String pattern) {
		fileList = Arrays.asList(dir.listFiles(new FileFilter(pattern)));
	}

	/**
	 * Method returns all files matching pattern in directory
	 * @return
	 */
	List<File> getFileList() {
		return(fileList);
	}
	
	/**
	 * Method returns the first file from all files in directory matching pattern given by constructor and additionally path matching regex
	 * @param regex
	 * @return
	 */
	File getFileMatching(String regex) {
		File ret = null;
		for(File file : fileList) {
			if(file.getName().matches(regex)) {
				ret = file;
			}
		}
		if(ret == null) {
			logger.warn("No match for " + regex);
		}
		return ret;
	}
}
