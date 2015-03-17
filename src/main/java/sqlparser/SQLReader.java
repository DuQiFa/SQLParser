package sqlparser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SQLReader {
	private static final Logger logger = LogManager.getLogger(SQLReader.class);
	
	private HashMap<String, List<String>> loadTable;
	private HashMap<String, List<String>> groupRelations;
	private HashMap<String, List<String>> groupProteins;
	
	/**
	 * Constructor loads the sqltable file into memory.
	 * @param sqlFile
	 * @throws FileNotFoundException
	 */
	public SQLReader(File sqlFile) throws FileNotFoundException {
		loadTable = new HashMap<String, List<String>>();
		Scanner sc = new Scanner(new FileReader(sqlFile));
		while (sc.hasNextLine()) {
			String[] col = sc.nextLine().split("\t");
			if (!loadTable.containsKey(col[0])) {
				ArrayList<String> list = new ArrayList<String>();
				list.add(col[4]);
				loadTable.put(col[0], list);
			} else {
				List<String> list = loadTable.get(col[0]);
				list.add(col[4]);
				loadTable.put(col[0], list);
			}
		}
		sc.close();
		groupRelations = new HashMap<String, List<String>>();
		groupProteins = new HashMap<String, List<String>>();
		logger.debug("Loaded " + sqlFile.getAbsolutePath() + " into memory.");
		createMaps();
	}
	
	/**
	 * Update relations table
	 * 
	 * @param key
	 * @param value
	 */
	private void updateRelations(String key, String value) {
		if (!groupRelations.containsKey(key)) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(value);
			groupRelations.put(key, list);
		} else {
			List<String> list = groupRelations.get(key);
			list.add(value);
			groupRelations.put(key, list);
		}
	}

	/**
	 * Update protein table
	 * 
	 * @param key
	 * @param value
	 */
	private void updateProteins(String key, String value) {
		if (!groupProteins.containsKey(key)) {
			ArrayList<String> list = new ArrayList<String>();
			list.add(value);
			groupProteins.put(key, list);
		} else {
			List<String> list = groupProteins.get(key);
			list.add(value);
			groupProteins.put(key, list);
		}
	}

	/**
	 * Initially load all maps
	 */
	private void createMaps() {
		for (Entry<String, List<String>> entry : loadTable.entrySet()) {
			List<String> values = entry.getValue();
			for (String value : values) {
				if (loadTable.containsKey(value)) {
					// Found value which is relation
					updateRelations(entry.getKey(), value);
				} else {
					// Found value which is a protein name
					updateProteins(entry.getKey(), value);
				}
			}
		}
		logger.debug("Created protein and relation tables.");
	}

	/**
	 * Return all proteins within a tree for given root branch
	 * 
	 * @param branch
	 * @return
	 */
	public ArrayList<String> getProteins(String branch) {
		// log.debug("Unfolding root branch " + branch);
		ArrayList<String> proteinList = new ArrayList<String>();
		if (groupProteins.containsKey(branch)) {
			proteinList.addAll(groupProteins.get(branch));
		}
		// Recurrence
		if (groupRelations.containsKey(branch)) {
			List<String> group = groupRelations.get(branch);
			for (String value : group) {
				List<String> sublist = getProteins(value);
				proteinList.addAll(sublist);
			}
		}
		return (proteinList);
	}

	/**
	 * Find all roots in loaded table
	 * @return
	 */
	public List<String> getAllRoots() {
		HashMap<String, Integer> rootList = new HashMap<String, Integer>();
		// Create HashMap with all references, root must not be referenced
		HashMap<String, Integer> reference = new HashMap<String, Integer>();
		for (List<String> values : groupRelations.values()) {
			for (String value : values) {
				reference.put(value, 1);
			}
		}
		for (String key : groupRelations.keySet()) {
			if (!reference.containsKey(key)) {
				rootList.put(key, 1);
			}
		}
		for (String key : groupProteins.keySet()) {
			if (!reference.containsKey(key)) {
				rootList.put(key, 1);
			}
		}
		logger.debug("Found " + rootList.size() + " roots.");
		ArrayList<String> roots = new ArrayList<String>();
		for (String root : rootList.keySet()) {
			roots.add(root);
		}
		return roots;
	}
}
