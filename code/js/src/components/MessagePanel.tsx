import * as React from "react";
import { Message } from "../fakeApiService";
import { Channel } from "../domain/Channel";
import { AuthContext } from "../AuthProvider";
import { ChannelDetailsModal } from "./ChannelDetails";
import { LeaveChannelModal } from "./LeaveChannelScreen";
import { MessagePanelState, MessageInput } from "../domain/MessagePanelProps";

export type Participant = {
    id: string;
    userId : number;
    name: string;
    permission: string;
};

type Action =
    | { type: "FETCH_START" }
    | { type: "FETCH_SUCCESS"; payload: { messages: Message[]; participants: Participant[] } }
    | { type: "FETCH_ERROR"; payload: string }
    | { type: "SEND_MESSAGE"; payload: Message }
    | { type: "RECEIVE_MESSAGE"; payload: Message }
    | { type: "SET_MESSAGE"; payload: string }
    | { type: "RESET" };

function messagePanelReducer(state: MessagePanelState, action: Action): MessagePanelState {
    switch (action.type) {
        case "FETCH_START":
            return { ...state, loading: true, error: null };
        case "FETCH_SUCCESS":
            return {
                ...state,
                messages: action.payload.messages,
                participants: action.payload.participants,
                loading: false,
            };
        case "FETCH_ERROR":
            return { ...state, loading: false, error: action.payload };
        case "SEND_MESSAGE":
            return { ...state, message: "" };
        case "RECEIVE_MESSAGE":
            return { ...state, messages: [...state.messages, action.payload] };
        case "SET_MESSAGE":
            return { ...state, message: action.payload };
        case "RESET":
            return { messages: [], participants: [], loading: false, error: null, message: "" };
        default:
            return state;
    }
}

export function MessagePanel({ channel }: { channel: Channel | null }) {
     const [state, dispatch] = React.useReducer(messagePanelReducer, {
        messages: [],
        participants: [],
        loading: false,
        error: null,
        message: "",
    });
    const [pressedLeave, setPressedLeave] = React.useState(false);
    const [isDetailsOpen, setIsDetailsOpen] = React.useState(false);
    const { token } = React.useContext(AuthContext);
    const handleLeaveChannel = () => {
        if (channel) {
            fetch(`/participant/leave/${channel.id}`, {
                method: "DELETE",
                headers: { Authorization: `Bearer ${token}` },
            })
                .then((response) => {
                    if (!response.ok) throw new Error("Failed to leave channel");
                    dispatch({ type: "RESET" });
                    window.location.reload();
                })
                .catch((error: Error) => {
                    console.error("Error leaving channel:", error.message);
                });
        }
    };

    React.useEffect(() => {
        if (!channel) {
            dispatch({ type: "RESET" });
            return;
        }

        dispatch({ type: "FETCH_START" });

        const fetchParticipants = fetch(`/participant/channel/${channel.id}`, {
            method: "GET",
            headers: { Authorization: `Bearer ${token}` },
        }).then((response) => {
            if (!response.ok) throw new Error("Failed to fetch participants");
            return response.json();
        });

        const fetchMessages = fetch(`/message/${channel.id}/`, {
            method: "GET",
            headers: { Authorization: `Bearer ${token}` },
        }).then((response) => {
            if (!response.ok) throw new Error("Failed to fetch messages");
            return response.json();
        });

        Promise.all([fetchParticipants, fetchMessages])
            .then(([participantsData, messagesData]) => {
                const participants = participantsData.map((participant: any) => ({
                    id: participant.id,
                    userId: participant.user.id,
                    name: participant.user.name,
                    permission: participant.permission,
                }));

                const messages = messagesData.map((message: any) => formattMessage(message));
                dispatch({
                    type: "FETCH_SUCCESS",
                    payload: { messages, participants },
                });
            })
            .catch((error: Error) => {
                dispatch({ type: "FETCH_ERROR", payload: error.message });
            });

        const eventSource = new EventSource(`/message/${channel.id}/listen`);

        eventSource.onmessage = (event) => {
            const newMessage = JSON.parse(event.data);
            dispatch({ type: "RECEIVE_MESSAGE", payload: formattMessage(newMessage) });
        };

        eventSource.onerror = () => {
            console.error("Error with SSE connection.");
            eventSource.close();
        };

        return () => {
            eventSource.close();
        };
    }, [channel, token]);

    
    const name = sessionStorage.getItem("username")
    
    return (
        <div className="message-panel">
            <MessagePanelHeader
                channel={channel}
                onDetailsClick={() => setIsDetailsOpen(true)}
                onLeaveChannel={() => setPressedLeave(true)}
            />
            <MessageList
                messages={state.messages}
                participants={state.participants}
                loading={state.loading}
                error={state.error}
            />
            {channel && name && state && state.participants && state.participants.find(it => it.name == name)?.permission == "READ_WRITE"  && (
                <MessagePanelFooter
                    message={state.message}
                    onMessageChange={(msg) =>
                        dispatch({ type: "SET_MESSAGE", payload: msg })
                    }
                    onSendMessage={() => {
                        if (state.message.trim() && channel) {
                            const newMessage: MessageInput = {
                                content: state.message.trim(),
                                channelId: channel.id,
                            };
                            fetch(`/message/send`, {
                                method: "POST",
                                headers: {
                                    Authorization: `Bearer ${token}`,
                                    "Content-Type": "application/json",
                                },
                                body: JSON.stringify(newMessage),
                            })
                                .then((response) => {
                                    if (!response.ok)
                                        throw new Error("Failed to send message");
                                    return response.json();
                                })
                                .then((message) => {
                                    dispatch({ type: "SEND_MESSAGE", payload: formattMessage(message) });
                                })
                                .catch((error) =>
                                    console.error("Error sending message:", error)
                                );
                        }
                    }}
                />
            )}
            {isDetailsOpen && channel && (
                <ChannelDetailsModal
                    channel={channel}
                    participants={state.participants}
                    onClose={() => setIsDetailsOpen(false)}
                />
            )}
            {pressedLeave && channel && (
                <LeaveChannelModal
                    channelName={channel.name}
                    onConfirm={() => {
                        handleLeaveChannel();
                    }}
                    onCancel={() => setPressedLeave(false)}
                />
            )}
        </div>
    );
}

