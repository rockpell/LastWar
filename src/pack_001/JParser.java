package pack_001;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

final class JParser { // 레이저 패턴과 패턴 순서를 json파일로 부터 읽어오는 클래스
	
	private Map<String, ArrayList<String>> sequenceData;
	private Map<String, ArrayList<String>> sequenceData2;
	private Map<String, JsonPattern> patternData;
	
	private int patternCount = 0;
	
	JParser(){
		sequenceData = new HashMap<String, ArrayList<String>>();
		sequenceData2 = new HashMap<String, ArrayList<String>>();
		patternData = new HashMap<String, JsonPattern>();
		
		File abc = new File("resource/last_war.json");
		
		JSONParser jsonParser = new JSONParser();
		try{
			
			File bhc = new File(abc.getAbsolutePath());
			
			JSONObject jsonObject = (JSONObject)jsonParser.parse(new FileReader(bhc));
			
			Set<String> keys = jsonObject.keySet();
			
			for(String name : keys){
				JSONObject temp = (JSONObject)jsonObject.get(name);
				Set<String> keys2 = temp.keySet();
				
				if(name.equals("story")){
					addSeqenceData(sequenceData, temp);
					calcPatternCount();
				} else if(name.equals("never")){
					addSeqenceData(sequenceData2, temp);
				} else if(name.contains("pattern")){
					patternData.put(name, new JsonPattern(temp));
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private void addSeqenceData(Map<String, ArrayList<String>> map, Object target){
		if(target instanceof JSONObject){
			JSONObject tempObject = (JSONObject)target;
			Set<String> keys = tempObject.keySet();
			
			for(String text : keys){
				map.put(text, seqenceDataToArray(tempObject.get(text)) );
			}
		}
	}
	
	private ArrayList<String> seqenceDataToArray(Object target){
		ArrayList<String> result = new ArrayList<String>();
		
		if(target instanceof JSONArray){
			JSONArray tempArray = (JSONArray)target;
			for(int i = 0; i < tempArray.size(); i++){
				result.add((String) tempArray.get(i));
			}
		}
		
		return result;
	}
	
	private void calcPatternCount(){
		int temp = 0;
		
		for(int i = 1; i <= sequenceData.size(); i++){
			temp += sequenceData.get(String.valueOf(i)).size();
		}
		patternCount = temp;
	}
	
	public Map<String, ArrayList<String>> getSequenceData(){
		return sequenceData;
	}
	
	public Map<String, JsonPattern> getPatternData(){
		return patternData;
	}
	
	public Map<String, ArrayList<String>> getSequenceData2(){
		return sequenceData2;
	}
	
	public int getPatterns(){
		return patternCount;
	}
}