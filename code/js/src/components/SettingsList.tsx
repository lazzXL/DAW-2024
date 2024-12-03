import * as React from "react";
import { AuthContext } from "../AuthProvider";
import { ProfileInfo } from "./Profile";



export function SettingsList() {
    const { token } = React.useContext(AuthContext);
    const [currentView, setCurrentView] = React.useState(null);

    const handleAccountInformation = () => {
        setCurrentView('ProfileInfo');
    };


    return (
        <>
            <div onClick={handleAccountInformation}>
                {"Account Information"}
            </div>
            <div onClick={() => setCurrentView('CreateInvitation')}>
                {"Create An Invitation"}
            </div>
            <div onClick={() => setCurrentView('AboutAuthors')}>
                {"About Authors"}
            </div>
            <div onClick={() => setCurrentView('DarkMode')}>
                {"Dark Mode"}
            </div>

            {currentView === 'ProfileInfo' && <ProfileInfo />}
        </>
    );
}




function createInvitation(){

}

function aboutAuthors(){

}

function darkMode(){
    console.log("DarkMode")
}