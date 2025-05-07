package io.github.defective4.dsp.vcd4j.data;

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
