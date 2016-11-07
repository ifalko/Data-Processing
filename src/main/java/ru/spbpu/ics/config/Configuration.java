package ru.spbpu.ics.config;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.bson.conversions.Bson;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class Configuration {
	
	private final Map<String, String> attr = new HashMap<String, String>(){
		private static final long serialVersionUID = 1L;
	{
		put("TECHNOLOGY","technology");
		put("NAME","name");
		put("PRIMARY","primary");
		put("DATE_FROM","date_from");
		put("DATE_TO","date_to");
	}};
	
	private final static String INPATH = "src/main/java/ru/spbpu/ics/config/confs/";
	private final static String OUTPATH = "src/main/java/ru/spbpu/ics/output/";
	private Set<String> keywords;
	private static String primary;
	private static String dateFrom;
	private static String dateTo;
	private static Map<Config, Configuration> configs;

    private Config config;
    private String inFile = "config.xml";

    static {
        configs = new HashMap<>();
        configs.put(Config.TASK_1, new Configuration(Config.TASK_1));
    }

    private Configuration(Config config) {
        this.config = config;
        init(config);
    }

    public static Configuration getInstance(Config config){
    	return configs.get(config);
    }
	
	private void init(Config config) {
		Set<String> keys = new HashSet<String>();
		try {	
            File inputFile = new File(INPATH + this.getInFile());
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            
            String primary = doc.getElementsByTagName(attr.get("PRIMARY")).item(0).getTextContent();
            if(primary!=null && primary.length()>0){
            	setPrimary(primary);
            }
            
            String dateFrom = doc.getElementsByTagName(attr.get("DATE_FROM")).item(0).getTextContent();
            if(dateFrom!=null && dateFrom.length()>0){
            	this.setDateFrom(dateFrom);
            }
            String dateTo = doc.getElementsByTagName(attr.get("DATE_TO")).item(0).getTextContent();
            if(dateTo!=null && dateTo.length()>0){
            	this.setDateTo(dateTo);
            }
            
            
            NodeList nList = doc.getElementsByTagName(attr.get("TECHNOLOGY"));
            for (int temp = 0; temp < nList.getLength(); temp++) {
               Node nNode = nList.item(temp);
               Element eElement = (Element) nNode;
               keys.add(eElement.getElementsByTagName(attr.get("NAME")).item(0).getTextContent());
            }
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		this.keywords = keys;
    }

	public Set<String> getKeywords() {
		return keywords;
	}
	
	public static String getOutPath(){
		return OUTPATH;
	}
	
	public Config getConfig() {
		return config;
	}

	public String getInFile() {
		return inFile;
	}

	public static String getPrimary() {
		return primary;
	}

	public static void setPrimary(String pmr) {
		primary = pmr;
	}

	public static String getDateFrom() {
		return dateFrom;
	}

	public void setDateFrom(String dfrom) {
		dateFrom = dfrom;
	}

	public static String getDateTo() {
		return dateTo;
	}

	public void setDateTo(String dto) {
		dateTo = dto;
	}

	public static FindIterable<org.bson.Document> getFindIterable(MongoCollection<org.bson.Document> collection){

		FindIterable<org.bson.Document> find;
		if(Configuration.getDateFrom() != null && Configuration.getDateTo() !=null ){
			Bson filter1 = Filters.gte("question_creation_date",Configuration.getDateFrom());
			Bson filter2 = Filters.lte("question_creation_date",Configuration.getDateTo());			
			find = collection.find(Filters.and(filter1, filter2));
		}else {
			find = collection.find();
		}
		return find;
	}
}
