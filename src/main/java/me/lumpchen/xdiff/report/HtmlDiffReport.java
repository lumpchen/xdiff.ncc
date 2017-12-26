package me.lumpchen.xdiff.report;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONObject;

import me.lumpchen.xdiff.PDocDiffResult;
import me.lumpchen.xdiff.PDocDiffResult.DocumentInfo;
import me.lumpchen.xdiff.PDocDiffResult.DocumentProperties;
import me.lumpchen.xdiff.PDocDiffResult.PageInfo;
import me.lumpchen.xdiff.PageDiffResult;
import me.lumpchen.xdiff.PageDiffResult.ContentAttr;
import me.lumpchen.xdiff.PageDiffResult.DiffContent;
import me.lumpchen.xdiff.afp.AFPCommentComparator.NopCompareEntry;
import me.lumpchen.xdiff.afp.TLECompartor.TLECompareEntry;

public class HtmlDiffReport {

	private static final String ResourcePackage = "me/lumpchen/xdiff/report/html/";

	private static final String diff_report_data_filename = "diff_report_data.js";
	private static final String diff_report_data_ns = "PDF_DIFF";
	private static final String diff_report_data = "diff_report_data";
	
	private static final String diff_page_count = "diff_page_count";
	private static final String diff_page_nums = "diff_page_nums";
	private static final String diff_accepted_page_nums = "diff_accepted_page_nums";
	
	private static final String base_pdf_json_obj = "base_pdf_json_obj";
	private static final String test_pdf_json_obj = "test_pdf_json_obj";
	private static final String diff_content_json_obj = "diff_content_json_obj";
	private static final String page_xor_images = "page_xor_images";
	
	private static final String afp_tle_compare_result = "afp_tle_compare_result";
	private static final String afp_tle_compare_data = "afp_tle_compare_data";
	private static final String afp_nop_compare_result = "afp_nop_compare_result";
	private static final String afp_nop_compare_data = "afp_nop_compare_data";
	
	private static final String Rendering_Resolution = "Rendering_Resolution";
	private static final String File_Format = "FileFormat";
	private static final String ShowDifferentPagesOnly = "ShowDifferentPagesOnly";
	private static final String Base_Stroke_Color = "Base_Stroke_Color";
	private static final String Test_Stroke_Color = "Test_Stroke_Color";
	private static final String Test_Fill_Color = "Test_Fill_Color";
	private static final String Base_Fill_Color = "Base_Fill_Color";

	private File imageDir;
	private File baseDir;
	private String mainName;
	private PDocDiffResult result;
	boolean showDifferentPagesOnly = false;

	public HtmlDiffReport(File baseDir, String name, PDocDiffResult result, boolean showDifferentPagesOnly) throws IOException {
		this.baseDir = baseDir;
		if (!this.baseDir.isDirectory()) {
			throw new IOException("Not a folder: " + this.baseDir.getAbsolutePath());
		}
		String path = baseDir.getAbsolutePath() + "/" + "images";
		this.imageDir = new File(path);
		if (!imageDir.exists()) {
			if (!imageDir.mkdir()) {
				throw new IOException("Cannot create /images: " + imageDir);
			}
		}
		this.mainName = name;
		this.result = result;
		this.showDifferentPagesOnly = showDifferentPagesOnly;
	}

	public void toHtml() throws IOException {
		writeHtml();
		writeCss();
		writeJS();
	}

	private void writeHtml() throws IOException {
		InputStream htmlTemplate = null;
		try {
			htmlTemplate = loadTemplate("html_report_template.html");
			copyFile(htmlTemplate, this.mainName + ".html", this.baseDir);
		} finally {
			if (htmlTemplate != null) {
				htmlTemplate.close();
			}
		}
	}

