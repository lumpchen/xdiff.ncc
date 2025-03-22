package me.lumpchen.xafp.sf;

import java.util.HashMap;
import java.util.Map;

import me.lumpchen.xafp.AFPConst;

public class Identifier {
	
	public static final byte[] SF_BAG = {(byte)0xD3, (byte)0xA8, (byte)0xC9}; //Begin Active Environment Group
	public static final byte[] SF_BBC = {(byte)0xD3, (byte)0xA8, (byte)0xEB}; //Begin Bar Code Object
	public static final byte[] SF_BCA = {(byte)0xD3, (byte)0xA8, (byte)0x77}; //Begin Color Attribute Table
	public static final byte[] SF_BDA = {(byte)0xD3, (byte)0xEE, (byte)0xEB}; //Bar Code Data
	public static final byte[] SF_BDD = {(byte)0xD3, (byte)0xA6, (byte)0xEB}; //Bar Code Data Descriptor
	public static final byte[] SF_BDG = {(byte)0xD3, (byte)0xA8, (byte)0xC4}; //Begin Document Environment Group
	public static final byte[] SF_BDI = {(byte)0xD3, (byte)0xA8, (byte)0xA7}; //Begin Document Index
	public static final byte[] SF_BDT = {(byte)0xD3, (byte)0xA8, (byte)0xA8}; //Begin Document
	public static final byte[] SF_BFG = {(byte)0xD3, (byte)0xA8, (byte)0xC5}; //Begin Form Environment Group (O)
	public static final byte[] SF_BFM = {(byte)0xD3, (byte)0xA8, (byte)0xCD}; //Begin Form Map
	public static final byte[] SF_BGR = {(byte)0xD3, (byte)0xA8, (byte)0xBB}; //Begin Graphics Object
	public static final byte[] SF_BII = {(byte)0xD3, (byte)0xA8, (byte)0x7B}; //Begin IM Image (C)
	public static final byte[] SF_BIM = {(byte)0xD3, (byte)0xA8, (byte)0xFB}; //Begin Image Object
	public static final byte[] SF_BMM = {(byte)0xD3, (byte)0xA8, (byte)0xCC}; //Begin Medium Map
	public static final byte[] SF_BMO = {(byte)0xD3, (byte)0xA8, (byte)0xDF}; //Begin Overlay
	public static final byte[] SF_BNG = {(byte)0xD3, (byte)0xA8, (byte)0xAD}; //Begin Named Page Group
	public static final byte[] SF_BOC = {(byte)0xD3, (byte)0xA8, (byte)0x92}; //Begin Object Container
	public static final byte[] SF_BOG = {(byte)0xD3, (byte)0xA8, (byte)0xC7}; //Begin Object Environment Group
	public static final byte[] SF_BPG = {(byte)0xD3, (byte)0xA8, (byte)0xAF}; //Begin Page
	public static final byte[] SF_BPS = {(byte)0xD3, (byte)0xA8, (byte)0x5F}; //Begin Page Segment
	public static final byte[] SF_BPT = {(byte)0xD3, (byte)0xA8, (byte)0x9B}; //Begin Presentation Text Object
	public static final byte[] SF_BRG = {(byte)0xD3, (byte)0xA8, (byte)0xC6}; //Begin Resource Group
	public static final byte[] SF_BRS = {(byte)0xD3, (byte)0xA8, (byte)0xCE}; //Begin Resource
	public static final byte[] SF_BSG = {(byte)0xD3, (byte)0xA8, (byte)0xD9}; //Begin Resource Environment Group
	public static final byte[] SF_CAT = {(byte)0xD3, (byte)0xB0, (byte)0x77}; //Color Attribute Table
	public static final byte[] SF_CDD = {(byte)0xD3, (byte)0xA6, (byte)0x92}; //Container Data Descriptor
	public static final byte[] SF_CTC = {(byte)0xD3, (byte)0xA7, (byte)0x9B}; //Composed Text Control (O)
	public static final byte[] SF_EAG = {(byte)0xD3, (byte)0xA9, (byte)0xC9}; //End Active Environment Group
	public static final byte[] SF_EBC = {(byte)0xD3, (byte)0xA9, (byte)0xEB}; //End Bar Code Object
	public static final byte[] SF_ECA = {(byte)0xD3, (byte)0xA9, (byte)0x77}; //End Color Attribute Table
	public static final byte[] SF_EDG = {(byte)0xD3, (byte)0xA9, (byte)0xC4}; //End Document Environment Group
	public static final byte[] SF_EDI = {(byte)0xD3, (byte)0xA9, (byte)0xA7}; //End Document Index
	public static final byte[] SF_EDT = {(byte)0xD3, (byte)0xA9, (byte)0xA8}; //End Document
	public static final byte[] SF_EFG = {(byte)0xD3, (byte)0xA9, (byte)0xC5}; //End Form Environment Group (O)
	public static final byte[] SF_EFM = {(byte)0xD3, (byte)0xA9, (byte)0xCD}; //End Form Map
	public static final byte[] SF_EGR = {(byte)0xD3, (byte)0xA9, (byte)0xBB}; //End Graphics Object
	public static final byte[] SF_EII = {(byte)0xD3, (byte)0xA9, (byte)0x7B}; //End IM Image (C)
	public static final byte[] SF_EIM = {(byte)0xD3, (byte)0xA9, (byte)0xFB}; //End Image Object
	public static final byte[] SF_EMM = {(byte)0xD3, (byte)0xA9, (byte)0xCC}; //End Medium Map
	public static final byte[] SF_EMO = {(byte)0xD3, (byte)0xA9, (byte)0xDF}; //End Overlay
	public static final byte[] SF_ENG = {(byte)0xD3, (byte)0xA9, (byte)0xAD}; //End Named Page Group
	public static final byte[] SF_EOC = {(byte)0xD3, (byte)0xA9, (byte)0x92}; //End Object Container
	public static final byte[] SF_EOG = {(byte)0xD3, (byte)0xA9, (byte)0xC7}; //End Object Environment Group
	public static final byte[] SF_EPG = {(byte)0xD3, (byte)0xA9, (byte)0xAF}; //End Page
	public static final byte[] SF_EPS = {(byte)0xD3, (byte)0xA9, (byte)0x5F}; //End Page Segment
	public static final byte[] SF_EPT = {(byte)0xD3, (byte)0xA9, (byte)0x9B}; //End Presentation Text Object
	public static final byte[] SF_ERG = {(byte)0xD3, (byte)0xA9, (byte)0xC6}; //End Resource Group
	public static final byte[] SF_ERS = {(byte)0xD3, (byte)0xA9, (byte)0xCE}; //End Resource
	public static final byte[] SF_ESG = {(byte)0xD3, (byte)0xA9, (byte)0xD9}; //End Resource Environment Group
	public static final byte[] SF_FGD = {(byte)0xD3, (byte)0xA6, (byte)0xC5}; //Form Environment Group Descriptor (O)
	public static final byte[] SF_GAD = {(byte)0xD3, (byte)0xEE, (byte)0xBB}; //Graphics Data
	public static final byte[] SF_GDD = {(byte)0xD3, (byte)0xA6, (byte)0xBB}; //Graphics Data Descriptor
	public static final byte[] SF_ICP = {(byte)0xD3, (byte)0xAC, (byte)0x7B}; //IM Image Cell Position (C)
	public static final byte[] SF_IDD = {(byte)0xD3, (byte)0xA6, (byte)0xFB}; //Image Data Descriptor
	public static final byte[] SF_IEL = {(byte)0xD3, (byte)0xB2, (byte)0xA7}; //Index Element
	public static final byte[] SF_IID = {(byte)0xD3, (byte)0xA6, (byte)0x7B}; //Image Input Descriptor (C)
	public static final byte[] SF_IMM = {(byte)0xD3, (byte)0xAB, (byte)0xCC}; //Invoke Medium Map
	public static final byte[] SF_IOB = {(byte)0xD3, (byte)0xAF, (byte)0xC3}; //Include Object
	public static final byte[] SF_IOC = {(byte)0xD3, (byte)0xA7, (byte)0x7B}; //IM Image Output Control (C)
	public static final byte[] SF_IPD = {(byte)0xD3, (byte)0xEE, (byte)0xFB}; //Image Picture Data
	public static final byte[] SF_IPG = {(byte)0xD3, (byte)0xAF, (byte)0xAF}; //Include Page
	public static final byte[] SF_IPO = {(byte)0xD3, (byte)0xAF, (byte)0xD8}; //Include Page Overlay
	public static final byte[] SF_IPS = {(byte)0xD3, (byte)0xAF, (byte)0x5F}; //Include Page Segment
	public static final byte[] SF_IRD = {(byte)0xD3, (byte)0xEE, (byte)0x7B}; //IM Image Raster Data (C)
	public static final byte[] SF_LLE = {(byte)0xD3, (byte)0xB4, (byte)0x90}; //Link Logical Element
	public static final byte[] SF_MBC = {(byte)0xD3, (byte)0xAB, (byte)0xEB}; //Map Bar Code Object
	public static final byte[] SF_MCA = {(byte)0xD3, (byte)0xAB, (byte)0x77}; //Map Color Attribute Table
	public static final byte[] SF_MCC = {(byte)0xD3, (byte)0xA2, (byte)0x88}; //Medium Copy Count
	public static final byte[] SF_MCD = {(byte)0xD3, (byte)0xAB, (byte)0x92}; //Map Container Data
	public static final byte[] SF_MCF = {(byte)0xD3, (byte)0xAB, (byte)0x8A}; //Map Coded Font
	public static final byte[] SF_MCF_1 = {(byte)0xD3, (byte)0xB1, (byte)0x8A}; //Map Coded Font Format-1 (C)
	public static final byte[] SF_MDD = {(byte)0xD3, (byte)0xA6, (byte)0x88}; //Medium Descriptor
	public static final byte[] SF_MDR = {(byte)0xD3, (byte)0xAB, (byte)0xC3}; //Map Data Resource
	public static final byte[] SF_MFC = {(byte)0xD3, (byte)0xA0, (byte)0x88}; //Medium Finishing Control
	public static final byte[] SF_MGO = {(byte)0xD3, (byte)0xAB, (byte)0xBB}; //Map Graphics Object
	public static final byte[] SF_MIO = {(byte)0xD3, (byte)0xAB, (byte)0xFB}; //Map Image Object
	public static final byte[] SF_MMC = {(byte)0xD3, (byte)0xA7, (byte)0x88}; //Medium Modification Control
	public static final byte[] SF_MMO = {(byte)0xD3, (byte)0xB1, (byte)0xDF}; //Map Medium Overlay
	public static final byte[] SF_MMT = {(byte)0xD3, (byte)0xAB, (byte)0x88}; //Map Media Type
	public static final byte[] SF_MPG = {(byte)0xD3, (byte)0xAB, (byte)0xAF}; //Map Page
	public static final byte[] SF_MPO = {(byte)0xD3, (byte)0xAB, (byte)0xD8}; //Map Page Overlay
	public static final byte[] SF_MPS = {(byte)0xD3, (byte)0xB1, (byte)0x5F}; //Map Page Segment
	public static final byte[] SF_MSU = {(byte)0xD3, (byte)0xAB, (byte)0xEA}; //Map Suppression
	public static final byte[] SF_NOP = {(byte)0xD3, (byte)0xEE, (byte)0xEE}; //No Operation
	public static final byte[] SF_OBD = {(byte)0xD3, (byte)0xA6, (byte)0x6B}; //Object Area Descriptor
	public static final byte[] SF_OBP = {(byte)0xD3, (byte)0xAC, (byte)0x6B}; //Object Area Position
	public static final byte[] SF_OCD = {(byte)0xD3, (byte)0xEE, (byte)0x92}; //Object Container Data
	public static final byte[] SF_PFC = {(byte)0xD3, (byte)0xB2, (byte)0x88}; //Presentation Fidelity Control
	public static final byte[] SF_PEC = {(byte)0xD3, (byte)0xA7, (byte)0xA8}; //Presentation Environment Control
	public static final byte[] SF_PGD = {(byte)0xD3, (byte)0xA6, (byte)0xAF}; //Page Descriptor
	public static final byte[] SF_PGP = {(byte)0xD3, (byte)0xB1, (byte)0xAF}; //Page Position
	public static final byte[] SF_PGP_1 = {(byte)0xD3, (byte)0xAC, (byte)0xAF}; //Page Position Format-1 (C)
	public static final byte[] SF_PMC = {(byte)0xD3, (byte)0xA7, (byte)0xAF}; //Page Modification Control
	public static final byte[] SF_PPO = {(byte)0xD3, (byte)0xAD, (byte)0xC3}; //Preprocess Presentation Object
	public static final byte[] SF_PTD = {(byte)0xD3, (byte)0xB1, (byte)0x9B}; //Presentation Text Data Descriptor
	public static final byte[] SF_PTD_1 = {(byte)0xD3, (byte)0xA6, (byte)0x9B}; //Presentation Text Descriptor Format-1 (C)
	public static final byte[] SF_PTX = {(byte)0xD3, (byte)0xEE, (byte)0x9B}; //Presentation Text Data
	public static final byte[] SF_TLE = {(byte)0xD3, (byte)0xA0, (byte)0x90}; //Tag Logical Element

