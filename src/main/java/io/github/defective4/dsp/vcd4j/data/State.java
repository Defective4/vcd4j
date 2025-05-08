package io.github.defective4.dsp.vcd4j.data;

/**
 * Represents one of four possible states of a 1-bit variable.<br>
 * This enum is used by {@link BinaryChangeEntry}
 */
public enum State {
    FLOATING('z'), HIGH('1'), LOW('0'), UNDEFINED('x');

    private final char ch;

    private State(char ch) {
        this.ch = ch;
    }

    public char getChar() {
        return ch;
    }

}
