package me.lumpchen.xafp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import me.lumpchen.xafp.sf.StructureField;
import me.lumpchen.xafp.sf.Identifier.Tag;

public class AFPFileReader {

	private AFPInputStream input;
	private Stack<AFPObject> objStack;
	private PrintFile printFile;
	
	public AFPFileReader() {
		this.objStack = new Stack<AFPObject>();
		this.printFile = new PrintFile();
	}
	
	public PrintFile getPrintFile() {
		return this.printFile;
	}

	/**
	 * Read all structure fields at once, all structure fields will be collected into printFile.
	 * This method might have memory limitation problem if input file size is extreme huge, in that case, 
	 * try using open/readNextPage/close methods to read page by page. 
	 * */
	public void read(File file) throws IOException {
		this.readBegin(file);
		
		while (true) {
			AFPContainer next = this.readNext();
			if (next == null) {
				break;
			}
			AFPObject parent = this.objStack.peek();
			this.addToParent(parent, next);
		}
		this.readEnd();
	}
	
	private AFPContainer readNext() throws IOException {
		while (true) {
			if (this.input.available() <= 0) {
				break;
			}
			byte first = this.input.readByte();
			if (AFPConst.Carriage_Control_Character != first) {
				break;
			}
			StructureField next = this.readNextSF();
			
			AFPObject obj = this.createObject(next);
			if (obj instanceof AFPContainer) {
				AFPContainer container = (AFPContainer) obj;
				if (container.isBegin()) {
					this.objStack.push(obj);
				} else {
					if (this.objStack.isEmpty()) {
						throw new AFPException("Not matched structure: " + next.getStructureTag());
					}
					AFPObject last = this.objStack.peek();
					if (this.isMatchedStructure(last, obj)) {
						last = this.objStack.pop();
						((AFPContainer) last).collect();
						return (AFPContainer) last;
					} else {
						throw new AFPException("Not matched structure: " + next.getStructureTag());
					}
				}
			} else {
				AFPObject parent = this.objStack.peek();
				this.addToParent(parent, obj);
			}
		}
		return null;
	}
	
	public void readBegin(File file) throws IOException {
		this.input = new AFPInputStream(new FileInputStream(file));
		this.objStack.push(this.printFile);
	}
	
	public void readEnd() {
		this.printFile.collect();
		this.objStack.pop();
	}
	
	public ResourceGroup readResourceGroup() throws IOException {
		while (true) {
			AFPContainer next = this.readNext();
			if (next == null) {
				break;
			}
			if (next instanceof ResourceGroup) {
				return (ResourceGroup) next;
			} else {
				AFPObject parent = this.objStack.peek();
				this.addToParent(parent, next);
			}
		}
		
		return null;
	}
	
	public Page readNextPage() throws IOException {
		while (true) {
			AFPContainer next = this.readNext();
			if (next == null) {
				break;
			}
			if (next instanceof Page) {
				return (Page) next;
			} else {
				AFPObject parent = this.objStack.peek();
				this.addToParent(parent, next);
			}
		}
		return null;
	}
	
	private void addToParent(AFPObject parent, AFPObject child) {
		if (parent instanceof AFPContainer) {
			((AFPContainer) parent).addChild(child);
		} else {
			throw new AFPException("Not matched structure.");
		}
	}
	
	private StructureField readNextSF() throws IOException {
		StructureField sf = StructureFieldReader.read(this.input);
		return sf;
	}
	
	public static final Map<Tag, Tag> pairedStructureField = new HashMap<Tag, Tag>();
	static {
		pairedStructureField.put(Tag.BRG, Tag.ERG);
		pairedStructureField.put(Tag.BRS, Tag.ERS);
		pairedStructureField.put(Tag.BOC, Tag.EOC);
		pairedStructureField.put(Tag.BDT, Tag.EDT);
		pairedStructureField.put(Tag.BPG, Tag.EPG);
		pairedStructureField.put(Tag.BAG, Tag.EAG);
		pairedStructureField.put(Tag.BPT, Tag.EPT);
		pairedStructureField.put(Tag.BFN, Tag.EFN);
		pairedStructureField.put(Tag.BCP, Tag.ECP);
		pairedStructureField.put(Tag.BNG, Tag.ENG);
		pairedStructureField.put(Tag.BIM, Tag.EIM);
		pairedStructureField.put(Tag.BOG, Tag.EOG);
		pairedStructureField.put(Tag.BFM, Tag.EFM);
		pairedStructureField.put(Tag.BMM, Tag.EMM);
		pairedStructureField.put(Tag.BGR, Tag.EGR);
		pairedStructureField.put(Tag.BPS, Tag.EPS);
		pairedStructureField.put(Tag.BMO, Tag.EMO);
		pairedStructureField.put(Tag.BSG, Tag.ESG);
	}
	
	private boolean isMatchedStructure(AFPObject begin, AFPObject end) {
		if (pairedStructureField.containsKey(begin.getStructureTag())) {
			if (end.getStructureTag() == pairedStructureField.get(begin.getStructureTag())) {
				return true;
			}
		}
		return false;
	}
	
	public void close() throws IOException {
		if (this.input != null) {
			this.input.close();
		}
	}
	
