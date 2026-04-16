import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import CompletedClientsList from "../CompletedClienstList";
jest.mock(
  "react-router-dom",
  () => ({
    useNavigate: () => jest.fn(),
  }),
  { virtual: true }
);
jest.mock("framer-motion", () => ({
  motion: {
    div: ({ children, ...props }) => <div {...props}>{children}</div>,
  },
}));

jest.mock("../../api/competedSerivce", () => ({
  getAllCompletedClients: jest.fn(),
  deleteCompletedClient: jest.fn(),
  searchCompletedClients: jest.fn(),
  getCompletedDetails: jest.fn(),
  updateCompletedDetails: jest.fn(),
  updateCompletedClient: jest.fn(),
}));
import {
  getAllCompletedClients,
  deleteCompletedClient,
  searchCompletedClients,
} from "../../api/competedSerivce";
describe("CompletedClientsList", () => {
  const sampleClient = {
    id: 1,
    firstName: "Jane",
    lastName: "Doe",
    caseNumber: "1/2025",
    submissionDate: "2025-01-01",
    status: "completed",
    companyName: "Acme",
    payed: "yes",
    clientUuid: "uuid-1",
  };
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem("authHeader", "Bearer test-token");
    getAllCompletedClients.mockResolvedValue([sampleClient]);
  });
  afterEach(() => {
    localStorage.clear();
  });
  test("loads completed clients on mount and shows a row", async () => {
    render(<CompletedClientsList />);
    await waitFor(() => {
      expect(getAllCompletedClients).toHaveBeenCalledTimes(1);
    });
    expect(await screen.findByText("Jane")).toBeInTheDocument();
    expect(screen.getByText("Doe")).toBeInTheDocument();
    expect(screen.getByRole("heading", { name: /completed clients/i })).toBeInTheDocument();
  });
  test("search failure shows alert with Search failed message", async () => {
    const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
    const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
    getAllCompletedClients.mockResolvedValue([]);
    searchCompletedClients.mockRejectedValue(new Error("bad request"));
    render(<CompletedClientsList />);
    await waitFor(() => {
      expect(getAllCompletedClients).toHaveBeenCalled();
    });
    fireEvent.change(screen.getByPlaceholderText(/search by name/i), {
      target: { value: "x" },
    });
    fireEvent.click(screen.getByRole("button", { name: "SEARCH" }));
    await waitFor(() => {
      expect(searchCompletedClients).toHaveBeenCalled();
    });
    expect(alertSpy).toHaveBeenCalledWith("Search failed. Check the format.");
    expect(consoleSpy).toHaveBeenCalled();
    alertSpy.mockRestore();
    consoleSpy.mockRestore();
  });
});