	// FOCA structured fields

	public static final byte[] SF_BCF = {(byte)0xD3, (byte)0xA8, (byte)0x8A}; //Begin Coded Font (BCF)
	public static final byte[] SF_BCP = {(byte)0xD3, (byte)0xA8, (byte)0x87}; //Begin Code Page (BCP)
	public static final byte[] SF_BFN = {(byte)0xD3, (byte)0xA8, (byte)0x89}; //Begin Font (BFN)
	public static final byte[] SF_CFC = {(byte)0xD3, (byte)0xA7, (byte)0x8A}; //Coded Font Control (CFC)
	public static final byte[] SF_CFI = {(byte)0xD3, (byte)0x8C, (byte)0x8A}; //Coded Font Index (CFI)
	public static final byte[] SF_CPC = {(byte)0xD3, (byte)0xA7, (byte)0x87}; //Code Page Control (CPC)
	public static final byte[] SF_CPD = {(byte)0xD3, (byte)0xA6, (byte)0x87}; //Code Page Descriptor (CPD)
	public static final byte[] SF_CPI = {(byte)0xD3, (byte)0x8C, (byte)0x87}; //Code Page Index (CPI)
	public static final byte[] SF_ECF = {(byte)0xD3, (byte)0xA9, (byte)0x8A}; //End Coded Font (ECF)
	public static final byte[] SF_ECP = {(byte)0xD3, (byte)0xA9, (byte)0x87}; //End Code Page (ECP)
	public static final byte[] SF_EFN = {(byte)0xD3, (byte)0xA9, (byte)0x89}; //End Font (EFN)
	public static final byte[] SF_FNC = {(byte)0xD3, (byte)0xA7, (byte)0x89}; //Font Control (FNC)
	public static final byte[] SF_FND = {(byte)0xD3, (byte)0xA6, (byte)0x89}; //Font Descriptor (FND)
	public static final byte[] SF_FNG = {(byte)0xD3, (byte)0xEE, (byte)0x89}; //Font Patterns (FNG)
	public static final byte[] SF_FNI = {(byte)0xD3, (byte)0x8C, (byte)0x89}; //Font Index (FNI)
	public static final byte[] SF_FNM = {(byte)0xD3, (byte)0xA2, (byte)0x89}; //Font Patterns Map (FNM)
	public static final byte[] SF_FNN = {(byte)0xD3, (byte)0xAB, (byte)0x89}; //Font Name Map (FNN)
	public static final byte[] SF_FNO = {(byte)0xD3, (byte)0xAE, (byte)0x89}; //Font Orientation (FNO)
	public static final byte[] SF_FNP = {(byte)0xD3, (byte)0xAC, (byte)0x89}; //Font Position (FNP)
	
