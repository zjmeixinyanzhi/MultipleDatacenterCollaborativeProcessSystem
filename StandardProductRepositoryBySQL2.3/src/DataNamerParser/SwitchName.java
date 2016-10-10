package DataNamerParser;

public class SwitchName 
{
	String name = "";
//	public SwitchName(String name)
//	{
//		this.name = name;
//	}
	public String ifNOAA(String name)
	{
		if(name.contains("NOAA"))
		{
			name.substring(0, name.length()-2);
			
		}
			return name;
	}
	public String ifMOD02(String name)
	{
		if(name.contains("MOD021KM_L"))
		{
			name = "mod02_1km_l";
		}
		else if(name.contains("MOD021KM_H"))
		{
			name = "mod02_1km_h";
		}
		return name;
	}
	public Boolean ifBd(String name){
		if(name.equals("MCD12Q1")||name.equals("MCD43B1")||name.equals("MCD43B3")||name.equals("MCD43C2")){
			return true;
		}
		else{
			return false;
		}
		
	}
//	public String ifBd(String name){
//		if(name.equals("MCD12Q1")||name.equals("MCD43B1")||name.equals("MCD43B3")||name.equals("MCD43C2")){
//			return name;
//		}
//		else{
//			return null;
//		}
//		
//	}
	//选择GRID表
	/*
	public String getTypeName(String name)
	{
		switch (name) 
		{
		case "adr":
			this.name = "ADR";
			break;
		case "aod":
			this.name = "AOD";
			break;
		case "arvi":
			this.name = "ARVI";
			break;
		case "brdf":
			this.name = "BRDF";
			break;
		case "cli":
			this.name = "CLI";
			break;
		case "dlr":
			this.name = "DLR";
			break;
		case "dsr":
			this.name = "DSR";
			break;
		case "et":
			this.name = "ET";
			break;
		case "evi":
			this.name = "EVI";
			break;
		case "fpar":
			this.name = "FPAR";
			break;
		case "fvc":
			this.name = "FVC";
			break;
		case "hai":
			this.name = "HAI";
			break;
		case "isc":
			this.name = "ISC";
			break;
		case "ism":
			this.name = "ISM";
			break;
		case "lai":
			this.name = "LAI";
			break;
		case "lc":
			this.name = "LC";
			break;
		case "lhf":
			this.name = "LHF";
			break;
		case "lsa":
			this.name = "LSA";
			break;
		case "lse":
			this.name = "LSE";
			break;
		case "lst":
			this.name = "LST";
			break;
		case "ndvi":
			this.name = "NDVI";
			break;
		case "ndwi":
			this.name = "NDWI";
			break;
		case "npp":
			this.name = "NPP";
			break;
		case "par":
			this.name = "PAR";
			break;
		case "phn":
			this.name = "PHN";
			break;
		case "pre":
			this.name = "PRE";
			break;
		case "ref":
			this.name = "REF";
			break;
		case "sai":
			this.name = "SAI";
			break;
		case "sbi":
			this.name = "SBI";
			break;
		case "shf":
			this.name = "SHF";
			break;
		case "sid":
			this.name = "SID";
			break;
		case "sit":
			this.name = "SIT";
			break;
		case "sm":
			this.name = "SM";
			break;
		case "smi":
			this.name = "SMI";
			break;
		case "swe":
			this.name = "SWE";
			break;
		case "tcwv":
			this.name = "TCWV";
			break;
		default:
			break;
		}
		return this.name;
	}
	*/
}
