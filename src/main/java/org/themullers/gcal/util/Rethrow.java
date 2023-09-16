package org.themullers.gcal.util;

import java.beans.Expression;

/**
 * This utility class exposes static methods that execute code, capturing and
 * rethrowing exceptions as a specified exception type.
 */
public class Rethrow {

    /**
     * Executes and returns the output of a function, catching and re-throwing any
     * exception as a RuntimeException.
     *
     * @param supplier  the function to invoke
     * @param <R>  the type returned by the function
     * @return  the output of the invoked function
     */
    static public <R> R exec(ThrowingSupplier<R> supplier) {
        try {
            return supplier.get();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes and returns the output of a function, catching and re-throwing any
     * exception as an exception of the specified type.
     *
     * @param c  the class of the exception to throw
     * @param supplier  the function to invoke
     * @param <R>  the type returned by the function
     * @param <T>  the type of exception to instantiate and rethrow
     * @return  the output of the invoked function
     * @throws T  an exception of this type is re-thrown if an exception is caught while executing the function
     */
    static public <R, T extends Throwable> R exec(Class<T> c, ThrowingSupplier<R> supplier) throws T {
        R value = null;
        try {
            value = supplier.get();
        }
        catch (Exception e) {
            instantiateAndThrow(c, e);
        }
        return value;
    }

    /**
     * Executes a method, catching any exception and re-throwing it as a RuntimeException.
     * @param runnable  the method to execute
     */
    static public void exec(ThrowingRunnable runnable) {
        try {
            runnable.run();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Executes a method, catching and re-throwing any
     * exception as an exception of the specified type.
     *
     * @param c  the class of the exception to throw
     * @param runnable  the function to invoke
     * @param <T>  the type of exception to instantiate and rethrow
     * @throws T  an exception of this type is re-thrown if an exception is caught while executing the function
     */
    static public <T extends Throwable> void exec(Class<T> c, ThrowingRunnable runnable) throws T {
        try {
            runnable.run();
        }
        catch (Exception e) {
            instantiateAndThrow(c, e);
        }
    }

    /**
     * Instantiates and throws an exception of the specified type, specifying the provided
     * exception object as the cause.  If a the specified exception type cannot be instantiated
     * (perhaps because no public constructor exists that takes an exception as an argument),
     * then a RuntimeException will be instantiated and thrown instead.
     *
     * @param c  the class of the exception to throw
     * @param cause  the cause to associate with the thrown exception
     * @param <T>  the type of the thrown exception
     * @throws T  an exception of this type will be thrown
     */
    static protected <T extends Throwable> void instantiateAndThrow(Class<T> c, Exception cause) throws T {
        try {
            // got the idea for this approach from https://stackoverflow.com/a/24772118/22480693
            var instantiate = new Expression(c, "new", new Object[]{cause});
            var exceptionInstance = instantiate.getValue();
            throw (T) exceptionInstance;
        }
        catch (Exception instantiationFailedException) {
            throw new RuntimeException(cause);
        }
    }

    /**
     * A functional interface declaring a method that returns a value and throws an exception.
     * @param <T>  the type of the value returned by the method
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    /**
     * A function interface declaring a method that throws an exception.
     */
    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }
}
