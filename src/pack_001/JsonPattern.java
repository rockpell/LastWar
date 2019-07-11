package pack_001;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

final class JsonPattern { // ������ ���� ������
	private Map<String, ArrayList<Point>> map = new HashMap<String, ArrayList<Point>>();
	
	JsonPattern(Object target){
		if(target instanceof JSONObject){
			JSONObject temp1 = ((JSONObject)target);
			Set<String> keys = temp1.keySet();
			
			for(String text : keys){
				map.put(text, parseToArray(temp1.get(text)) );
			}
			
		}
//		System.out.println(map);
	}
	
	ArrayList<Point> parseToArray(Object target){ // ������ ���� ��ǥ(x,y)�� ����� �����͵��� ArrayList�� ��ȯ
		ArrayList<Point> result = new ArrayList<Point>();
		
		if(target instanceof JSONArray){
			JSONArray temp1 = ((JSONArray)target);
			
			for(int i = 0; i< temp1.size(); i++){
				JSONObject tempObject = (JSONObject)temp1.get(i);
				
				int x = Integer.parseInt("" + tempObject.get("x"));
				int y = Integer.parseInt("" + tempObject.get("y"));
				result.add(new Point(x, y));
			}
		}
		
		return result;
	}
	
	public Map<String, ArrayList<Point>> getMap(){
		return map;
	}
}