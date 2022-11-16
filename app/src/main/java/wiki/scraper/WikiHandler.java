package wiki.scraper;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class WikiHandler {

    public static Document getDocument(String url) {
        Connection conn = Jsoup.connect(url);
        Document document = null;
        try {
            document = conn.get();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            System.exit(0);
        }
        return document;
    }

    // Expects a html element as string from wikipedia to be searched
    // Finds first link in element with exceptions
    // If no link is found in element, returns null
    // If exception is found, finds next link in element
    // Returns cropped link url ex: /wiki/foobar
    public String findFirstLinkInP(String scrapedHtml) {
        if (!scrapedHtml.contains("href=")) {
            return null;
        }
        int linkStart = scrapedHtml.indexOf("href=\"") + 6;
        scrapedHtml = scrapedHtml.substring(linkStart);
        int linkEnd = scrapedHtml.indexOf("\"");
        String foundLink;

        // Some links for citations start with #. We do not want these links
        if (scrapedHtml.substring(0, 1).equals("#")) {
            scrapedHtml = scrapedHtml.substring(linkEnd);
            return findFirstLinkInP(scrapedHtml);
        }

        foundLink = scrapedHtml.substring(0, linkEnd);

        // TODO: Find a better way to skip language origins
        if (foundLink.contains("Help:") || foundLink.contains("File:")
                || foundLink.contains("language")
                || foundLink.contains("Latin")
                || foundLink.contains("upload.wikimedia.org")
                || foundLink.contains("wiktionary.org")) {
            scrapedHtml = scrapedHtml.substring(linkEnd);
            return findFirstLinkInP(scrapedHtml);
        }
        return foundLink;
    }

    // Expects a wikipedia article url
    // Searches the main content for the first <p> that contains a link
    // Sends the found <p> to findFirstLink and returns its results
    public String findFirstLinkInWiki(String url) {
        Elements pElements = getDocument(url).select("div.mw-parser-output > p");
        String foundP = "";
        Boolean linkIsGood = false;
        int i = 0;
        while (pElements != null && !linkIsGood) {
            if (!pElements.isEmpty()) {
                foundP = pElements.get(i).html();
            } else {
                System.out.println("No <p> found");
                System.exit(0);
            }
            if (findFirstLinkInP(foundP) != null) {
                linkIsGood = true;
            }
            i++;
        }
        return findFirstLinkInP(foundP);
    }
}