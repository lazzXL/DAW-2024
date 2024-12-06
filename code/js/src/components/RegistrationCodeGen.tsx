import * as React from "react";
import { AuthContext } from "../AuthProvider";

export function RegistrationCode() {
    const [code, setCode] = React.useState<string | null>(null);
    const [isCopied, setIsCopied] = React.useState(false);
    const [loading, setLoading] = React.useState(false);
    const [error, setError] = React.useState<string | null>(null);

    const { token } = React.useContext(AuthContext);

    async function fetchCode() {
        setLoading(true);
        setError(null);

        try {
            const response = await fetch("/registerInvitation/create", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
            });

            if (!response.ok) {
                throw new Error("Failed to fetch the registration code");
            }

            const data = await response.json();
            setCode(data.code);
        } catch (err) {
            setError((err as Error).message);
        } finally {
            setLoading(false);
        }
    }

    function handleCopy() {
        if (code) {
            navigator.clipboard.writeText(code).then(() => {
                setIsCopied(true);
                setTimeout(() => setIsCopied(false), 5000);
            });
        }
    }

    return (
        <div className="registration-code-container">
            <div className="code-container">
                {code ? (
                    <>
                        <p className="code-label">Your Code:</p>
                        <div className="code-wrapper">
                            <p className="code-text">{code}</p>
                            <button className="btn-copy-code" onClick={handleCopy}>
                                {isCopied ? "Copied!" : "Copy"}
                            </button>
                        </div>
                    </>
                ) : (
                    <p className="code-placeholder">Generate a registration code</p>
                )}
            </div>

            <button
                className="btn-fetch-code"
                onClick={fetchCode}
                disabled={loading}
            >
                {loading ? "Generating Code..." : "Get Registration Code"}
            </button>

            {error && <div className="code-error-message">{error}</div>}
        </div>
    );
}
