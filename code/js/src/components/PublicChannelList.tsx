import * as React from 'react';
import { AuthContext } from "../AuthProvider";
import { PublicChannel, PublicChannelListProps } from '../domain/PublicChannel';

export function PublicChannelList({ onSelectChannel }: PublicChannelListProps) {
    const [publicChannels, setPublicChannels] = React.useState<PublicChannel[]>([]);
    const { token } = React.useContext(AuthContext);

    React.useEffect(() => {
        fetch('/channel/public', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        })
        .then(response => response.json())
        .then(data => setPublicChannels(data))
        .catch(error => console.error('Error fetching public channels:', error));
    }, [token]);
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