	private static final Map<Integer, Identifier> instanceMap = new HashMap<Integer, Identifier>();
	static {
		instanceMap.put(toInt(SF_BDI), new Identifier(Tag.BDI, SF_BDI));
		instanceMap.put(toInt(SF_EDI), new Identifier(Tag.EDI, SF_EDI));
		instanceMap.put(toInt(SF_IEL), new Identifier(Tag.IEL, SF_IEL));
		
		instanceMap.put(toInt(SF_BRG), new Identifier(Tag.BRG, SF_BRG));
		instanceMap.put(toInt(SF_ERG), new Identifier(Tag.ERG, SF_ERG));
		instanceMap.put(toInt(SF_BRS), new Identifier(Tag.BRS, SF_BRS));
		instanceMap.put(toInt(SF_ERS), new Identifier(Tag.ERS, SF_ERS));
		instanceMap.put(toInt(SF_BOC), new Identifier(Tag.BOC, SF_BOC));
		instanceMap.put(toInt(SF_EOC), new Identifier(Tag.EOC, SF_EOC));
		instanceMap.put(toInt(SF_OCD), new Identifier(Tag.OCD, SF_OCD));
		instanceMap.put(toInt(SF_BCP), new Identifier(Tag.BCP, SF_BCP));
		instanceMap.put(toInt(SF_ECP), new Identifier(Tag.ECP, SF_ECP));
		instanceMap.put(toInt(SF_CPD), new Identifier(Tag.CPD, SF_CPD));
		instanceMap.put(toInt(SF_CPC), new Identifier(Tag.CPC, SF_CPC));
		instanceMap.put(toInt(SF_CFI), new Identifier(Tag.CFI, SF_CFI));
		instanceMap.put(toInt(SF_CPI), new Identifier(Tag.CPI, SF_CPI));
		instanceMap.put(toInt(SF_BFN), new Identifier(Tag.BFN, SF_BFN));
		instanceMap.put(toInt(SF_FND), new Identifier(Tag.FND, SF_FND));
		instanceMap.put(toInt(SF_FNC), new Identifier(Tag.FNC, SF_FNC));
		instanceMap.put(toInt(SF_FNO), new Identifier(Tag.FNO, SF_FNO));
		instanceMap.put(toInt(SF_FNP), new Identifier(Tag.FNP, SF_FNP));
		instanceMap.put(toInt(SF_FNM), new Identifier(Tag.FNM, SF_FNM));
		instanceMap.put(toInt(SF_FNI), new Identifier(Tag.FNI, SF_FNI));
		instanceMap.put(toInt(SF_FNN), new Identifier(Tag.FNN, SF_FNN));
		instanceMap.put(toInt(SF_FNG), new Identifier(Tag.FNG, SF_FNG));
		instanceMap.put(toInt(SF_EFN), new Identifier(Tag.EFN, SF_EFN));
		instanceMap.put(toInt(SF_BDT), new Identifier(Tag.BDT, SF_BDT));
		instanceMap.put(toInt(SF_BFG), new Identifier(Tag.BFG, SF_BFG));
		instanceMap.put(toInt(SF_BPG), new Identifier(Tag.BPG, SF_BPG));
		instanceMap.put(toInt(SF_BAG), new Identifier(Tag.BAG, SF_BAG));
		instanceMap.put(toInt(SF_BDG), new Identifier(Tag.BDG, SF_BDG));
		instanceMap.put(toInt(SF_PGD), new Identifier(Tag.PGD, SF_PGD));
		instanceMap.put(toInt(SF_PTD), new Identifier(Tag.PTD, SF_PTD));
		instanceMap.put(toInt(SF_PTD_1), new Identifier(Tag.PTD_1, SF_PTD_1));
		instanceMap.put(toInt(SF_EAG), new Identifier(Tag.EAG, SF_EAG));
		instanceMap.put(toInt(SF_EDG), new Identifier(Tag.EDG, SF_EDG));
		instanceMap.put(toInt(SF_BPT), new Identifier(Tag.BPT, SF_BPT));
		instanceMap.put(toInt(SF_PTX), new Identifier(Tag.PTX, SF_PTX));
		instanceMap.put(toInt(SF_EPT), new Identifier(Tag.EPT, SF_EPT));
		instanceMap.put(toInt(SF_IOB), new Identifier(Tag.IOB, SF_IOB));
		instanceMap.put(toInt(SF_EPG), new Identifier(Tag.EPG, SF_EPG));
		instanceMap.put(toInt(SF_MCD), new Identifier(Tag.MCD, SF_MCD));
		instanceMap.put(toInt(SF_MCF), new Identifier(Tag.MCF, SF_MCF));
		instanceMap.put(toInt(SF_MCF_1), new Identifier(Tag.MCF_1, SF_MCF_1));
		instanceMap.put(toInt(SF_EDT), new Identifier(Tag.EDT, SF_EDT));
		instanceMap.put(toInt(SF_EFG), new Identifier(Tag.EFG, SF_EFG));
		
		instanceMap.put(toInt(SF_CTC), new Identifier(Tag.CTC, SF_CTC));
		
		instanceMap.put(toInt(SF_BNG), new Identifier(Tag.BNG, SF_BNG));
		instanceMap.put(toInt(SF_ENG), new Identifier(Tag.ENG, SF_ENG));
		
		instanceMap.put(toInt(SF_BOG), new Identifier(Tag.BOG, SF_BOG));
		instanceMap.put(toInt(SF_EOG), new Identifier(Tag.EOG, SF_EOG));
		
		instanceMap.put(toInt(SF_BII), new Identifier(Tag.BII, SF_BII));
		instanceMap.put(toInt(SF_EII), new Identifier(Tag.EII, SF_EII));
		
		instanceMap.put(toInt(SF_BIM), new Identifier(Tag.BIM, SF_BIM));
		instanceMap.put(toInt(SF_EIM), new Identifier(Tag.EIM, SF_EIM));
		instanceMap.put(toInt(SF_IOC), new Identifier(Tag.IOC, SF_IOC));
		instanceMap.put(toInt(SF_IID), new Identifier(Tag.IID, SF_IID));
		instanceMap.put(toInt(SF_IRD), new Identifier(Tag.IRD, SF_IRD));
		
		instanceMap.put(toInt(SF_OBD), new Identifier(Tag.OBD, SF_OBD));
		instanceMap.put(toInt(SF_OBP), new Identifier(Tag.OBP, SF_OBP));
		instanceMap.put(toInt(SF_MIO), new Identifier(Tag.MIO, SF_MIO));
		instanceMap.put(toInt(SF_IDD), new Identifier(Tag.IDD, SF_IDD));
		instanceMap.put(toInt(SF_IPD), new Identifier(Tag.IPD, SF_IPD));
		
		instanceMap.put(toInt(SF_BFM), new Identifier(Tag.BFM, SF_BFM));
		instanceMap.put(toInt(SF_EFM), new Identifier(Tag.EFM, SF_EFM));
		instanceMap.put(toInt(SF_BMM), new Identifier(Tag.BMM, SF_BMM));
		instanceMap.put(toInt(SF_EMM), new Identifier(Tag.EMM, SF_EMM));
		instanceMap.put(toInt(SF_PGP), new Identifier(Tag.PGP, SF_PGP));
		instanceMap.put(toInt(SF_PGP_1), new Identifier(Tag.PGP_1, SF_PGP_1));
		instanceMap.put(toInt(SF_MDD), new Identifier(Tag.MDD, SF_MDD));
		instanceMap.put(toInt(SF_MCC), new Identifier(Tag.MCC, SF_MCC));
		instanceMap.put(toInt(SF_PEC), new Identifier(Tag.PEC, SF_PEC));
		instanceMap.put(toInt(SF_MMC), new Identifier(Tag.MMC, SF_MMC));
		instanceMap.put(toInt(SF_MMO), new Identifier(Tag.MMO, SF_MMO));
		instanceMap.put(toInt(SF_IMM), new Identifier(Tag.IMM, SF_IMM));
		
		instanceMap.put(toInt(SF_BGR), new Identifier(Tag.BGR, SF_BGR));
		instanceMap.put(toInt(SF_EGR), new Identifier(Tag.EGR, SF_EGR));
		instanceMap.put(toInt(SF_GDD), new Identifier(Tag.GDD, SF_GDD));
		instanceMap.put(toInt(SF_GAD), new Identifier(Tag.GAD, SF_GAD));
		
		instanceMap.put(toInt(SF_BPS), new Identifier(Tag.BPS, SF_BPS));
		instanceMap.put(toInt(SF_EPS), new Identifier(Tag.EPS, SF_EPS));
		instanceMap.put(toInt(SF_MPS), new Identifier(Tag.MPS, SF_MPS));
		instanceMap.put(toInt(SF_IPS), new Identifier(Tag.IPS, SF_IPS));
		
		instanceMap.put(toInt(SF_BMO), new Identifier(Tag.BMO, SF_BMO));
		instanceMap.put(toInt(SF_EMO), new Identifier(Tag.EMO, SF_EMO));
		instanceMap.put(toInt(SF_MPO), new Identifier(Tag.MPO, SF_MPO));
		instanceMap.put(toInt(SF_IPO), new Identifier(Tag.IPO, SF_IPO));
		
		instanceMap.put(toInt(SF_BSG), new Identifier(Tag.BSG, SF_BSG));
		instanceMap.put(toInt(SF_ESG), new Identifier(Tag.ESG, SF_ESG));
		
		instanceMap.put(toInt(SF_MGO), new Identifier(Tag.MGO, SF_MGO));
		
		instanceMap.put(toInt(SF_MDR), new Identifier(Tag.MDR, SF_MDR));
		
		instanceMap.put(toInt(SF_TLE), new Identifier(Tag.TLE, SF_TLE));
		instanceMap.put(toInt(SF_NOP), new Identifier(Tag.NOP, SF_NOP));
	}
	
