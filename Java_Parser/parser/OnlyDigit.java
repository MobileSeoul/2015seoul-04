package parser;

import java.util.ArrayList;
import bean.StationObject;

public class OnlyDigit extends ArrayList<StationObject> {
	private static final long serialVersionUID = 1230707252290826608L;

	public static String getOnlyDigit(String str) {
		if (str != null && str.length() != 0) {
			String tmpStr = "";
			char[] chArr = str.toCharArray();
			int cntcharrTelno = chArr.length;

			for (int i = 0; i < cntcharrTelno; i++) {
				if (Character.isDigit((chArr[i]))) { // Convert any character into numeric
					tmpStr += chArr[i];
				}
			}
			return tmpStr;
		} else
			return "";
	}
}
