package parser;

import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import bean.StationObject;
import map.SubwayMap;

public class LocationParser {
	static DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
	static DocumentBuilder documentBuilder;
	static Document xml = null;
	static XPath xpath = XPathFactory.newInstance().newXPath();

	static String key = "7171725073696e7335316a4449756f";
	static String Url;
	static String rowPath = "SearchSTNInfoByIDService/row";
	static String[] stationData;
	
	public static String[] doParse(String stCode) {
		Url = "http://openapi.seoul.go.kr:8088/" + key + "/xml/SearchSTNInfoByIDService/1/9/" + stCode;
		
		try {
			if (documentBuilder == null)
				documentBuilder = documentBuilderFactory.newDocumentBuilder();
			xml = documentBuilder.parse(Url);

			NodeList cols = (NodeList) xpath.evaluate(rowPath, xml, XPathConstants.NODESET);

			for (int idx = 0; idx < cols.getLength(); idx++)
				stationData = cols.item(idx).getTextContent().split("\n");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stationData; // Data가 1개뿐이라 그대로 return
	}
}
