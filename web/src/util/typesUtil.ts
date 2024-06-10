import { SummaryItem, PieEntry, SummaryTypeItem, UnCategorisedTransaction, LineEntry, CategorySummary } from "./types";
import Papa from "papaparse";

export function summaryItemsToCategoriesPieEntries(summaryItems: SummaryItem[], showIncome: boolean, divideBy: number) {
  if (!showIncome) {
    summaryItems = summaryItems.filter((item) => item.category !== "Salary" && item.category !== "Other Income");
  }

  if (divideBy !== 1) {
    summaryItems = summaryItems.map((item) => ({
      ...item,
      value: item.value / divideBy,
    }));
  }

  return summaryItems.map((item) => ({
    id: item.category,
    label: item.category,
    value: item.value,
  }));
}

export function categorySummariesToLineEntries(categorySummaries: CategorySummary[]) {
  const lineEntries: LineEntry[] = [];

  for (const categorySummary of categorySummaries) {
    categorySummary.monthValues = categorySummary.monthValues.sort((a, b) => dateToNumber(a.month) - dateToNumber(b.month));

    const lineEntry: LineEntry = {
      id: categorySummary.classification.category,
      data: categorySummary.monthValues.map((monthValue) => ({
        x: dateToMonthYear(monthValue.month),
        y: Math.round(monthValue.value * 100) / 100,
      })),
    }

    lineEntries.push(lineEntry);
  }

  return lineEntries;
}

// Converts a date string in the format of dd/mm/yyyy to a number yyyymmdd
function dateToNumber(date: string) {
  const parts = date.split("/");
  return parseInt(parts[2] + parts[1] + parts[0]);
}

// Converts a date string in the format of dd/mm/yyyy to a string in the format of mm/yyyy
function dateToMonthYear(date: string) {
  const parts = date.split("/");
  return parts[1] + "/" + parts[2];
}

// Combines all the summary items with the same types
function summaryItemsToSummaryTypeItems(summaryItems: SummaryItem[]) {
  const summaryTypeItems: SummaryTypeItem[] = [];
  const typeSumMap: Map<string, number> = new Map();

  for (const item of summaryItems) {
    const currentValue = typeSumMap.get(item.type);
    const newValue = currentValue !== undefined ? currentValue + item.value : item.value;
    typeSumMap.set(item.type, newValue);
  }

  for (const [type, value] of typeSumMap) {
    summaryTypeItems.push({
      type,
      value,
    });
  }

  return summaryTypeItems;
}

export function summaryItemsToTypesPieEntries(summaryItems: SummaryItem[], showSavings: boolean, divideBy: number) {
  let summaryTypeItems = summaryItemsToSummaryTypeItems(summaryItems);

  // Replace Income with Savings
  if (showSavings) {
    // Sum the "Needs" and "Wants"
    const needsVal = summaryTypeItems.find((item) => item.type === "Needs")?.value ?? 0;
    const wantsVal = summaryTypeItems.find((item) => item.type === "Wants")?.value ?? 0;
    const needsAndWantsSum = needsVal + wantsVal;

    // Sum the "Income"
    const incomeVal = summaryTypeItems.find((item) => item.type === "Income")?.value ?? 0;

    // Calculate savings
    const savingsVal = incomeVal + needsAndWantsSum; // Plus because needsAndWantsSum is negative

    // Remove the "Income" item
    summaryTypeItems = summaryTypeItems.filter((item) => item.type !== "Income");

    // Add the "Savings" item
    summaryTypeItems.push({
      type: "Savings",
      value: savingsVal,
    });
  }

  return summaryTypeItems.map((item) => ({
    id: item.type,
    label: item.type,
    value: item.value / divideBy,
  })).sort((a, b) => a.id.localeCompare(b.id));
}

export function makePieEntriesNonNegative(pieEntries: PieEntry[]) {
  return pieEntries.map((entry) => ({
    ...entry,
    value: Math.abs(entry.value),
  }));
}

export async function fileToUncategorisedTransactions(file: File): Promise<UnCategorisedTransaction[]> {
  return new Promise((resolve, reject) => {
    const reader = new FileReader();
    reader.addEventListener("load", (event) => {
      const fileContent = event.target?.result;

      if (typeof fileContent === "string") {
        Papa.parse(fileContent, {
          skipEmptyLines: true,
          complete: function (results) {
            const transactions: UnCategorisedTransaction[] = (results.data as string[][]).map((row) => {
              return {
                date: row[0],
                amount: row[1],
                description: row[2],
              };
            });
            resolve(transactions);
          },
        });
      }
    });

    reader.addEventListener("Error reading file", reject);
    reader.readAsText(file);
  });
}

export function queryToParts(inputString: string) {
  // input: 'this is "some example" with "quoted strings"'
  // output: ['this', 'is', '"some example"', 'with', '"quoted strings"']
  inputString = inputString.toLowerCase().trim();
  const regex = /(['"][^'"]+['"]|\w+)/g;
  return inputString.match(regex) || [];
}
