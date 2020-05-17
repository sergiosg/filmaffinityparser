package sergiosg;

import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ObjectUtils;

import java.nio.file.NoSuchFileException;

@SpringBootApplication
public class SpringBootApp implements CommandLineRunner {

    @Autowired
    ParserService service;

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApp.class, args);
    }

    protected static final String EXPECTED_ARGUMENT = "Expected argument. Input folder to be parsed";
    protected static final String TOO_MANY_ARGUMENTS = "Too many arguments. Expected folder to be parsed as unique argument";
    protected static final String EMPTY_ARGUMENT =  "Wrong argument. Input folder to be parsed";
    protected static final String UNEXISTING_FOLDER = "Unexisting folder";

    @Override
    public void run(String... args) throws Exception {

        if( ObjectUtils.isEmpty(args)) {
            System.err.println(EXPECTED_ARGUMENT);
        }
        else if( args.length > 1){
            System.err.println(TOO_MANY_ARGUMENTS);
        }
        else if(Strings.isBlank(args[0])) {
            System.err.println(EMPTY_ARGUMENT);
        }
        else {
            try {
                service.parseFolder(args[0]);
            }
            catch (NoSuchFileException nfe){
                System.err.println(UNEXISTING_FOLDER);
            }
        }
    }
}