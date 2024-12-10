import * as React from "react";
import { ProfileInfo } from "./Profile";
import { AuthContext } from "../AuthProvider";
import { useNavigate } from "react-router-dom";

export function SettingsList() {
    const {setToken} = React.useContext(AuthContext);
    const navigate = useNavigate();
    const [currentView, setCurrentView] = React.useState(null);

    return (
        <>
            <div className="settings-list">
                <div className="settings-item" onClick={() => setCurrentView('ProfileInfo')}>
                    {"Account Information"}
                </div>
                <div className="settings-item" onClick={() => setCurrentView('AboutAuthors')}>
                    {"About Authors"}
                </div>
                <div className="settings-item" onClick={() => {
                    setToken(undefined);
                    navigate("/login");
                }}>
                    {"Logout"}
                </div>
            </div>
            
            {currentView === 'ProfileInfo' && <ProfileInfo />}
            {currentView === 'AboutAuthors' && <AboutAuthors />}
        </>
    );
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
        
    <div className="author">
        <h3>{name}</h3>
        <p>Age: {age}</p>
        <p>Email: <a href={`mailto:${email}`}>{email}</a></p>
        <p>GitHub: <a href={`https://github.com/${githubName}`} target="_blank" rel="noopener noreferrer">{githubName}</a></p>
    </div>



);}


