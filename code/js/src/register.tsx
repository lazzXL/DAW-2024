import * as React from 'react';
import { json, Navigate, useLocation, useNavigate } from 'react-router-dom';
import { AuthContext } from './AuthProvider';

/***********************
 * RequireAuth Component
 */
export function Register() {
    const location = useLocation()
    const navigate = useNavigate();
    const [state, dispatch] = React.useReducer(reduce, {
        tag: "editing", inputs: {
            invitation : "",
            username: "",
            email : "",
            password: ""
        }
    })
    if (state.tag === "redirect") return (
        <Navigate to={location.state?.source ? location.state.source : "/"} replace={true} />

    )
    function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault()
        if (state.tag != "editing") { return }
        dispatch({ type: "submit" })
        const { invitation, username, email, password} = state.inputs
        registerFetch(invitation, username, email, password)
            .then(res => {
                dispatch( res
                    ? {type: "success"}
                    : {type: "error", message: `Invalid username or password: ${username} or ${password}`}
            )})
            .catch(err => dispatch({type: "error", message: err.message}))
    }

    function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
        dispatch({ type: "edit", inputName: event.target.name, inputValue: event.target.value })
    }

    function handleLoginRedirect() {
        navigate("/login"); 
    }
    const invitation = state.tag === "editing" ? state.inputs.invitation : ""
    const usr = state.tag === "editing" ? state.inputs.username : ""
    const email = state.tag === "editing" ? state.inputs.email : ""
    const password = state.tag === "editing" ? state.inputs.password : ""
    
    
    return (
        <div>

        <div className="login-container">
            <div className="logo-container">
                <img src="chimp.png" alt="App Logo" className="app-logo" />
                <h1 className="logo-text">ChIMP</h1>
            </div>
            <form onSubmit={handleSubmit} className="login-form">
                <fieldset disabled={state.tag !== 'editing'} className="form-fieldset">
                    <div className="form-group">
                        <label htmlFor="invitation" className="form-label">Invitation Code</label>
                        <input 
                            id="invitation" 
                            type="text" 
                            name="invitation" 
                            value={invitation} 
                            onChange={handleChange} 
                            className="form-input" 
                            placeholder="Enter your invitation code"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="username" className="form-label">Username</label>
                        <input 
                            id="username" 
                            type="text" 
                            name="username" 
                            value={usr} 
                            onChange={handleChange} 
                            className="form-input" 
                            placeholder="Enter your username"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="email" className="form-label">Email</label>
                        <input 
                            id="email" 
                            type="text" 
                            name="email" 
                            value={email} 
                            onChange={handleChange} 
                            className="form-input" 
                            placeholder="Enter your email"
                        />
                    </div>
                    <div className="form-group">
                        <label htmlFor="password" className="form-label">Password</label>
                        <input 
                            id="password" 
                            type="password" 
                            name="password" 
                            value={password} 
                            onChange={handleChange} 
                            className="form-input" 
                            placeholder="Enter your password"
                        />
                    </div>
                    {state.tag === 'editing' && state.error && (
                        <div className="error-message">{state.error}</div>
                    )}
                    <div className="form-actions">
                        <button type="submit" className="btn-submit">Register</button>
                    </div>

                    <div className="register-container">
                        <button type="button" className="btn-register" onClick={handleLoginRedirect}>
                            Login
                        </button>
                    </div>

                </fieldset>
            </form>
        </div>
    </div>
    );
}

/***********************
 * REDUCER
 */

function reduce(state: State, action: Action): State {
    switch (state.tag) {
        case 'editing':
            switch (action.type) {
                case "edit": return { tag: 'editing', inputs: { ...state.inputs, [action.inputName]: action.inputValue } }
                case "submit": return { tag: 'submitting' }
            }
        case 'submitting':
            switch (action.type) {
                case "success": return { tag: 'redirect' }
                case "error": return { tag: 'editing', error: action.message, inputs: { invitation: "", username: "",email: "", password: "" } }
            }
        case 'redirect':
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
    }
}

type State = { tag: 'editing'; error?: string, inputs: { invitation: string, username: string, email : string, password: string }; }
    | { tag: 'submitting' }
    | { tag: 'redirect' }

type Action = { type: "edit", inputName: string, inputValue: string }
    | { type: "submit" }
    | { type: "success" }
    | { type: "error", message: string }

/************************
 * Auxiliary Functions emulating authenticate
 */

async function registerFetch( invitation : String, username: string, email : String, password: string): Promise<true | undefined> {
    try {
        const response = await fetch("/user/register", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                invitation : invitation,
                name: username,
                email : email,
                password: password
            }),
        });

        if (!response.ok) {
            throw new Error("Failed to authenticate");
        }

        return true
    } catch (error) {
        console.error("Error during authentication:", error);
        return undefined;
    }
}