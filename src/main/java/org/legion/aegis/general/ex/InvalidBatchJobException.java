package org.legion.aegis.general.ex;

public class InvalidBatchJobException extends RuntimeException {

    public InvalidBatchJobException(){}

    public InvalidBatchJobException(String msg) {
        super(msg);
    }
}
