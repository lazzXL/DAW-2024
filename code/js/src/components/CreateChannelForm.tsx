import * as React from 'react';
import { Navigate } from 'react-router-dom';
import { AuthContext } from '../AuthProvider';

/***********************
 * CreateChannel Component
 */
export function CreateChannel() {

    const [state, dispatch] = React.useReducer(reduce, {
        tag: "editing", inputs: {
            name: "",
            description: "",
            visibility: true
        }
    });

    const { token } = React.useContext(AuthContext);

    if (state.tag === "redirect") return (
        <Navigate to={"/"} replace={true}></Navigate>
    )

    async function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        if (state.tag !== "editing") { return; }
        dispatch({ type: "submit" });

        const { name, description, visibility } = state.inputs;

        try {
            const response = await fetch("/channel/create", {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    "name": name,
                    "description": description,
                    "isPublic": visibility,
                }),
            });

            if (!response.ok) {
                throw new Error("Failed to create channel.");
            }

            dispatch({ type: 'success' });

        } catch (error) {
            dispatch({ type: "error", message: error.message });
        }
    }

    function handleChange(event: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) {
        const value = event.target.name === "visibility" ? event.target.value === "true" : event.target.value;
        dispatch({ type: "edit", inputName: event.target.name, inputValue: value });
    }

    const { name, description, visibility } = state.tag === "editing" ? state.inputs : {
        name: "",
        description: "",
        visibility: true
    };

    return (
        <div className="form-container">
            <form className="create-channel-form" onSubmit={handleSubmit}>
                <fieldset className="create-channel-fieldset" disabled={state.tag !== 'editing'}>
                    <div className="create-channel-field">
                        <label className="create-channel-label" htmlFor="name">Channel Name</label>
                        <input className="create-channel-input" id="name" type="text" name="name" value={name} onChange={handleChange} />
                    </div>
                    <div className="create-channel-field">
                        <label className="create-channel-label" htmlFor="description">Description</label>
                        <input className="create-channel-input" id="description" type="text" name="description" value={description} onChange={handleChange} />
                    </div>
                    <div className="create-channel-field">
                        <label className="create-channel-label" htmlFor="visibility">Visibility</label>
                        <select className="create-channel-select" id="visibility" name="visibility" value={visibility ? "true" : "false"} onChange={handleChange}>
                            <option value="true">Public</option>
                            <option value="false">Private</option>
                        </select>
                    </div>
                    <div className="create-channel-actions">
                        <button className="create-channel-submit-button" type="submit">Create Channel</button>
                    </div>
                </fieldset>
                {state.tag === 'editing' && state.error && (
                    <p className="create-channel-error">{state.error}</p>
                )}
            </form>
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
                case "edit": return { tag: 'editing', inputs: { ...state.inputs, [action.inputName]: action.inputValue } };
                case "submit": return { tag: 'submitting' };
            }
        case 'submitting':
            switch (action.type) {
                case "success": return { tag: 'redirect' };
                case "error": return { tag: 'editing', error: action.message, inputs: { name: "", description: "", visibility: true } };
            }
        case 'redirect':
            throw new Error("Already in final State 'redirect' and should not reduce to any other State.");
    }
}

type State = { tag: 'editing'; error?: string, inputs: { name: string, description: string, visibility: boolean }; }
    | { tag: 'submitting' }
    | { tag: 'redirect' };

type Action = { type: "edit", inputName: string, inputValue: string | boolean }
    | { type: "submit" }
    | { type: "success" }
    | { type: "error", message: string };