import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Counter {
	List<Map<List<String>, Integer>> freqmaps=new ArrayList<Map<List<String>, Integer>>();
	List<Rule> rules=new ArrayList<Rule>();
	int len;
	double conf;
	double sup;
	
	public Counter(double c, double s){
		conf=c;
		sup=s;
	}
	
	//Run a priori algorithm to the records.
	public void apriori(List<List<String>> records){
		Map<List<String>, Integer> map=firstfilter(records);
		int k=2;
		while(true){
			Map<List<String>, Integer> keys=getNewKeys(map, k);
			freqmaps.add(map);
			map=filter(keys, records);
			if(map.size()==0) break;
			k++;
		}
	}
	
	//First run to count each item.
	public Map<List<String>, Integer> firstfilter(List<List<String>> records){
		Map<List<String>, Integer> map=new HashMap<List<String>, Integer>();
		len=records.size();
		
		for(List<String> record : records){
			for(String factor : record){
				List<String> list=new ArrayList<String>();
				list.add(factor);
				if(map.containsKey(list)){
					map.put(list, map.get(list)+1);
				}
				else{
					map.put(list, 1);
				}
			}
		}
		
		//Remove factors below support.
		int minsup=(int)(len*sup);
		List<List<String>> removelist=new ArrayList<List<String>>();
		for(Map.Entry<List<String>, Integer> entry : map.entrySet()){
			if(entry.getValue()<minsup){
				removelist.add(entry.getKey());
			}
		}
		for(List<String> removeitem : removelist){
			map.remove(removeitem);
		}
		return map;
	}
	
	//Count and filter item combinations.
	public Map<List<String>, Integer> filter(Map<List<String>, Integer> keys, List<List<String>> records){
		Map<List<String>, Integer> map=new HashMap<List<String>, Integer>();
		int len=records.size();
		
		for(List<String> record : records){
			for(List<String> key : keys.keySet()){
				boolean flag=true;
				for(String s : key){
					if(!record.contains(s)){
						flag=false;
						break;
					}
				}
				if(flag){
					if(map.containsKey(key)){
						map.put(key, map.get(key)+1);
					}
					else{
						map.put(key, 1);
					}
				}
			}
		}
		
		//Remove factors below support.
		int minsup=(int)(len*sup);
		List<List<String>> removelist=new ArrayList<List<String>>();
		for(Map.Entry<List<String>, Integer> entry : map.entrySet()){
			if(entry.getValue()<minsup){
				removelist.add(entry.getKey());
			}
		}
		for(List<String> removeitem : removelist){
			map.remove(removeitem);
		}
		return map;
	}
	
	//Get possible item combinations from previous result.
	public Map<List<String>, Integer> getNewKeys(Map<List<String>, Integer> map, int k){
		Map<List<String>, Integer> newkeys=new HashMap<List<String>, Integer>();
		for(List<String> key1:map.keySet()){
			for(List<String> key2:map.keySet()){
				List<String> newkey=new ArrayList<String>();
				for(String s:key1){
					newkey.add(s);
				}
				for(String s:key2){
					if(!newkey.contains(s)){
						newkey.add(s);
					}
				}
				if(newkey.size()==k){
					Collections.sort(newkey);
					if(!newkeys.containsKey(newkey)){
						newkeys.put(newkey, 0);
					}
				}
			}
		}
		return newkeys;
	}
	
	public void showFreqItems(){
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter(new File("output.txt")));
			bw.write("==Frequent itemsets (min_sup="+sup*100+"%)\n");
			for(int i=0;i<freqmaps.size();i++){
				String s=(i+1)+" item combinations:\n";
				bw.write(s+"\n");
				for(Map.Entry<List<String>, Integer> entry : freqmaps.get(i).entrySet()){
					bw.write("["+entry.getKey().get(0));
					for(int j=1;j<entry.getKey().size();j++){
						bw.write(", "+entry.getKey().get(j));
					}
					bw.write("], "+entry.getValue()*100.0/len+"%\n");
				}
				bw.write("Size: "+freqmaps.get(i).size()+"\n\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	class Rule{
		List<String> left;
		String right;
		double confidence;
		double support;
		Rule(List<String> l, String r, double c, double s){
			left=l;
			right=r;
			confidence=c;
			support=s;
		}
	}
	
	//Derive association rules.
	public void deriveRules(){
		int k=freqmaps.size();
		for(int i=0;i<k-1;i++){
			Map<List<String>, Integer> map0=freqmaps.get(i);
			Map<List<String>, Integer> map1=freqmaps.get(i+1);
			for(Map.Entry<List<String>, Integer> entry : map1.entrySet()){
				int times=entry.getValue();
				for(int j=0;j<entry.getKey().size();j++){
					List<String> list=new ArrayList<String>();
					for(int jj=0;jj<entry.getKey().size();jj++){
						if(j==jj) continue;
						list.add(entry.getKey().get(jj));
					}
					if(map0.containsKey(list)){
						double c=1.0*times/map0.get(list);
						if(c>=conf){
							Rule rule=new Rule(list, entry.getKey().get(j), c, 1.0*times/len);
							int l=0;
							for(;l<rules.size();l++){
								if(rule.confidence>rules.get(l).confidence) break;
							}
							rules.add(l, rule);
						}
					}
				}
			}
		}
	}
	
	public void showRules(){
		try {
			BufferedWriter bw=new BufferedWriter(new FileWriter("output.txt", true));
			bw.write("==High-confidence association rules (min_conf="+conf*100+"%)\n");
			for(Rule rule : rules){
				bw.write("["+rule.left.get(0));
				for(int i=1;i<rule.left.size();i++){
					bw.write(","+rule.left.get(i));
				}
				bw.write("] => ["+rule.right+"] (Conf:"+(rule.confidence*100)+"%, Supp: "+rule.support*100+"%)\n");
			}
			bw.write("Number of rules: "+rules.size()+"\n");
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
