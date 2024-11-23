import * as React from "react";
import { ChannelList } from "./ChannelList";
import { MessagePanel } from "./MessagePanel";
import { fetchChannels } from "../fakeApiService";
import { messagingReducer, MessagingState, Action } from "../reducer";

export function MessagingApp() {
    const [state, dispatch] = React.useReducer<React.Reducer<MessagingState, Action>>(messagingReducer, {
        selectedChannel: null,
    });
    const [channels, setChannels] = React.useState<string[]>([]);

    React.useEffect(() => {
        fetchChannels().then(setChannels);
    }, []);

    const handleSelectChannel = (channel: string) => {
        dispatch({ type: "select-channel", channel });
    };

    return (
        <div style={{ display: "flex", height: "100vh", fontFamily: "Arial, sans-serif" }}>
            <ChannelList
                selectedChannel={state.selectedChannel}
                onSelectChannel={handleSelectChannel}
                channels={channels}
            />
            <MessagePanel channel={state.selectedChannel} />
        </div>
    );
}
