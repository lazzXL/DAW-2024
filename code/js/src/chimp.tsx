import * as React from "react";
import * as ReactDOM from "react-dom/client";
import { RouterProvider } from "react-router-dom";
import { router } from "./router";
import "./MessagingApp.css";
import { AuthProvider } from "./AuthProvider";


export function messagingApp() {
    ReactDOM.createRoot(document.getElementById("container")).render(
        <AuthProvider>
            <RouterProvider router={router} future={{ v7_startTransition: true }} />
        </AuthProvider>
    )
}
