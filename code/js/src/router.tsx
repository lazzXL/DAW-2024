import * as React from "react";
import { createBrowserRouter } from "react-router-dom";
import { Layout } from "./components/Layout";
import { MessagingApp } from "./components/MessagingApp";
import { Profile } from "./pages/Profile";
import { Settings } from "./pages/Settings";
import { DiscoverPublic } from "./pages/DiscoverPublic";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <Layout />, // Apply the layout here
        children: [
            { path: "/", element: <MessagingApp /> },
            { path: "/profile", element: <Profile /> },
            { path: "/settings", element: <Settings /> },
            { path: "/discover-channels", element: <DiscoverPublic /> },
        ],
    },
]);
