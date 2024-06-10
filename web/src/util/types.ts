export type SummaryItem = {
  category: string;
  type: string;
  description: string;
  value: number;
};

export type SummaryTypeItem = {
  type: string;
  value: number;
};

export type Filter = {
  category: string;
  query: string;
};

export type PieEntry = {
  id: string;
  label: string;
  value: number;
};

export type PieType = "Categories" | "Types";

export type UnCategorisedTransaction = {
  date: string;
  amount: string;
  description: string;
};

export type CategorisedTransaction = UnCategorisedTransaction & { category: string };
