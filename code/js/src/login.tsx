import * as React from "react";
import * as ReactDOM from "react-dom/client";

const container: HTMLElement = document.getElementById('container');

export function loginDemo() {
    const root = ReactDOM.createRoot(container);
    root.render(
        <LoginPage />
    );
}

type LoginState = {
    username: string;
    password: string;
    error: string | null;
};

type Action =
    | { type: "update-field"; field: "username" | "password"; value: string }
    | { type: "login-success" }
    | { type: "login-failure"; error: string };

function loginReducer(state: LoginState, action: Action): LoginState {
    switch (action.type) {
        case "update-field":
            return { ...state, [action.field]: action.value };
        case "login-success":
            return { ...state, error: null };
        case "login-failure":
            return { ...state, error: action.error };
    }
}

function LoginPage() {
    const [state, dispatch] = React.useReducer(loginReducer, {
        username: "",
        password: "",
        error: null,
    });

    const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value } = e.target;
        dispatch({ type: "update-field", field: name as "username" | "password", value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        const { username, password } = state;

        // Basic validation
        if (!username || !password) {
            dispatch({ type: "login-failure", error: "All fields are required." });
            return;
        }

        try {
            const response = await fetch("http://localhost:8080/login", {
                method: "?",
                headers: {
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({ username, password }),
            });

            if (!response.ok) {
                const errorData = await response.json();
                throw new Error(errorData.message || `HTTP Error: ${response.status}`);
            }

            const data = await response.json();
            console.log("Login successful:", data);

            dispatch({ type: "login-success" });
            alert("Login successful!");
        } catch (error) {
            console.error("Login failed:", error);
            dispatch({ type: "login-failure", error: String(error) });
        }
    };

    return (
        <div style={{ maxWidth: "400px", margin: "50px auto", fontFamily: "Arial, sans-serif" }}>
            <h2>Login</h2>
            <form onSubmit={handleSubmit}>
                <div style={{ marginBottom: "15px" }}>
                    <label>
                        Username:
                        <input
                            type="text"
                            name="username"
                            value={state.username}
                            onChange={handleInputChange}
                            style={{
                                display: "block",
                                width: "100%",
                                padding: "8px",
                                marginTop: "5px",
                                boxSizing: "border-box",
                            }}
                        />
                    </label>
                </div>
                <div style={{ marginBottom: "15px" }}>
                    <label>
                        Password:
                        <input
                            type="password"
                            name="password"
                            value={state.password}
                            onChange={handleInputChange}
                            style={{
                                display: "block",
                                width: "100%",
                                padding: "8px",
                                marginTop: "5px",
                                boxSizing: "border-box",
                            }}
                        />
                    </label>
                </div>
                {state.error && (
                    <p style={{ color: "red", marginBottom: "15px" }}>{state.error}</p>
                )}
                <button
                    type="submit"
                    style={{
                        padding: "10px 15px",
                        backgroundColor: "#007BFF",
                        color: "white",
                        border: "none",
                        borderRadius: "4px",
                        cursor: "pointer",
                    }}
                >
                    Login
                </button>
            </form>
        </div>
    );
}