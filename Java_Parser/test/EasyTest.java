package test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

import bean.Checker;
import bean.MovableStation;
import bean.ResultObject;
import bean.StationObject;
import map.SubwayMap;
import parser.LineParser;
import parser.LineParser1;
import parser.LineParser2;
import parser.LineParser5;
import parser.LineParser6;
import parser.LineParserK;

public class EasyTest {
	public static void main(String[] args) {
		
		
		HashSet<String> transferList = new HashSet<String>();
		SubwayMap n = new SubwayMap();
		
		LineParser lp = new LineParser(transferList);
		LineParser1 lp1 = new LineParser1(transferList);
		LineParser2 lp2 = new LineParser2(transferList);
		LineParser5 lp5 = new LineParser5(transferList);
		LineParser6 lp6 = new LineParser6(transferList);
		LineParserK lpK = new LineParserK(transferList); // 경의중앙선
		
		lp1.doParse(n, '1'); // 1호선
		lp2.doParse(n, '2'); // 2호선
		lp5.doParse(n, '5'); // 5호선
		lp6.doParse(n, '6'); // 6호선
		lpK.doParse(n, 'K'); // 경의중앙

		lp.doParse(n, '3'); // 3호선
		lp.doParse(n, '4'); // 4호선
		lp.doParse(n, '7'); // 7호선
		lp.doParse(n, '8'); // 8호선
		lp.doParse(n, '9'); // 9호선
		
		lp.doParse(n, 'I'); // 인천1호선
		lp.doParse(n, 'Y'); // 수인선 SU(원래코드)
		lp.doParse(n, 'A'); // 공항철도
		lp.doParse(n, 'B'); // 분당선
		lp.doParse(n, 'S'); // 신분당선
		lp.doParse(n, 'E'); // 에버라인
		lp.doParse(n, 'U'); // 의정부선

		n.transferStationMovableRefresh(transferList);//환승역의 데이터 갱신

		/*
		//getdata
		SubwayMap n = null;
        try {
    		FileInputStream fis = new FileInputStream("stationMap.data");
            ObjectInputStream ois = new ObjectInputStream(fis);
            n = (SubwayMap) ois.readObject();
			ois.close();
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		*/
		
		long start = System.currentTimeMillis();
	
		/*
		try {
			
			try{
				FileOutputStream fos = new FileOutputStream("stationMap.data");
		        ObjectOutputStream oos = new ObjectOutputStream(fos);
		        oos.writeObject(n);
		 
		        fos.close();
				}catch(Exception e){
					e.printStackTrace();
				}
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			
		}
	*/
		n.MAXCHECKABLE = 50;
			ArrayList<String> a = new ArrayList<String>();
			a.add("대림");
			a.add("총신대입구(이수)");
			a.add("선바위");
			ResultObject ro = n.searchMidStation(a);
			
			System.out.println(ro.getMidStationName());
			
		long end = System.currentTimeMillis();
		System.out.println("수행 시간 : " + (end - start) / 1000.0);
		System.out.println("대치 위도: " + n.get("대치").getLatitude());
		System.out.println("대치 경도: " + n.get("대치").getLongitude());
		
		System.out.println("영등포 위도: " + n.get("영등포").getLatitude());
		System.out.println("영등포 경도: " + n.get("영등포").getLongitude());
	}
		
}
