import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;
import org.apache.http.Header;
import com.google.common.io.Files;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;

public class MyCrawler extends WebCrawler {
	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|mp3|zip|tar|gz|ico|mp2|mid|mp4|wav|avi|mov|mpeg|ram|m4v|rm|smil|wmv|swf|wma|zip|rar|gz|xml))$");
	
	private static String attemptUrls;
	private static String visitedUrls;
    private static String discoveredUrls;
    private static int dCount;
    private static int aCount;
    private static int successUrlCount;
    private static int abortedUrlCount;
    private static int failedUrlCount;
    
    
    private static File storageFolder;
    
    
    public MyCrawler() {
    	attemptUrls = new String();
    	visitedUrls = new String();
        discoveredUrls = new String();
    }
    
    public static String getDiscoveredUrls(){
    	return discoveredUrls;
    }
    
    public static String getVisitedUrls(){
    	return visitedUrls;
    }
    
    public static String getAttemptedUrls(){
    	return attemptUrls;
    }
    
    public static int getDiscoveredCount(){
    	return dCount;
    }
    
    public static int getVisitedCount(){
    	return successUrlCount;
    }
    
    public static int getAttemptedCount(){
    	return aCount;
    }
    
    public static int getAbortedUrlCount(){
    	return abortedUrlCount;
    }
    
    public static int getFailedUrlCount(){
    	return failedUrlCount;
    }

	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		 String href = url.getURL().toLowerCase();
	        
		 String type = "N_OK";
	     if (href.startsWith("http://www.usatoday.com/")) {
	        type = "OK";
	     }
	     
	     discoveredUrls += href + "," + type + "\n";
	     dCount++;
	     return !FILTERS.matcher(href).matches() && href.contains("www.usatoday.com");
	}
	 
	 @Override
	  public void visit(Page page) {
        String url = page.getWebURL().getURL();
        String contentType = page.getContentType().split(";")[0];
        String outgoingUrls = new String();
        
        int status = page.getStatusCode();
        
        if(status >= 200 && status < 300)
	    	successUrlCount++;
        
        if (contentType.equals("text/html")) { // html
            if (page.getParseData() instanceof HtmlParseData) {
                HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
                Set<WebURL> links = htmlParseData.getOutgoingUrls();
                for (WebURL link : links) {
                    outgoingUrls += link.getURL();
                }
                visitedUrls += url + "," + page.getContentData().length + "," + outgoingUrls.length() + "," + contentType + "\n";
            }
            else{
            	visitedUrls += url + "," + page.getContentData().length + "," + outgoingUrls.length() + "," + contentType + "\n";
            }
        } else if (contentType.equals("application/msword") || contentType.equals("application/pdf")) { // doc
        	visitedUrls += url + "," + page.getContentData().length + "," + outgoingUrls.length() + "," + contentType + "\n";
        } else if (contentType.equals("image/jpeg") || contentType.equals("image/jpg") || contentType.equals("image/png") || contentType.equals("image/tiff")
        		|| contentType.equals("image/gif")) { // doc
        	visitedUrls += url + "," + page.getContentData().length + "," + outgoingUrls.length() + "," + contentType + "\n";
        } else{
        	visitedUrls += url + "," + page.getContentData().length + "," + outgoingUrls.length() + "," + contentType + "\n";
        }
	 }
	 
	@Override
    protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {
		 attemptUrls += webUrl.getURL() + "," + Integer.toString(statusCode) + "\n";
	     aCount++;
	     
	     if(statusCode >= 300 && statusCode < 400)
	       	abortedUrlCount++;
	     else if (statusCode != 200)
	       	failedUrlCount++;
    }
	
	@Override
	protected void onUnexpectedStatusCode(String urlStr, int statusCode, String contentType,String description) {
		logger.warn("Skipping URL: {}, StatusCode: {}, {}, {}", urlStr, statusCode, contentType,description);
		// Do nothing by default (except basic logging)
		// Sub-classed can override this to add their custom functionality
	}
}
