package de.szut.lf8_starter.hello;

public class HelloNotFoundException extends RuntimeException {

    public HelloNotFoundException(long id) {
        super("Hello " + id + " nicht gefunden");
    }
}
