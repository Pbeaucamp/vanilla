package bpm.vanillahub.runtime.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

public class TestCrawl {

	public static void main(String[] args) {

		// String url =
		// "http://www.bureau-vallee.fr/80gr-carton-5-ram-a4-blanc-clairalfa-52103.html";
		// String start =
		// "<span class=\"price\" id=\"price-including-tax-52103\">";
		// String end = "</span>";

		// String url =
		// "http://www.first-office.fr/papier-repro-blanc/497-carton-5-ramettes-500-feuilles-papier-blanc-clairalfa-a4-80g-m2-3329685197902.html";
		// String start = "<span itemprop=\"price\">";
		// String end = "</span>";

		// String url =
		// "http://www.top-office.com/lot-5-ramettes-500-feuilles-blanches-a4-clairefontaine-clairalfa-80g.html";
		// String start = "<p class=\"prix prix-seul\">";
		// String end = "<";

		// String url =
		// "http://www.officedepot.fr/a/pb/Clairalfa-carton-de-5-ramettes-de-500-feuilles-A4-80g/id=0006804/";
		// String start = "<span class=\"taxedAmount\">";
		// String end = "</span>";

		// String url =
		// "http://robinet-pas-cher.com/tete-de-robinet/44003-grohe-tete-a-clapet-12x17-ref-06170000-4005176015717.html";
		// String start = "<span class=\"price\" id=\"our_price_display\">";
		// String end = "</span>";

		// String url =
		// "http://tout-pour-le-plombier.com/lavabo/44003-tete-a-clapet-12x17-ref-06170000-grohe-4005176015717.html";
		// String start = "<span class=\"price\" id=\"our_price_display\">";
		// String end = "</span>";

		String url = "http://la-centrale-pro.com/tete/44003-tute-a-clapet-12x17-ref-06170000-4005176015717.html";
		String start = "<span id=\"our_price_display\">";
		String end = "</span>";

		// String url =
		// "http://la-centrale-pro.com/tete/44003-tute-a-clapet-12x17-ref-06170000-4005176015717.html";
		// String start = "<span id=\"our_price_display\">";
		// String end = "</span>";

		try {
			URL my_url = new URL(url);
			BufferedReader br = new BufferedReader(new InputStreamReader(my_url.openStream()));

			StringBuffer buf = new StringBuffer();
			String strTemp = "";
			while (null != (strTemp = br.readLine())) {
				buf.append(strTemp);
			}
			String html = buf.toString();

			extractPricePart(html);

			int indexOfStart = html.indexOf(start) + start.length();

			if (indexOfStart - start.length() > 0) {
				int indexOfEndOfSpan = html.indexOf(end, indexOfStart);
				String price = html.substring(indexOfStart, indexOfEndOfSpan);
				System.out.println("PRICE = " + price);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		// try {
		// String crawlStorageFolder = "/data/crawl/root";
		// int numberOfCrawlers = 7;
		//
		// CrawlConfig config = new CrawlConfig();
		// config.setCrawlStorageFolder(crawlStorageFolder);
		//
		// /*
		// * Instantiate the controller for this crawl.
		// */
		// PageFetcher pageFetcher = new PageFetcher(config);
		// RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
		// RobotstxtServer robotstxtServer = new
		// RobotstxtServer(robotstxtConfig, pageFetcher);
		// CrawlController controller = new CrawlController(config, pageFetcher,
		// robotstxtServer);
		//
		// /*
		// * For each crawl, you need to add some seed urls. These are the
		// * first URLs that are fetched and then the crawler starts following
		// * links which are found in these pages
		// */
		// controller.addSeed("http://la-centrale-pro.com/tete/44003-tute-a-clapet-12x17-ref-06170000-4005176015717.html");
		//
		// /*
		// * Start the crawl. This is a blocking operation, meaning that your
		// * code will reach the line after this only when crawling is
		// * finished.
		// */
		// controller.start(MyCrawler.class, numberOfCrawlers);
		//
		//
		// } catch (Exception ex) {
		// ex.printStackTrace();
		// }
	}
	
	private static final int START_INDEX = 75;
	private static final int END_INDEX = 100;

	private static void extractPricePart(String html) {
		String word = "price";

		int index = html.indexOf(word);
		while (index >= 0) {
			int indexStart = 0;
			if (index - START_INDEX > 0) {
				indexStart = index - START_INDEX;
			}

			int indexEnd = html.length();
			if (index + END_INDEX < html.length()) {
				indexEnd = index + END_INDEX;
			}
			
			
			StringBuffer buf = new StringBuffer();
			buf.append("\n\n\n=====================================================================\n\n\n");
			buf.append(html.substring(indexStart, indexEnd));
			buf.append("\n\n\n=====================================================================\n\n\n");
			
			System.out.println(buf.toString());
			
			index = html.indexOf(word, index + word.length());
		}
	}

}
