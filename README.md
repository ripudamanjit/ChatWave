<img width="1920" height="1080" alt="Screenshot 2025-11-28 212727" src="https://github.com/user-attachments/assets/ee2e5c2d-df47-4753-b0eb-9488b891cf49" />
<img width="1920" height="1080" alt="Screenshot 2025-11-28 212750" src="https://github.com/user-attachments/assets/6f3f0f92-7f82-4a88-a01c-d157db81415e" />
<img width="1920" height="1080" alt="Screenshot 2025-11-28 212755" src="https://github.com/user-attachments/assets/f3ef8378-3485-4210-bc5d-ae16cb3294e8" />
<img width="1920" height="1080" alt="Screenshot 2025-11-28 212820" src="https://github.com/user-attachments/assets/b4fcd5a5-22e7-45fd-b68d-7dac269ed6c9" />
<img width="1920" height="1080" alt="Screenshot 2025-11-28 212829" src="https://github.com/user-attachments/assets/a4c6f8ae-b1b7-4f69-883f-7d30a9314077" />
<img width="1920" height="1080" alt="Screenshot 2025-11-28 212840" src="https://github.com/user-attachments/assets/fd1a6675-d812-46fa-9593-56844491270f" />
# 🌊 Chatwave - Real-Time Collaboration Platform

A modern, full-stack real-time chat application built for seamless
communication, featuring instant messaging, group chats, and live user
status updates.

## 📖 Overview

Chatwave is a robust messaging platform engineered to demonstrate the
power of event-driven architecture. Unlike traditional request-response
web apps, Chatwave utilizes **WebSockets (STOMP protocol)** to establish
a persistent, bi-directional connection between the client and server.
This enables sub-millisecond delivery and real-time collaboration.

The application follows a clean **Controller-Service-Repository**
backend structure.\
The frontend runs on **native HTML/CSS/JS**, served directly by Spring
Boot (static resources).

------------------------------------------------------------------------

## ✨ Key Features

### ⚡ Real-Time Messaging

Instant delivery using WebSocket + STOMP.

### 👥 Group Chats

Create dynamic chat rooms and broadcast to shared topics.

### 💾 Cloud Persistence

MongoDB Atlas stores all chat history securely.

### 🎨 Responsive UI

WhatsApp-inspired layout using Flexbox & Grid.

------------------------------------------------------------------------

## 🛠️ Tech Stack

### Frontend

-   **HTML5**
-   **CSS3**
-   **JavaScript (ES6+)**
-   **SockJS & StompJS**
-   Served using Spring Boot static folder (`src/main/resources/static`)

### Backend

-   **Java 17+**
-   **Spring Boot**
-   **Spring WebSocket**
-   **Spring Data MongoDB**
-   **Lombok**

### Database

-   **MongoDB Atlas (Cloud)** --- scalable, flexible NoSQL store.

------------------------------------------------------------------------

## 🏗️ System Design & Architecture

### Data Flow (Message Journey)

    graph TD
        User[👤 User / Web Client] -- "1. Sends Message (STOMP)" --> WS_Endpoint[🔌 WebSocket Endpoint]

        subgraph "Spring Boot Backend"
            WS_Endpoint --> Controller[🎮 ChatController]
            Controller --> Service[⚙️ ChatService]

            Service -- "2. Process Logic" --> Repository[🗄️ ChatRepository]
            Repository -- "3. Save Data" --> Atlas[(☁️ MongoDB Atlas)]

            Service -- "4. Publish to Topic" --> Broker[📡 SimpMessagingTemplate (Broker)]
        end

        Broker -- "5. Push Message" --> Recipient[👤 Recipient / Group Subscribers]

------------------------------------------------------------------------

## 🔍 Layer Breakdown

### **1. Frontend (Web Client)**

Sends user messages through:

``` javascript
stompClient.send("/app/chat", {}, JSON.stringify(payload));
```

### **2. Controller Layer**

-   Listens to `/app/chat`, `/app/group`, etc.
-   Entry point for all WebSocket messages.

### **3. Service Layer**

-   Business logic
-   Validates room
-   Adds timestamps
-   Calls repository to store message

### **4. Repository Layer**

Interfaces with MongoDB via `MongoRepository`.\
Collections: - `chat_messages` - `chat_rooms`

### **5. Message Broker**

Publishes messages to topic: - `/topic/messages` -
`/user/{id}/queue/messages`

------------------------------------------------------------------------

## 🚀 Future Roadmap

### 1. 🗑️ Delete Chat & Group Management

Use **soft delete** (`isDeleted: true`) for safe auditing.

### 2. 🤖 AI Chat Bot

Using **Spring AI** or **LangChain4j**.\
Messages tagged with `@bot` trigger an LLM response.

### 3. 🛡️ Spring Security (JWT + OAuth2)

-   Stateless WebSocket auth
-   Role-based access
-   Protect private queues `/user/{id}/queue`

### 4. 🌳 Trie-Based Profanity Filter

Replaces slow regex filtering with **O(L)** prefix-tree scanning.

------------------------------------------------------------------------

## 🏃‍♂️ How to Run

### **1. Prerequisites**

-   Java 17+
-   MongoDB Atlas Cluster

### **2. Clone Repo**

    git clone https://github.com/yourusername/chatwave.git

### **3. Configure Database**

Edit `application.properties`:

    spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.mongodb.net/chatwave

### **4. Run the App**

    ./mvnw spring-boot:run

### **5. Open in Browser**

    http://localhost:8080

------------------------------------------------------------------------

## ⚠️ CORS Configuration (if frontend runs separately)

Use when running frontend on Live Server or React:

``` java
@Override
public void registerStompEndpoints(StompEndpointRegistry registry) {
    registry.addEndpoint("/ws")
            .setAllowedOrigins("http://127.0.0.1:5500", "http://localhost:3000")
            .withSockJS();
}
```

------------------------------------------------------------------------

## 🎉 Happy Chatting with Chatwave! 🌊
