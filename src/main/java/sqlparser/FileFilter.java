package sqlparser;

import java.io.File;
import java.io.FilenameFilter;

/**
 * File filter with specific extension
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class FileFilter implements FilenameFilter {
	private String pattern;
	
	/**
	 * Constructor for filter object to filter files with specific extension
	 * @param pattern
	 */
	public FileFilter(String pattern) {
		this.pattern = pattern;
	}
	
	/**
	 * Implemented method for accepting files meeting filter criteria
	 */
	public boolean accept(File dir, String name) {
		 return name.toLowerCase().matches(".*"+pattern.toLowerCase()+".*");
	}
}
