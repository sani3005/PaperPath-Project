package com.example.paperpath;

import java.util.ArrayList;
import java.util.List;

public class ConferenceRepository {

    public static List<Conference> getAllConferences() {
        List<Conference> list = new ArrayList<>();

        // 1. IEEE ICAI 2026 (AI & ML, IEEE)
        list.add(new Conference(
                "ieee_ai_2026",
                "IEEE ICAI",
                "IEEE International Conference on AI",
                "AI & ML",
                "CORE A*",
                "Vancouver, Canada",
                "Jan 15 - 18, 2026",
                "Sep 10, 2025",
                25,
                "Oct 15, 2025",
                "Nov 5, 2025",
                "The IEEE International Conference on Artificial Intelligence (ICAI) brings together researchers, practitioners and industry experts to discuss the latest advances in AI, machine learning and intelligent systems.",
                "AI, Deep Learning, Foundation Models, Robotics",
                "https://www.ieee.org/conferences/ai",
                "Upcoming",
                new String[]{"AI & ML", "Computer Science", "IEEE"}
        ));

        // 2. ACM SIGCOMM 2026 (Systems & Networks, ACM)
        list.add(new Conference(
                "sigcomm_2026",
                "ACM SIGCOMM",
                "ACM SIGCOMM 2026",
                "Systems",
                "CORE A*",
                "New York, USA",
                "Aug 24 - 28, 2026",
                "Jan 20, 2026",
                60,
                "Mar 15, 2026",
                "May 10, 2026",
                "ACM SIGCOMM is the flagship annual conference of the Special Interest Group on Data Communication, focusing on the applications, technologies, architectures, and protocols for computer communication.",
                "Networks, Distributed Systems, Protocols",
                "https://sigcomm.org/2026",
                "Upcoming",
                new String[]{"Networks", "Systems", "ACM"}
        ));

        // 3. NeurIPS 2025 (Machine Learning)
        list.add(new Conference(
                "neurips_2025",
                "NeurIPS",
                "NeurIPS 2025",
                "Machine Learning",
                "CORE A*",
                "San Diego, USA",
                "Dec 9 - 15, 2025",
                "May 15, 2025",
                12,
                "Sep 25, 2025",
                "Oct 20, 2025",
                "The Conference on Neural Information Processing Systems is the premier annual gathering on neural computing and machine learning.",
                "Machine Learning, AI, Deep Learning",
                "https://neurips.cc",
                "Upcoming",
                new String[]{"Machine Learning", "AI", "Deep Learning"}
        ));

        // 4. ICML 2026 (Machine Learning)
        list.add(new Conference(
                "icml_2026",
                "ICML",
                "ICML 2026",
                "Machine Learning",
                "CORE A*",
                "Vancouver, Canada",
                "Jul 13 - 19, 2026",
                "Jan 30, 2026",
                95,
                "Apr 10, 2026",
                "May 20, 2026",
                "The International Conference on Machine Learning is the leading international academic conference dedicated to the advancement of machine learning.",
                "Machine Learning, AI, Data Science",
                "https://icml.cc",
                "Upcoming",
                new String[]{"Machine Learning", "AI", "Data Science"}
        ));

        // 5. ICLR 2026 (Deep Learning & Vision)
        list.add(new Conference(
                "iclr_2026",
                "ICLR",
                "ICLR 2026",
                "Deep Learning",
                "CORE A*",
                "Singapore",
                "Apr 27 - May 1, 2026",
                "Sep 28, 2025",
                30,
                "Jan 22, 2026",
                "Mar 05, 2026",
                "The International Conference on Learning Representations is the premier gathering of professionals dedicated to the advancement of representation learning.",
                "Deep Learning, Representation Learning, Vision",
                "https://iclr.cc",
                "Upcoming",
                new String[]{"AI & ML", "Computer Science", "Deep Learning"}
        ));

        // 6. AAAI 2026 (Artificial Intelligence)
        list.add(new Conference(
                "aaai_2026",
                "AAAI",
                "AAAI 2026",
                "Artificial Intelligence",
                "CORE A*",
                "Philadelphia, USA",
                "Feb 25 - Mar 4, 2026",
                "Aug 15, 2025",
                45,
                "Nov 10, 2025",
                "Dec 15, 2025",
                "The AAAI Conference on Artificial Intelligence promotes research in artificial intelligence and scientific exchange among AI researchers and practitioners.",
                "AI, Knowledge Representation, Robotics",
                "https://aaai.org",
                "Upcoming",
                new String[]{"AI & ML", "Engineering", "AAAI"}
        ));

        return list;
    }

    public static Conference getById(String id) {
        if (id == null) return null;
        for (Conference conf : getAllConferences()) {
            if (conf.getId().equals(id)) {
                return conf;
            }
        }
        return null;
    }
}
