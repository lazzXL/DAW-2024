import * as React from "react";

interface ProfileProps {
    username: string;
    email: string;
    joinedDate: string;
    bio?: string;
    avatarUrl?: string;
}

export function ProfileInfo() {
    return (
        <div className="profile-container">
            <div className="profile-header">
                <img
                    src={"chimp.png"}
                    alt={`José's avatar`}
                    className="profile-avatar"
                />
                <div className="profile-info">
                    <h1 className="profile-username">{"José"}</h1>
                    <p className="profile-email">{"josé@email.com"}</p>
                </div>
            </div>
        </div>
    );
}
