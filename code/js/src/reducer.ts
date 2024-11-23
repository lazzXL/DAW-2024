export type MessagingState = {
    selectedChannel: string | null;
};

export type Action = { type: "select-channel"; channel: string };

export function messagingReducer(state: MessagingState, action: Action): MessagingState {
    switch (action.type) {
        case "select-channel":
            return { ...state, selectedChannel: action.channel };
        default:
            return state;
    }
}
