import * as React from "react";
import { Link } from "react-router-dom";

export function Sidebar() {
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
            <div className="spacer"></div>
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
