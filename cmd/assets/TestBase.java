import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

class TestBase {
    ByteArrayOutputStream baos;

    void stdOutCaptureStart() {
        this.baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(this.baos));
    }

    String getStdOut() {
        return this.baos.toString();
    }

    void setNewStdIn(String s) {
        System.setIn(new ByteArrayInputStream(s.getBytes()));
    }
}
