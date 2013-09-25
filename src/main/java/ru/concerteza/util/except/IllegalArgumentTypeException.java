package ru.concerteza.util.except;

import java.lang.reflect.Type;

/**
 * Thrown to indicate that a method has been passed an argument of an illegal or inappropriate type.
 *
 * @author  Timofey Gorshkov
 * created 25.09.2013
 */
public class IllegalArgumentTypeException extends IllegalArgumentException {

    /**
     * Constructs an {@code IllegalArgumentTypeException} with the specified argument type.
     *
     * @param t the illegal or inappropriate argument type.
     */
    public IllegalArgumentTypeException(Type t) {
        super("Illegal argument type: " + t);
    }
}
