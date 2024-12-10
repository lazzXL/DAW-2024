export type Visibility = "PUBLIC" | "PRIVATE";

export type Channel = {
    id: number;  
    name: string;
    adminID: number;
    description: string;
    visibility: Visibility;
}
