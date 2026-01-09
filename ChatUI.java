import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.geometry.Side;

import java.io.File;
import javafx.scene.layout.Priority;

public class ChatUI extends Application {

    private final ChatbotEngine bot = new ChatbotEngine();
    private final FAQManager faqManager = new FAQManager();
    private VBox chatContainer;
    private File selectedImage;

    @Override
    public void start(Stage stage) {

        /* =========================
           NAVBAR
        ========================= */
        Label title = new Label("NovaGPT");
        title.setStyle("""
            -fx-font-size: 25;
            -fx-font-weight: bold;
            -fx-text-fill: #22c55e;
        """);

        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#22c55e"));
        glow.setRadius(25);
        title.setEffect(glow);

        HBox navbar = new HBox(title);
        navbar.setAlignment(Pos.CENTER);
        navbar.setPadding(new Insets(14));
        navbar.setStyle("-fx-background-color: #020617;");

        /* =========================
           CHAT AREA
        ========================= */
        chatContainer = new VBox(12);
        chatContainer.setPadding(new Insets(15));

        ScrollPane scrollPane = new ScrollPane(chatContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setStyle("""
            -fx-background: #0f172a;
            -fx-border-color: transparent;
        """);

        // 🔒 lock chat width to viewport (prevents horizontal scrolling)
        chatContainer.prefWidthProperty()
                .bind(scrollPane.widthProperty().subtract(20));

        /* =========================
           INPUT BAR
        ========================= */
        TextField input = new TextField();
        input.setPromptText("Message NovaGPT...");
        input.setStyle("""
            -fx-background-radius: 20;
            -fx-padding: 10;
            -fx-font-size: 17;
        """);

        Button sendBtn = new Button("Send");
        sendBtn.setStyle("""
            -fx-background-color: #22c55e;
            -fx-text-fill: black;
            -fx-font-size: 15;
            -fx-background-radius: 20;
            -fx-padding: 10 20;
        """);

        Button photoBtn = new Button("📎");
        photoBtn.setStyle("""
            -fx-background-color: #1e293b;
            -fx-text-fill: white;
            -fx-background-radius: 20;
            -fx-padding: 8 12;
        """);

        photoBtn.setOnAction(e -> openImage(stage));

        HBox inputBox = new HBox(10, photoBtn, input, sendBtn);
        inputBox.setPadding(new Insets(10));
        inputBox.setAlignment(Pos.CENTER);
        HBox.setHgrow(input, Priority.ALWAYS);

        Button faqBtn = new Button("❓");
        faqBtn.setStyle("""
            -fx-background-color: #22c55e;
            -fx-text-fill: black;
            -fx-font-size: 16;
            -fx-background-radius: 50%;
            -fx-min-width: 42;
            -fx-min-height: 42;
            -fx-max-width: 42;
            -fx-max-height: 42;
        """);
        Tooltip.install(faqBtn, new Tooltip("FAQs"));

        ContextMenu faqMenu = new ContextMenu();
        for (String q : faqManager.getAllQuestions()) {
            MenuItem item = new MenuItem(q);
            item.setOnAction(e -> {
                input.setText(q);
                input.requestFocus();
            });
            faqMenu.getItems().add(item);
        }

        faqBtn.setOnAction(e ->
            faqMenu.show(faqBtn, Side.TOP, 0, -5)
        );

        /* =========================
           SEND MESSAGE
        ========================= */
        Runnable sendMessage = () -> {
            String msg = input.getText().trim();
            if (msg.isEmpty()) return;

            addUserMessage(msg);
            input.clear();
            // Check FAQ first
            String faqAnswer = faqManager.getAnswer(msg);
            if (faqAnswer != null) {
                createBotMessage();
                appendToLastBotMessage(faqAnswer);
                return;
            }
            createBotMessage();

            new Thread(() ->
                bot.askStream(msg, new StreamListener() {

                    @Override
                    public void onToken(String token) {
                        Platform.runLater(() ->
                                appendToLastBotMessage(token)
                        );
                    }

                    @Override
                    public void onComplete() {}

                    @Override
                    public void onError(String error) {
                        Platform.runLater(() ->
                                appendToLastBotMessage("\n[Error: " + error + "]")
                        );
                    }
                })
            ).start();
        };

        input.setOnAction(e -> sendMessage.run());
        sendBtn.setOnAction(e -> sendMessage.run());

        /* =========================
           ROOT
        ========================= */
        StackPane root = new StackPane();
        BorderPane mainLayout = new BorderPane();
        mainLayout.setTop(navbar);
        mainLayout.setCenter(scrollPane);
        mainLayout.setBottom(inputBox);
        mainLayout.setStyle("-fx-background-color: #0f172a;");
        StackPane.setAlignment(faqBtn, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(faqBtn, new Insets(0, 20, 70, 0));
        root.getChildren().addAll(mainLayout, faqBtn);


        Scene scene = new Scene(root, 900, 650);
        stage.setTitle("NovaGPT");
        stage.setScene(scene);
        stage.show();
    }

    /* =========================
       USER MESSAGE
    ========================= */
    private void addUserMessage(String text) {
        Label msg = new Label(text);
        msg.setWrapText(true);
        msg.setMaxWidth(700);
        msg.setPadding(new Insets(10));
        msg.setStyle("""
            -fx-background-color: #22c55e;
            -fx-text-fill: black;
            -fx-font-size: 15;
            -fx-background-radius: 15;
        """);

        HBox box = new HBox(msg);
        box.setAlignment(Pos.CENTER_RIGHT);
        box.setPadding(new Insets(5));

        chatContainer.getChildren().add(box);
        scrollToBottom();
    }

    /* =========================
       BOT MESSAGE (STREAM)
    ========================= */
    private void createBotMessage() {

        Label msg = new Label("");
        msg.setWrapText(true);
        msg.setMaxWidth(700);
        msg.setPadding(new Insets(10));
        msg.setStyle("""
            -fx-background-color: #1e293b;
            -fx-text-fill: white;
            -fx-font-size: 15;
            -fx-background-radius: 15;
        """);

        Button copyBtn = new Button("📋");
        copyBtn.setTooltip(new Tooltip("Copy"));
        copyBtn.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #94a3b8;
        """);

        copyBtn.setOnAction(e -> {
            ClipboardContent content = new ClipboardContent();
            content.putString(msg.getText());
            Clipboard.getSystemClipboard().setContent(content);
        });

        VBox bubble = new VBox(6, msg, copyBtn);
        bubble.setAlignment(Pos.BOTTOM_LEFT);

        HBox box = new HBox(bubble);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(5));

        chatContainer.getChildren().add(box);
        scrollToBottom();
    }

    private void appendToLastBotMessage(String token) {
        HBox box = (HBox) chatContainer.getChildren()
                .get(chatContainer.getChildren().size() - 1);
        VBox bubble = (VBox) box.getChildren().get(0);
        Label msg = (Label) bubble.getChildren().get(0);
        msg.setText(msg.getText() + token);
        scrollToBottom();
    }

    /* =========================
       IMAGE PICKER
    ========================= */
    private void openImage(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Select Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg")
        );

        File file = chooser.showOpenDialog(stage);
        if (file != null) {
            addUserMessage("📷 Image selected: " + file.getName());
        }
    }

    private void scrollToBottom() {
        Platform.runLater(() -> {
            ScrollPane sp = (ScrollPane) chatContainer.getParent();
            sp.setVvalue(1.0);
        });
    }

    public static void main(String[] args) {
        launch();
    }
}