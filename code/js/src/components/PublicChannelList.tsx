import * as React from 'react';

interface PublicChannel {
    name: string;
    description: string;
}
type PublicChannelListProps = {
    onSelectChannel: (channel: PublicChannel) => void;
    publicChannels: PublicChannel[];
};

export function PublicChannelList({ onSelectChannel, publicChannels }: PublicChannelListProps) {
    return (
        <div>
            <ul>
                {publicChannels.map((channel, index) => (
                    <li key={index} className="channel-item">
                        <div className="channel-info">
                            <h2 className="channel-name">{channel.name}</h2>
                            <p className="channel-description">{channel.description}</p>
                        </div>
                        <button className="join-button" onClick={() => onSelectChannel(channel)}>Join</button>
                    </li>
                ))}
            </ul>
        </div>
    );
};

export default PublicChannelList;