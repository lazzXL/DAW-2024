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
            {currentView === 'AboutAuthors' && <AboutAuthors />}
        </>
    );
}




function createInvitation(){

}

export function AboutAuthors() {
    return (
        <div>
            {author("Tomás Silva", "20", "a50458@alunos.isel.pt", "tomas-ns")}
            {author("Tiago Adriano", "20", "a50968@alunos.isel.pt", "adriano9358")}
            {author("Gabriel Lemos", "20", "a50997@alunos.isel.pt", "lazzXl")}
        </div>
    );
}

function author(
    name: String,
    age : String,
    email : String, 
    githubName : String
){
    return (
    <div className="profile-container">
        <div className="profile-header">
            <img
                src={"chimp.png"}
                alt={`${name}'s avatar`}
                className="profile-avatar"
            />
            <div className="profile-info">
                <h1 className="profile-username">{name}</h1>
                <p className="profile-email">{email}</p>
                <p className="profile-age">{"Age: " + age}</p>
                <p className="profile-github">{"Github: " +githubName}</p>
            </div>
        </div>
    </div>
);}

function darkMode(){
    console.log("DarkMode")
}

