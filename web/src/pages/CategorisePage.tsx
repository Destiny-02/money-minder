/* eslint-disable @typescript-eslint/ban-ts-comment */
import { Container, Group, Loader, Space, Button, Title, Text, FileButton, Flex, Table } from "@mantine/core";
// @ts-ignore
import fetchData from "../util/fetchData";
import { Filter, CategorisedTransaction } from "../util/types";
import { fileToUncategorisedTransactions, queryToParts } from "../util/typesUtil";
// @ts-ignore
import { useAuth } from "../util/Auth";
// @ts-ignore
import { useLocalStorage } from "../hooks/useLocalStorage";
import { SHEETS_URL } from "../api/urls";
import { useState } from "react";
import ErrorMessage from "../components/ErrorMessage";
import { IconUpload } from "@tabler/icons-react";

/**
 * The categorise transactions tool where users can use apply their filters on a CSV file containing the transactions.
 */
const CategorisePage: React.FC = () => {
  const [sheetId] = useLocalStorage("sheetId", "");
  const { token } = useAuth();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [categorisedTransactions, setCategorisedTransactions] = useState<CategorisedTransaction[]>([]);

  async function loadFilters() {
    setIsLoading(true);
    const { data, error } = await fetchData(`${SHEETS_URL}/filters?sheetId=${sheetId}`, token);
    setIsLoading(false);

    if (error != null || data == null) {
      setError("Error loading filters.");
      return null;
    } else {
      let filtersResponse = data as Filter[];
      // Filter out duplicate filters (same category and query)
      filtersResponse = filtersResponse.filter(
        (filter, index, self) => index === self.findIndex((f) => f.category === filter.category && f.query === filter.query)
      );
      if (filtersResponse.length === 0) {
        setError("No filters found.");
        return null;
      }
      setError(null);
      return filtersResponse;
    }
  }

  async function onFileUpload(file: File | null) {
    if (file) {
      const loadedFilters = await loadFilters();
      if (loadedFilters) {
        setIsLoading(true);
        categoriseTransactions(file, loadedFilters);
        setIsLoading(false);
      }
    }
  }

  function categoriseTransactions(file: File, filters: Filter[]) {
    fileToUncategorisedTransactions(file)
      .then((transactions) => {
        const cTransac = [];
        for (const transaction of transactions) {
          const matchingFilter = filters.find((filter) => matchesQuery(filter.query, transaction.description));

          cTransac.push({
            ...transaction,
            classification: matchingFilter ? matchingFilter.category : "",
          } as CategorisedTransaction);
        }
        setCategorisedTransactions(cTransac);
      })
      .catch((error) => {
        setError(error.message);
      });
  }

  function removeQuotes(str: string) {
    return str.trim().replace(/^['"]|['"]$/g, '');
  }

  function matchesQuery(query: string, description: string) {
    query = query.toLowerCase().trim();
    description = description.toLowerCase().trim();

    if (query.startsWith("equals ")) {
      return description === removeQuotes(query.substring("equals ".length));
    } else if (query.startsWith("contains ")) {
      query = query.substring("contains ".length);
      let queryParts = queryToParts(query) as string[];
      queryParts = queryParts.filter((part) => part !== "and");
      queryParts = queryParts.map((part) => removeQuotes(part));
      return queryParts.every((part) => description.includes(part));
    }
  }

  return (
    <Container>
      <Group position="apart">
        <Title>Categorise Transactions Tool</Title>
        <FileButton onChange={onFileUpload} accept=".csv">
          {(props) => (
            <Button {...props} rightIcon={<IconUpload size="1rem" />}>
              Upload
            </Button>
          )}
        </FileButton>
      </Group>
      <Space h="lg" />
      <Flex align="center" direction="column" gap="md">
        <Text>
          <p>Please upload your uncategorised transactions CSV in the format &lt;dd/mm/yyyy&gt;, &lt;amount&gt;, &lt;description&gt;.</p>
        </Text>
        {error && !isLoading && (
          <div style={{ width: "50%" }}>
            <Space h="lg" />
            <ErrorMessage message={error ?? ""} />
          </div>
        )}
        {isLoading && <Loader />}
        {!isLoading && categorisedTransactions.length > 0 && (
          <>
            <Table>
              <thead>
                <tr>
                  <th>Date</th>
                  <th>Amount</th>
                  <th>Category</th>
                  <th>Description</th>
                </tr>
              </thead>
              <tbody>
                {categorisedTransactions.map((ct) => (
                  // Assume no duplicate transactions
                  <tr key={ct.date + ct.amount + ct.classification + ct.description}>
                    <td>{ct.date}</td>
                    <td>{ct.amount}</td>
                    <td>{ct.classification}</td>
                    <td>{ct.description}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
          </>
        )}
      </Flex>
    </Container>
  );
};

export default CategorisePage;
