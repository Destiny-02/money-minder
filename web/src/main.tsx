/* eslint-disable @typescript-eslint/ban-ts-comment */
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
// @ts-ignore
import AppContextProvider from "./AppContextProvider";
import App from "./App";
import "./index.css";
// @ts-ignore
import { AuthContextProvider } from "./util/Auth";
// @ts-ignore
import LightDarkTheme from "./components/LightDarkTheme";
import { GoogleOAuthProvider } from "@react-oauth/google";

const clientId = import.meta.env.VITE_REACT_APP_CLIENT_ID ?? "";

ReactDOM.createRoot(document.getElementById("root") as HTMLElement).render(
  <GoogleOAuthProvider clientId={clientId}>
    <AuthContextProvider>
      <AppContextProvider>
        <BrowserRouter>
          <LightDarkTheme>
            <App />
          </LightDarkTheme>
        </BrowserRouter>
      </AppContextProvider>
    </AuthContextProvider>
  </GoogleOAuthProvider>
);
