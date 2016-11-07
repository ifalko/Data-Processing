package ru.spbpu.ics.analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.mongodb.Block;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;

import ru.spbpu.ics.config.Config;
import ru.spbpu.ics.config.Configuration;
import ru.spbpu.ics.generator.GeneratorHtml_1;

public final class AnalyzerTask_1 {
	@SuppressWarnings({ "resource", "unchecked" })
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {

		MongoCollection<Document> collection = new MongoClient().getDatabase("data").getCollection("stackoverdb");
		Map<String, Integer> map = new HashMap<>();
		Configuration.getInstance(Config.TASK_1).getKeywords().forEach((key) -> map.put(key, 0));

		Configuration.getFindIterable(collection).forEach(new Block<Document>() {
			@Override
			public void apply(Document doc) {
				if (doc.get("tags") != null)
					for (String tag : (List<String>) doc.get("tags")) {
						if (map.containsKey(tag)) {
							map.compute(tag, (k, v) -> v == null ? 1 : v + 1);
						}
					}
			}
		});
		
		CharSequence html = new GeneratorHtml_1(map).doGenerate();
        String file = Configuration.getOutPath() + "task_1.html";
        try(PrintWriter out = new PrintWriter(new File(file).getAbsoluteFile())){ out.print(html); }
        catch (FileNotFoundException e) { e.printStackTrace(); }
	}
}
