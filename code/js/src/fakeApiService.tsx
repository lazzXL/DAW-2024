import { Channel } from "./domain/Channel";

//Legacy code

export type Message = {
    id: string;
    sender: string;
    content: string;
    timestamp: string;
};

const fakeChannels: Channel[] = [
    { id: 1, name: "General", adminID: 101, description: "General discussion for everyone.", visibility: "PUBLIC" },
    { id: 2, name: "Support", adminID: 102, description: "Technical support and help.", visibility: "PRIVATE" },
    { id: 3, name: "Random", adminID: 103, description: "Casual chat and random topics.", visibility: "PUBLIC" },
    { id: 4, name: "Project Alpha", adminID: 104, description: "Discussions about Project Alpha.", visibility: "PRIVATE" },
    { id: 5, name: "Marketing", adminID: 105, description: "Marketing team discussions.", visibility: "PUBLIC" },
    { id: 6, name: "HR Announcements", adminID: 106, description: "Important announcements from HR.", visibility: "PUBLIC" },
    { id: 7, name: "Development", adminID: 107, description: "Development team discussions.", visibility: "PRIVATE" },
    { id: 8, name: "Design", adminID: 108, description: "Discussions about UI/UX design.", visibility: "PRIVATE" },
    { id: 9, name: "Finance", adminID: 109, description: "Finance-related discussions.", visibility: "PRIVATE" },
    { id: 10, name: "Operations", adminID: 110, description: "Operations team updates.", visibility: "PUBLIC" },
    { id: 11, name: "Product Updates", adminID: 111, description: "Updates on product features.", visibility: "PUBLIC" },
    { id: 12, name: "Customer Feedback", adminID: 112, description: "Customer feedback and suggestions.", visibility: "PUBLIC" },
    { id: 13, name: "IT Support", adminID: 113, description: "Internal IT support requests.", visibility: "PRIVATE" },
    { id: 14, name: "Legal", adminID: 114, description: "Legal team discussions.", visibility: "PRIVATE" },
    { id: 15, name: "Sales", adminID: 115, description: "Sales team discussions.", visibility: "PUBLIC" },
    { id: 16, name: "QA Testing", adminID: 116, description: "Quality Assurance and testing.", visibility: "PRIVATE" },
    { id: 17, name: "Client Projects", adminID: 117, description: "Discussions about client projects.", visibility: "PRIVATE" },
    { id: 18, name: "Company Events", adminID: 118, description: "Organizing company events.", visibility: "PUBLIC" },
    { id: 19, name: "R&D", adminID: 119, description: "Research and development topics.", visibility: "PRIVATE" },
    { id: 20, name: "Social", adminID: 120, description: "Non-work-related social chat.", visibility: "PUBLIC" },
];

export const fetchChannels = (): Promise<Channel[]> => {
    return new Promise((resolve) => {
        setTimeout(() => resolve(fakeChannels), 4000); 
    });
};


