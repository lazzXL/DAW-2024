import * as React from 'react'

type AuthContextType = {
    token: string | undefined;
    setToken: (v: string | undefined) => void
}

export const AuthContext = React.createContext<AuthContextType>({
    token: undefined,
    setToken: () => { throw Error("Not implemented!") }
})

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = React.useState<string | undefined>(() =>
        sessionStorage.getItem("authToken") || undefined
    );

    React.useEffect(() => {
        if (user) {
            sessionStorage.setItem("authToken", user);
        } else {
            sessionStorage.removeItem("authToken");
        }
    }, [user]);

    return (
        <AuthContext.Provider value={{ token: user, setToken: setUser }}>
            {children}
        </AuthContext.Provider>
    );
}