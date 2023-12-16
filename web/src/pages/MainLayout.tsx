import { Outlet } from "react-router-dom";
import { AppShell, Navbar, Header, Text, NavLink, MediaQuery, Burger, useMantineTheme, Flex } from "@mantine/core";
import { useState } from "react";
import { IconChartPie2, IconFileSpreadsheet, IconFilter, IconTool } from "@tabler/icons-react";
import ActionToggle from "../components/ActionToggle";
import ProfileMenu from "../components/ProfileMenu";
import { AppRoutes, pageNames } from "../util/routes";

/**
 * Used in all authenticated app pages. Includes displaying the username,
 * the ability to logout, and links to all app sections. The navbar is
 * hidden on small screens and can be toggled with a burger menu.
 */
const MainLayout: React.FC = () => {
  const theme = useMantineTheme();
  const [opened, setOpened] = useState(false);
  const navLinks = [
    {
      href: AppRoutes.SHEET_SELECT,
      label: pageNames[AppRoutes.SHEET_SELECT],
      icon: <IconFileSpreadsheet size="1.1rem" stroke={1.5} />,
      boldIcon: <IconFileSpreadsheet size="1.1rem" stroke={2.5} />,
    },
    {
      href: AppRoutes.TRANSACTIONS_SUMMARY,
      label: pageNames[AppRoutes.TRANSACTIONS_SUMMARY],
      icon: <IconChartPie2 size="1.1rem" stroke={1.5} />,
      boldIcon: <IconChartPie2 size="1.1rem" stroke={2.5} />,
    },
    {
      href: AppRoutes.MY_FILTERS,
      label: pageNames[AppRoutes.MY_FILTERS],
      icon: <IconFilter size="1.1rem" stroke={1.5} />,
      boldIcon: <IconFilter size="1.1rem" stroke={2.5} />,
    },
    {
      href: AppRoutes.CATEGORISE_TOOL,
      label: pageNames[AppRoutes.CATEGORISE_TOOL],
      icon: <IconTool size="1.1rem" stroke={1.5} />,
      boldIcon: <IconTool size="1.1rem" stroke={2.5} />,
    },
  ];

  return (
    <AppShell
      styles={{
        main: {
          background: theme.colorScheme === "dark" ? theme.colors.dark[8] : theme.colors.gray[0],
        },
      }}
      navbarOffsetBreakpoint="sm"
      asideOffsetBreakpoint="sm"
      navbar={
        <Navbar p="md" hiddenBreakpoint="sm" hidden={!opened} width={{ sm: 200, lg: 300 }}>
          <Navbar.Section grow>
            {
              // Navbar Links
              navLinks.map((link) => (
                <NavLink
                  key={link.href}
                  icon={window.location.pathname === link.href ? link.boldIcon : link.icon}
                  component="a"
                  href={link.href}
                  label={link.label}
                  active={window.location.pathname === link.href}
                  style={{
                    fontWeight: window.location.pathname === link.href ? "bold" : "normal",
                  }}
                />
              ))
            }
          </Navbar.Section>
          {/* Navbar Logout Button */}
          {/* <Navbar.Section>
            <Flex justify="end">
              <LogoutButton />
            </Flex>
          </Navbar.Section> */}
        </Navbar>
      }
      header={
        <Header height={{ base: 50, md: 70 }} p="md">
          <div style={{ display: "flex", alignItems: "center", height: "100%" }}>
            <MediaQuery largerThan="md" styles={{ display: "none" }}>
              <Burger opened={opened} onClick={() => setOpened((o) => !o)} size="sm" color={theme.colors.gray[6]} mr="xl" />
            </MediaQuery>
            <MediaQuery smallerThan="md" styles={{ display: "none" }}>
              <Text>Budgeting App</Text>
            </MediaQuery>
            <div style={{ marginLeft: "auto" }}>
              <Flex gap="sm" align="center">
                <ActionToggle />
                <ProfileMenu />
              </Flex>
            </div>
          </div>
        </Header>
      }
    >
      <Outlet />
    </AppShell>
  );
};

export default MainLayout;