	public enum Tag {
		BDT("Begin Document", TagType.begin),
		BFG("Begin Form Environment Group (O)", TagType.begin),
		BPG("Begin Page", TagType.begin),
		BAG("Begin Active Environment Group", TagType.begin),
		BRG("Begin Resource Group", TagType.begin),
		BRS("Begin Resource", TagType.begin),
		ERG("End Resource Group", TagType.begin),
		BOC("Begin Object Container", TagType.begin),
		BBC("Begin Bar Code Object", TagType.begin),
		BCA("Begin Color Attribute Table", TagType.begin),
		BDA("Bar Code Data", TagType.begin),
		BDD("Bar Code Data Descriptor", TagType.begin),
		BDG("Begin Document Environment Group", TagType.begin),
		BDI("Begin Document Index", TagType.begin),
		BFM("Begin Form Map", TagType.begin),
		BGR("Begin Graphics Object", TagType.begin),
		BIM("Begin Image Object", TagType.begin),
		BMM("Begin Medium Map", TagType.begin),
		BMO("Begin Overlay", TagType.begin),
		BNG("Begin Named Page Group", TagType.begin),
		BOG("Begin Object Environment Group", TagType.begin),
		BPS("Begin Page Segment", TagType.begin),
		BSG("Begin Resource Environment Group", TagType.begin),
		BPT("Begin Presentation Text Object", TagType.begin),
		BCP("Begin Code Page", TagType.begin),
		BFN("Begin Font", TagType.begin),
		BII("Begin IM Image", TagType.begin),
		
