import * as React from "react";
import { Participant } from "./MessagePanel"; 

type ChannelDetailsModalProps = {
    channelName: string;
    channelDescription: string;
    participants: Participant[];
    onClose: () => void;
};

export function ChannelDetailsModal({
    channelName,
    channelDescription,
    participants,
    onClose,
}: ChannelDetailsModalProps) {
    const [isExpanded, setIsExpanded] = React.useState(false);

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button onClick={onClose} className="close-button">
                    &times;
                </button>
                <h2>Channel Details</h2>
                <div className="channel-details-info">
                    <p>
                        <strong>Channel Name:</strong> {channelName}
                    </p>
                    <p>
                        <strong>Description:</strong> {channelDescription || "No description provided."}
                    </p>
                </div>
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
                    )}
                </div>
            </div>
        </div>
    );
}