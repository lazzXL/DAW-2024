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
import { RegistrationCode } from "./components/RegistrationCodeGen";
import { JoinPrivateChannel } from "./components/JoinPrivateChannel";
import { CreateChannel } from "./components/CreateChannelForm";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <AuthRequire><Layout /></AuthRequire>,
        children: [
            { path: "/", element: <MessagingApp /> },
            { path: "/profile", element: <Profile /> },
            { path: "/settings", element: <Settings /> },
            { path: "/discover-channels", element: <DiscoverPublic /> },
            { path: "/generate-registration-invitation", element: <RegistrationCode /> },
            { path: "/join-priv-channel", element: <JoinPrivateChannel /> },
            { path: "/create-channel", element: <CreateChannel /> },
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

