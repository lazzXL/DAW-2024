import * as React from 'react';
import { useState, useEffect } from 'react';
import PublicChannelList from './PublicChannelList';
import { AuthContext } from '../AuthProvider';
import { Navigate } from 'react-router-dom';

interface PublicChannel {
    id: number;
    name: string;
    description: string;
}

export function DisplayPublicChannels() {
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
                return <Navigate to={"/"} />
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