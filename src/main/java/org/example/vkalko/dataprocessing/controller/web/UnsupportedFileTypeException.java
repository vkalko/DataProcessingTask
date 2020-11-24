package org.example.vkalko.dataprocessing.controller.web;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class UnsupportedFileTypeException extends RuntimeException {

    private static final long serialVersionUID = 456278107654L;

    public UnsupportedFileTypeException(String msg) {
        super(msg);
    }

}
