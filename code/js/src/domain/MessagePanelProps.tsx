import { Message } from "../fakeApiService";
import { Participant } from "./Participant";

export type MessageInput = {
    content: string;
    channelId: number;
};
export type MessagePanelState = {
    messages: Message[];
    participants: Participant[];
    loading: boolean;
    error: string | null;
    message: string;
};
