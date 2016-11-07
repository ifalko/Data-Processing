package ru.spbpu.ics.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bson.Document;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import ru.spbpu.ics.config.Config;
import ru.spbpu.ics.config.Configuration;
import ru.spbpu.ics.generator.GeneratorHtml_1;

public final class AnalyzerTask_3 {
	
	static class ResponseDelay{
		private int count = 0; 
		private int days = 0;
		public void addDelayResponse(int delay){
			++count;
			days += delay;
		}
		public int getDelayDays() { return Math.round((float)days/count); }
	}
	
	@SuppressWarnings({ "resource", "unchecked" })
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		
		MongoCollection<Document> collection = new MongoClient().getDatabase("data").getCollection("stackoverdb");
		Map<String, ResponseDelay> map = new HashMap<>();
		Configuration.getInstance(Config.TASK_1).getKeywords().forEach((key)-> map.put(key, new ResponseDelay()));
		
		Configuration.getFindIterable(collection).forEach(new Block<Document>(){
			@Override
			public void apply(Document doc) {
				List<String> tags = (List<String>) doc.get("tags");
				if(tags != null && doc.get("question_creation_date") != null && doc.get("answer_creation_date") != null){
					for(String tag: tags){
						if(map.containsKey(tag)){
							DateTime questionDate = new DateTime(doc.get("question_creation_date"));
							DateTime answerDate = new DateTime(doc.get("answer_creation_date"));
							int days = Days.daysBetween(questionDate.toLocalDate(), answerDate.toLocalDate()).getDays();
							map.get(tag).addDelayResponse(days); 
						}
					}
				}
			}
		});
		
		Map<String, Integer> convertMap = new HashMap<>();
		
		for(Entry<String, ResponseDelay> e: map.entrySet()){
			convertMap.put(e.getKey(), e.getValue().getDelayDays());
		}
		
        CharSequence html = new GeneratorHtml_1(convertMap).doGenerate();
        String file = Configuration.getOutPath() + "task_3.html";
        try(PrintWriter out = new PrintWriter(new File(file).getAbsoluteFile())){ out.print(html); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
	}
}
