package me.lumpchen.xdiff;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Util {
	
	private static Logger logger = Logger.getLogger(TestCaseText.class.getName());

	public static boolean compareReportData(File baselineReportFile, File testReportFile) throws IOException {
		JSONObject baselineRoot = readJson(baselineReportFile);
		JSONObject testRoot = readJson(testReportFile);
		
		return compareJSon(baselineRoot, testRoot);
	}
	
	static JSONObject readJson(File reportFile) throws IOException {
		String s = readFile(reportFile);
		String json = stripJson(s);
		
		JSONTokener tokener = new JSONTokener(json);
		JSONObject root = new JSONObject(tokener);
		return root;
	}
	
	static String stripJson(String reportData) {
		String var = "PDF_DIFF.diff_report_data =";
		int begin = reportData.indexOf(var);
		if (begin < 0) {
			logger.severe("Can't find begin index of \"PDF_DIFF.diff_report_data =\"");
		}
		String json = reportData.substring(begin + var.length());
		return json;
	}
	
	static String readFile(File file) throws IOException {
		InputStream in = new FileInputStream(file);
		try {
			byte[] b  = new byte[(int) file.length()];
			int len = b.length;
			int total = 0;

			while (total < len) {
			  int result = in.read(b, total, len - total);
			  if (result == -1) {
			    break;
			  }
			  total += result;
			}

			return new String(b, "utf-8");
		} finally {
			in.close();
		}
	}
	
	static boolean compareJSon(JSONObject root_1, JSONObject root_2) {
		Iterator<String> keys = root_1.keys();
		
		JSONArray pageNums_1 = null;
		JSONArray pageNums_2 = null;
		JSONArray diffContent_1 = null;
		JSONArray diffContent_2= null;
		
		while (keys.hasNext()) {
			String key = keys.next();
			if (!root_2.has(key)) {
				return false;
			}
			
			Object val_1 = root_1.get(key);
			Object val_2 = root_2.get(key);
			
			if (key.equals("diff_page_nums")) {
				pageNums_1 = (JSONArray) val_1;
				pageNums_2 = (JSONArray) val_2;
			} else if (key.equals("diff_content_json_obj")) {
				diffContent_1 = (JSONArray) val_1;
				diffContent_2 = (JSONArray) val_2;
			} else if (key.equals("base_pdf_json_obj") || key.equals("test_pdf_json_obj")) {
				continue;
			} else {
				if (!compareJSon(val_1, val_2)) {
					return false;
				}
			}
		}
		
		if (pageNums_1 != null && pageNums_2 != null
				&& diffContent_1 != null && diffContent_2 != null) {
			if (!compareJSon(pageNums_1, pageNums_2, diffContent_1, diffContent_2)) {
				return false;
			}
		}
		
		return true;
	}
	
	static boolean compareJSon(JSONArray pageNums_1, JSONArray pageNums_2, JSONArray diffContent_1, JSONArray diffContent_2) {
		if (pageNums_1.length() != pageNums_2.length()) {
			return false;
		}
		if (diffContent_1.length() != diffContent_2.length()) {
			return false;
		}
		
		List<Integer> pageNumsList_1 = new ArrayList<Integer>();
		List<Integer> pageNumsList_2 = new ArrayList<Integer>();
		for (int i = 0; i < pageNums_1.length(); i++) {
			pageNumsList_1.add(pageNums_1.getInt(i));
			pageNumsList_2.add(pageNums_2.getInt(i));
		}
		
		for (Integer page : pageNumsList_1) {
			int index_1 = pageNumsList_1.indexOf(page);
			int index_2 = pageNumsList_2.indexOf(page);
			
			if (!compareJSon(diffContent_1.get(index_1), diffContent_2.get(index_2))) {
				return false;
			}
		}
		return true;
	}
	
	static boolean compareJSon(Object obj_1, Object obj_2) {
		if (obj_1.getClass() != obj_2.getClass()) {
			logger.warning("Diff value found: " + obj_1.toString() + " | " + obj_2.toString());
			return false;
		}
		
		if (obj_1 instanceof String
				|| obj_1 instanceof Boolean
				|| obj_1 instanceof Float
				|| obj_1 instanceof Double
				|| obj_1 instanceof Long
				|| obj_1 instanceof Integer) {
			if (!obj_1.equals(obj_2)) {
				logger.severe("Diff value found: " + obj_1.toString() + " | " + obj_2.toString());
				return false;
			}
		} else if (obj_1 instanceof JSONArray) {
			if (!compareJSon((JSONArray) obj_1, (JSONArray) obj_2)) {
				return false;
			}
		} else if (obj_1 instanceof JSONObject) {
			if (!compareJSon((JSONObject) obj_1, (JSONObject) obj_2)) {
				return false;
			}
		}
		
		return true;
	}
	
	static boolean compareJSon(JSONArray arr_1, JSONArray arr_2) {
//		if (arr_1.length() != arr_2.length()) {
//			return false;
//		}
		
		Iterator<Object> it = arr_1.iterator();
		while (it.hasNext()) {
			Object obj_1 = it.next();
			for (int j = 0; j < arr_2.length(); j++) {
				if (obj_1.toString().equals(arr_2.get(j).toString())) {
					arr_2.remove(j);
					it.remove();
					break;
				}
			}
		}
		
		if (arr_1.length() == arr_2.length()) {
			for (int i = 0; i < arr_1.length(); i++) {
				Object obj_1 = arr_1.get(i);
				Object obj_2 = arr_2.get(i);
				
				if (!compareJSon(obj_1, obj_2)) {
					return false;
				}
			}
		} else {
			int maxSize = arr_1.length() > arr_2.length() ? arr_1.length() : arr_2.length();
			for (int i = 0; i < maxSize; i++) {
				Object obj_1 = i < arr_1.length() ? arr_1.get(i) : null;
				Object obj_2 = i < arr_2.length() ? arr_2.get(i) : null;
				
				if (obj_1 == null) {
					logger.severe("Diff value found: " + "NULL" + " | " + obj_2.toString());
					return false;
				}
				
				if (obj_2 == null) {
					logger.severe("Diff value found: " + obj_1.toString() + " | " +"NULL");
					return false;
				}
				
				if (!compareJSon(obj_1, obj_2)) {
					return false;
				}
			}
		}
		
		return true;
	}
	
}
