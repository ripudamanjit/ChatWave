// =========================================
// 1. GLOBAL STATE
// =========================================
let stompClient = null;
let currentUser = null; // Object: {id, username, email}
let currentChatId = null;
let currentSubscription = null;

// API Base URL
const API_BASE = 'http://localhost:8090/api';

// State for Group Creation Modal
let currentGroupParticipants = [];

// =========================================
// 2. AUTHENTICATION (Login / Register)
// =========================================

function openAuth(mode) {
    document.getElementById('landing-page').classList.add('hidden');
    document.getElementById('auth-wrapper').classList.remove('hidden');

    // Set UI based on mode
    const title = document.getElementById('auth-title');
    const btn = document.getElementById('auth-btn');
    const toggle = document.getElementById('toggle-text');
    const nameGroup = document.getElementById('name-group');
    const emailGroup = document.getElementById('email-group');

    if (mode === 'register') {
        title.innerText = "Create Account";
        btn.innerText = "Sign Up";
        toggle.innerText = "Already have an account? Login";
        nameGroup.style.display = 'none';
        emailGroup.style.display = 'block';
        document.getElementById('auth-wrapper').dataset.mode = 'register';
    } else {
        title.innerText = "Welcome Back";
        btn.innerText = "Login";
        toggle.innerText = "Don't have an account? Register";
        nameGroup.style.display = 'none';
        emailGroup.style.display = 'none';
        document.getElementById('auth-wrapper').dataset.mode = 'login';
    }
}

function toggleAuthMode() {
    const currentMode = document.getElementById('auth-wrapper').dataset.mode;
    openAuth(currentMode === 'login' ? 'register' : 'login');
}

async function handleAuth() {
    const mode = document.getElementById('auth-wrapper').dataset.mode;
    const username = document.getElementById('login-username').value;
    const password = document.getElementById('password').value;
    const email = document.getElementById('email').value;
    const errorMsg = document.getElementById('login-error');

    if (!username || !password) {
        errorMsg.innerText = "Username and password are required";
        errorMsg.classList.remove('hidden');
        return;
    }

    const endpoint = mode === 'register' ? '/users/register' : '/users/login';
    const payload = mode === 'register'
        ? { username, password, email }
        : { username, password };

    try {
        const response = await fetch(API_BASE + endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            currentUser = await response.json();
            // Success: Switch to Dashboard
            document.getElementById('auth-wrapper').classList.add('hidden');
            document.getElementById('dashboard-screen').classList.remove('hidden');
            document.getElementById('display-username').innerText = currentUser.username;

            // Connect to Websocket and Load Chats
            connectWebSocket();
            loadChats();
        } else {
            const text = await response.text();
            errorMsg.innerText = text || "Authentication failed";
            errorMsg.classList.remove('hidden');
        }
    } catch (error) {
        console.error(error);
        errorMsg.innerText = "Server error. Is the backend running?";
        errorMsg.classList.remove('hidden');
    }
}

function logout() {
    if (stompClient) stompClient.disconnect();
    location.reload();
}

// =========================================
// 3. WEBSOCKET CONNECTION
// =========================================

