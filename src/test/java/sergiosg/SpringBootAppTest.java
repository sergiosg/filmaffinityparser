package sergiosg;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.NoSuchFileException;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SpringBootAppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.err;

    @BeforeEach
    public void setUpStreams() {
        System.setErr(new PrintStream(outContent));
    }

    @AfterEach
    public void restoreStreams() {
        System.setErr(originalOut);
    }

    @Mock
    private ParserService parserService;

    @InjectMocks
    private SpringBootApp app = new SpringBootApp();

    @DisplayName("Test parse files within folder")
    @Test
    void testParse() throws Exception {

        String args[] = {"myFolder"};

        doNothing().when(parserService).parseFolder(any(String.class));

        app.run(args);

        verify(parserService).parseFolder(args[0]);
    }

    @DisplayName("Test Empty arg")
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t", "\n"})
    void errorMessage_when_empty_arg(String folderName) throws Exception {

        String args[] = {folderName};

        app.run(args);

        assertEquals(SpringBootApp.EMPTY_ARGUMENT +  System.lineSeparator(),
                outContent.toString());
    }

    @DisplayName("Test unexisting folder")
    @Test
    void error_when_unexisting_folder() throws Exception {

        String args[] = {"unexistingFolder"};

        doThrow(NoSuchFileException.class)
                .when(parserService)
                .parseFolder(args[0]);

        app.run(args);

        assertEquals(SpringBootApp.UNEXISTING_FOLDER +  System.lineSeparator(),
                outContent.toString());
    }
}
