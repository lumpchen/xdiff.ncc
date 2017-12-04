package me.lumpchen.xdiff.pdf;

import java.awt.geom.GeneralPath;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotation;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationLink;
import org.apache.pdfbox.pdmodel.interactive.annotation.PDAnnotationWidget;

import me.lumpchen.xdiff.document.AnnotContent;

public class AnnotContentHelper {

	public static AnnotContent createAnnotContent(PDAnnotation annotDict) {
		AnnotContent annotContent =  AnnotContent.newInstance(annotDict.getSubtype());
		mapAttrs(annotContent, annotDict);
		
		return annotContent;
	}

	private static void mapAttrs(AnnotContent annotContent, PDAnnotation annotDict) {
		mapCommonAttrs(annotContent, annotDict);
		
		if (annotContent.getSubType() == AnnotContent.LINK) {
			mapAdditionalAttrs((AnnotContent.Link) annotContent, (PDAnnotationLink) annotDict);
		} else if (annotContent.getSubType() == AnnotContent.WIDGET) {
			mapAdditionalAttrs((AnnotContent.Widget) annotContent, (PDAnnotationWidget) annotDict);
		}
	}
	
	private static void mapCommonAttrs(AnnotContent annotContent, PDAnnotation annotDict) {
		if (annotDict.getRectangle() != null) {
			GeneralPath rect = annotDict.getRectangle().toGeneralPath();
			annotContent.addOutlineShape(rect);
		}
		
		COSArray border = annotDict.getBorder();
		if (border != null) {
			annotContent.setBorder(getArrayStr(border));
		}
		
		String name = annotDict.getAnnotationName();
		if (name != null) {
			annotContent.setName(name);
		}

		String contents = annotDict.getContents();
		if (contents != null) {
			annotContent.setContents(contents);
		}
		
		annotContent.setFlags(annotDict.getAnnotationFlags());
	}
	
	private static void mapAdditionalAttrs(AnnotContent.Link annotContent, PDAnnotationLink annotDict) {
		PDAction action = annotDict.getAction();
		if (action != null) {
			String actionType = action.getSubType(); // /S
			annotContent.setAction(actionType, getActionDest(action));
		}
		
	}
	
	private static void mapAdditionalAttrs(AnnotContent.Widget annotContent, PDAnnotationWidget annotDict) {
		COSDictionary parent = null;
		if (annotDict.getCOSObject().getDictionaryObject(COSName.PARENT) != null) {
			parent = (COSDictionary) annotDict.getCOSObject().getDictionaryObject(COSName.PARENT);			
		}
		
		// collect /Widget information from own node or its parent node
		if (annotDict.getCOSObject().getCOSName(COSName.FT) == null) {
			if (parent != null && parent.getCOSName(COSName.FT) != null) {
				annotContent.setFieldType(parent.getCOSName(COSName.FT).getName());	
			}
		} else {
			annotContent.setFieldType(annotDict.getCOSObject().getCOSName(COSName.FT).getName());
		}
		
		annotContent.setFieldName(annotDict.getCOSObject().getString(COSName.T));
		if (annotContent.getFieldName() == null) {
			if (parent != null) {
				annotContent.setFieldName(parent.getString(COSName.T));	
			}
		}
		
		annotContent.setAlternateFieldName(annotDict.getCOSObject().getString(COSName.TU));
		if (annotContent.getAlternateFieldName() == null) {
			if (parent != null) {
				annotContent.setAlternateFieldName(parent.getString(COSName.TU));
			}
		}
	}

	private static String getActionDest(PDAction action) {
		String actionType = action.getSubType(); // /S
		if (COSName.URI.getName().equals(actionType)) {
			return ((PDActionURI) action).getURI();
		}
		return "";
	}
	
	private static String getArrayStr(COSArray arr) {
		StringBuilder buf = new StringBuilder();
		buf.append("[");
		for (COSBase item : arr) {
			if (item instanceof COSInteger) {
				buf.append(((COSInteger) item).intValue());
			} else if (item instanceof COSFloat) {
				buf.append(((COSFloat) item).floatValue());
			} else if (item instanceof COSArray) {
				buf.append(getArrayStr((COSArray) item));
			}
			buf.append(" ");
		}
		buf.append("]");
		return buf.toString();
	}
	
}
