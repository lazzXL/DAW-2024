import * as React from "react";
import { Link } from "react-router-dom";
import { AuthContext } from "../AuthProvider";

export function Sidebar() {
    const {setToken} = React.useContext(AuthContext)
    return (
        <div className="sidebar">
            <div>
                <Link to="/">
                    <img src="chimp.png" alt="App Logo" style={{ cursor: "pointer" }} />
                </Link>
            </div>
            <button>
                <Link to="/discover-channels" style={{ textDecoration: "none", color: "inherit" }}>
                    🧭
                </Link>
            </button>
            <button>
                <Link to="/generate-registration-invitation" style={{ textDecoration: "none", color: "inherit" }}>
                    ✉
                </Link>
            </button>
            <button>
            <Link to="/create-channel" style={{ textDecoration: "none", color: "inherit" }}>
                <img 
                    src="newChannel.png" 
                    alt="New Channel" 
                    style={{ cursor: "pointer", width: "20px", height: "20px" }}
                />
            </Link>
            </button>
            <div className="spacer"></div>
            <button>
                <Link to="/login" onClick = {() => setToken(undefined) } style={{ textDecoration: "none", color: "inherit" }}>
                    🚪
                </Link>
            </button>
            <button>
                <Link to="/settings" style={{ textDecoration: "none", color: "inherit" }}>
                    ⚙
                </Link>
            </button>
            <button>
                <Link to="/profile" style={{ textDecoration: "none", color: "inherit" }}>
                    👤
                </Link>
            </button>
        </div>
    );
}
