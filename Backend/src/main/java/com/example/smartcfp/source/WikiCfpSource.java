package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class WikiCfpSource implements ConferenceSource {

    @Override
    public String getSourceName() {
        return "WikiCFP";
    }

    @Override
    public List<Conference> scrape() throws IOException {
        String url = "https://www.wikicfp.com/cfp/";

        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(20000)
                .get();

        return parse(doc);
    }

    List<Conference> parse(Document doc) {
        List<Conference> conferences = new ArrayList<>();
        Elements links = doc.select("a[href*='event.showcfp']");

        for (Element link : links) {
//            System.out.println("Link: " + link.absUrl("href"));
            String shortTitle = link.text();
            String eventLink = link.absUrl("href");

            Element row = link.closest("tr");

            String fullTitle = "";
            String location = "Not available";
            String deadline = "Not available";

            if (row != null) {

                Elements cells = row.select("td");

                if (cells.size() > 1) {
                    fullTitle = cells.get(1).text();
                }

                Element detailsRow = row.nextElementSibling();

                if (detailsRow != null) {

                    Elements details = detailsRow.select("td");

                    if (details.size() >= 3) {
                        location = details.get(1).text();
                        deadline = details.get(2).text();
                    }
                }
            }
            Conference conference = new Conference(
                    null,
                    shortTitle,
                    fullTitle,
                    location,
                    deadline,
                    eventLink,
                    getSourceName()
            );
            conferences.add(conference);
//            System.out.println("Conference: " + conference);

        }

        return conferences;
    }
}
