package me.lumpchen.xafp.render;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import me.lumpchen.xafp.AFPException;
import me.lumpchen.xafp.AFPObject;
import me.lumpchen.xafp.CodePage;
import me.lumpchen.xafp.Font;
import me.lumpchen.xafp.ImageObject;
import me.lumpchen.xafp.ObjectContainer;
import me.lumpchen.xafp.ObjectContainer.ObjectTypeIdentifier;
import me.lumpchen.xafp.tool.AFPTool;
import me.lumpchen.xafp.Overlay;
import me.lumpchen.xafp.PageSegment;
import me.lumpchen.xafp.Resource;
import me.lumpchen.xafp.ResourceGroup;

public class ResourceManager {

	private static Logger logger = Logger.getLogger(ResourceManager.class.getName());
	
	private FontManager fontManager;
	private Map<String, ObjectContainer> objMap;
	private Map<String, ImageObject> iocaObjMap;
	private Map<String, PageSegment> psgMap;
	private Map<String, Overlay> overlayMap;

	public ResourceManager(ResourceGroup resourceGroup) {
		this.fontManager = new FontManager();
		this.objMap = new HashMap<String, ObjectContainer>();
		this.iocaObjMap = new HashMap<String, ImageObject>();
		
		this.collect(resourceGroup);
	}
	
	public FontManager getFontManager() {
		return this.fontManager;
	}
	
	public ObjectTypeIdentifier getObjectTypeIdentifier(String resName) {
		ObjectContainer obj = this.objMap.get(resName);
		if (obj == null) {
			return null;
		}
		return obj.getObjectTypeIdentifier();
	}
	
	public byte[] getObjectData(String resName) {
		ObjectContainer obj = this.objMap.get(resName);
		if (obj == null) {
			return null;
		}
		return obj.getObjectData();
	}
	
	public ImageObject getIOCAObject(String resName) {
		if (this.iocaObjMap != null) {
			return this.iocaObjMap.get(resName);
		}
		return null;
	}
	
	public Overlay getOverlay(String resName) {
		if (this.overlayMap != null) {
			return this.overlayMap.get(resName);
		}
		return null;
	}
	
	public PageSegment getPageSegment(String resName) {
		if (this.psgMap != null) {
			return this.psgMap.get(resName);
		}
		return null;
	}
	
	private void collect(ResourceGroup resourceGroup) {
		for (Resource res : resourceGroup.getAllResource()) {
			Resource.Type type = res.getType();
			if (type != null) {
				if (Resource.Type.CODE_PAGE == type) {
					AFPObject[] children = res.getChildren();
					for (AFPObject child : children) {
						if (child instanceof CodePage) {
							this.fontManager.addCodePage(res.getNameStr(), (CodePage) child);
						}
					}
				} else if (Resource.Type.CHARACTER_SET == type) {
					AFPObject[] children = res.getChildren();
					for (AFPObject child : children) {
						if (child instanceof Font) {
							this.fontManager.addCharset(res.getNameStr(), (Font) child);
						}
					}
				} else if (Resource.Type.OBJECT_CONTAINER == type) {
					String key = res.getNameStr();
					AFPObject[] children = res.getChildren();
					for (AFPObject child : children) {
						if (child instanceof ObjectContainer) {
							ObjectContainer objc = (ObjectContainer) child;
							ObjectTypeIdentifier.Component component = objc.getObjectTypeIdentifier().getComponent();
							if (component == ObjectTypeIdentifier.Component.TRUETYPE_FONT) {
								this.fontManager.addTrueTypeFont(objc.getObjectData());
							} else if (component == ObjectTypeIdentifier.Component.TRUETYPE_FONT_COLLECTION) {
								this.fontManager.addTrueTypeFont(objc.getObjectData(), true);
							} else {
								this.objMap.put(key, objc);	
							}
						}
					}
				} else if (Resource.Type.IOCA == type) {
					String key = res.getNameStr();
					AFPObject[] children = res.getChildren();
					for (AFPObject child : children) {
						if (child instanceof ImageObject) {
							this.iocaObjMap.put(key, (ImageObject) child);
						}
					}
				} else if (Resource.Type.PAGE_SEGMENT == type) {
					String key = res.getNameStr();
					if (this.psgMap == null) {
						this.psgMap = new HashMap<String, PageSegment>();
					}
					AFPObject[] children = res.getChildren();
					for (AFPObject child : children) {
						if (child instanceof PageSegment) {
							this.psgMap.put(key, (PageSegment) child);
						}
					}
				} else if (Resource.Type.OVERLAY == type) {
					String key = res.getNameStr();
					if (this.overlayMap == null) {
						this.overlayMap = new HashMap<String, Overlay>();
					}
					AFPObject[] children = res.getChildren();
					for (AFPObject child : children) {
						if (child instanceof Overlay) {
							this.overlayMap.put(key, (Overlay) child);
						}
					}
				} else {
					logger.severe("Unsupported type: " + type);
				}
			}
		}
	}
	

}
