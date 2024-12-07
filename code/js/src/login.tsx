import * as React from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { AuthContext } from './AuthProvider';

/***********************
 * RequireAuth Component
 */
export function Login() {
    const location = useLocation()
    const navigate = useNavigate();
    const {token, setToken} = React.useContext(AuthContext)
    const [state, dispatch] = React.useReducer(reduce, {
        tag: "editing", inputs: {
            username: "",
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
        const { username, password } = state.inputs
        authenticate(username, password)
            .then(res => { 
                console.log(res)
                if(res) { setToken(res) }
                dispatch( res
                    ? {type: "success"}
                    : {type: "error", message: `Invalid username or password: ${username} or ${password}`}
            )})
            .catch(err => dispatch({type: "error", message: err.message}))
    }

    function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
        dispatch({ type: "edit", inputName: event.target.name, inputValue: event.target.value })
    }

    function handleRegisterRedirect() {
        navigate("/register"); 
    }

    const usr = state.tag === "editing" ? state.inputs.username : ""
    const password = state.tag === "editing" ? state.inputs.password : ""

    return (
        <div className="login-container">
            <div className="logo-container">
                <img src="chimp.png" alt="App Logo" className="app-logo" />
                <h1 className="logo-text">ChIMP</h1>
            </div>
            <form onSubmit={handleSubmit} className="login-form">
                <fieldset disabled={state.tag !== 'editing'} className="form-fieldset">
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
                        <button type="submit" className="btn-submit">Login</button>
                    </div>
                </fieldset>
            </form>
            <div className="register-container">
                <button className="btn-register" onClick={handleRegisterRedirect}>
                    Register
                </button>
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
                case "error": return { tag: 'editing', error: action.message, inputs: { username: "", password: "" } }
            }
        case 'redirect':
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
    }
}

type State = { tag: 'editing'; error?: string, inputs: { username: string, password: string }; }
    | { tag: 'submitting' }
    | { tag: 'redirect' }

type Action = { type: "edit", inputName: string, inputValue: string }
    | { type: "submit" }
    | { type: "success" }
    | { type: "error", message: string }

/************************
 * Auxiliary Functions emulating authenticate
 */

function delay(delayInMs: number) {
    return new Promise(resolve => {
        setTimeout(() => resolve(undefined), delayInMs);
    });
}

async function authenticate(username: string, password: string): Promise<string | undefined> {
    try {
        const response = await fetch("/user/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                name: username,
                password: password,
            }),
        });

        if (!response.ok) {
            throw new Error("Failed to authenticate");
        }

        const data = await response.json();
        sessionStorage.setItem("username", username);
        return data.token;
    } catch (error) {
        console.error("Error during authentication:", error);
        return undefined;
    }
}