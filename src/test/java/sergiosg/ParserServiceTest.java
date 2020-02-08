package sergiosg;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class ParserServiceTest {

    ParserService parserService = new ParserService();

    private static String BAD_BOYS_HTML;


    @BeforeAll
    static void beforeAll() throws IOException {
        BAD_BOYS_HTML = Files.readString(Paths.get("film103813.html"));
    }

    @Test
    public void shouldParseMovie(){
        System.out.println(BAD_BOYS_HTML);

        Movie movie = parserService.parse(BAD_BOYS_HTML);

        assertThat(movie).isNotNull();

    }
}
