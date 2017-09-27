package map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

import bean.Checker;
import bean.ResultObject;
import bean.StationObject;
import bean.MovableStation;

public class SubwayMap extends HashMap<String, StationObject> {
	private static final long serialVersionUID = -214379867263068591L;

	public int MAXCHECKABLE = 76;
	private Checker foundChecker = null;
	private HashSet<String> exceptTransferStation = new HashSet<String>();
	private HashSet<String> TransferStation = new HashSet<String>();
	private HashSet<String> exceptForMidStation = new HashSet<String>();
	private HashSet<String> foundCheckerList = new HashSet<String>();
	private HashSet<String> midStationCandidate = new HashSet<String>();
	private boolean findMidStationMode = false;
	private int maxCost = 0; // ���� �̵� �ְ� �ڽ�Ʈ

	public ResultObject searchMidStation(ArrayList<String> list) {
		ResultObject ro;

		if (list.size() < 2)
			return null;

		else if (list.size() == 2) {// �Էµ� ���� 2���� ��� �ܼ��ϰ� ��� ������
									// ����
			ro = new ResultObject();
			Checker result = searchAtoB(list.get(0), list.get(1), new Checker(
					list.get(0)));
			if (result != null) {
				ro.setMidStationName(result.getLastStationList().get(
						result.getLastStationList().size() / 2));
				ro.getDetails().add(result);
				ro.setMaxCost(result.getCost());
				return ro;
			} else
				return null;

		} else {
			ro = new ResultObject();
			// �߰��� �˻����� ����ϴ� ȯ�¿� ���()�� ����
			Checker c = searchAtoB(list.get(0), list.get(1),
					new Checker(list.get(0)));
			if (c == null)
				return null;
			else {
				exceptForMidStation.addAll(exceptTransferStation);
				foundCheckerList.addAll(foundChecker.getLastStationList());
				maxCost = c.getCost();

				for (int i = 0; i < list.size() - 1; i++) {
					for (int j = i + 1; j < list.size(); j++) {
						if (i == 0 && j == 1)
							continue; // ������ �Ѱ� ����

						exceptTransferStation.clear();
						TransferStation.clear();
						foundChecker = null;
						c = searchAtoB(list.get(i), list.get(j), new Checker(
								list.get(0)));
						if (c == null)
							return null;// 거리초과
						exceptForMidStation.addAll(exceptTransferStation);
						foundCheckerList.addAll(foundChecker
								.getLastStationList());
						if (maxCost < c.getCost()) {
							maxCost = c.getCost();
						}
					}
				}
				exceptForMidStation.removeAll(foundCheckerList);

				// System.out.println("�˻����� ���������� ���� - MAXCOST : " +
				// maxCost);
				// System.out.println(exceptForMidStation + "\n");

				// �ĺ��� �˻�����
				findCandidate(list.get(0), list, midStationCandidate,
						new Checker(list.get(0)));

				// System.out.println("�߰��� �ĺ���");
				// System.out.println(midStationCandidate +"\n");

				// �ĺ����� �߰��� ã�Ƴ�
				findMidStationMode = true; // �߰��� �˻� ���. max cost�� ���ܿ���
											// �̿��� �� Ÿ��Ʈ�ϰ� �˻�
				findMidStationName(ro, list, midStationCandidate);
				ro.setMaxCost(maxCost);
				findMidStationMode = false;

				return ro;
			}
		}
	}

