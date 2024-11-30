import * as React from "react";
import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./components/Layout";
import { MessagingApp } from "./components/MessagingApp";
import { Profile } from "./pages/Profile";
import { Settings } from "./pages/Settings";
import { DiscoverPublic } from "./pages/DiscoverPublic";
import { AuthRequire } from "./AuthRequire";
import { Login } from "./login";
import { Register } from "./register";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <AuthRequire><Layout /></AuthRequire>, // Dynamically redirect based on auth state
        children: [
            { path: "/", element: <MessagingApp /> },
            { path: "/profile", element: <Profile /> },
            { path: "/settings", element: <Settings /> },
            { path: "/discover-channels", element: <DiscoverPublic /> },
        ],
    },
    {
        path: "/login",
        element: <Login />,
    },
    {
        path: "/register",
        element: <Register />,
    },
]);

