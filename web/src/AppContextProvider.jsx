import React from "react";

export const AppContext = React.createContext();

/**
 * A component which provides application-wide context info to children.
 */
export default function AppContextProvider({ children }) {
  const context = {};

  return <AppContext.Provider value={context}>{children}</AppContext.Provider>;
}
