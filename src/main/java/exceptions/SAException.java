package exceptions;

/**
 * Created by dmytro on 16.04.16.
 */
public class SAException extends Exception {
    private boolean error;

    public SAException(String message){
        super(message);
        error = false;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }
}