	private void writeCss() throws IOException {
		this.copyTemplate("report_styles.css", "css");
		this.copyTemplate("bootstrap.css", "css");
		this.copyTemplate("bootstrap-treeview.css", "css");
		this.copyTemplate("jquery.contextMenu.css", "css");
		this.copyTemplate("jquery-ui.css", "css");
		
		this.copyTemplate("context-menu-icons.eot", "css/font");
		this.copyTemplate("context-menu-icons.ttf", "css/font");
		this.copyTemplate("context-menu-icons.woff", "css/font");
		this.copyTemplate("context-menu-icons.woff2", "css/font");
		
		this.copyTemplate("ui-bg_glass_20_555555_1x400.png", "css/images");
		this.copyTemplate("ui-bg_glass_40_0078a3_1x400.png", "css/images");
		this.copyTemplate("ui-bg_glass_40_ffc73d_1x400.png", "css/images");
		this.copyTemplate("ui-bg_gloss-wave_25_333333_500x100.png", "css/images");
		this.copyTemplate("ui-bg_highlight-soft_80_eeeeee_1x100.png", "css/images");
		this.copyTemplate("ui-bg_inset-soft_25_000000_1x100.png", "css/images");
		this.copyTemplate("ui-bg_inset-soft_30_f58400_1x100.png", "css/images");
		this.copyTemplate("ui-icons_222222_256x240.png", "css/images");
		this.copyTemplate("ui-icons_4b8e0b_256x240.png", "css/images");
		this.copyTemplate("ui-icons_a83300_256x240.png", "css/images");
		this.copyTemplate("ui-icons_cccccc_256x240.png", "css/images");
		this.copyTemplate("ui-icons_ffffff_256x240.png", "css/images");
		
		this.copyTemplate("glyphicons-halflings-regular.woff2", "fonts");
		this.copyTemplate("glyphicons-halflings-regular.woff", "fonts");
		this.copyTemplate("glyphicons-halflings-regular.ttf", "fonts");
	}
	
	private void writeJS() throws IOException {
		this.copyTemplate("jquery.js", "js");
		this.copyTemplate("bootstrap-treeview.js", "js");
		this.copyTemplate("diff_report_view.js", "js");
		
		this.copyTemplate("jquery.contextMenu.js", "js");
		this.copyTemplate("jquery.ui.position.js", "js");
		this.copyTemplate("jquery-ui.js", "js");
		
		this.writeDataJS();
	}

