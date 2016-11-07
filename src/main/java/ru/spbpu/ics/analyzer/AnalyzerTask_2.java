package ru.spbpu.ics.analyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.bson.Document;
import org.joda.time.DateTime;

import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import ru.spbpu.ics.config.Configuration;
import ru.spbpu.ics.generator.GeneratorHtml_2;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public final class AnalyzerTask_2 {

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		
		MongoCollection<Document> collection = new MongoClient().getDatabase("data").getCollection("stackoverdb");
		Map<Integer, Integer> owner = new HashMap<>();
		Map<Integer, Integer> answerer = new HashMap<>();
		final int now = new DateTime().getYear();
		
		Configuration.getFindIterable(collection).forEach(new Block<Document>(){
			@Override
			public void apply(Document doc) {
				int dateQ = new DateTime(doc.get("question_creation_date")).getYear();
				int dateA = new DateTime(doc.get("answer_creation_date")).getYear();
				int owner_age = doc.getInteger("owner_age") == null ? 0 : doc.getInteger("owner_age");
				int answerer_age = doc.getInteger("answerer_age") == null ? 0 : doc.getInteger("answerer_age");
				owner_age = owner_age - (now - dateQ);
				answerer_age = answerer_age - (now - dateA);
				if( 17 < owner_age && owner_age < 66)
					owner.compute(owner_age, (k, v)-> v == null ? 1 : v + 1);
				if( 17 < answerer_age && answerer_age < 66)
					answerer.compute(answerer_age, (k, v)-> v == null ? 1 : v + 1);
        	}
				
		});
		
		int count_owner_age = 0;
		for(Integer i: owner.keySet()){ count_owner_age += owner.get(i); }
		SortedMap<String, Double> own = new TreeMap<>();
		for(Integer i: owner.keySet()){ own.put(i.toString(), ((100. * owner.get(i)) / count_owner_age) ); }
		
		int count_answerer_age = 0;
		for(Integer i: answerer.keySet()){ count_answerer_age += answerer.get(i); }
		SortedMap<String, Double> ans = new TreeMap<>();
		for(Integer i: answerer.keySet()){ ans.put(i.toString(), ((100. * answerer.get(i)) / count_answerer_age) ); }
		
        CharSequence html = new GeneratorHtml_2(own, ans).doGenerate();
        String file = Configuration.getOutPath() + "task_2.html";
        try(PrintWriter out = new PrintWriter(new File(file).getAbsoluteFile())){ out.print(html); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
	}
}