	private AFPObject createObject(StructureField sf) throws IOException {
		Tag tag = sf.getStructureTag();
		AFPObject obj = null;
		if (Tag.BRG == tag || Tag.ERG == tag) {
			obj = new ResourceGroup(sf);	
		} else if (Tag.BRS == tag || Tag.ERS == tag) {
			obj = new Resource(sf);
		} else if (Tag.BOC == tag || Tag.EOC == tag) {
			obj = new ObjectContainer(sf);
		} else if (Tag.BDT == tag || Tag.EDT == tag) {
			obj = new Document(sf);
		} else if (Tag.BNG == tag || Tag.ENG == tag) {
			obj = new NamedPageGroup(sf);
		} else if (Tag.BPG == tag || Tag.EPG == tag) {
			obj = new Page(sf);
		} else if (Tag.BAG == tag || Tag.EAG == tag) {
			obj = new ActiveEnvironmentGroup(sf);
		} else if (Tag.BPT == tag || Tag.EPT == tag) {
			obj = new PresentationTextObject(sf);
		} else if (Tag.BFN == tag || Tag.EFN == tag) {
			obj = new Font(sf);
		} else if (Tag.BCP == tag || Tag.ECP == tag) {
			obj = new CodePage(sf);
		} else if (Tag.BIM == tag || Tag.EIM == tag) {
			obj = new ImageObject(sf);
		} else if (Tag.BGR == tag || Tag.EGR == tag) {
			obj = new GraphicsObject(sf);
		} else if (Tag.BOG == tag || Tag.EOG == tag) {
			obj = new ObjectEnvironmentGroup(sf);
		} else if (Tag.BFM == tag || Tag.EFM == tag) {
			obj = new FormMap(sf);
		} else if (Tag.BMM == tag || Tag.EMM == tag) {
			obj = new MediumMap(sf);
		} else if (Tag.BPS == tag || Tag.EPS == tag) {
			obj = new PageSegment(sf);
		} else if (Tag.BMO == tag || Tag.EMO == tag) {
			obj = new Overlay(sf);
		} else if (Tag.BSG == tag || Tag.ESG == tag) {
			obj = new ResourceEnvironmentGroup(sf);
		} else {
			if (Tag.OCD == tag) {
				obj = new ObjectContainerData(sf);
			} else if (Tag.PGD == tag) {
				obj = new PageDescriptor(sf);
			} else if (Tag.PTD == tag) {
				obj = new PresentationTextDescriptor(sf);
			} else if (Tag.PTX == tag) {
				obj = new PresentationTextData(sf);
			} else if (Tag.IOB == tag) {
				obj = new IncludeObject(sf);
			} else if (Tag.CPD == tag) {
				obj = new CodePageDescriptor(sf);
			} else if (Tag.CPI == tag) {
				obj = new CodePageIndex(sf);
			} else if (Tag.CPC == tag) {
				obj = new CodePageControl(sf);
			} else if (Tag.NOP == tag) {
				obj = new NoOperation(sf);
			} else if (Tag.FND == tag) {
				obj = new FontDescriptor(sf);
			} else if (Tag.FNC == tag) {
				obj = new FontControl(sf);
			} else if (Tag.FNO == tag) {
				obj = new FontOrientation(sf);
			} else if (Tag.FNP == tag) {
				obj = new FontPosition(sf);
			} else if (Tag.FNI == tag) {
				obj = new FontIndex(sf);
			} else if (Tag.FNN == tag) {
				obj = new FontNameMap(sf);
			} else if (Tag.FNG == tag) {
				obj = new FontPatterns(sf);
			} else if (Tag.FNM == tag) {
				obj = new FontPatternsMap(sf);
			} else if (Tag.MCF == tag) {
				obj = new MapCodedFontFormat2(sf);
			} else if (Tag.OBD == tag) {
				obj = new ObjectAreaDescriptor(sf);
			} else if (Tag.OBP == tag) {
				obj = new ObjectAreaPosition(sf);
			} else if (Tag.MIO == tag) {
				obj = new MapImageObject(sf);
			} else if (Tag.IDD == tag) {
				obj = new ImageDataDescriptor(sf);
			} else if (Tag.IPD == tag) {
				obj = new ImagePictureData(sf);
			} else if (Tag.PGP == tag) {
				obj = new PagePosition(sf);
			} else if (Tag.MDD == tag) {
				obj = new MediumDescriptor(sf);
			} else if (Tag.MCC == tag) {
				obj = new MediumCopyCount(sf);
			} else if (Tag.PEC == tag) {
				obj = new PresentationEnvironmentControl(sf);
			} else if (Tag.MMC == tag) {
				obj = new MediumModificationControl(sf);
			} else if (Tag.IMM == tag) {
				obj = new InvokeMediumMap(sf);	
			} else if (Tag.TLE == tag) {
				obj = new TagLogicalElement(sf);
			} else if (Tag.TLE == tag) {
				obj = new GraphicsDataDescriptor(sf);
			} else if (Tag.GAD == tag) {
				obj = new GraphicsData(sf);
			} else if (Tag.MPS == tag) {
				obj = new MapPageSegment(sf);
			} else if (Tag.MDR == tag) {
				obj = new MapDataResource(sf);
			} else if (Tag.IPS == tag) {
				obj = new IncludePageSegment(sf);
			} else if (Tag.MPO == tag) {
				obj = new MapPageOverlay(sf);
			} else if (Tag.IPO == tag) {
				obj = new IncludePageOverlay(sf);
			} else if (Tag.MGO == tag) {
				obj = new MapGraphicsObject(sf);
			}
		}
		
		return obj;
	}
	
}
