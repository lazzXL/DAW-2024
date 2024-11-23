import * as React from "react";
import { fetchMessages, Message } from "../fakeApiService";

type MessagePanelProps = {
    channel: string | null;
};

export function MessagePanel({ channel }: MessagePanelProps) {
    const [messages, setMessages] = React.useState<Message[]>([]);
    const [loading, setLoading] = React.useState(false);
    const [message, setMessage] = React.useState("");

    React.useEffect(() => {
        if (channel) {
            setLoading(true);
            fetchMessages(channel).then((msgs) => {
                setMessages(msgs);
                setLoading(false);
            });
        } else {
            setMessages([]);
        }
    }, [channel]);

    const handleSendMessage = () => {
        if (message.trim() && channel) {
            const newMessage: Message = {
                channel,
                sender: "You",
                content: message.trim(),
                timestamp: new Date().toLocaleString(),
            };
            setMessages((prev) => [...prev, newMessage]);
            setMessage("");
        }
    };

    return (
        <div className="message-panel">
            <div className="message-panel-header">{channel || "No Channel Selected"}</div>
            <div className="message-panel-messages">
                {loading ? (
                    <p>Loading messages...</p>
                ) : channel ? (
                    messages.map((msg, index) => (
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
            {channel && (
                <div className="message-panel-footer">
                    <input
                        type="text"
                        value={message}
                        onChange={(e) => setMessage(e.target.value)}
                        placeholder="Type a message..."
                    />
                    <button onClick={handleSendMessage}>Send</button>
                </div>
            )}
        </div>
    );
}
