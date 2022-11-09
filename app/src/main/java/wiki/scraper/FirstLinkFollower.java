package wiki.scraper;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class FirstLinkFollower {
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
            startLink = WikiHandler.getDocument("https://en.wikipedia.org/wiki/Special:Random").location();
        } else if (userInput.contains("https://en.wikipedia.org/wiki/")) {
            startLink = userInput;
        } else {
            System.out.println("Invalid URL entered\nGoodbye");
            System.exit(0);
        }
        in.close();

        nextLink = new WikiHandler().findAllP(startLink);
        int urlStart = startLink.indexOf("/wiki/");
        startLink = startLink.substring(urlStart);
        if (out != null) {
            out.println(startLink);

            if (nextLink != null) {
                links.add(nextLink);
                out.println(nextLink);
                while (!done) {
                    nextLink = new WikiHandler().findAllP("https://en.wikipedia.org" + nextLink);

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
