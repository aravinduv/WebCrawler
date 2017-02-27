import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class Controller {
	
	private static String crawlStorageFolder = "/data/crawl";
	private static String name = "Aravind Utpat Vijendra";
	private static String ID = "4043156924";
	private static String newsSite1 = "cnn.com";
	
	public static void writeUrlsCSV() throws Exception {
        String fileName = crawlStorageFolder + "/urls.csv";
        FileWriter writer = new FileWriter(fileName);
        writer.append("URL,Type\n");
        writer.append(MyCrawler.getDiscoveredUrls());
        writer.flush();
        writer.close();
    }
	
	public static void writeVisitedUrlsCSV() throws Exception {
        String fileName = crawlStorageFolder + "/visit.csv";
        FileWriter writer = new FileWriter(fileName);
        writer.append("URL,Size,# of outlinks, Content-type\n");
        writer.append(MyCrawler.getVisitedUrls());
        writer.flush();
        writer.close();
    }
	
	public static void writeFetchUrlsCSV() throws Exception {
        String fileName = crawlStorageFolder + "/fetch.csv";
        FileWriter writer = new FileWriter(fileName);
        writer.append("URL,Status Code\n");
        writer.append(MyCrawler.getAttemptedUrls());
        writer.flush();
        writer.close();
    }
	
    public static void saveStatistics() throws Exception {
        String fileName = crawlStorageFolder + "/CrawlReport.txt";
        FileWriter writer = new FileWriter(fileName);

        // Personal Info
        writer.append("Name: " + name + "\n");
        writer.append("USC ID: " + ID + "\n");
        writer.append("News site crawled: " + newsSite1 + "\n");
        writer.append("\n");

        // Fetch Statistics
        writer.append("Fetch Statistics\n=====================\n");
        writer.append("# fetches attempted: " + MyCrawler.getAttemptedCount() + "\n");
        writer.append("# fetched succeeded: " + MyCrawler.getVisitedCount() + "\n");

        writer.append("# fetched aborted: " + MyCrawler.getAbortedUrlCount() + "\n");
        writer.append("# fetched failed: " + MyCrawler.getFailedUrlCount() + "\n");
        writer.append("\n");

        // Outgoing URLS
        HashSet<String> hashSet1 = new HashSet<String>();
        
        int uniqueUrls = 0;
        int uniqueWithin = 0;
        int uniqueOutside = 0;
        int outUrls = 0;
        writer.append("Outgoing URLs\n=====================\n");
        writer.append("Total URLS extracted: " + MyCrawler.getDiscoveredCount() + "\n");
        
        String[] data = MyCrawler.getDiscoveredUrls().split("\n");
        for (String names:data){
        	if(names != ""){
        		String url = names.split(",")[0];
        		String validStatus = names.split(",")[1];
        		
        		if(!hashSet1.contains(url)){
        			hashSet1.add(url);
        			uniqueUrls++;
        			
       				if(validStatus.equals("OK")){
       					uniqueWithin++;
       				}
       				
       				if(validStatus.equals("N_OK")){
      					uniqueOutside++;
       				}
        		}
        	}	
        }

        writer.append("# unique URLs extracted: " + uniqueUrls + "\n");
        writer.append("# unique URLs within newssite: " + uniqueWithin + "\n");
        writer.append("# unique URLs outside newssite: " + uniqueOutside + "\n");
        writer.append("\n");

        // Status Code
        writer.append("Status Codes\n=====================\n");
        
        HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
        
        String[] data1 = MyCrawler.getAttemptedUrls().split("\n");
        for (String names:data1){
        	if(names != ""){
        		if (hashMap.containsKey(names.split(",")[1])) {
        			hashMap.put(names.split(",")[1], hashMap.get(names.split(",")[1]) + 1);
        		} else {
        			hashMap.put(names.split(",")[1], 1);
        		}
        	}
        }
        

        writer.append("200 OK: " + MyCrawler.getVisitedCount() + "\n");
        
        if(hashMap.containsKey("301"))	
        	writer.append("301 Moved Permanently: " + hashMap.get("301") + "\n");
        else
        	writer.append("301 Moved Permanently: " + 0 + "\n");
        
        if(hashMap.containsKey("302"))	
        	writer.append("302 Moved Temporarily: " + hashMap.get("302") + "\n");
        else
        	writer.append("302 Moved Temporarily: " + 0 + "\n");
        
        if(hashMap.containsKey("401"))	
        	writer.append("401 Unauthorized: " + hashMap.get("401") + "\n");
        else
        	writer.append("401 Unauthorized: " + 0 + "\n");
        
        if(hashMap.containsKey("403"))	
        	writer.append("403 Forbidden: " + hashMap.get("403") + "\n");
        else
        	writer.append("403 Forbidden: " + 0 + "\n");
        
        if(hashMap.containsKey("404"))	
        	writer.append("404 Not Found: " + hashMap.get("404") + "\n");
        else
        	writer.append("404 Not Found: " + 0 + "\n");
        
        	
        // File Size
        writer.append("\n");
        writer.append("File Size\n=====================\n");
        int oneK = 0;
        int tenK = 0;
        int hundredK = 0;
        int oneM = 0;
        int other = 0;
        
        String[] data3 = MyCrawler.getVisitedUrls().split("\n");
        for (String names:data3){
        	if(names != ""){
        		if(Integer.parseInt(names.split(",")[1]) < 1024)
        			oneK++;
        		else if(Integer.parseInt(names.split(",")[1]) < 10240)
        			tenK++;
        		else if(Integer.parseInt(names.split(",")[1]) < 102400)
        			hundredK++;
        		else if(Integer.parseInt(names.split(",")[1]) < 1024 * 1024)
        			oneM++;
        		else
        			other++;
        	}
        }
        
        writer.append("< 1KB: " + oneK + "\n");
        writer.append("1KB ~ <10KB: " + tenK + "\n");
        writer.append("10KB ~ <100KB: " + hundredK + "\n");
        writer.append("100KB ~ <1MB: " + oneM + "\n");
        writer.append(">= 1MB: " + other + "\n");
        writer.append("\n");
        
        int hcount = 0;
        int gif = 0;
        int jpeg = 0;
        int png = 0;
        int pdf = 0;
        
        String[] data4 = MyCrawler.getVisitedUrls().split("\n");
        for (String names:data4){
        	if(names != ""){
        		if(names.split(",")[3].equals("text/html"))
        			hcount++;
        		else if(names.split(",")[3].equals("image/gif"))
        			gif++;
        		else if(names.split(",")[3].equals("image/jpeg"))
        			jpeg++;
        		else if(names.split(",")[3].equals("image/png"))
        			png++;
        		else if(names.split(",")[3].equals("application/pdf"))
        			pdf++;
        		else
        			continue;
        	}
        }
        writer.append("\n");
        writer.append("Content Types:\n=====================\n");
        writer.append("text/html: " + hcount + "\n");
        writer.append("image/gif: " + gif + "\n");
        writer.append("image/jpeg: " + jpeg + "\n");
        writer.append("image/png: " + png + "\n");
        writer.append("application/pdf: " + pdf + "\n");
        writer.append("\n");

        writer.flush();
        writer.close();
    }
	
	public static void main(String[] args) throws Exception {
		 
		 int numberOfCrawlers = 7;
		 CrawlConfig config = new CrawlConfig();
		 config.setCrawlStorageFolder(crawlStorageFolder);
		 config.setMaxDepthOfCrawling(16);
		 config.setMaxPagesToFetch(10000);
		 config.setPolitenessDelay(250);
		 config.setIncludeBinaryContentInCrawling(true);
		 config.setMaxDownloadSize(5*1024*1024);
		 /*
		 * Instantiate the controller for this crawl.
		 */
		 PageFetcher pageFetcher = new PageFetcher(config);
		 RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		 RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
		 CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
		 /*
		 * For each crawl, you need to add some seed urls. These are the first
		 * URLs that are fetched and then the crawler starts following links
		 * which are found in these pages
		 */
		 controller.addSeed("http://www.usatoday.com/");
		 //controller.addSeed("http://www.cnn.com/");
		 /*
		 * Start the crawl. This is a blocking operation, meaning that your code
		 * will reach the line after this only when crawling is finished.
		 */
		 controller.start(MyCrawler.class, numberOfCrawlers);
		 
		 writeFetchUrlsCSV();
		 writeVisitedUrlsCSV();
		 writeUrlsCSV();
		 
		 saveStatistics();
	}
}
