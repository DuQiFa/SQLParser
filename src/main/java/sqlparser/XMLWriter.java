package sqlparser;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.stream.XMLStreamException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import sbc.orthoxml.io.OrthoXMLWriter;
import sbc.orthoxml.Database;
import sbc.orthoxml.Gene;
import sbc.orthoxml.Group;
import sbc.orthoxml.Species;

@SuppressWarnings("restriction")
public class XMLWriter {
		private static final Logger logger = LogManager.getLogger(XMLWriter.class);
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
		public XMLWriter(ArrayList<ArrayList<String>> orthoGroups,
				HashMap<String, String> map, HashSet<String> pset) {
			this.orthoGroups = orthoGroups;
			this.map = map;
			this.fset = pset;
		}
	
		/**
		 * Method printing out pairs to file by PrintWriter
		 * @param orthoXMLWriter
		 * @throws FileNotFoundException
		 * @throws XMLStreamException 
		 */
		public void writeXML(OrthoXMLWriter orthoXMLWriter) throws FileNotFoundException, XMLStreamException {
			HashMap<String,Database> dbmap = new HashMap<String, Database>();
			String timestamp = new java.text.SimpleDateFormat("MM-dd-yyyy").format(new Date());
			
			Iterator<ArrayList<String>> groupIterator = orthoGroups.iterator();
			for(int g = 0 ; groupIterator.hasNext(); g++ ) {
				ArrayList<String> group = groupIterator.next();
				
				// Create ortholog group
			    Group orthoxmlgroup = new Group();
			    orthoxmlgroup.setId(Integer.toString(g));
			    
			    // Iterate all pairs in the group
					for (int i = 0; i < group.size(); i++) {
						for (int j = i + 1; j < group.size(); j++) {
							// Get protein identifiers
							String istr = group.get(i);
							String jstr = group.get(j);
							counter++;
							if (map.containsKey(istr) && map.containsKey(jstr)) {
								String iorganism = map.get(istr);
								String jorganism = map.get(jstr);
								if (!iorganism.contains(jorganism)) { // If two organisms in a pair are not the same
									if (fset.contains(iorganism)      // If two organisms belong to a pair of species considered at the time
											&& fset.contains(jorganism)) {
										
										// Create species
										Species x = new Species(i, iorganism);
										Species y = new Species(j, jorganism);
										
										// Create database
										Database db = null;
										if(dbmap.containsKey(iorganism+jorganism)) {
											db = dbmap.get(iorganism+jorganism);
										} else {
											db = new Database("Proteomes", timestamp);
											dbmap.put(iorganism+jorganism, db);
										}
										
										// Create gene objects
										Gene genex = new Gene(x, db);
										Gene geney = new Gene(y, db);
										
										// Add genes to ortholog group
										if(!jorganism.isEmpty() && !jorganism.isEmpty()) {
											orthoxmlgroup.setGenes(Arrays.asList(genex, geney));
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
					if(!orthoxmlgroup.getGenes().isEmpty())
						orthoXMLWriter.write(orthoxmlgroup);
			}
		}

}
