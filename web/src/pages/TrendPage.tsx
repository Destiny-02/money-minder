/* eslint-disable @typescript-eslint/ban-ts-comment */
import { Container, Title, Flex, Space, Loader, Center, Stack, Group, Button } from "@mantine/core";
import { DatePickerInput } from "@mantine/dates";
// @ts-ignore
import fetchData from "../util/fetchData";
import { CategorySummary } from "../util/types";
import { getDatesThisMonth, formatDate } from "../util/dateUtil";
// @ts-ignore
import { useAuth } from "../util/Auth";
// @ts-ignore
import { useLocalStorage } from "../hooks/useLocalStorage";
import { TRANSACTIONS_URL } from "../api/urls";
import { useState, useEffect } from "react";
import ErrorMessage from "../components/ErrorMessage";
import { IconArrowRight } from "@tabler/icons-react";
import LineChart from "../components/LineChart";

/**
 * The transactions summary page.
 */
const TrendPage: React.FC = () => {
  const [sheetId] = useLocalStorage("sheetId", "");
  const { token } = useAuth();
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [categorySummaries, setCategorySummaries] = useState<CategorySummary[]>([]);
  const [dateRange, setDateRange] = useState<[Date, Date]>(getDatesThisMonth());

  // Load data once on page load
  useEffect(() => {
    reloadData();
  }, []);

  async function reloadData() {
    setIsLoading(true);
    const startDate = formatDate(dateRange[0]);
    const endDate = formatDate(dateRange[1]);
    const { data, error } = await fetchData(`${TRANSACTIONS_URL}/category-summary?sheetId=${sheetId}&startDate=${startDate}&endDate=${endDate}`, token);
    if (error != null || data == null) {
      setError("Error loading transactions summary.");
    } else {
      setError(null);
      setCategorySummaries(data as CategorySummary[]);
    }
    setIsLoading(false);
  }

  return (
    <Container>
      <Title>Monthly Trend</Title>
      <Space h="lg" />
      <Center>
        <Stack>
          <Group>
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
          </Group>
          <Center>
            <Button onClick={reloadData} rightIcon={<IconArrowRight size="1.2rem" />}>
              Go
            </Button>
          </Center>
        </Stack>
      </Center>
      {!isLoading && error && (
        <div style={{ width: "50%" }}>
          <Space h="lg" />
          <ErrorMessage message={error ?? ""} />
        </div>
      )}
      <Space h="lg" />
      <Title order={2}>Categories</Title>
      <Flex align="center" direction="column" gap="md">
        {isLoading && <Loader />}
        {!isLoading && categorySummaries.length !== 0 && <LineChart categorySummaries={categorySummaries} />}
        {!isLoading && categorySummaries.length === 0 && <ErrorMessage message="No transactions found." />}
      </Flex>
      <Space h="lg" />
    </Container>
  );
};

export default TrendPage;
