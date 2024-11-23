import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import { router } from "./router";
import "./MessagingApp.css";


export function messagingAppDemo() {
    ReactDOM.createRoot(document.getElementById("container")).render(
        <RouterProvider router={router} future={{ v7_startTransition: true }} />
    )
}
/*
const router = createBrowserRouter([
    {
        "path": "/",
        "element": <MessagingApp />,
    },
    {
        "path": "/profile",
        "element": <Profile />
    },
    {
        "path": "/settings",
        "element": <Settings />
    },
    {
        "path": "/discover-channels",
        "element": <DiscoverPublic />
    },
])


function Settings() {
    return <div>Settings Page</div>;
}

function Profile() {
    return <div>Profile Page</div>;
}

function DiscoverPublic() {
    return <div>Discover Public Channels</div>;
}

type MessagingState = {
    selectedChannel: string | null;
    messages: { channel: string; sender: string; content: string; timestamp: string }[];
};

type Action =
    | { type: "select-channel"; channel: string }
    | { type: "send-message"; channel: string; sender: string; content: string };

function messagingReducer(state: MessagingState, action: Action): MessagingState {
    switch (action.type) {
        case "select-channel":
            return { ...state, selectedChannel: action.channel };
        case "send-message":
            return {
                ...state,
                messages: [
                    ...state.messages,
                    {
                        channel: action.channel,
                        sender: action.sender,
                        content: action.content,
                        timestamp: new Date().toLocaleString(),
                    },
                ],
            };
        default:
            return state;
    }
}


function MessagingApp() {
    const [state, dispatch] = React.useReducer(messagingReducer, {
        selectedChannel: null,
        messages: [],
    });
    const [channels, setChannels] = React.useState<string[]>([]);
    const [loadingMessages, setLoadingMessages] = React.useState(false);

    React.useEffect(() => {
        fetchChannels().then(setChannels);
    }, []);

    const handleSelectChannel = (channel: string) => {
        dispatch({ type: "select-channel", channel });
        setLoadingMessages(true);

        fetchMessages(channel).then((msgs) => {
            setLoadingMessages(false);
            dispatch({ type: "send-message", channel, sender: "", content: "" }); // Clear old messages
            msgs.forEach((msg) =>
                dispatch({
                    type: "send-message",
                    channel: msg.channel,
                    sender: msg.sender,
                    content: msg.content,
                })
            );
        });
    };

    return (
        <div style={{ display: "flex", height: "100vh", fontFamily: "Arial, sans-serif" }}>
            <Sidebar />
            <ChannelList
                selectedChannel={state.selectedChannel}
                onSelectChannel={handleSelectChannel}
                channels={channels}
            />
            <MessagePanel
                channel={state.selectedChannel}
                messages={state.messages.filter((msg) => msg.channel === state.selectedChannel)}
                onSendMessage={(content) => {
                    if (state.selectedChannel) {
                        dispatch({
                            type: "send-message",
                            channel: state.selectedChannel,
                            sender: "You",
                            content,
                        });
                    }
                }}
                loading={loadingMessages}
            />
        </div>
    );
}



function Sidebar() {
    return (
        <div className="sidebar">
            <div>
                <Link to="/">
                    <img src="chimp.png" alt="App Logo" style={{ cursor: "pointer" }} />
                </Link>
            </div>

            <button>
                <Link to="/discover-channels" style={{ textDecoration: "none", color: "inherit" }}>
                    🧭
                </Link>
            </button>

            <div className="spacer"></div>

            <button>
                <Link to="/settings" style={{ textDecoration: "none", color: "inherit" }}>
                    ⚙
                </Link>
            </button>

            <button>
                <Link to="/profile" style={{ textDecoration: "none", color: "inherit" }}>
                    👤
                </Link>
            </button>
        </div>
    );
}



type ChannelListProps = {
    selectedChannel: string | null;
    onSelectChannel: (channel: string) => void;
    channels: string[];
};

function ChannelList({ selectedChannel, onSelectChannel, channels }: ChannelListProps) {
    return (
        <div className="channel-list">
            <div className="channel-list-header">Channels</div>
            <div>
                {channels.map((channel) => (
                    <div
                        key={channel}
                        onClick={() => onSelectChannel(channel)}
                        className={`channel-list-item ${
                            selectedChannel === channel ? "active" : ""
                        }`}
                    >
                        {channel}
                    </div>
                ))}
            </div>
        </div>
    );
}

type MessagePanelProps = {
    channel: string | null;
    messages: Message[];
    onSendMessage: (content: string) => void;
    loading: boolean;
};

function MessagePanel({ channel, messages, onSendMessage, loading }: MessagePanelProps) {
    const [message, setMessage] = React.useState("");
    const currentSender = "You";

    const handleSendMessage = () => {
        if (message.trim()) {
            onSendMessage(message.trim());
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
                            className={`message-bubble ${
                                msg.sender === currentSender ? "sent" : "received"
                            }`}
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


const iconButtonStyle: React.CSSProperties = {
    backgroundColor: "transparent",
    border: "none",
    color: "white",
    fontSize: "20px",
    margin: "10px 0",
    cursor: "pointer",
};
*/