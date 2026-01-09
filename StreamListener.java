public interface StreamListener {

    // Called every time the model sends a new token (word/fragment)
    void onToken(String token);

    // Called when streaming is finished
    void onComplete();

    // Called if something goes wrong
    void onError(String error);
}