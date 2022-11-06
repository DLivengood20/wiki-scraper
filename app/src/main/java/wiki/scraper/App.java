package wiki.scraper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class App {

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
    // If exception is found, finds next link in element
    // Returns cropped link url ex: /wiki/foobar
    public String findFirstLink(String scrapedHtml) {
        if (!scrapedHtml.contains("href=")) {
            return null;
        }
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
        Elements pElements = null;

        Element htmlElement = getDocument(url).getElementById("mw-content-text");
        if (htmlElement != null) {
            pElements = htmlElement.getElementsByTag("p");
        }
        String scrape = "";
        Boolean linkIsGood = false;
        int i = 0;
        while (pElements != null && !linkIsGood) {
            if (!pElements.isEmpty()) {
                scrape = pElements.get(i).html();
            } else {
                System.out.println("No <p> found");
                System.exit(0);
            }
            if (findFirstLink(scrape) != null) {
                linkIsGood = true;
            }
            i++;
        }
        return findFirstLink(scrape);
    }

    public static void main(String[] args) {
        PrintWriter out = null;
        String startLink = null;
        String nextLink = null;
        List<String> links = new ArrayList<>();
        Boolean done = false;
        Scanner in = new Scanner(System.in);

        try {
            out = new PrintWriter("links.txt");
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        System.out.println("Enter a wikipedia url or leave blank for a random url");
        String userInput = in.nextLine();

        if (userInput.length() == 0) {
            // startLink grabs the random URL generated from wikipedia's random function
            startLink = getDocument("https://en.wikipedia.org/wiki/Special:Random").location();
        } else if (userInput.contains("https://en.wikipedia.org/wiki/")) {
            startLink = userInput;
        } else {
            System.out.println("Invalid URL entered\nGoodbye");
            System.exit(0);
        }
        in.close();

        nextLink = new App().scrapeHTML(startLink);
        int cropStart = startLink.indexOf("/wiki/");
        startLink = startLink.substring(cropStart);
        if (out != null) {
            out.println(startLink);

            if (nextLink != null) {
                links.add(nextLink);
                out.println(nextLink);
                while (!done) {
                    nextLink = new App().scrapeHTML("https://en.wikipedia.org" + nextLink);

                    if (!links.contains(nextLink)) {
                        links.add(nextLink);
                    } else {
                        done = true;
                    }
                    out.println(nextLink);
                }
            }
            out.close();
        }
    }
}
