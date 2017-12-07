package me.lumpchen.xdiff.pdf;

import java.awt.geom.GeneralPath;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.pdfbox.cos.COSArray;
import org.apache.pdfbox.cos.COSBase;
import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.cos.COSFloat;
import org.apache.pdfbox.cos.COSInteger;
import org.apache.pdfbox.cos.COSName;
import org.apache.pdfbox.cos.COSString;
import org.apache.pdfbox.pdmodel.common.COSArrayList;
import org.apache.pdfbox.pdmodel.interactive.action.PDAction;
import org.apache.pdfbox.pdmodel.interactive.action.PDActionURI;
import org.apache.pdfbox.pdmodel.interactive.action.PDAnnotationAdditionalActions;
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
		
		COSName as = annotDict.getAppearanceState();
		if (as != null) {
			annotContent.setAppearenceState(as.getName());
		}
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
		
		annotContent.setFieldValue(annotDict.getCOSObject().getString(COSName.V));
		if (annotContent.getFieldValue() == null) {
			if (parent != null) {
				annotContent.setFieldValue(parent.getString(COSName.V));
			}
		}
		
		annotContent.setFieldDefaultValue(annotDict.getCOSObject().getString(COSName.DV));
		if (annotContent.getFieldDefaultValue() == null) {
			if (parent != null) {
				annotContent.setFieldDefaultValue(parent.getString(COSName.DV));
			}
		}
		
		annotContent.setAlternateFieldName(annotDict.getCOSObject().getString(COSName.TU));
		if (annotContent.getAlternateFieldName() == null) {
			if (parent != null) {
				annotContent.setAlternateFieldName(parent.getString(COSName.TU));
			}
		}
		
		PDAnnotationAdditionalActions actions = annotDict.getActions();
		if (actions != null) {
			PDAction action = actions.getD();
			if (action != null) {
				annotContent.setAction("D", action.getSubType(), getActionDest(action), "mouse button down");
			}
			
			action = actions.getU();
			if (action != null) {
				annotContent.setAction("U", action.getSubType(), getActionDest(action), "mouse button up");
			}
			
			action = actions.getE();
			if (action != null) {
				annotContent.setAction("E", action.getSubType(), getActionDest(action), "cursor enter");
			}
			
			action = actions.getX();
			if (action != null) {
				annotContent.setAction("X", action.getSubType(), getActionDest(action), "cursor exit");
			}
			
			action = actions.getFo();
			if (action != null) {
				annotContent.setAction("Fo", action.getSubType(), getActionDest(action), "focus input");
			}
			
			action = actions.getBl();
			if (action != null) {
				annotContent.setAction("Bl", action.getSubType(), getActionDest(action), "blurred");
			}
			
			action = actions.getPO();
			if (action != null) {
				annotContent.setAction("PO", action.getSubType(), getActionDest(action), "page opened");
			}
			
			action = actions.getPC();
			if (action != null) {
				annotContent.setAction("PC", action.getSubType(), getActionDest(action), "page closed");
			}
			
			action = actions.getPV();
			if (action != null) {
				annotContent.setAction("PV", action.getSubType(), getActionDest(action), "page visible");
			}
			
			action = actions.getPI();
			if (action != null) {
				annotContent.setAction("PI", action.getSubType(), getActionDest(action), "page invisible");
			}
		}
		
		List<String> options = getOptions(annotDict);
		annotContent.setOptions(options.toArray(new String[options.size()]));
	}
	
    public static List<String> getOptions(PDAnnotationWidget annotDict) {
        COSBase values = annotDict.getCOSObject().getDictionaryObject(COSName.OPT);
        if (values == null) {
        	return new ArrayList<String>(0);
        }
        return getPairableItems(values, 0);
    }
    
    static List<String> getPairableItems(COSBase items, int pairIdx)
    {
        if (pairIdx < 0 || pairIdx > 1) {
            throw new IllegalArgumentException("Only 0 and 1 are allowed as an index into two-element arrays");
        }
        
        if (items instanceof COSString) {
            List<String> array = new ArrayList<String>();
            array.add(((COSString) items).getString());
            return array;
        } else if (items instanceof COSArray) {
            // test if there is a single text or a two-element array 
            COSBase entry = ((COSArray) items).get(0);
            if (entry instanceof COSString) {
                return COSArrayList.convertCOSStringCOSArrayToList((COSArray)items);
            } else {
                return getItemsFromPair(items, pairIdx);
            }            
        }
        return Collections.emptyList();
    }
    
    private static List<String> getItemsFromPair(COSBase items, int pairIdx) {
        List<String> exportValues = new ArrayList<String>();
        int numItems = ((COSArray) items).size();
        for (int i = 0; i < numItems; i++) {
            COSArray pair = (COSArray) ((COSArray) items).get(i);
            COSString displayValue = (COSString) pair.get(pairIdx);
            exportValues.add(displayValue.getString());
        }
        return exportValues;        
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
