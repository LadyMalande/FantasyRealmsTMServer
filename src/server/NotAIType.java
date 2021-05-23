package server;

/**
 * Custom Exception informing the user that arguments written in the command line are not recognized AI types.
 * @author Tereza Miklóšová
 */
public class NotAIType extends Exception {
    /**
     * Constructor of the exception.
     * @param message Message to tell to the client.
     */
    public NotAIType(String message)
    {
        super(message);
    }
}
