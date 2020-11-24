package org.example.vkalko.dataprocessing.controller.web;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ParsingException extends RuntimeException {

    private static final long serialVersionUID = 8945456276L;

    public ParsingException(String msg) {
        super(msg);
    }

}
