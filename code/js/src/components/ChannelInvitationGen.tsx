import * as React from "react";
import { AuthContext } from "../AuthProvider";
import { channelInvitationProps } from "../domain/ChannelInvitationProps";


export function ChannelInvitation(
    {channelId} : channelInvitationProps
) {
    const [code, setCode] = React.useState<string | null>(null);
    const [isCopied, setIsCopied] = React.useState(false);
    const [loading, setLoading] = React.useState(false);
    const [error, setError] = React.useState<string | null>(null);

    const [permission, setPermission] = React.useState(false);

    const { token } = React.useContext(AuthContext);

    async function fetchCode() {
        setLoading(true);
        setError(null);

        try {
            const response = await fetch("/invitation/create", {
                method: "POST",
                headers: {
                    Authorization: `Bearer ${token}`,
                    "Content-Type": "application/json",
                },
                body: JSON.stringify({
                    channelID : channelId,
                    permission : permission? "READ_WRITE" : "READ_ONLY"
                }),
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

    function handleChange(event: React.ChangeEvent<HTMLSelectElement>) {
        const value = event.target.value === "true"; 
        setPermission(value);
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
                        <div className="create-channel-field">
                        <label className="create-channel-label" htmlFor="visibility">Visibility</label>
                    </div>
                    </>
                ) : (
                    <p className="code-placeholder">Generate a registration code</p>
                )}
            </div>

            <select className="create-channel-select" id="visibility" name="visibility" value={permission ? "true" : "false"} onChange={handleChange}>       
                            <option value="true">Read-write</option>
                            <option value="false">Read-only</option>
                            
            </select>

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


