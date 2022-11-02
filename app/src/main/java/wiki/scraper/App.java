package wiki.scraper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class App {

    public static Document getDocument(String url) {
        Connection conn = Jsoup.connect(url);
        Document document = null;
        try {
            document = conn.get();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return document;
    }

    // Expects a html element as string from wikipedia to be searched
    // Finds first link in element with exceptions
    // If exception is found, finds next link in element
    // Returns cropped link url ex: /wiki/foobar
    public String findFirstLink(String scrapedHtml) {
        int linkIndex = scrapedHtml.indexOf("href=\"") + 6;
        scrapedHtml = scrapedHtml.substring(linkIndex);
        int linkEnd = scrapedHtml.indexOf("\"");
        String foundLink;

        // Some links for citations start with #. We do not want these links
        if (scrapedHtml.substring(0, 1).equals("#")) {
            scrapedHtml = scrapedHtml.substring(linkEnd);
            return findFirstLink(scrapedHtml);
        }

        foundLink = scrapedHtml.substring(0, linkEnd);

        // TODO: Find a better way to skip language origins
        if (foundLink.contains("Help:") || foundLink.contains("File:")
                || foundLink.contains("language")
                || foundLink.contains("Latin")
                || foundLink.contains("upload.wikimedia.org")
                || foundLink.contains("wiktionary.org")) {
            scrapedHtml = scrapedHtml.substring(linkEnd);
            return findFirstLink(scrapedHtml);
        }

        return foundLink;
    }

    // Expects a wikipedia article url
    // Searches the main content for the first <p> that contains a link
    // Sends the found <p> to findFirstLink and returns its results
    public String scrapeHTML(String url) {
        Element htmlElement = getDocument(url).getElementById("mw-content-text");
        String scrape = "";
        int i = 0;
        while (!scrape.contains("href") && htmlElement != null) {
            scrape = htmlElement.getElementsByTag("p").get(i).html();
            i++;
        }

        return findFirstLink(scrape);
    }

    public static void main(String[] args) {
        PrintWriter out = null;
        String nextLink = null;
        List<String> links = new ArrayList<>();
        Boolean done = false;

        try {
            out = new PrintWriter("links.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        nextLink = new App().scrapeHTML("https://en.wikipedia.org/wiki/Special:Random");

        // TODO: Add and Print initial article's url
        if (out != null && nextLink != null) {
            links.add(nextLink);
            out.println(nextLink);
            while (!done) {
                nextLink = new App().scrapeHTML("https://en.wikipedia.org" + nextLink);

                if (!links.contains(nextLink)) {
                    links.add(nextLink);
                    out.println(nextLink);
                } else {
                    done = true;
                    out.println(nextLink);
                }
            }
            out.close();
        }
    }
}
