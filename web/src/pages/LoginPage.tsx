/* eslint-disable @typescript-eslint/ban-ts-comment */
import { useNavigate } from "react-router-dom";
import { useMantineTheme, Container, Image, Space, Stack, Text, Center, Anchor } from "@mantine/core";
// @ts-ignore
import { useAuth } from "../util/Auth";
import { CredentialResponse } from "@react-oauth/google";
import { GoogleLogin } from "@react-oauth/google";
import ErrorMessage from "../components/ErrorMessage";
import { Link } from "react-router-dom";
import { useState } from "react";
import { AppRoutes } from "../util/routes";

/**
 * Displays a button allowing the user to log in with Google.
 * If successful, will redirect the user to the homepage. Otherwise, an error message will be displayed.
 */
const LoginPage: React.FC = () => {
  const theme = useMantineTheme();
  const navigate = useNavigate();
  const { setToken } = useAuth();
  const [error, setError] = useState<string | null>(null);

  function handleResponse(response: CredentialResponse) {
    setToken(response.credential);
    navigate(AppRoutes.SHEET_SELECT, { replace: true });
  }

  function handleError() {
    setError("An error occurred while logging in.");
  }

  return (
    <div
      style={{
        display: "flex",
        flexDirection: "column",
        justifyContent: "center",
        height: "100vh",
        background: theme.colorScheme === "dark" ? theme.colors.dark[8] : theme.colors.gray[1],
      }}
    >
      <div style={{ transform: "translateY(-25%)" }}>
        <Container size="xs" p="lg">
          <Stack align="center">
            <Image src="/logo.png" alt="Logo" width={50} height={50} />
            <Text align="center">Welcome to Budgeting App</Text>
            <Text align="center">Log in with your Google account to continue.</Text>
            {/* To avoid white background issue in dark mode */}
            <div style={{ colorScheme: "light" }}>
              <GoogleLogin onSuccess={handleResponse} onError={handleError} theme="filled_blue" text="signin_with" shape="pill" />
            </div>
          </Stack>
          <Space h="lg" />
          {error && <ErrorMessage message={error ?? ""} />}
        </Container>
      </div>
      <Center>
        <Anchor
          style={{
            bottom: "0",
            position: "absolute",
          }}
          pb="xl"
          size="sm"
          component={Link}
          to={"https://docs.google.com/document/d/1Vpe1hawi9dAVAXlHiQF5KIEhsWiuXczrY8oTN-XN1o0"}
          target="_blank"
        >
          Terms of use
        </Anchor>
      </Center>
    </div>
  );
};

export default LoginPage;
