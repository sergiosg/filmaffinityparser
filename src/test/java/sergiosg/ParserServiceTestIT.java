package sergiosg;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;


class ParserServiceTestIT {

    ParserService parserService = new ParserService();

    private static String BAD_BOYS_MOVIE_HTML;


    @BeforeAll
    static void beforeAll() throws IOException {
        BAD_BOYS_MOVIE_HTML = Files.readString(
                Paths.get(ParserServiceTestIT.class.getClassLoader()
                        .getResource("film103813.html")
                        .getPath()));
    }

    @Test
    void shouldParseFolder() throws NoSuchFileException {
        parserService.parseFolder("src/test/resources/");
    }

    @Test
    void shouldParseMovie(){

        Movie movie = parserService.parse(BAD_BOYS_MOVIE_HTML);


        assertThat(movie).isNotNull();
        assertThat(movie.getTitle()).isEqualTo("Bad Boys for Life");
        assertThat(movie.getRating()).isEqualTo(6.2f);
        assertThat(movie.getVotes()).isEqualTo(485);

    }
}
