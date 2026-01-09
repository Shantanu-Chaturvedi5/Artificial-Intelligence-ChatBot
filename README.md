# 🤖 NovaGPT – JavaFX AI Chatbot

NovaGPT is a **Java-based AI chatbot application** with a modern ChatGPT-like interface. It supports **real-time streaming responses**, **FAQ-based rule handling**, and integration with **open-source large language models (LLMs)** via **Ollama**.

---

## ✨ Key Features

### 💬 Interactive Chat Interface

* Clean, modern **ChatGPT-style UI** built using **JavaFX**
* Left–right aligned chat bubbles (User ↔ Bot)
* Automatic text wrapping (no horizontal scrolling)
* Messages dynamically resize based on content length

### ⚡ Real-Time Streaming Responses

* Bot responses stream **token by token** for a natural typing effect
* Uses `StreamListener` for asynchronous updates

### 🧠 AI-Powered Conversations

* Integrates with **Ollama API**
* Supports models like **Mistral / LLaMA**
* Uses **Machine Learning–based NLP** for intelligent replies

### 📚 FAQ System (Rule-Based Training)

* Built-in **FAQ Manager** with predefined questions and answers
* Circular FAQ button at the bottom-right of the UI
* One-click insertion of FAQs into the chat
* Demonstrates **rule-based chatbot logic**

### 📋 Copy-to-Clipboard

* Each bot message includes a **copy button**
* Tooltip appears on hover (`Copy`)

### 🖼️ Image Support (Extensible)

* Image picker integrated in the UI
* Ready for multimodal models like **LLaVA**

---

## 🧩 Project Structure

```
AI-ChatBot/
│
├── ChatUI.java          # JavaFX UI (chat layout, FAQ button, input bar)
├── ChatbotEngine.java   # Handles API calls & streaming responses
├── StreamListener.java  # Streaming callback interface
├── FAQManager.java     # Rule-based FAQ question–answer logic
├── lib/                 # External libraries (Gson, etc.)
└── README.md            # Project documentation
```

---

## 🛠️ Technologies Used

* **Java 17+**
* **JavaFX** (GUI)
* **Ollama API** (LLM backend)
* **Mistral / LLaMA models**
* **Gson** (JSON parsing)

---

## 🚀 How to Compile & Run

### 🔹 Compile

```bash
javac -d bin \
 -cp ".;lib/*" \
 --module-path "<path-to-javafx-lib>" \
 --add-modules javafx.controls \
 ChatUI.java ChatbotEngine.java FAQManager.java StreamListener.java
```

### 🔹 Run

```bash
java -cp "bin;lib/*" \
 --module-path "<path-to-javafx-lib>" \
 --add-modules javafx.controls \
 ChatUI
```

> ⚠️ Make sure **Ollama is running locally**:

```bash
ollama run mistral
```

---

## 📸 Screenshots

### 🖥️ Main Chat Interface

<img width="1918" height="1141" alt="Screenshot 2026-01-08 225401" src="https://github.com/user-attachments/assets/8d61acd1-c52c-4b17-8b4b-841529c1c961" />


### ❓ FAQ Menu

<img width="1906" height="1139" alt="Screenshot 2026-01-08 225543" src="https://github.com/user-attachments/assets/e74ad971-c528-4c4a-97a0-838415e268a3" />


### 🔄 Streaming Response

<img width="1919" height="1139" alt="image" src="https://github.com/user-attachments/assets/37350b92-092e-4ce4-ad6c-bce176edfd63" />


---

## 📌 Project Requirements Checklist

| Requirement                   | Status            |
| ----------------------------- | ----------------- |
| Java-based project            | ✅                 |
| Interactive chatbot           | ✅                 |
| NLP techniques                | ✅ (via ML models) |
| ML or rule-based logic        | ✅ (Hybrid)        |
| FAQ training                  | ✅                 |
| GUI for real-time interaction | ✅                 |

---

## 🔮 Future Enhancements

* Persistent chat history
* Local NLP intent detection
* User authentication
* Dark/Light theme toggle
* Web-based frontend

---


⭐ *If you like this project, feel free to expand it or integrate additional AI models!*
