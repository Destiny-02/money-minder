/* eslint-disable @typescript-eslint/ban-ts-comment */
import { Route, Routes } from "react-router-dom";
import MainLayout from "./pages/MainLayout";
// @ts-ignore
import { RequiresAuth, RequiresNonAuth } from "./util/Auth";
import LoginPage from "./pages/LoginPage";
import SheetPage from "./pages/SheetPage";
import SummaryPage from "./pages/SummaryPage";
import TrendPage from "./pages/TrendPage";
import { AppRoutes } from "./util/routes";
import FiltersPage from "./pages/FiltersPage";
import CategorisePage from "./pages/CategorisePage";

/**
 * Main app component. Controls the frontend routing.
 */
export default function App() {
  return (
    <Routes>
      <Route
        path={AppRoutes.HOME}
        element={
          <RequiresAuth>
            <MainLayout />
          </RequiresAuth>
        }
      >
        <Route index element={<SheetPage />} />
        <Route path={AppRoutes.SHEET_SELECT} element={<SheetPage />} />
        <Route path={AppRoutes.TRANSACTIONS_SUMMARY} element={<SummaryPage />} />
        <Route path={AppRoutes.TREND} element={<TrendPage />} />
        <Route path={AppRoutes.MY_FILTERS} element={<FiltersPage />} />
        <Route path={AppRoutes.CATEGORISE_TOOL} element={<CategorisePage />} />
      </Route>

      <Route
        path={AppRoutes.LOGIN}
        element={
          <RequiresNonAuth>
            <LoginPage />
          </RequiresNonAuth>
        }
      />
    </Routes>
  );
}
