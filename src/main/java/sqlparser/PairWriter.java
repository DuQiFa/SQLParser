package sqlparser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Class that implements printing out pairs of orthologs
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class PairWriter {
	private static final Logger logger = LogManager.getLogger(PairWriter.class);
	private ArrayList<ArrayList<String>> orthoGroups;
	private HashMap<String, String> map;
	private HashSet<String> fset;
	private static Integer counter = 0;
	
	/**
	 * Constructor
	 * @param orthoGroups
	 * @param map
	 * @param pset
	 */
	public PairWriter(ArrayList<ArrayList<String>> orthoGroups,
			HashMap<String, String> map, HashSet<String> pset) {
		this.orthoGroups = orthoGroups;
		this.map = map;
		this.fset = pset;
	}

	/**
	 * Method printing out pairs to file by PrintWriter
	 * @param fout
	 * @throws FileNotFoundException
	 */
	public void writePairs(PrintWriter fout) throws FileNotFoundException {
		for (ArrayList<String> group : orthoGroups) {
			for (int i = 0; i < group.size(); i++) {
				for (int j = i + 1; j < group.size(); j++) {
					String istr = group.get(i);
					String jstr = group.get(j);
					counter++;
					if (map.containsKey(istr) && map.containsKey(jstr)) {
						String iorganism = map.get(istr);
						String jorganism = map.get(jstr);
						if (!iorganism.contains(jorganism)) { // If two organisms in a pair are not the same
							if (fset.contains(iorganism)      // If two organisms belong to a pair of species considered at the time
									&& fset.contains(jorganism)) {
								String tmp = (group.get(i) + "\t" + group
										.get(j));
								if(!tmp.isEmpty())
									fout.println(tmp);
							}
						}
					} else {
						logger.warn("Proteomes map does not contain a pair of proteins: "
								+ istr + "," + jstr);
						System.exit(-1);
					}
				}
			}
		}
	}
}
