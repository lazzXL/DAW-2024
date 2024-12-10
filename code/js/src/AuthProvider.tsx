import * as React from 'react'
import { AuthContextType } from './domain/AuthContextType';

export const AuthContext = React.createContext<AuthContextType>({
    token: undefined,
    setToken: () => { }
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