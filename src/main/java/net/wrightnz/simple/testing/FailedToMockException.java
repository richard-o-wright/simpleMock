/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.wrightnz.simple.testing;

/**
 *
 * @author Richard
 */
public class FailedToMockException extends RuntimeException {

    public FailedToMockException(String message, Throwable cause) {
        super(message, cause);
    }

}
