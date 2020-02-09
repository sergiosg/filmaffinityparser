package sergiosg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class ParserService {


    public Movie parse(String movie) {

        Document document = Jsoup
                .parse(movie);

        Movie result = new Movie();

        result.setRating(Float.valueOf(
                document.getElementById("movie-rat-avg").attr("content"))
        );

        result.setTitle(document
                .getElementById("main-title")
                .getElementsByAttribute("itemprop")
                .text()
        );

        result.setVotes(Integer.valueOf(document
                .getElementById("movie-count-rat")
                .child(0)
                .attr("content"))
        );

        return result;
    }
}
