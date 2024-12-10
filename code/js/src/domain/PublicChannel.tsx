export interface PublicChannel {
    id: number;
    name: string;
    description: string;
}
export type PublicChannelListProps = {
    onSelectChannel: (channel: PublicChannel) => void;
};

