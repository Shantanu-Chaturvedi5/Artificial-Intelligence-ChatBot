import java.util.HashMap;
import java.util.Map;

public class FAQManager {

    private final Map<String, String> faqAnswers;

    public FAQManager() {
        faqAnswers = new HashMap<>();
        faqAnswers.put("What can you do?", 
            "I can answer questions, provide recommendations, chat with you, and help with problem-solving!");
        faqAnswers.put("How does this chatbot work?", 
            "I use an AI model to understand your text and respond in real-time. You can also ask FAQs for quick answers.");
        faqAnswers.put("Which model are you using?", 
            "I am currently using the Mistral model for generating responses.");
        faqAnswers.put("Can you analyze images?", 
            "Yes! I can provide insights on images you upload using the LLAVA model.");
        faqAnswers.put("Who created you?", 
            "I was created by Shantanu Chaturvedi.");
    }

    /** 
     * Returns the answer if input matches a FAQ, otherwise null.
     */
    public String getAnswer(String question) {
        return faqAnswers.getOrDefault(question, null);
    }

    /** 
     * Returns all FAQ questions for populating the menu.
     */
    public String[] getAllQuestions() {
        return faqAnswers.keySet().toArray(new String[0]);
    }
}