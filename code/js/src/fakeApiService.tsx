export type Message = {
    channel: string;
    sender: string;
    content: string;
    timestamp: string;
};

const fakeChannels = [  "General", "Support", "Random",
                        "General2", "Support2", "Random2",
                        "General3", "Support3", "Random3",
                        "General4", "Support4", "Random4",
                        "General5", "Support5", "Random5",
                        "General6", "Support6", "Random6",
                        "General7", "Support7", "Random7",
                        "General8", "Support8", "Random8", ];
const fakeMessages: Message[] = [
    { channel: "General", sender: "Alice", content: "Hello!", timestamp: "2024-11-21 10:00 AM" },
    { channel: "General", sender: "Bob", content: "Hi there!", timestamp: "2024-11-21 10:05 AM" },
    { channel: "Support", sender: "SupportBot", content: "How can I assist you?", timestamp: "2024-11-21 9:50 AM" },
    { channel: "Random", sender: "Charlie", content: "Did you watch the game?", timestamp: "2024-11-20 8:30 PM" },
];


export const fetchChannels = (): Promise<string[]> => {
    return new Promise((resolve) => {
        setTimeout(() => resolve(fakeChannels), 1000); 
    });
};

export const fetchMessages = (channel: string): Promise<Message[]> => {
    return new Promise((resolve) => {
        setTimeout(() => resolve(fakeMessages.filter((msg) => msg.channel === channel)), 1000); // Simulate 1-second delay
    });
};
