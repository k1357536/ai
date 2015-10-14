package at.jku.cp.ai.competition.runtime;

import java.io.OutputStream;
import java.io.PrintStream;

class NullPrinter {
    public static final PrintStream out = new PrintStream(new OutputStream() {
        public void write(int b) {
        }
    });
}
