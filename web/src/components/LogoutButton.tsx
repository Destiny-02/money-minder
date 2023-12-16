/* eslint-disable @typescript-eslint/ban-ts-comment */
import { useNavigate } from "react-router-dom";
// @ts-ignore
import { useAuth } from "../util/Auth";
import { Button } from "@mantine/core";
import { IconLogout } from "@tabler/icons-react";
import { AppRoutes } from "../util/routes";

/**
 * A button which logs the user out when clicked.
 */
const LogoutButton: React.FC = () => {
  const { setToken } = useAuth();
  const navigate = useNavigate();

  function handleClick() {
    setToken(null);
    navigate(AppRoutes.LOGIN);
  }

  return (
    <Button variant="outline" rightIcon={<IconLogout size="1.1rem" />} onClick={handleClick}>
      Log out
    </Button>
  );
};

export default LogoutButton;