function MessagePanelHeader({ channel, onDetailsClick, onLeaveChannel }: { channel: Channel | null; onDetailsClick: () => void; onLeaveChannel: () => void }) {
    return (
        <div className="message-panel-header">
            <span className="channel-title">{channel?.name || "No Channel Selected"}</span>
            {channel && (
                <div className="header-actions">
                    <button onClick={onLeaveChannel} className="leave-button">
                        Leave
                    </button>
                    <button onClick={onDetailsClick} className="details-button">
                        Channel Details
                    </button>
                </div>
            )}
        </div>
    );
}

function MessageList({
    messages,
    participants,
    loading,
    error,
}: {
    messages: Message[];
    participants: Participant[];
    loading: boolean;
    error: string | null;
}) {
    const getSenderName = (senderId: string) => {
        const participant = participants.find((p) => p.id === senderId);
        return participant ? participant.name : "Unknown";
    };

    if (loading) {
        return (
            <div className="message-panel-messages loading">
                <p>Loading...</p>
            </div>
        );
    }
    if (error) return <p>Error: {error}</p>;
    if (!messages.length)
        return (
            <div className="message-panel-messages">
                <p>No messages yet.</p>
            </div>
        );

    return (
        <div className="message-panel-messages">
            {messages.map((msg, index) => (
                <div
                    key={index}
                    className={`message-bubble ${msg.sender === "You" ? "sent" : "received"}`}
                >
                    <div className="message-info">
                        <span className="message-sender">{getSenderName(msg.sender)}</span>
                        <span className="message-timestamp">{msg.timestamp}</span>
                    </div>
                    <div className="message-content">{msg.content}</div>
                </div>
            ))}
        </div>
    );
}

function MessagePanelFooter({
    message,
    onMessageChange,
    onSendMessage,
}: {
    message: string;
    onMessageChange: (msg: string) => void;
    onSendMessage: () => void;
}) {
    return (
        <div className="message-panel-footer">
            <input
                type="text"
                value={message}
                onChange={(e) => onMessageChange(e.target.value)}
                placeholder="Type a message..."
            />
            <button onClick={onSendMessage} disabled={!message.trim()}>
                Send
            </button>
        </div>
    );
}

function formattMessage(newMessage : any ) : Message{
    return {
        id: newMessage.id,
        content: newMessage.content,
        sender: newMessage.sender,
        timestamp: new Intl.DateTimeFormat('en-US', {
            year: 'numeric',
            month: 'long',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit',
            second: '2-digit',
            hour12: false,
        }).format(new Date(newMessage.date))
    }
}
