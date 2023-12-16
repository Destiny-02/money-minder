/* eslint-disable @typescript-eslint/ban-ts-comment */
import { useNavigate } from "react-router-dom";
import { Menu, Text, Group, Avatar, rem, UnstyledButton } from "@mantine/core";
import { IconChevronDown, IconLogout } from "@tabler/icons-react";
// @ts-ignore
import { useAuth } from "../util/Auth";
import { AppRoutes } from "../util/routes";

export default function ProfileMenu() {
  const { user, setToken } = useAuth();
  const navigate = useNavigate();

  function handleClick() {
    setToken(null);
    navigate(AppRoutes.LOGIN);
  }

  return (
    <Menu width={200} position="bottom-end" transitionProps={{ transition: "pop-top-right" }} withinPortal>
      <Menu.Target>
        <UnstyledButton>
          <Group spacing={8}>
            <Avatar src={user.picture} alt={user.name} radius="xl" size="md" variant="outline" />
            <Text weight={500} size="sm" mr={4}>
              {user.name}
            </Text>
            <IconChevronDown size="0.8rem" />
          </Group>
        </UnstyledButton>
      </Menu.Target>
      <Menu.Dropdown>
        <Menu.Label>Account Settings</Menu.Label>
        <Menu.Item icon={<IconLogout size="0.9rem" />} onClick={handleClick}>
          Logout
        </Menu.Item>
      </Menu.Dropdown>
    </Menu>
  );
}
