package org.example.vkalko.dataprocessing.controller.web;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnexpectedEventTypeException extends RuntimeException {

    private static final long serialVersionUID = 658292101010L;

    public UnexpectedEventTypeException(String msg) {
        super(msg);
    }

}
