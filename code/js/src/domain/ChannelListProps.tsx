import { Channel } from "./Channel";

export type ChannelListProps = {
    selectedChannel: Channel | null;
    onSelectChannel: (channel: Channel) => void;
};
