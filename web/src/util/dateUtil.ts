export function getDatesThisMonth(): [Date, Date] {
  const currentDate = new Date();
  const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
  return [startDate, currentDate];
}

export function getDatesLast30Days(): [Date, Date] {
  const currentDate = new Date();
  const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), currentDate.getDate() - 30);
  return [startDate, currentDate];
}

export function getDatesMonthlyAverage(): [Date, Date] {
  const currentDate = new Date();
  const startDate = new Date(currentDate.getFullYear(), currentDate.getMonth() - 5, currentDate.getDate());
  return [startDate, currentDate];
}

// Format date to "dd/mm/yyyy"
// Include leading zeros
export function formatDate(date: Date): string {
  const day = date.getDate();
  const month = date.getMonth() + 1;
  const year = date.getFullYear();

  return `${day < 10 ? "0" : ""}${day}/${month < 10 ? "0" : ""}${month}/${year}`;
}
