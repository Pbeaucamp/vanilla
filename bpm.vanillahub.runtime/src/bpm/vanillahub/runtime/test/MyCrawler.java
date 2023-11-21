package bpm.vanillahub.runtime.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

public class MyCrawler extends WebCrawler {

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|gif|jpg" + "|png|mp3|mp3|zip|gz))$");

	private List<String> prices = new ArrayList<>();
	private boolean toContinue = true;

	/**
	 * This method receives two parameters. The first parameter is the page in
	 * which we have discovered this new url and the second parameter is the new
	 * url. You should implement this function to specify whether the given url
	 * should be crawled or not (based on your crawling logic). In this example,
	 * we are instructing the crawler to ignore urls that have css, js, git, ...
	 * extensions and to only accept urls that start with
	 * "http://www.ics.uci.edu/". In this case, we didn't need the referringPage
	 * parameter to make the decision.
	 */
	@Override
	public boolean shouldVisit(Page referringPage, WebURL url) {
		String href = url.getURL().toLowerCase();
		boolean shouldCrawl = toContinue && !FILTERS.matcher(href).matches() && href.startsWith("http://la-centrale-pro.com/");
		if (shouldCrawl && href.endsWith(".html")) {
			System.out.println("Should crawl");
		}
		return shouldCrawl;
		// return false;
	}

	/**
	 * This function is called when a page is fetched and ready to be processed
	 * by your program.
	 */
	@Override
	public void visit(Page page) {
		String url = page.getWebURL().getURL();
		
		if (url.endsWith(".html")) {
			System.out.println("URL: " + url);
			
			if (page.getParseData() instanceof HtmlParseData) {
				HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
				String text = htmlParseData.getText();
				String html = htmlParseData.getHtml();
				Set<WebURL> links = htmlParseData.getOutgoingUrls();

				System.out.println("Text length: " + text.length());
				System.out.println("Html length: " + html.length());
				System.out.println("Number of outgoing links: " + links.size());

				String span = "<span id=\"our_price_display\">";
				int indexOfSpan = html.indexOf(span);
				String endSpan = "</span>";

				if (indexOfSpan > 0) {
					int indexOfEndOfSpan = html.indexOf(endSpan, indexOfSpan);
					String price = html.substring(html.indexOf(span) + span.length(), indexOfEndOfSpan);

					prices.add(price);
				}
			}

			if (prices.size() > 15) {
				this.toContinue = false;
				for (String price : prices) {
					System.out.println("PRICE = " + price);
				}
			}
		}
	}

	public List<String> getPrices() {
		return prices;
	}
}