		EBC("End Bar Code Object", TagType.end),
		ERS("End Resource", TagType.end),
		EOC("End Object Container", TagType.end),
		ECA("End Color Attribute Table", TagType.end),
		EDG("End Document Environment Group", TagType.end),
		EDI("End Document Index", TagType.end),
		EFM("End Form Map", TagType.end),
		EGR("End Graphics Object", TagType.end),
		EIM("End Image Object", TagType.end),
		EMM("End Medium Map", TagType.end),
		EMO("End Overlay", TagType.end),
		ENG("End Named Page Group", TagType.end),
		EOG("End Object Environment Group", TagType.end),
		EPS("End Page Segment", TagType.end),
		ESG("End Resource Environment Group", TagType.end),
		EAG("End Active Environment Group", TagType.end),
		EPT("End Presentation Text Object", TagType.end),
		EPG("End Page", TagType.end),
		EDT("End Document", TagType.end),
		EFG("End Form Environment Group (O)", TagType.end),
		ECP("End Code Page", TagType.end),
		EFN("End Font", TagType.end),
		EII("End IM Image", TagType.end),
		
		CAT("Color Attribute Table", TagType.obj),
		CDD("Container Data Descriptor", TagType.obj),
		CTC("Composed Text Control (O)", TagType.obj),
		GAD("Graphics Data", TagType.obj),
		GDD("Graphics Data Descriptor", TagType.obj),
		IDD("Image Data Descriptor", TagType.obj),
		IEL("Index Element", TagType.obj),
		IMM("Invoke Medium Map", TagType.obj),
		IPD("Image Picture Data", TagType.obj),
		IPG("Include Page", TagType.obj),
		IPO("Include Page Overlay", TagType.obj),
		IPS("Include Page Segment", TagType.obj),
		LLE("Link Logical Element", TagType.obj),
		MBC("Map Bar Code Object", TagType.obj),
		MCA("Map Color Attribute Table", TagType.obj),
		MCC("Medium Copy Count", TagType.obj),
		MCD("Map Container Data", TagType.obj),
		MDD("Medium Descriptor", TagType.obj),
		MDR("Map Data Resource", TagType.obj),
		MFC("Medium Finishing Control", TagType.obj),
		MGO("Map Graphics Object", TagType.obj),
		MIO("Map Image Object", TagType.obj),
		MMC("Medium Modification Control", TagType.obj),
		MMO("Map Medium Overlay", TagType.obj),
		MMT("Map Media Type", TagType.obj),
		MPG("Map Page", TagType.obj),
		MPO("Map Page Overlay", TagType.obj),
		MPS("Map Page Segment", TagType.obj),
		MSU("Map Suppression", TagType.obj),
		OBD("Object Area Descriptor", TagType.obj),
		OBP("Object Area Position", TagType.obj),
		OCD("Object Container Data", TagType.obj),
		PEC("Presentation Environment Control", TagType.obj),
		PFC("Presentation Fidelity Control", TagType.obj),
		PGP("Page Position (PGP) Format 2", TagType.obj),
		PGP_1("Page Position Format-1 (C)", TagType.obj),
		PMC("Page Modification Control", TagType.obj),
		PPO("Preprocess Presentation Object", TagType.obj),
		TLE("Tag Logical Element", TagType.obj),
		PGD("Page Descriptor", TagType.obj),
		PTD("Presentation Text Data Descriptor", TagType.obj),
		PTD_1("Presentation Text Descriptor Format-1 (C)", TagType.obj),
		PTX("Presentation Text Data", TagType.obj),
		IOB("Include Object", TagType.obj),
		IOC("IM Image Output Control", TagType.obj),
		IID("Image Input Descriptor", TagType.obj),
		IRD("IM Image Raster Data", TagType.obj),
		MCF("Map Coded Font", TagType.obj),
		MCF_1("Map Coded Font Format-1 (C)", TagType.obj),
		CPD("Code Page Descriptor", TagType.obj),
		CPC("Code Page Control", TagType.obj),
		CFI("Coded Font Index", TagType.obj),
		CPI("Code Page Index", TagType.obj),
		FND("Font Descriptor", TagType.obj),
		FNC("Font Control", TagType.obj),
		FNO("Font Orientation", TagType.obj),
		FNP("Font Position", TagType.obj),
		FNI("Font Index", TagType.obj),
		FNN("Font Name Map", TagType.obj),
		FNG("Font Patterns", TagType.obj),
		FNM("Font Patterns Map", TagType.obj),
		NOP("No Operation", TagType.obj);
		
