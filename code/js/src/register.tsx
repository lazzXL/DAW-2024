import * as React from 'react';
import { json, Navigate, useLocation } from 'react-router-dom';
import { AuthContext } from './AuthProvider';

/***********************
 * RequireAuth Component
 */
export function Register() {
    const location = useLocation()
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
    const invitation = state.tag === "editing" ? state.inputs.invitation : ""
    const usr = state.tag === "editing" ? state.inputs.username : ""
    const email = state.tag === "editing" ? state.inputs.email : ""
    const password = state.tag === "editing" ? state.inputs.password : ""
    
    
    return (
        <form onSubmit={handleSubmit}>
            <fieldset disabled={state.tag !== 'editing'}>
                <div>
                    <label htmlFor="invitation">Invtitation Code</label>
                    <input id="invitation" type="text" name="invitation" value={invitation} onChange={handleChange} />
                </div>
                <div>
                    <label htmlFor="username">Username</label>
                    <input id="username" type="text" name="username" value={usr} onChange={handleChange} />
                </div>
                <div>
                    <label htmlFor="email">Email</label>
                    <input id="email" type="text" name="email" value={email} onChange={handleChange} />
                </div>
                <div>
                    <label htmlFor="password">Password</label>
                    <input id="password" type="text" name="password" value={password} onChange={handleChange} />
                </div>
                <div>
                    <button type="submit">Register</button>
                </div>
            </fieldset>
            {state.tag === 'editing' && state.error}
        </form>
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