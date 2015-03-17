package sqlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A builder for ortholog groups
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class GroupBuilder {
	private static final Logger logger = LogManager.getLogger(GroupBuilder.class);
	private ArrayList<ArrayList<String>> orthologGroups;
	
	/**
	 * Constructor builds ortholog groups
	 * @param sqlFile
	 * @throws FileNotFoundException
	 */
	public GroupBuilder(File sqlFile) throws FileNotFoundException {
		SQLReader sqlParser = new SQLReader(sqlFile);
		
		// Find all roots (the groups which are not referenced)
		List<String> rootList = sqlParser.getAllRoots();
		
		// Initialize the collection
		orthologGroups = new ArrayList<ArrayList<String>>();
		
		// Unfold all found roots
		if (!rootList.isEmpty()) {
			for (String root : rootList) {
				// System.out.println(sqlParser.getProteins(root));
				ArrayList<String> group = sqlParser.getProteins(root);
				orthologGroups.add(group);
			}
		} else {
			logger.error("Could not find root branches.");
		}
	}

	/**
	 * A method returning unfolded groups of orthologs
	 * @return
	 */
	public ArrayList<ArrayList<String>> getOrthologGroups() {
		return orthologGroups;
	}
}
