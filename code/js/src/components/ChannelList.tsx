import * as React from "react";
import { fetchChannels } from "../fakeApiService";
import { Channel } from "../domain/Channel";

type ChannelListProps = {
    selectedChannel: Channel | null;
    onSelectChannel: (channel: Channel) => void;
    //channels: string[];
};

export function ChannelList({ selectedChannel, onSelectChannel, /*channels*/ }: ChannelListProps) {

    const [channels, setChannels] = React.useState<Channel[]>([]);

    React.useEffect(() => {
        fetchChannels().then(setChannels);
        /*fetch("")
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Failed to fetch channels");
                }
                return response.json(); 
            })
            .then((data: Channel[]) => {
                setChannels(data); 
            })
            .catch((error) => console.error(error));*/
    }, []);

    return (
        <div className="channel-list">
            <div className="channel-list-header">Channels</div>
            <div>
                {channels.map((channel) => (
                    <div
                        key={channel.id}
                        onClick={() => onSelectChannel(channel)}
                        className={`channel-list-item ${ selectedChannel?.id === channel.id ? "active" : ""}`}
                    >
                        {channel.name}
                    </div>
                ))}
            </div>
        </div>
    );
}
