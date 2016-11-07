package ru.spbpu.ics.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.Document;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import ru.spbpu.ics.config.Config;
import ru.spbpu.ics.config.Configuration;
import ru.spbpu.ics.generator.GeneratorHtml_1;

public class AnalyzerTask_5 {
	
	@SuppressWarnings({ "unchecked", "resource" })
	public static void main(String[] args) {
		
		MongoCollection<Document> collection = new MongoClient().getDatabase("data").getCollection("stackoverdb");
		Map<String, Integer> map = new HashMap<>();
		Set<String> keywords = Configuration.getInstance(Config.TASK_1).getKeywords();
		
		Configuration.getFindIterable(collection).forEach(new Block<Document>(){
			@Override
			public void apply(Document doc) {
				List<String> tags = (List<String>) doc.get("tags");
				String primary = Configuration.getPrimary();
				if(tags != null){
					if(tags.contains(Configuration.getPrimary())){
						for(String tag: tags){
							if (!keywords.contains(tag) && map.containsKey(tag)) { 
								map.compute(tag, (k, v)-> v == null ? 1 : v + 1); 
							}else if(!keywords.contains(tag) && !map.containsKey(tag) && !tag.equals(primary)){
								map.put(tag, 1);
							}
						}
					}
				}
			}
		});
		
		Set<Entry<String, Integer>> set = map.entrySet();
		List<Entry<String, Integer>> list = new ArrayList<Entry<String, Integer>>(
                set);
        Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
            public int compare(Map.Entry<String, Integer> o1,
                    Map.Entry<String, Integer> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        
        Map<String, Integer> filteredmap = new HashMap<>();
        int k = 1;        
        for (Entry<String, Integer> entry : list) {
        	filteredmap.put(entry.getKey(), entry.getValue());
            if(k == 15){ break; }
            ++k;
        }

		CharSequence html = new GeneratorHtml_1(filteredmap).doGenerate();
        String file = Configuration.getOutPath() + "task_5.html";
        try(PrintWriter out = new PrintWriter(new File(file).getAbsoluteFile())){ out.print(html); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
	}
}
