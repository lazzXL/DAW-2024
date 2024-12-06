import * as React from 'react';
import { Navigate, useLocation, useNavigate } from 'react-router-dom';
import { AuthContext } from '../AuthProvider';


/***********************
 * RequireAuth Component
 */
export function JoinPrivateChannel() {
    const location = useLocation();
    const navigate = useNavigate();
    const { token, setToken } = React.useContext(AuthContext);

    const [state, dispatch] = React.useReducer(reduce, {
        tag: 'editing',
        inputs: {
            code: ''
        }
    });

    if (state.tag === "redirect") return (
        <Navigate to={"/"} replace={true}></Navigate>
    )

    async function joinChannel(code: string): Promise<string | undefined> {
        try {
            const response = await fetch("/participant/join-invite", {
                method: "POST",
                headers: { 
                    "Content-Type": "application/json",
                    "Authorization": "Bearer " + token
                },
                body: JSON.stringify({
                    code: code,
                }),
            });

            if (!response.ok) {
                throw new Error("Invalid code");
            }

            dispatch({ type: 'success' });

        } catch (error) {
            console.error("Error during authentication:", error);
            return undefined;
        }
    }

    function handleSubmit(event: React.FormEvent<HTMLFormElement>) {
        event.preventDefault();
        if (state.tag !== 'editing') {
            return;
        }
        dispatch({ type: 'submit' });
        const { code } = state.inputs;
        joinChannel(code)
    }

    function handleChange(event: React.ChangeEvent<HTMLInputElement>) {
        dispatch({ type: 'edit', inputName: event.target.name, inputValue: event.target.value });
    }

    const code = state.tag === 'editing' ? state.inputs.code : '';

    return (
        <div className="login-container">
            <form onSubmit={handleSubmit} className="login-form">
                <fieldset disabled={state.tag !== 'editing'} className="form-fieldset">
                    <div className="form-group">
                        <label htmlFor="code" className="form-label">Code</label>
                        <input
                            id="code"
                            type="text"
                            name="code"
                            value={code}
                            onChange={handleChange}
                            className="form-input"
                            placeholder="Enter your code"
                        />
                    </div>
                    {state.tag === 'editing' && state.error && (
                        <div className="error-message">{state.error}</div>
                    )}
                    <div className="form-actions">
                        <button type="submit" className="btn-submit">Join Channel</button>
                    </div>
                </fieldset>
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
                case 'edit':
                    return { tag: 'editing', inputs: { ...state.inputs, [action.inputName]: action.inputValue } };
                case 'submit':
                    return { tag: 'submitting' };
            }
        case 'submitting':
            switch (action.type) {
                case 'success':
                    return { tag: 'redirect' };
                case 'error':
                    return { tag: 'editing', error: action.message, inputs: { code: '' } };
            }
        case 'redirect':
            throw Error("Already in final State 'redirect' and should not reduce to any other State.");
    }
}

/***********************
 * Type Definitions
 */
type State =
    | { tag: 'editing'; error?: string; inputs: { code: string } }
    | { tag: 'submitting' }
    | { tag: 'redirect' };

type Action =
    | { type: 'edit'; inputName: string; inputValue: string }
    | { type: 'submit' }
    | { type: 'success' }
    | { type: 'error'; message: string };


