import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ParserTest {

    @Test
    void isNumber() throws IOException {
            Parser ps = new Parser(new ReadFile());

          assertEquals("true",ps.isNumber("10,123","shaull"));

    }
}