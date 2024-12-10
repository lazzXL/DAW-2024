import * as React from "react";
import { Channel } from "../domain/Channel";
import { AuthContext } from "../AuthProvider";
import { useNavigate } from "react-router-dom";
import { ChannelListProps } from "../domain/ChannelListProps";

export function ChannelList({ selectedChannel, onSelectChannel }: ChannelListProps) {
    const [channels, setChannels] = React.useState<Channel[]>([]);
    const { token } = React.useContext(AuthContext)
    const navigate = useNavigate();

    React.useEffect(() => {
        fetch("/channel/joined", {
            method : "GET",
            headers: {
                "Authorization" : "Bearer " + token
            }
        })
            .then((response) => {
                if (!response.ok) {
                    throw new Error("Failed to fetch channels");
                }
                return response.json(); 
            })
            .then((data: Channel[]) => {
                setChannels(data); 
            })
            .catch((error) => console.error(error));
    }, []);

    return (
        <div className="channel-list">
            <div className="channel-list-header-container">
                <div className="channel-list-header">Channels</div>
                <button
                    className="join-channel-button"
                    onClick={() => navigate("/join-priv-channel")}
                >
                    +
                </button>
            </div>
            <div>
                {channels.map((channel) => (
                    <div
                        key={channel.id}
                        onClick={() => onSelectChannel(channel)}
                        className={`channel-list-item ${
                            selectedChannel?.id === channel.id ? "active" : ""
                        }`}
                    >
                        {channel.name}
                    </div>
                ))}
            </div>
        </div>
    );
}
