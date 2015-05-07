import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		double sup=Double.parseDouble(args[1]);
		double conf=Double.parseDouble(args[2]);
		//Read data file.
		String datafile=args[0];
		
		BufferedReader br;
		List<List<String>> records=new ArrayList<List<String>>();
		try {
			br = new BufferedReader(new FileReader(new File(datafile)));
			String s=br.readLine();
			String[] factors=s.trim().split(",");
			while((s=br.readLine())!=null){
				String[] list=s.trim().split(",");
				List<String> record=new ArrayList<String>();
				for(int i=1;i<9;i++){
					record.add(factors[i]+"="+list[i]);
				}
				records.add(record);
			}
			br.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//Run a priori and derive association rules.
		Counter counter=new Counter(conf, sup);
		counter.apriori(records);
		counter.showFreqItems();
		counter.deriveRules();
		counter.showRules();
	}
}
