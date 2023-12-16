/* eslint-disable @typescript-eslint/ban-ts-comment */
import { Container, Title, Flex, Space, Loader, Switch, Select, Center, Stack, Group, Button } from "@mantine/core";
import { DatePickerInput } from "@mantine/dates";
// @ts-ignore
import fetchData from "../util/fetchData";
import { SummaryItem } from "../util/types";
import { getDatesThisMonth, getDatesLast30Days, getDatesMonthlyAverage, formatDate } from "../util/dateUtil";
// @ts-ignore
import { useAuth } from "../util/Auth";
// @ts-ignore
import { useLocalStorage } from "../hooks/useLocalStorage";
import { TRANSACTIONS_URL } from "../api/urls";
import { useState, useEffect } from "react";
import ErrorMessage from "../components/ErrorMessage";
import PieChart from "../components/PieChart";
import { IconArrowRight } from "@tabler/icons-react";

/**
 * The transactions summary page.
 */
const SummaryPage: React.FC = () => {
  const [sheetId] = useLocalStorage("sheetId", "");
  const { token } = useAuth();
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [summaryItems, setSummaryItems] = useState<SummaryItem[]>([]);
  const [showIncome, setShowIncome] = useState<boolean>(false);
  const [showSavings, setShowSavings] = useState<boolean>(false);
  const [dateRange, setDateRange] = useState<[Date, Date]>(getDatesThisMonth());
  const [customSelected, setCustomSelected] = useState<boolean>(false);

  // Load data once on page load
  useEffect(() => {
    reloadData();
  }, []);

  async function reloadData() {
    setIsLoading(true);
    const startDate = formatDate(dateRange[0]);
    const endDate = formatDate(dateRange[1]);
    const { data, error } = await fetchData(`${TRANSACTIONS_URL}/summary?sheetId=${sheetId}&startDate=${startDate}&endDate=${endDate}`, token);
    if (error != null || data == null) {
      setError("Error loading transactions summary.");
    } else {
      setError(null);
      setSummaryItems(data as SummaryItem[]);
    }
    setIsLoading(false);
  }

  function handleSelectChange(value: string | null) {
    if (value == null) return;

    setCustomSelected(false);

    switch (value) {
      case "thisMonth":
        setDateRange(getDatesThisMonth());
        break;
      case "lastMonth":
        setDateRange(getDatesLast30Days());
        break;
      case "monthlyAverage":
        setDateRange(getDatesMonthlyAverage());
        break;
      case "custom":
        setCustomSelected(true);
    }
  }

  return (
    <Container>
      <Title>Transactions Summary</Title>
      <Space h="lg" />
      <Center>
        <Stack>
          <Group>
            <Select
              label="Date range"
              data={[
                { value: "thisMonth", label: "This month" },
                { value: "lastMonth", label: "Last 30 days" },
                { value: "monthlyAverage", label: "Monthly average (last 6 months)" },
                { value: "custom", label: "Custom" },
              ]}
              defaultValue="thisMonth"
              onChange={(value) => {
                handleSelectChange(value);
              }}
            />
            {customSelected && (
              <>
                <DatePickerInput
                  label="Start date"
                  value={dateRange[0]}
                  onChange={(value) => {
                    if (value == null) return;
                    setDateRange([value, dateRange[1]]);
                  }}
                />
                <DatePickerInput
                  label="End date"
                  value={dateRange[1]}
                  onChange={(value) => {
                    if (value == null) return;
                    setDateRange([dateRange[0], value]);
                  }}
                />
              </>
            )}
          </Group>
          <Center>
            <Button onClick={reloadData} rightIcon={<IconArrowRight size="1.2rem" />}>
              Go
            </Button>
          </Center>
        </Stack>
      </Center>
      {error && (
        <div style={{ width: "50%" }}>
          <Space h="lg" />
          <ErrorMessage message={error ?? ""} />
        </div>
      )}
      <Space h="lg" />
      <Title order={2}>Categories</Title>
      <Flex align="center" direction="column" gap="md">
        {isLoading && <Loader />}
        {!isLoading && summaryItems.length !== 0 && <PieChart summaryItems={summaryItems} checked={showIncome} pieType={"Categories"} divideBy={1} />}
        {!isLoading && summaryItems.length === 0 && <ErrorMessage message="No transactions found." />}
        <Switch label="Show income" onChange={(e) => setShowIncome(e.currentTarget.checked)} />
      </Flex>
      <Space h="lg" />
      <Title order={2}>Types</Title>
      <Flex align="center" direction="column" gap="md">
        {isLoading && <Loader />}
        {!isLoading && summaryItems.length !== 0 && <PieChart summaryItems={summaryItems} checked={showSavings} pieType={"Types"} divideBy={1} />}
        {!isLoading && summaryItems.length === 0 && <ErrorMessage message="No transactions found." />}
        <Switch label="Show savings" onChange={(e) => setShowSavings(e.currentTarget.checked)} />
      </Flex>
      <Space h="lg" />
    </Container>
  );
};

export default SummaryPage;
