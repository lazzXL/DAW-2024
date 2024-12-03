import * as React from "react";
import { fetchMessages, Message } from "../fakeApiService";
import { Channel } from "../domain/Channel";
import { AuthContext } from "../AuthProvider";

type MessagePanelState = {
    messages: Message[];
    loading: boolean;
    message: string;
    error: string | null;
};

type Action =
    | { type: "FETCH_MESSAGES_START" }
    | { type: "FETCH_MESSAGES_SUCCESS"; payload: Message[] }
    | { type: "FETCH_MESSAGES_ERROR"; payload: string }
    | { type: "SEND_MESSAGE"; payload: Message }
    | { type: "SET_MESSAGE"; payload: string }
    | { type: "RESET_MESSAGES" };

function messagePanelReducer(state: MessagePanelState, action: Action): MessagePanelState {
    switch (action.type) {
        case "FETCH_MESSAGES_START":
            return { ...state, loading: true, error: null };
        case "FETCH_MESSAGES_SUCCESS":
            return { ...state, messages: action.payload, loading: false };
        case "FETCH_MESSAGES_ERROR":
            return { ...state, loading: false, error: action.payload };
        case "SEND_MESSAGE":
            return { ...state, messages: [...state.messages, action.payload], message: "" };
        case "SET_MESSAGE":
            return { ...state, message: action.payload };
        case "RESET_MESSAGES":
            return { ...state, messages: [], loading: false, error: null };
        default:
            return state;
    }
}

export function MessagePanel({ channel }: { channel: Channel | null }) {
    const { token } = React.useContext(AuthContext);
    const [state, dispatch] = React.useReducer(messagePanelReducer, {
        messages: [],
        loading: false,
        message: "",
        error: null,
    });

    React.useEffect(() => {
        if (channel) {
            dispatch({ type: "FETCH_MESSAGES_START" });
            fetchMessages(channel.name)
                .then((msgs) => {
                    dispatch({ type: "FETCH_MESSAGES_SUCCESS", payload: msgs as Message[] });
                })
                .catch((error: string) => {
                    dispatch({ type: "FETCH_MESSAGES_ERROR", payload: error });
                });
        } else {
            dispatch({ type: "RESET_MESSAGES" });
        }
    }, [channel]);

    const handleSendMessage = () => {
        if (state.message.trim() && channel) {
            const newMessage: Message = {
                channel: channel.name,
                sender: "You",
                content: state.message.trim(),
                timestamp: new Date().toLocaleString(),
            };
            dispatch({ type: "SEND_MESSAGE", payload: newMessage });
        }
    };

    const handleLeaveChannel = () => {
        if (channel) {
            fetch(`participant/leave/${channel.id}`, { method: "DELETE", headers: { "Authorization": `Bearer ${token}` } })
                .then((response) => {
                    if (response.ok) {
                        alert("Successfully left channel");
                        //Todo: Refresh when we have cookies support
                    } else {
                        throw new Error("Failed to leave channel");
                    }
                    dispatch({ type: "RESET_MESSAGES" });
                })
                .catch((error: string) => {
                    dispatch({ type: "FETCH_MESSAGES_ERROR", payload: error });
                });
        }
    };
        
    

    const handleRetry = () => {
        if (channel) {
            dispatch({ type: "FETCH_MESSAGES_START" });
            fetchMessages(channel.name)
                .then((msgs) => {
                    dispatch({ type: "FETCH_MESSAGES_SUCCESS", payload: msgs as Message[] });
                })
                .catch((error: string) => {
                    dispatch({ type: "FETCH_MESSAGES_ERROR", payload: error });
                });
        }
    };

    return (
        <div className="message-panel">
            <div className="message-panel-header">
                {channel?.name || "No Channel Selected"}
                {channel && (
                    <div className = "parent-container">
                    <button className="leave-button" onClick={handleLeaveChannel}>
                        Leave
                    </button>
                    </div>
                )}
            </div>
            
            <div className="message-panel-messages">
                {state.loading ? (
                    <p>Loading messages...</p>
                ) : state.error ? (
                    <div>
                        <p>{state.error}</p>
                        <button onClick={handleRetry}>Retry</button>
                    </div>
                ) : channel ? (
                    state.messages.map((msg, index) => (
                        <div
                            key={index}
                            className={`message-bubble ${msg.sender === "You" ? "sent" : "received"}`}
                        >
                            <div className="message-info">
                                <span className="message-sender">{msg.sender}</span>
                                <span className="message-timestamp">{msg.timestamp}</span>
                            </div>
                            <div className="message-content">{msg.content}</div>
                        </div>
                    ))
                ) : (
                    <p>Select a channel to view messages</p>
                )}
            </div>
            {!state.loading && !state.error && channel && (
                <div className="message-panel-footer">
                    <input
                        type="text"
                        value={state.message}
                        onChange={(e) => dispatch({ type: "SET_MESSAGE", payload: e.target.value })}
                        placeholder="Type a message..."
                        disabled={state.loading}
                    />
                    <button onClick={handleSendMessage} disabled={state.loading || !state.message.trim()}>
                        Send
                    </button>
                </div>
            )}
        </div>
    );
}