function connectWebSocket() {

    const socket = new SockJS('http://localhost:8090/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, onConnected, onError);
}

function onConnected() {
    stompClient.subscribe('/topic/public', onMessageReceived);
}

function onError(error) {
    console.error('WebSocket Error:', error);
}

function sendMessage() {
    const input = document.getElementById('message-input');
    const content = input.value.trim();

    if (content && stompClient && currentChatId) {
        const chatMessage = {
            senderId: currentUser.username,
            chatId: currentChatId,
            content: content,
            type: 'CHAT'
        };

        stompClient.send("/app/chat.sendMessage", {}, JSON.stringify(chatMessage));
        input.value = '';
    }
}

function onMessageReceived(payload) {
    const message = JSON.parse(payload.body);

    // Only display the message if it belongs to the currently open chat
    if (message.chatId === currentChatId) {
        displayMessage(message);
        scrollToBottom();
    }
}

// =========================================
// 4. CHAT MANAGEMENT & SIDEBAR
// =========================================

async function loadChats() {
    try {
        const response = await fetch(`${API_BASE}/chats/user/${currentUser.username}`);
        if (response.ok) {
            const chats = await response.json();
            renderChatList(chats);
        }
    } catch (error) {
        console.error("Failed to load chats", error);
    }
}

function renderChatList(chats) {
    const list = document.getElementById('chat-list');
    list.innerHTML = '';

    chats.forEach(chat => {
        const div = document.createElement('div');
        div.className = 'chat-item';
        div.onclick = () => selectChat(chat);

        // Determine Display Name
        let displayName = chat.name;
        if (chat.type === 'PRIVATE') {
            displayName = chat.participants.find(p => p !== currentUser.username);
        }

        div.innerHTML = `
            <div class="chat-avatar">${displayName.charAt(0).toUpperCase()}</div>
            <div class="chat-info">
                <div class="chat-name">${displayName}</div>
                <div class="chat-last-msg" style="font-size: 12px; color: #94a3b8;">${chat.type}</div>
            </div>
        `;
        list.appendChild(div);
    });
}

async function selectChat(chat) {
    currentChatId = chat.id;

    // Update Header Name
    let displayName = chat.name;
    if (chat.type === 'PRIVATE') {
        displayName = chat.participants.find(p => p !== currentUser.username);
    }
    document.getElementById('current-chat-name').innerText = displayName;

    // --- NEW: Toggle "Add Member" Button ---
    const addBtn = document.getElementById('add-member-btn');
    if (chat.type === 'GROUP') {
        addBtn.classList.remove('hidden');
    } else {
        addBtn.classList.add('hidden');
    }
    // ---------------------------------------

    // Enable Inputs
    document.getElementById('message-input').disabled = false;
    document.getElementById('send-btn').disabled = false;

    // Mobile View Toggle
    document.body.classList.add('mobile-chat-active');
    document.getElementById('mobile-back-btn').classList.remove('hidden');

    // Load History
    await loadChatHistory(chat.id);
}

async function loadChatHistory(chatId) {
    const container = document.getElementById('messages-list');
    container.innerHTML = '<p style="text-align:center; color:gray">Loading...</p>';

    try {
        const response = await fetch(`${API_BASE}/messages/${chatId}`);
        if (response.ok) {
            const messages = await response.json();
            container.innerHTML = '';
            messages.forEach(displayMessage);
            scrollToBottom();
        }
    } catch (error) {
        console.error("Error loading history", error);
    }
}

function displayMessage(message) {
    const container = document.getElementById('messages-list');

    const div = document.createElement('div');
    const isMe = message.senderId === currentUser.username;

    div.className = `message ${isMe ? 'my-message' : 'other-message'}`;

    // Show sender name in groups if it's not me
    let senderLabel = '';
    if (!isMe) {
        senderLabel = `<div style="font-size:10px; opacity:0.7; margin-bottom:2px;">${message.senderId}</div>`;
    }

    div.innerHTML = `${senderLabel}${message.content}`;
    container.appendChild(div);
}

// =========================================
// 5. MODAL SYSTEM (Create & Add Member)
// =========================================

function startNewChat() {
    document.getElementById('modal-overlay').classList.remove('hidden');
    document.getElementById('modal-selection').classList.remove('hidden');
    // Ensure others are hidden
    document.getElementById('modal-private').classList.add('hidden');
    document.getElementById('modal-group').classList.add('hidden');
    document.getElementById('modal-add-member').classList.add('hidden');
}

function closeModal() {
    document.getElementById('modal-overlay').classList.add('hidden');
    // Reset Data
    currentGroupParticipants = [];
    document.getElementById('group-participants-list').innerHTML = '';
    document.getElementById('group-name').value = '';
    document.getElementById('private-username').value = '';
    document.getElementById('group-user-input').value = '';
    document.getElementById('add-member-username').value = '';
}

function showPrivateModal() {
    document.getElementById('modal-selection').classList.add('hidden');
    document.getElementById('modal-private').classList.remove('hidden');
}

function showGroupModal() {
    document.getElementById('modal-selection').classList.add('hidden');
    document.getElementById('modal-group').classList.remove('hidden');

    // Auto-add myself
    currentGroupParticipants = [currentUser.username];
    renderGroupParticipants();
}

function backToSelection() {
    document.getElementById('modal-private').classList.add('hidden');
    document.getElementById('modal-group').classList.add('hidden');
    document.getElementById('modal-selection').classList.remove('hidden');
}

// --- Group Creation Logic ---

function addGroupParticipant() {
    const input = document.getElementById('group-user-input');
    const username = input.value.trim();

    if (username && !currentGroupParticipants.includes(username)) {
        currentGroupParticipants.push(username);
        renderGroupParticipants();
        input.value = '';
    } else if (currentGroupParticipants.includes(username)) {
        alert("User already added!");
    } else if (!username) {
        alert("Please enter a username");
    }
}

function removeParticipant(username) {
    if (username === currentUser.username) return;
    currentGroupParticipants = currentGroupParticipants.filter(u => u !== username);
    renderGroupParticipants();
}

function renderGroupParticipants() {
    const container = document.getElementById('group-participants-list');
    container.innerHTML = '';

    currentGroupParticipants.forEach(user => {
        const tag = document.createElement('div');
        tag.className = 'user-tag';

        const removeBtn = user === currentUser.username
            ? '(You)'
            : `<span class="remove-user" onclick="removeParticipant('${user}')">×</span>`;

        tag.innerHTML = `${user} ${removeBtn}`;
        container.appendChild(tag);
    });
}

// --- Add Member to Existing Group Logic (NEW) ---

function showAddMemberModal() {
    document.getElementById('modal-overlay').classList.remove('hidden');
    document.getElementById('modal-selection').classList.add('hidden');
    document.getElementById('modal-private').classList.add('hidden');
    document.getElementById('modal-group').classList.add('hidden');

    // Show Add Member Modal
    document.getElementById('modal-add-member').classList.remove('hidden');
}

async function submitAddMember() {
    const username = document.getElementById('add-member-username').value.trim();
    if (!username) return alert("Please enter a username");

    if (!currentChatId) return;

    try {
        const response = await fetch(`${API_BASE}/chats/${currentChatId}/add`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: username
        });

        if (response.ok) {
            alert("User added successfully!");
            closeModal();
            // Optional: Reload chats if you want to update participant counts immediately
        } else {
            const errorText = await response.text();
            alert("Error: " + errorText);
        }
    } catch (error) {
        console.error(error);
        alert("Failed to add user. Check console.");
    }
}

