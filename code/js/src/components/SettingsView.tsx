import * as React from "react";
import { SettingsPanel } from "./SettingsPanel"



export function SettingsView() {
    return (
        <div style={{ display: "flex", height: "100vh", fontFamily: "Arial, sans-serif" }}>
            {/*
            <SettingsList
                selectedSetting={}
                onSelectSetting={}
                settings={settings}
            />
            */}
            <SettingsPanel setting={"Account Info"} />
        </div>
    );
}