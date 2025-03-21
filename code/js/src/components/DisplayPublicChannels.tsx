import * as React from 'react';
import { useState, useEffect } from 'react';
import PublicChannelList from './PublicChannelList';
import { AuthContext } from '../AuthProvider';
import { useNavigate } from 'react-router-dom';
import { PublicChannel } from '../domain/PublicChannel';


export function DisplayPublicChannels() {
    const navigate = useNavigate();
    const setChannels = useState<PublicChannel[]>([]);

    const { token } = React.useContext(AuthContext);

    const handleSelectChannel = (channel: PublicChannel) => {
        fetch("/participant/join", {
            method: 'POST',
            headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
            },
            body: JSON.stringify({ channelId: channel.id })
        }).then(response => {
            switch (response.status) {
            case 200:
                alert('Successfully joined channel');
                return navigate("/")
            case 409:
                alert('Already joined channel');
                break;
            default:
                alert('Failed to join channel');
                break;
            }
        }).catch(error => {
            console.error('Error joining channel:', error);
        });
    };
        

    return (
        <div className='publicChannelsContainer'>
            <PublicChannelList onSelectChannel={handleSelectChannel} />
        </div>
    );
}

export default DisplayPublicChannels;