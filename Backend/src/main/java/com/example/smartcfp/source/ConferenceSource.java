
package com.example.smartcfp.source;

import com.example.smartcfp.model.Conference;
import java.io.IOException;
import java.util.List;

public interface ConferenceSource {

    String getSourceName();

    List<Conference> scrape() throws IOException;


}