	public boolean resourceClear() {
		try {
			foundChecker = null;
			foundCheckerList.clear();
			exceptTransferStation.clear();
			exceptForMidStation.clear();
			midStationCandidate.clear();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private ResultObject findMidStationName(ResultObject ro,
			ArrayList<String> list, HashSet<String> midStationCandidate) {

		if (midStationCandidate.size() == 0 || list.size() == 0)
			return null;

		else {
			ArrayList<Checker> temp = new ArrayList<Checker>();
			int innerSum, Sum, innerMax, innerMin, SumCost;
			String midStation = "", iteMidStation;

			Iterator<String> ite = midStationCandidate.iterator();
			Sum = SumCost = Integer.MAX_VALUE;

			// ���������� �ĺ��� �� '�߰���' �˻�����
			while (ite.hasNext()) {
				iteMidStation = ite.next();
				if (midStation.equals(""))
					midStation = iteMidStation; // �ʱⰪ����
				innerSum = 0;
				innerMax = innerMin = -1;
				temp.clear();

				for (int i = 0; i < list.size(); i++) {
					exceptTransferStation.clear();
					TransferStation.clear();
					foundChecker = null;
					Checker c = searchAtoB(list.get(i), iteMidStation,
							new Checker(list.get(i)));

					if (c != null)
						temp.add(c);

					if (c == null) {// �߰���������ΰ�� null�� ���ֳ��� null�ΰ��
									// �˻����� �����ؾ���. �����ϰ� �� ���Ԥ�
						innerSum += 10000;
						break;
					}

					if (innerMax == -1 || innerMax < c.getCost())
						innerMax = c.getCost();

					if (innerMin == -1 || innerMin > c.getCost())
						innerMin = c.getCost();

					innerSum += c.getCost();

					if (innerSum > SumCost * 1.2)
						break; // �ʰ��ع������ �� ��� �� ����������.
				}

				if (temp.size() == list.size() && innerSum <= SumCost * 1.2 ){
					if (innerMax - innerMin < Sum) {
						SumCost = innerSum;
						Sum = innerMax - innerMin;
						midStation = iteMidStation;
						ro.getDetails().clear();
						ro.getDetails().addAll(temp);
					}else if(innerMax - innerMin == Sum && innerSum < SumCost){
						SumCost = innerSum;
						Sum = innerMax - innerMin;
						midStation = iteMidStation;
						ro.getDetails().clear();
						ro.getDetails().addAll(temp);
					}
				}
			}
			ro.setMidStationName(midStation);

			return ro;
		}
	}

	private boolean findCandidate(String start, ArrayList<String> list,
			HashSet<String> midStationCandidate, Checker c) {
		boolean add = false;
		if (c.getCost() > maxCost)
			return false;
		if (exceptForMidStation.contains(start))
			return false;

		StationObject startStation = this.get(start);
		for (MovableStation m : startStation.getMovableStations()) {
			boolean indexMode = false;

			for (String s : list) {// Movable�� ����Ʈ�� �����ϸ� ����Ʈ���� �ĺ���
									// ����
				if (m.getStationList().contains(s)) {
					for (int i = 0; i < m.getStationList().size(); i++) {
						midStationCandidate.add(m.getStationList().get(i));
						if (m.getStationList().get(i).equals(s))
							break;
					}
					add = true;
					indexMode = true;
					break;
				}
			}

			if (indexMode)
				continue;

			if (!c.getLastStationList().contains(m.getStationName())) {
				Checker cloneC = c.clone();
				cloneC.addListtoLastStationList(m.getStationList());
				cloneC.setCost(cloneC.getCost() + m.getCost());

				if (findCandidate(m.getStationName(), list,
						midStationCandidate, cloneC)) {
					add = true;
					midStationCandidate.addAll(m.getStationList());
				}
			}
		}
		return add;
	}

	public Checker searchAtoB(String now, String target, Checker c) {
		boolean flag = false; // ���� �� ������ ���� Ȯ�ο� �÷���

		if (findMidStationMode) { // �߰��� ������� ��� �˻������� �� Ÿ��Ʈ����
			if (c.getCost() > maxCost || exceptForMidStation.contains(now))
				return null;
		}

		if (now.equals(target))
			return c;

		else if (c.getLastStationList().size() > MAXCHECKABLE) {
			return new Checker("");// �Ÿ��־ �����Ÿ� ��Ÿ��� �ѱ�

		}

		else if (foundChecker != null) {
			if (foundChecker.getCost() < c.getCost())
				return null;
		}
		StationObject nowStation = this.get(now);
		for (MovableStation m : nowStation.getMovableStations()) {

			if (c.getLastStationList().size() > 1
					&& c.getLastStationList().contains(
							m.getStationList().get(0))) {
				continue;
			}

			Checker cloneC = c.clone();
			int index = m.getStationList().indexOf(target);
			if (index != -1) {
				int addCost = 3;
				for (int i = 0; i < index; i++) {
					cloneC.getLastStationList().add(m.getStationList().get(i));
					addCost += 3;
					if (this.get(m.getStationList().get(i)).getLineNum() == 'T') // ȯ�¿��̸�
																					// 2��
																					// ����
						addCost += 2;
				}
				cloneC.getLastStationList().add(target);
				cloneC.setCost(cloneC.getCost() + addCost);

				Checker k = searchAtoB(m.getStationList().get(index), target,
						cloneC);
				if (k != null) {
					flag = true;
					TransferStation.add(now);
					if (k.getCost() != 0
							&& (foundChecker == null || foundChecker.getCost() > k
									.getCost())) {
						foundChecker = k;
					}
				}

			} else if (!exceptTransferStation.contains(m.getStationName())) {

				cloneC.setCost(cloneC.getCost() + m.getCost());
				cloneC.addListtoLastStationList(m.getStationList());
				Checker result = searchAtoB(m.getStationName(), target, cloneC);

				if (result != null) {
					flag = true;// �Ÿ��� �� ��쿣 ���ܸ�Ͽ� ���� ����
					TransferStation.add(now);
					if (result.getCost() != 0
							&& foundChecker.getCost() > result.getCost())
						foundChecker = result;
				}
			}
		}

		if (!flag && !TransferStation.contains(now)) // ���±��� ���°��
			exceptTransferStation.add(now);

		return foundChecker;
	}

	public boolean transferStationMovableRefresh(HashSet<String> transferList) {// ȯ�¿�
																				// movable
																				// ���ſ�

		try {

			for (Iterator<String> it = transferList.iterator(); it.hasNext();) {
				StationObject s = this.get(it.next());
				ArrayList<MovableStation> newMovableList = new ArrayList<MovableStation>();

				int movableSize = s.getMovableStations().size();
				for (int i = 0; i < movableSize; i++) {
					int newMovableCost = 3;
					ArrayList<String> newMovable = getNewMovable(
							this.get(s.getMovableStations().get(i)
									.getStationName()), s.getStationName(),
							new ArrayList<String>());

					for (int j = 0; j < newMovable.size(); j++) { // ���ѵ� ����
																	// �ڽ�Ʈ ���
						if (this.get(newMovable.get(j)).getLineNum() == 'T')
							newMovableCost += 5;
						else
							newMovableCost += 3;
					}

					MovableStation newAddMovable = new MovableStation(
							newMovable, newMovableCost);
					newMovableList.add(newAddMovable);
				}
				s.getMovableStations().clear();
				s.getMovableStations().addAll(newMovableList);
			}

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	private ArrayList<String> getNewMovable(StationObject nowStation,
			String prev, ArrayList<String> n) {

		n.add(nowStation.getStationName());

		if (nowStation.getLineNum() == 'T' || nowStation.getLineNum() == 'P')
			return n;

		else {
			for (MovableStation m : nowStation.getMovableStations()) {
				if (!m.getStationList().get(0).equals(prev))
					getNewMovable(this.get(m.getStationName()),
							nowStation.getStationName(), n);
			}
		}

		return n;
	}

}