	private void writeDataJS() throws IOException {
		String path = this.baseDir.getAbsolutePath() + "/js/" + diff_report_data_filename;
		File dataJS = new File(path);
		if (!dataJS.exists()) {
			if (!dataJS.createNewFile()) {
				throw new IOException("Cannot create report data js file: " + path);
			}
		}
		FileOutputStream fos = null;
		Writer writer = null;
		try {
/*			fos = new FileOutputStream(dataJS);

			StringBuilder data = new StringBuilder();
			
			data.append("var " + diff_report_data_ns + " = " + diff_report_data_ns + " || {};");
			data.append("\n");
			data.append(diff_report_data_ns + "." + diff_report_data + " = ");
			
			data.append(this.toJSonString());
			fos.write(data.toString().getBytes("UTF-8"));*/
			
			writer = new OutputStreamWriter(new FileOutputStream(dataJS), StandardCharsets.UTF_8);
			writer.write("var " + diff_report_data_ns + " = " + diff_report_data_ns + " || {};");
			writer.write("\n");
			writer.write(diff_report_data_ns + "." + diff_report_data + " = ");
			toJSonString(writer);
			writer.flush();
		} finally {
			if (fos != null) {
				fos.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
	}

	private void toJSonString(Writer writer) throws IOException {
		JSONObject json = new JSONObject();
		
		// change by setting
		json.put(Rendering_Resolution, this.result.getResolution());
		json.put(File_Format, this.result.getFormat());
		json.put(ShowDifferentPagesOnly, this.showDifferentPagesOnly);
    	json.put(Base_Stroke_Color, "red");
		json.put(Test_Stroke_Color, "red");
		json.put(Test_Fill_Color, "rgba(138, 43, 226, 0.2)");
		json.put(Base_Fill_Color, "rgba(138, 43, 226, 0.2)");
	
		json.put(diff_page_count, this.result.countOfDiffPages());
		json.put(diff_page_nums, this.result.getDiffPageNums());
		json.put(diff_accepted_page_nums, this.result.getAcceptDiffPageNums());
		
		json.put(base_pdf_json_obj, this.toJSon(this.result.getBaseDocumentInfo()));
		json.put(test_pdf_json_obj, this.toJSon(result.getTestDocumentInfo()));
		json.put(diff_content_json_obj, this.toJSon(result));
		
		JSONObject xorImageObj = new JSONObject();
		Map<Integer, String> xorImageMap = result.getPageXORImageMap();
		String tagSuffix = result.getBaseDocumentInfo().getImageSuffix();
		if (!xorImageMap.isEmpty()) {
			Iterator<Entry<Integer, String>> it = xorImageMap.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, String> entry = it.next();
				String href = writeXORImage(entry.getKey(), entry.getValue(), tagSuffix);
				xorImageObj.put(entry.getKey().toString(), href);
			}
		}
		json.put(page_xor_images, xorImageObj);
		
		if (this.result.getFormat().equals(PDocDiffResult.FORMAT_AFP)) {
			json.put(afp_tle_compare_result, this.result.getTLECompareResult());
			JSONArray arr = this.toJSon(this.result.getTLECompareData());
			json.put(afp_tle_compare_data, arr);
				
			json.put(afp_nop_compare_result, this.result.getNopCompareResult());
			arr = this.toJSon2(this.result.getNopCompareData());
			json.put(afp_nop_compare_data, arr);
			
		}

		json.write(writer);
//		return json.toString(4);
	}
	
	private JSONArray toJSon(List<TLECompareEntry> entries) {
		JSONArray result = new JSONArray();
		
		if (entries == null) {
			return result;
		}
		for (TLECompareEntry entry : entries) {
			JSONArray jarr = new JSONArray();
			
			jarr.put(entry.equals);
			jarr.put(this.toJSon(entry.baseItem));
			jarr.put(this.toJSon(entry.testItem));
			result.put(jarr);
		}
		return result;
	}
	
	private JSONArray toJSon2(List<NopCompareEntry> entries) {
		JSONArray result = new JSONArray();
		
		if (entries == null) {
			return result;
		}
		for (NopCompareEntry entry : entries) {
			JSONArray jarr = new JSONArray();
			
			jarr.put(entry.equals);
			jarr.put(entry.baseItem);
			jarr.put(entry.testItem);
			result.put(jarr);
		}
		return result;
	}
	
	private JSONArray toJSon(String[] arr) {
		JSONArray jarr = new JSONArray();
		for (String s : arr) {
			jarr.put(s);
		}
		return jarr;
	}

	private static InputStream loadTemplate(String name) {
		InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(ResourcePackage + name);
		return is;
	}

	private JSONObject toJSon(DocumentInfo docInfo) throws IOException {
		JSONObject docJson = new JSONObject();
		docJson.put("pageCount", docInfo.getPageCount());
		docJson.put("fileName", docInfo.getFileName());
		
		if (docInfo.getProperties() != null) {
			DocumentProperties props = docInfo.getProperties();
			docJson.put("title", props.title != null ? props.title : "");
			docJson.put("author", props.author != null ? props.author : "");
			docJson.put("subject", props.subject != null ? props.subject : "");
			docJson.put("keywords", props.keywords != null ? props.keywords : "");
			docJson.put("created", props.created != null ? props.created : "");
			docJson.put("modified", props.modified != null ? props.modified : "");
			docJson.put("producer", props.producer != null ? props.producer : "");
			docJson.put("version", props.version != null ? props.version : "");
			docJson.put("location", props.location != null ? props.location : "");
			docJson.put("fileSize", props.fileSize != null ? props.fileSize : "");
		}
		
		String tagPrefix = docInfo.getCategory();
		String tagSuffix = docInfo.getImageSuffix();

		JSONArray pageArray = new JSONArray();
		for (int i = 0; i < docInfo.getPageCount(); i++) {
			PageInfo pageInfo = docInfo.getPageInfo(i);
			if (pageInfo != null) {
				JSONObject pageJson = this.toJSon(pageInfo, tagPrefix, tagSuffix);
				pageArray.put(pageJson);
			} else {
				pageArray.put(new JSONObject());
			}
		}
		docJson.put("pages", pageArray);
		return docJson;
	}

	private JSONArray toJSon(PDocDiffResult docResult) throws IOException {
		JSONArray result = new JSONArray();
		Integer[] nums = docResult.getDiffPageNums();

		for (int i : nums) {
			JSONObject pageEntry = new JSONObject();
			PageDiffResult pageResult = docResult.getPageDiffResult(i);
			pageEntry.put("PageNo", i);
			JSONObject obj = this.toJSon(pageResult);
			pageEntry.put("Result", obj);
			result.put(pageEntry);
		}
		
		for (int i : docResult.getAcceptDiffPageNums()) {
			JSONObject pageEntry = new JSONObject();
			PageDiffResult pageResult = docResult.getPageDiffResult(i);
			pageEntry.put("PageNo", i);
			JSONObject obj = this.toJSon(pageResult);
			pageEntry.put("Result", obj);
			result.put(pageEntry);
		}
		return result;
	}

	private JSONObject toJSon(PageDiffResult pageResult) throws IOException {
		JSONObject json = new JSONObject();
		List<DiffContent> contentList = pageResult.getContentList();

		JSONArray pageArr = new JSONArray();
		JSONArray textArr = new JSONArray();
		JSONArray imageArr = new JSONArray();
		JSONArray pathArr = new JSONArray();
		JSONArray annotArr = new JSONArray();
		JSONArray acceptDifftArr = new JSONArray();
		
		for (DiffContent content : contentList) {
			JSONObject obj = this.toJSon(content);
			
			if (content.getCategory() == DiffContent.Category.Page) {
				pageArr.put(obj);
			}
			
			if (content.getCategory() == DiffContent.Category.Text) {
				textArr.put(obj);
			}

			if (content.getCategory() == DiffContent.Category.Image) {
				imageArr.put(obj);
			}

			if (content.getCategory() == DiffContent.Category.Path) {
				pathArr.put(obj);
			}

			if (content.getCategory() == DiffContent.Category.Annot) {
				annotArr.put(obj);
			}
			
			if (content.getCategory() == DiffContent.Category.AcceptDifference) {
				acceptDifftArr.put(obj);
			}
		}
		json.put(DiffContent.Category.Page.text, pageArr);
		json.put(DiffContent.Category.Text.text, textArr);
		json.put(DiffContent.Category.Image.text, imageArr);
		json.put(DiffContent.Category.Path.text, pathArr);
		json.put(DiffContent.Category.Annot.text, annotArr);
		json.put(DiffContent.Category.AcceptDifference.text, acceptDifftArr);
		return json;
	}

	private JSONObject toJSon(DiffContent diffContent) throws IOException {
		JSONObject json = new JSONObject();
		List<ContentAttr> attrs = diffContent.getAttrList();
		JSONArray attrArr = new JSONArray();
		for (ContentAttr attr : attrs) {
			JSONObject attrMap = new JSONObject();
			attrMap.put("Key", attr.key);
			attrMap.put("Equals", attr.equals);
			JSONArray arr = new JSONArray();
			arr.put(attr.baseVal == null ? "" : attr.baseVal);
			arr.put(attr.testVal == null ? "" : attr.testVal);
			attrMap.put("Value", arr);
			attrArr.put(attrMap);
		}
		json.put("Attributes", attrArr);

		JSONArray arr = new JSONArray();
		JSONArray sRect = new JSONArray();
		if (diffContent.getBaseBBox() != null) {
			Rectangle2D rect = diffContent.getBaseBBox();
			this.rectToJSon(sRect, rect);
		}
		arr.put(sRect);

		sRect = new JSONArray();
		if (diffContent.getTestBBox() != null) {
			Rectangle2D rect = diffContent.getTestBBox();
			this.rectToJSon(sRect, rect);
		}
		arr.put(sRect);
		json.put("Outline", arr);
		
		arr = new JSONArray();
		sRect = new JSONArray();
		if (diffContent.getBaseAcceptDiffRegion() != null) {
			Rectangle2D rect = diffContent.getBaseAcceptDiffRegion();
			this.rectToJSon(sRect, rect);
		}
		arr.put(sRect);

		sRect = new JSONArray();
		if (diffContent.getTestAcceptDiffRegion() != null) {
			Rectangle2D rect = diffContent.getTestAcceptDiffRegion();
			this.rectToJSon(sRect, rect);
		}
		arr.put(sRect);
		json.put("AcceptDiffRegion", arr);
		
		JSONArray subOutline = new JSONArray();
		JSONArray baseSubOutline = new JSONArray();
		if (diffContent.getBaseSubBBox() != null) {
			for (int i = 0; i < diffContent.getBaseSubBBox().size(); i++) {
				sRect = new JSONArray();			
				Rectangle2D rect = diffContent.getBaseSubBBox().get(i);
				this.rectToJSon(sRect, rect);
				
				baseSubOutline.put(sRect);
			}
		}
		subOutline.put(baseSubOutline);
		JSONArray testSubOutline = new JSONArray();
		if (diffContent.getTestSubBBox() != null) {
			for (int i = 0; i < diffContent.getTestSubBBox().size(); i++) {
				sRect = new JSONArray();
				Rectangle2D rect = diffContent.getTestSubBBox().get(i);
				this.rectToJSon(sRect, rect);
				
				testSubOutline.put(sRect);
			}
		}
		subOutline.put(testSubOutline);
		json.put("SubOutline", subOutline);
		
		return json;
	}
	
	private void rectToJSon(JSONArray sRect, Rectangle2D rect) {
		sRect.put(roundM(rect.getX()));
		sRect.put(roundM(rect.getY()));
		sRect.put(roundM(rect.getWidth()));
		sRect.put(roundM(rect.getHeight()));
	}
	
	private static double roundM(double d) {
		return Math.round(d * 1000) / 1000d;
	}

	private JSONObject toJSon(PageInfo pageInfo, String tagPrefix, String tagSuffix) throws IOException {
		JSONObject map = new JSONObject();
		map.put("num", pageInfo.getPageNo());
		map.put("width", pageInfo.getWidth());
		map.put("height", pageInfo.getHeight());
		String imageTag = this.writeImages(pageInfo, tagPrefix, tagSuffix);
		map.put("imageTag", imageTag);

		return map;
	}
	
	private void copyTemplate(String resName, String folderName) throws IOException {
		InputStream srcFile = null;
		try {
			String subPath = folderName == null || folderName.isEmpty() ? resName : folderName + "/" + resName;
			srcFile = loadTemplate(subPath);

			File dest = this.baseDir;
			if (folderName != null && !folderName.isEmpty()) {
				dest = new File(this.baseDir, folderName);
			}
			copyFile(srcFile, resName, dest);
		} finally {
			if (srcFile != null) {
				srcFile.close();
			}
		}
	}

	private static void copyFile(InputStream src, String fileName, File dstDir) throws IOException {
		if (!dstDir.exists()) {
			if (!dstDir.mkdir()) {
				throw new IOException("Cannot create directory: " + dstDir);
			}
		}
		
		String dst = dstDir.getAbsolutePath() + "/" + fileName;
		File dstFile = new File(dst);
		if (!dstFile.exists()) {
			if (!dstFile.createNewFile()) {
				throw new IOException("Cannot create file: " + dst);
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(dstFile);
			byte[] buf = new byte[1024];
			int read;
			while ((read = src.read(buf)) != -1) {
				fos.write(buf, 0, read);
			}
		} finally {
			if (fos != null) {
				fos.close();
			}
		}
	}
	
	private String writeImages(PageInfo pageInfo, String tagPrefix, String tagSuffix) throws IOException {
		String path = pageInfo.getPreviewImage();
		File image = new File(path);
		if (image.exists()) {
			String imageTag = tagPrefix + "-" + pageInfo.getPageNo() + "." + tagSuffix;
			File imageFile = new File(this.imageDir.getAbsolutePath() + "/" + imageTag);
			if (!imageFile.exists()) {
				if (!imageFile.createNewFile()) {
					throw new IOException("Cannot create preview image file: " + imageFile.getAbsolutePath());
				}
			}
			cutTo(image, imageFile);
			return imageTag;
		}
		return "";
	}
	
	private String writeXORImage(int pageNo, String path, String tagSuffix) throws IOException {
		File image = new File(path);
		if (image.exists()) {
			String imageTag = pageNo + "-" + "xor" + "." + tagSuffix;
			File imageFile = new File(this.imageDir.getAbsolutePath() + "/" + imageTag);
			if (!imageFile.exists()) {
				if (!imageFile.createNewFile()) {
					throw new IOException("Cannot create preview xor image file: " + imageFile.getAbsolutePath());
				}
			}
			cutTo(image, imageFile);
			return imageTag;
		}
		return "";
	}
	
	private static void cutTo(File src, File dst) throws IOException {
		FileInputStream is = new FileInputStream(src);
		FileOutputStream os = new FileOutputStream(dst);
		
		try {
			byte[] buf = new byte[1024];
			int read;
			while ((read = is.read(buf)) != -1) {
				os.write(buf, 0, read);
			}
		} finally {
			if (is != null) {
				is.close();
			}
			if (os != null) {
				os.close();
			}
		}
		
		if (!src.delete()) {
			src.deleteOnExit();
		}
	}
}
