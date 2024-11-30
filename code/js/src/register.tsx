import * as React from 'react';
import { Navigate } from 'react-router-dom';

export function Register() {
    const [state, dispatch] = React.useReducer(reduce, {
        tag: "editing", inputs: {
            username: "",
            password: "",
            email : "",
            invitation: ""
        }
    })
    if (state.tag === "redirect") return (
        <Navigate to={"/login"} replace={true}></Navigate>
    )
    function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault()
        if (state.tag != "editing") { return }
        dispatch({ type: "submit" })
        const { username, password, email, invitation } = state.inputs
        registerConfirm(username, password, email, invitation)
            .then(res => { 
                dispatch( res
                    ? {type: "success"}
                    : {type: "error", message: `Invalid`}
            )})
            .catch(err => dispatch({type: "error", message: err.message}))
    }

    function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
        dispatch({ type: "edit", inputName: event.target.name, inputValue: event.target.value })
    }
    const usr = state.tag === "editing" ? state.inputs.username : ""
    const password = state.tag === "editing" ? state.inputs.password : ""
    const email = state.tag === "editing" ? state.inputs.email : ""
    const invitation = state.tag === "editing" ? state.inputs.invitation : ""
    return (
        <form onSubmit={handleSubmit}>
            <fieldset disabled={state.tag !== 'editing'}>
                <div>
                    <label htmlFor="username">Username</label>
                    <input id="username" type="text" name="username" value={usr} onChange={handleChange} />
                </div>
                <div>
                    <label htmlFor="password">Password</label>
                    <input id="password" type="text" name="password" value={password} onChange={handleChange} />
                </div>
                <div>
                    <label htmlFor="email">Email</label>
                    <input id="email" type="text" name="email" value={email} onChange={handleChange} />
                </div>
                <div>
                    <label htmlFor="invitation">Invitation</label>
                    <input id="invitation" type="text" name="invitation" value={invitation} onChange={handleChange} />
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
                case "error": return { tag: 'editing', error: action.message, inputs: { username: "", password: "", email : "", invitation : ""} }
            }
        case 'redirect':
            throw Error("Already in final State 'redirect' and should not reduce to any other State.")
    }
}

type State = { tag: 'editing'; error?: string, inputs: { username: string, password: string , email : string, invitation : string}; }
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

async function registerConfirm(username : string, password : string , email : string, invitation : string): Promise<true | undefined> {
    await delay(1000);
    if ((username == 'roger' || username == 'bob') && password == 'schmidt') {
        return true;
    }
    return undefined;
}