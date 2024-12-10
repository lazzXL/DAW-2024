import { Channel } from "./Channel";
import { Participant } from "./Participant";

export type ChannelDetailsModalProps = {
    channel: Channel;
    participants: Participant[];
    onClose: () => void;
};
