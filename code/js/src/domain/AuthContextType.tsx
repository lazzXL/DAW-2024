export type AuthContextType = {
    token: string | undefined;
    setToken: (v: string | undefined) => void;
};
