package exceptions;

/**
 * Created by dmytro on 10.03.16.
 */
public class InvalidIDException extends Exception{

    private String invalidId;

    public String getInvalidId() {
        return invalidId;
    }

    public void setInvalidId(String invalidId) {
        this.invalidId = invalidId;
    }

    public InvalidIDException(String message){
        super(message);
    }
}
