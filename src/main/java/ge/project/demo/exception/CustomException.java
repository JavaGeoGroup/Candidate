package ge.project.demo.exception;

public class CustomException extends Exception{
  private String message;

  public CustomException(String message) {
    super(message);
    this.message = message;
  }

  public String getMessage() {
    return message;
  }
}
