/* eslint-disable @typescript-eslint/ban-ts-comment */
import { Center, Container, Group, Loader, Space, Button, Table, Title } from "@mantine/core";
// @ts-ignore
import fetchData from "../util/fetchData";
import { Filter } from "../util/types";
// @ts-ignore
import { useAuth } from "../util/Auth";
// @ts-ignore
import { useLocalStorage } from "../hooks/useLocalStorage";
import { SHEETS_URL } from "../api/urls";
import { useState, useEffect } from "react";
import ErrorMessage from "../components/ErrorMessage";
import FilterRow from "../components/FilterRow";
import { IconEdit } from "@tabler/icons-react";
import { Link } from "react-router-dom";

/**
 * The filters page where users can view their filters.
 */
const FiltersPage: React.FC = () => {
  const [sheetId] = useLocalStorage("sheetId", "");
  const { token } = useAuth();
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [filters, setFilters] = useState<Filter[]>([]);

  // Load data once on page load
  useEffect(() => {
    reloadData();
  }, []);

  async function reloadData() {
    setIsLoading(true);
    const { data, error } = await fetchData(`${SHEETS_URL}/filters?sheetId=${sheetId}`, token);
    setIsLoading(false);

    if (error != null || data == null) {
      setError("Error loading filters.");
    } else {
      let filtersResponse = data as Filter[];
      // Filter out duplicate filters (same category and query)
      filtersResponse = filtersResponse.filter(
        (filter, index, self) => index === self.findIndex((f) => f.category === filter.category && f.query === filter.query)
      );
      setFilters(filtersResponse);
      if (filtersResponse.length === 0) {
        setError("No filters found.");
      }
      setError(null);
    }
  }

  return (
    <Container>
      <Group position="apart">
        <Title>My Filters</Title>
        <Button rightIcon={<IconEdit size="1rem" />} component={Link} to={`https://docs.google.com/spreadsheets/d/${sheetId}`} target="_blank">
          Edit
        </Button>
      </Group>
      <Space h="lg" />
      <Center>
        {error && !isLoading && (
          <div style={{ width: "50%" }}>
            <Space h="lg" />
            <ErrorMessage message={error ?? ""} />
          </div>
        )}
        {isLoading && <Loader />}
      </Center>
      {filters.length > 0 && (
        <Table>
          <thead>
            <tr>
              <th>Category</th>
              <th>Query</th>
            </tr>
          </thead>
          <tbody>
            {filters.map((filter) => (
              // Assume that the filter is unique (no duplicate filters in the table)
              <FilterRow key={filter.category + filter.query} filter={filter} />
            ))}
          </tbody>
        </Table>
      )}
    </Container>
  );
};

export default FiltersPage;
