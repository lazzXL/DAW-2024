import * as React from "react";
import { ChannelList } from "./ChannelList";
import { MessagePanel } from "./MessagePanel";
import { Channel } from "../domain/Channel";

export function MessagingApp() {
    const [selectedChannel, setSelectedChannel] = React.useState<Channel | null>(null);

    return (
        <div style={{ display: "flex", height: "100vh", fontFamily: "Arial, sans-serif" }}>
            <ChannelList
                selectedChannel={selectedChannel}
                onSelectChannel={(channel) => setSelectedChannel(channel)}
            />
            <MessagePanel channel={selectedChannel} />
        </div>
    );
}
