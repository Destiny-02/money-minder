/* eslint-disable @typescript-eslint/ban-ts-comment */
import { Container, Title, TypographyStylesProvider, Flex, Text, TextInput, Button, Checkbox, Space, Anchor } from "@mantine/core";
import CustomCopyButton from "../components/CopyButton";
import { useState, useEffect } from "react";
import { IconDeviceFloppy } from "@tabler/icons-react";
import ErrorMessage from "../components/ErrorMessage";
import CustomNotification from "../components/Notification";
// @ts-ignore
import { useLocalStorage } from "../hooks/useLocalStorage";
// @ts-ignore
import fetchData from "../util/fetchData";
import { SHEETS_URL } from "../api/urls";
// @ts-ignore
import { useAuth } from "../util/Auth";

/**
 * The sheet select page.
 * Also the default page for authenticated users.
 */
const SheetPage: React.FC = () => {
  const [sheetId, setSheetId] = useState("");
  const [savedSheetId, setSavedSheetId] = useLocalStorage("sheetId", "");
  const [checked, setChecked] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [loading, setLoading] = useState(false);
  const [notification, setNotification] = useState<string | null>(null);
  const [showNotification, setShowNotification] = useState(false);
  const { token } = useAuth();
  const serviceAccountEmail = "service-account@budgeting-app-389210.iam.gserviceaccount.com";

  const handleViewStatsClick = async () => {
    setError(null);

    if (sheetId === "") {
      setError("Please enter a Google Sheet ID.");
      return;
    }

    if (!checked) {
      setError("Please agree to the Terms of Use.");
      return;
    }

    setLoading(true);

    const response = await verifySheetId(sheetId);
    if (response.error || !response.isValid) {
      setError("Error validating sheet ID. ");
    } else {
      // Valid sheet ID
      setSavedSheetId(sheetId);
      setNotification("Sheet ID saved!");
      setShowNotification(true);
    }
    setLoading(false);
  };

  const verifySheetId = async (sheetId: string) => {
    const { data: isValid, error } = await fetchData(`${SHEETS_URL}/verify?sheetId=${sheetId}`, token);
    return { isValid, error };
  };

  // Set saved sheet ID if it exists
  useEffect(() => {
    if (savedSheetId !== "") {
      setSheetId(savedSheetId);
    }
  }, [savedSheetId]);

  return (
    <div>
      <Container>
        <CustomNotification notification={notification ?? ""} showNotification={showNotification} setShowNotification={setShowNotification} />
      </Container>
      <Container>
        <Title>Sheet Select</Title>
        <Space h="lg" />
        <Flex align="center" direction="column" gap="md">
          <TypographyStylesProvider>
            Please share your Google Sheet with edit permissions with the email address below. Please ensure that your Google Sheet is in the right{" "}
            <a href="https://docs.google.com/document/d/1UWql0gntXs781yd3M3EMSsdCJHGfpmZq4U7UGSUPGRs">format</a>.
          </TypographyStylesProvider>
          <Flex>
            <Text>{serviceAccountEmail}</Text>
            <CustomCopyButton copyText={serviceAccountEmail} />
          </Flex>
          <Space h="md" />
          <TextInput
            placeholder="Your Google Sheet ID"
            label="Google Sheet ID"
            value={sheetId}
            onChange={(event) => setSheetId(event.currentTarget.value)}
            withAsterisk
            style={{ width: "50%" }}
          />
          <Space h="md" />
          <Button rightIcon={<IconDeviceFloppy size="1rem" />} onClick={handleViewStatsClick} loading={loading}>
            Save
          </Button>
          <Checkbox
            label={
              <>
                I agree with the{" "}
                <Anchor href="https://docs.google.com/document/d/1Vpe1hawi9dAVAXlHiQF5KIEhsWiuXczrY8oTN-XN1o0" target="_blank">
                  Terms of Use
                </Anchor>
              </>
            }
            checked={checked}
            onChange={(event) => setChecked(event.currentTarget.checked)}
          />

          {error && (
            <div style={{ width: "50%" }}>
              <ErrorMessage message={error ?? ""} />
            </div>
          )}
        </Flex>
      </Container>
    </div>
  );
};

export default SheetPage;
