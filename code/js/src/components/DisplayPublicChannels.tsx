import * as React from 'react';
import { useState, useEffect } from 'react';
import PublicChannelList from './PublicChannelList';
import { fetchPublicChannels } from '../fakeApiService';

interface PublicChannel {
    name: string;
    description: string;
}

export function DisplayPublicChannels() {
    const [channels, setChannels] = useState<PublicChannel[]>([]);

    useEffect(() => {
        fetchPublicChannels().then(setChannels);
    }, []);

    const handleSelectChannel = (channel: PublicChannel) => {
        console.log(`Selected channel: ${channel.name}`);
    };

    return (
        <div className='publicChannelsContainer'>
            <PublicChannelList publicChannels={channels} onSelectChannel={handleSelectChannel} />
        </div>
    );
}

export default DisplayPublicChannels;