package sqlparser;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Class that implements printing out lists of orthologs
 * @author Mateusz Kaduk <mateusz.kaduk@gmail.com>
 *
 */
public class ListWriter {
	private ArrayList<ArrayList<String>> orthoGroups;
	private HashMap<String, String> map;
	private HashSet<String> pset;

	/**
	 * Constructor
	 * @param orthoGroups
	 */
	public ListWriter(ArrayList<ArrayList<String>> orthoGroups,
			HashMap<String, String> map, HashSet<String> pset) {
		this.orthoGroups = orthoGroups;
		this.map = map;
		this.pset = pset;
	}

	/**
	 * Method that prints groups into the file associated with PrintWrinter
	 * @param lout
	 */
	public void writeLists(PrintWriter lout) {
		for (ArrayList<String> group : orthoGroups) {
			PrintableArrayList<String> cgroup = new PrintableArrayList<String>();
			for(String protein : group) {
				String organism = map.get(protein);
				if(pset.contains(organism)) {
					cgroup.add(protein);
				}
			}
			lout.println(cgroup);
		}
	}

	
}
