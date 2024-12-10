import * as React from "react";
import { ChannelInvitation } from "./ChannelInvitationGen";
import { ChannelDetailsModalProps } from "../domain/ChannelDetailsProps";

export function ChannelDetailsModal({
    channel,
    participants,
    onClose,
}: ChannelDetailsModalProps) {
    const [isExpanded, setIsExpanded] = React.useState(false);
    const name = sessionStorage.getItem("username")

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button onClick={onClose} className="close-button">
                    &times;
                </button>
                <h2>Channel Details</h2>
                <div className="channel-details-info">
                    <p>
                        <strong>Channel Name:</strong> {channel.name}
                    </p>
                    <p>
                        <strong>Description:</strong> {channel.description || "No description provided."}
                    </p>
                </div>
                { 
                    participants.find( it =>  it.userId == channel.adminID).name == name &&(
                        <ChannelInvitation
                            channelId = {channel.id}
                        />
                    )}
                <div className="participants-section">
                    <p>
                        <strong>Participants:</strong> {participants.length}
                    </p>
                    <button
                        onClick={() => setIsExpanded(!isExpanded)}
                        className="expand-button"
                    >
                        {isExpanded ? "Hide Participants" : "Show Participants"}
                    </button>
                    {isExpanded && (
    <div className="participants-list-scrollable">
        <ul className="participants-list">
            {participants.map((participant) => (
                <li key={participant.id} className="participant-item">
                    <span className="participant-name">{participant.name}</span>
                    <span className="participant-permission">
                        {participant.permission.replace("_", " ")}
                    </span>
                </li>
            ))}
        </ul>
    </div>
)}
                </div>
            </div>
        </div>
    );
}