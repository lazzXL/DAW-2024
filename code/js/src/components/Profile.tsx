import * as React from "react";
import { AuthContext } from "../AuthProvider";

async function getUser(token : String) {
    return fetch("/user/findByToken", {
        method: "GET",
        headers: {
            "Authorization": "Bearer " + token,
        },
    })
        .then((response) => {
            if (!response.ok) {
                throw new Error("Failed to fetch user data");
            }
            return response.json();
        })
        .catch((error) => {
            console.error(error);
            return null;
        });
}

export function ProfileInfo() {
    const { token } = React.useContext(AuthContext);
    const [ user, setUser] = React.useState(null);
    const [ loading, setLoading] = React.useState(true);

    React.useEffect(() => {
        getUser(token).then((userData) => {
            setUser(userData);
            setLoading(false);
        });
    }, [token]);

    if (loading) {
        return <p>Loading...</p>;
    }

    if (!user) {
        return <p>Failed to load user information.</p>;
    }

    return (
        <div className="profile-container">
            <div className="profile-header">
                <img
                    src={"chimp.png"}
                    alt={`${user.name}'s avatar`}
                    className="profile-avatar"
                />
                <div className="profile-info">
                    <h1 className="profile-username">{user.name}</h1>
                    <p className="profile-email">{user.email}</p>
                </div>
            </div>
        </div>
    );
}
        

