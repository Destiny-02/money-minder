import { MantineProvider, ColorSchemeProvider } from "@mantine/core";
import { useLocalStorage } from "../hooks/useLocalStorage";

/**
 * A component which provides light/dark theme context info to children.
 * Source: https://ui.mantine.dev/category/color-scheme 
 */
export default function LightDarkTheme({ children }) {
  const [colorScheme, setColorScheme] = useLocalStorage("theme", "light");
  const toggleColorScheme = (value) => setColorScheme(value || (colorScheme === "dark" ? "light" : "dark"));

  return (
    <ColorSchemeProvider colorScheme={colorScheme} toggleColorScheme={toggleColorScheme}>
      <MantineProvider theme={{
        fontFamily: 'poppins, sans-serif',
        headings: { fontFamily: 'poppins, sans-serif' },
        colorScheme,
        colors: {
          'theme': ['#E2DFFF', '#C9D2ED', '#B0C5DB', '#97B8C9', '#7EABB7', '#649FA5', '#4B9293', '#328581', '#19786F', '#006B5D'],
        },
        primaryColor: 'theme',
        primaryShade: 7,
      }} withNormalizeCSS withGlobalStyles>
        {children}
      </MantineProvider>
    </ColorSchemeProvider>
  );
}
