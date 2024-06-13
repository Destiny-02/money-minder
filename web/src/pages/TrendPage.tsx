/* eslint-disable @typescript-eslint/ban-ts-comment */
import { Container, Title, Flex, Space, Loader, Center, Stack, Group, Button, MultiSelect } from "@mantine/core";
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
  const [categories, setCategories] = useState<string[]>([]);
  const [categorySummariesInChart, setCategorySummariesInChart] = useState<CategorySummary[]>([]);
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
      const categorySummariesData = data as CategorySummary[]
      setCategorySummaries(categorySummariesData);
      setCategorySummariesInChart((categorySummariesData).slice(0, 5));
      setCategories((categorySummariesData).map((categorySummary: CategorySummary) => categorySummary.classification.category));
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
      <Flex align="center" direction="column" gap="md">
        {isLoading && <Loader />}
        {!isLoading && categorySummaries.length !== 0 && 
          <Flex align="center" direction="column" gap="sm">
            <MultiSelect 
              data={categories} defaultValue={categories.slice(0, 5)} 
              label="Categories" placeholder="Categories" style={{ width: "80%" }}
              maxDropdownHeight={160}
              searchable={true}
              onChange={(value) => {
                setCategorySummariesInChart(categorySummaries.filter((categorySummary: CategorySummary) => value.includes(categorySummary.classification.category)));
              }}
            />
            <LineChart categorySummaries={categorySummariesInChart} />
          </Flex>
        }
        {!isLoading && categorySummaries.length === 0 && <ErrorMessage message="No transactions found." />}
      </Flex>
      <Space h="lg" />
    </Container>
  );
};

export default TrendPage;