// --- API Calls for Creation ---

async function createPrivateChat() {
    const otherUser = document.getElementById('private-username').value.trim();
    if (!otherUser) return alert("Please enter a username");

    const payload = {
        type: "PRIVATE",
        participants: [currentUser.username, otherUser]
    };

    await sendCreateRequest('/chats/create', payload);
}

async function submitGroupChat() {
    const groupName = document.getElementById('group-name').value.trim();

    if (!groupName) return alert("Group name is required");
    if (currentGroupParticipants.length < 3) return alert("Minimum 3 participants required");

    const payload = {
        name: groupName,
        type: "GROUP",
        participants: currentGroupParticipants
    };

    await sendCreateRequest('/chats/create-group', payload);
}

async function sendCreateRequest(endpoint, payload) {
    try {
        const response = await fetch(API_BASE + endpoint, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });

        if (response.ok) {
            const chat = await response.json();
            closeModal();
            await loadChats();
            selectChat(chat);
        } else {
            const errorMsg = await response.text();
            alert("Error: " + errorMsg);
        }
    } catch (error) {
        console.error("Error creating chat:", error);
        alert("Failed to connect to server.");
    }
}


// =========================================
// 6. UTILITIES / UI HELPERS
// =========================================

function scrollToBottom() {
    const container = document.getElementById('messages-list');
    container.scrollTop = container.scrollHeight;
}

function handleKeyPress(event) {
    if (event.key === 'Enter') {
        sendMessage();
    }
}

function showChatList() {
    document.body.classList.remove('mobile-chat-active');
    document.getElementById('mobile-back-btn').classList.add('hidden');
    currentChatId = null;
}