		private String description;
		private TagType type;
		
		private Tag(String description, TagType type) {
			this.description = description;
			this.type = type;
		}
		
		public String getDesc() {
			return this.description;
		}
		
		public TagType getType() {
			return this.type;
		}
	};
	
	public enum TagType {
		begin, end, obj
	};
	
	private Tag tag;
	private byte[] bytes;
	
	private Identifier(Tag tag, byte[] bytes) {
		this.tag = tag;
		this.bytes = bytes;
	}
	
	public static Identifier instance(byte[] bytes) {
		if (bytes == null || bytes.length != 3) {
			throw new java.lang.IllegalArgumentException("Invalid arguments!");
		}
		
		int v = toInt(bytes);
		if (!instanceMap.containsKey(v)) {
			throw new java.lang.IllegalArgumentException("Invalid identifier: "
					+ AFPConst.bytesToHex(bytes));
		}
		
		return instanceMap.get(v);
	}
	
	public static boolean isContainerTag(Tag tag) {
		if (tag.getType() == TagType.begin || tag.getType() == TagType.end) {
			return true;
		}
		return false;
	}
	
	private static int toInt(byte[] bytes) {
		int v = 0;
		v |= (bytes[0] & 0x000000FF) << 16;
		v |= (bytes[1] & 0x000000FF) << 8;
		v |= (bytes[2] & 0x000000FF);
		return v;
	}

	public Tag getTag() {
		return tag;
	}

	public byte[] getBytes() {
		return bytes;
	}
	
}
 