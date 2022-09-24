package org.demo202209;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class TikaParserResourceTest {

    @Test
    public void testHelloQuarkusPdfFormat() throws Exception {
        checkText("application/pdf", "pdf");
    }

    @Test
    public void testPDFwithTextAndEmbeddedImages() throws Exception {
        given()
                .when()
                .header("Content-Type", "application/pdf")
                .body(readQuarkusFile("/untitled.pdf"))
                .post("/parse/text")
                .then()
                .statusCode(200)
                .body(allOf(
                    containsString("This is a PDF containing text and images; this was part of some text."),
                    containsString("The quick brown dog jumped over")));
    }

    private byte[] readQuarkusFile(String fileName) throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            return readBytes(is);
        }
    }

    static byte[] readBytes(InputStream is) throws Exception {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[4096];
        int len;
        while ((len = is.read(buffer)) != -1) {
            os.write(buffer, 0, len);
        }
        return os.toByteArray();
    }

    private void checkText(String contentType, String extension) throws Exception {
        given()
                .when().header("Content-Type", contentType)
                .body(readQuarkusFile("/quarkus." + extension))
                .post("/parse/text")
                .then()
                .statusCode(200)
                .body(is("Hello Quarkus"));
    }
}