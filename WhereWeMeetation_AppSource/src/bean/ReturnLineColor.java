package bean;

public class ReturnLineColor {
	public static String get(String s){
		if(s.equals("1"))
			return "#263c96";
		else if(s.equals("K"))
			return "#26a97f";		
		else if(s.equals("U"))
			return "#ff8e00";		
		else if(s.equals("E"))
			return "#77c371";			
		else if(s.equals("S"))
			return "#a71e31";			
		else if(s.equals("Y"))
			return "#edb217";			
		else if(s.equals("B"))
			return "#ffce33";			
		else if(s.equals("A"))
			return "#71b8e5";			
		else if(s.equals("9"))
			return "#cea43a";			
		else if(s.equals("8"))
			return "#e51e6e";			
		else if(s.equals("7"))
			return "#697215";			
		else if(s.equals("6"))
			return "#b5500b";			
		else if(s.equals("5"))
			return "#8936e0";			
		else if(s.equals("4"))
			return "#2c9ede";			
		else if(s.equals("3"))
			return "#ff7300";			
		else if(s.equals("2"))
			return "#3cb44a";
		else
			return null;
	}
}
