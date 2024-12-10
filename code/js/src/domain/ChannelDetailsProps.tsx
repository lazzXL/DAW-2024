import { Channel } from "./Channel";
import { Participant } from "../components/MessagePanel";

export type ChannelDetailsModalProps = {
    channel: Channel;
    participants: Participant[];
    onClose: () => void;
};
