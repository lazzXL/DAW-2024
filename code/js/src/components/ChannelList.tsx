import * as React from "react";

type ChannelListProps = {
    selectedChannel: string | null;
    onSelectChannel: (channel: string) => void;
    channels: string[];
};

export function ChannelList({ selectedChannel, onSelectChannel, channels }: ChannelListProps) {
    return (
        <div className="channel-list">
            <div className="channel-list-header">Channels</div>
            <div>
                {channels.map((channel) => (
                    <div
                        key={channel}
                        onClick={() => onSelectChannel(channel)}
                        className={`channel-list-item ${selectedChannel === channel ? "active" : ""}`}
                    >
                        {channel}
                    </div>
                ))}
            </div>
        </div>
    );
}
