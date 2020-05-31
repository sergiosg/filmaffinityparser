package sergiosg;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;

@Service
public class ParserService {

    private static final Logger logger = LoggerFactory.getLogger(ParserService.class);

    @Autowired
    private Producer<Long, String> producer;

    @Autowired
    private ObjectMapper mapper;

    public void parseFolder(String folder) throws NoSuchFileException {

        Path folderPath = Paths.get(folder);

        logger.info("Parsing folder: {}", folderPath);
        try (Stream<Path> paths = Files.walk(folderPath)) {
            paths
                    .filter(Files::isRegularFile)
                    .filter(path ->
                            path.getFileName().toString().startsWith("film") && path.getFileName().toString().endsWith(".html"))
                    .map(this::getFileContent)
                    .map(this::toMovie)
                    .forEach(this::send);
        } catch (NoSuchFileException fe) {
            throw fe;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    protected Movie toMovie(String fileContent) {

        Document document = Jsoup
                .parse(fileContent);

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

    protected void send(Movie movie) {
        logger.info("Sending movie to queue. Title: {}", movie.getTitle());

        final ProducerRecord<Long, String> record;
        try {
            record = new ProducerRecord(KafkaProducerConfig.TOPIC, mapper.writeValueAsString(movie));
            RecordMetadata metadata = producer.send(record).get();
            logger.info("Record sent with title {}  to partition {} with offset {}",
                    movie.getTitle(),
                    metadata.partition(),
                    metadata.offset());
        } catch (ExecutionException e) {
            logger.error("Error in sending record", e);
        } catch (InterruptedException e) {
            logger.error("Error in sending record", e);
        } catch (JsonProcessingException e) {
            logger.error("Movie can not be serialized", e);
        }
    }


    private Optional<String> getFileContent(Path p) {
        try {
            return Optional.of(Files.readString(p));
        } catch (IOException e) {
            logger.error("Error reading file: " + p);
            return Optional.empty();
        }
    }

    private Movie toMovie(Optional<String> fileContent){
        return toMovie(fileContent.get());
    }
}
