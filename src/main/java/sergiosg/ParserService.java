package sergiosg;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Optional;
import java.util.stream.Stream;

@Service
public class ParserService {

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);


    public void parseFolder( String folder ) throws NoSuchFileException {

        Path folderPath = Paths.get(folder);

        logger.info( "Parsing folder: %s", folderPath);
        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths
                .filter(Files::isRegularFile)
                .filter(path ->
                        path.getFileName().toString().startsWith("film") && path.getFileName().toString().endsWith(".html"))
                .map(p -> {
                    try {
                        return Optional.of(Files.readString(p));
                    } catch (IOException e) {
                        logger.error("Error reading file: %s ", p);
                        return Optional.empty();
                    }
                })
                .map(movie ->  parse(((Optional<String>)movie).get()))
                .forEach(this::send);
        }
        catch (NoSuchFileException fe){
           throw fe;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }


    protected Movie parse(String movie) {


        Document document = Jsoup
                .parse(movie);

        Movie result = new Movie();

        result.setRating(Float.parseFloat(
                document.getElementById("movie-rat-avg").attr("content"))
        );

        result.setTitle(document
                .getElementById("main-title")
                .getElementsByAttribute("itemprop")
                .text()
        );

        result.setVotes(Integer.parseInt(document
                .getElementById("movie-count-rat")
                .child(0)
                .attr("content"))
        );

        return result;
    }

    protected void send(Movie movie){
        logger.info("Sending to queue movie: %s ", movie.getTitle());
    }
}
