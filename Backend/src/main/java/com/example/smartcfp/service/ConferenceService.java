package com.example.smartcfp.service;
import  com.example.smartcfp.model.Conference;
import com.example.smartcfp.source.ConferenceSource;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ConferenceService {
//    public List<Conference> getAllConferences(){
//        Conference c1 = new Conference(1L,"AI","Artificial intelligence","Goa","fufsfus","uush","jj");
//        Conference c2 = new Conference(1L,"ML","Machine learning","fjkf","fufsfus","uush","jj");
//        return Arrays.asList(c1,c2);
//    }

    public List<Conference> searchConferences(String keyword) {
        return getAllConferences().stream().filter(c ->c.getShortTitle().toLowerCase(Locale.ROOT).contains(keyword.toLowerCase(Locale.ROOT)) || c.getLongTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                c.getLocation().toLowerCase().contains(keyword.toLowerCase())).collect(Collectors.toList());
    }



        private final List<ConferenceSource> sources;

        public ConferenceService(List<ConferenceSource> sources) {
            this.sources = sources;
        }

        public List<Conference> getAllConferences() {
            List<Conference> all = new ArrayList<>();

            for (ConferenceSource source : sources) {
                try {
                    all.addAll(source.scrape());
                } catch (Exception e) {
                    System.err.println(
                            "Failed to fetch from "
                                    + source.getSourceName()
                                    + ": " + e.getMessage()
                    );
                }
            }

            return all;
        }
    }




