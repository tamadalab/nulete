import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

class TestBase {
    ByteArrayOutputStream baos;

    void stdOutCaptureStart() {
        this.baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.baos));
    }

    String getStdOut() {
        return this.baos.toString();
    }
}
