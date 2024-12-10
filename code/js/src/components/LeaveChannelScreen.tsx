import * as React from "react";
import { LeaveChannelModalProps } from "../domain/LeaveChannelProps";

export function LeaveChannelModal({channelName, onConfirm, onCancel}: LeaveChannelModalProps) {
    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <button onClick={onCancel} className="close-button">
                    &times;
                </button>
                <h2>Leave Channel</h2>
                <p>Are you sure you want to leave the channel <strong>{channelName}</strong>?</p>
                <div className="modal-actions">
                    <button onClick={onConfirm} className="confirm-button">
                        Yes, Leave
                    </button>
                    <button onClick={onCancel} className="cancel-button">
                        Cancel
                    </button>
                </div>
            </div>
        </div>

        
    );
}
export default LeaveChannelModal;