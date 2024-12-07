import * as React from "react";

type SettingPanelProps = {
    setting: string;
};

export function SettingsPanel({ setting }: SettingPanelProps) {
    const [loading, setLoading] = React.useState(false);
    /*
    React.useEffect(() => {
        setLoading(true);
        fetchSettingInfo(setting).then((msgs) => {
            setMessages(msgs);
            setLoading(false);
        });
    
    }, [setting]);*/

    return (
        <div className="settings-panel">
            <div className="settings-panel-header">{setting}</div>
            <div className="settings-panel-messages">
                {loading ? (
                    <p>Loading {setting}</p>
                ) : setting ? (
                    
                        <div
                            key={"index"}
            
                        >
                            <h1>Setting page example</h1>
                        </div>
                    
                ) : (
                    <p>Select a channel to view messages</p>
                )}
            </div>
        </div>
    );
}
