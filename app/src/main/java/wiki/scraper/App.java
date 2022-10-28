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

    public String findFirstLink(String scrapedHtml) {
        int linkIndex = scrapedHtml.indexOf("href=\"") + 6;
        scrapedHtml = scrapedHtml.substring(linkIndex);
        int linkEnd = scrapedHtml.indexOf("\"");
        String foundLink;

        if (scrapedHtml.substring(0, 1).equals("#")) {
            scrapedHtml = scrapedHtml.substring(linkEnd);
            return findFirstLink(scrapedHtml);
        }

        foundLink = scrapedHtml.substring(0, linkEnd);

        if (foundLink.contains("Help:") || foundLink.contains("File:")
                || foundLink.contains("language")
                || foundLink.contains("upload.wikimedia.org")
                || foundLink.contains("wiktionary.org")) {
            scrapedHtml = scrapedHtml.substring(linkEnd);
            return findFirstLink(scrapedHtml);
        }

        return foundLink;
    }

    public String scrapeHTML(String url) throws Exception {
        Element htmlElement = getDocument(url).getElementById("mw-content-text");
        String scrape;
        if (htmlElement != null) {
            scrape = htmlElement.select("p").html();
        } else {
            throw new Exception("Element not found");
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

        try {
            nextLink = new App().scrapeHTML("https://en.wikipedia.org/wiki/Special:Random");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        if (out != null && nextLink != null) {
            // System.out.println(nextLink);
            links.add(nextLink);
            out.println(nextLink);
            while (!done) {
                try {
                    nextLink = new App().scrapeHTML("https://en.wikipedia.org" + nextLink);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
                if (!links.contains(nextLink)) {
                    // System.out.println(nextLink);
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
