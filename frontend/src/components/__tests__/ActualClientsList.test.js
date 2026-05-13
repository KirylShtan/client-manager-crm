import React from "react";
import { render, screen, fireEvent, waitFor, waitForElementToBeRemoved } from "@testing-library/react";
import "@testing-library/jest-dom";
import ActualClientsList from "../ActualClientsList";
import userEvent from "@testing-library/user-event";
import { act } from "react";

jest.mock(
    "react-router-dom",
    () => ({ useNavigate: () => jest.fn() }),
    { virtual: true }
  );
jest.mock("framer-motion", () => ({
  motion: {
    div: ({ children, ...props }) => <div {...props}>{children}</div>,
  },
}));
jest.mock("../../api/clientService", () => ({
  getActualClients: jest.fn(),
  deleteActualClient: jest.fn(),
  addActualClient: jest.fn(),
  updateActualClient: jest.fn(),
  archiveClient: jest.fn(),
  searchActualClients: jest.fn(),
  getDetails: jest.fn(),
  updateDetails: jest.fn(),
  searchClientsByDate: jest.fn(),
  checkStatus: jest.fn(),
  sendNotification: jest.fn(),
  sendTelegramNotification: jest.fn(),
  getCasePassword: jest.fn(),
}));
import {
  getActualClients,
  addActualClient,
  deleteActualClient,
  archiveClient,
  updateActualClient,
  getDetails,
  updateDetails,
  searchActualClients,
  searchClientsByDate,
  getCasePassword,
  sendNotification,
  sendTelegramNotification,
  
} from "../../api/clientService";
describe("ActualClientsList - handleAdd", () => {
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem("authHeader", "Bearer test-token");
    getActualClients.mockResolvedValue([]);
  });
  afterEach(async () => {
  localStorage.clear();
  await act(async () => {
    await Promise.resolve();
  });
});
  test("shows alert and does not call API when at least one field is empty", async () => {
    const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
    render(<ActualClientsList />);
    
    fireEvent.change(screen.getByPlaceholderText("firstName"), { target: { value: "John" } });
    fireEvent.change(screen.getByPlaceholderText("lastName"), { target: { value: "Marston" } });
    fireEvent.click(screen.getByRole("button", { name: "ADD" }));
    expect(alertSpy).toHaveBeenCalledWith("Complete all fields!");
    expect(addActualClient).not.toHaveBeenCalled();
    alertSpy.mockRestore();
  });
  test("adds client, sends today's submissionDate, and resets form on success", async () => {
    
    const expectedDate = new Date().toISOString().split("T")[0];
    addActualClient.mockResolvedValue({
      id: 1,
      firstName: "John",
      lastName: "Marston",
      caseNumber: "2356/2025",
      submissionDate: expectedDate,
      status: "Processing",
      companyName: "Girteka",
      realPassword: "secret123",
      email: "john@example.com",
      payed: "no",
    });
    render(<ActualClientsList />);
    fireEvent.change(screen.getByPlaceholderText("firstName"), { target: { value: "John" } });
    fireEvent.change(screen.getByPlaceholderText("lastName"), { target: { value: "Marston" } });
    fireEvent.change(screen.getByPlaceholderText("caseNumber"), { target: { value: "2356/2025" } });
    fireEvent.change(screen.getByPlaceholderText("status"), { target: { value: "Processing" } });
    fireEvent.change(screen.getByPlaceholderText("companyName"), { target: { value: "Girteka" } });
    fireEvent.change(screen.getByPlaceholderText("realPassword"), { target: { value: "secret123" } });
    fireEvent.change(screen.getByPlaceholderText("email"), { target: { value: "john@example.com" } });
    fireEvent.click(screen.getByRole("button", { name: "ADD" }));
    await waitFor(() => {
      expect(addActualClient).toHaveBeenCalledTimes(1);
    });
    expect(addActualClient).toHaveBeenCalledWith({
      firstName: "John",
      lastName: "Marston",
      caseNumber: "2356/2025",
      status: "Processing",
      companyName: "Girteka",
      realPassword: "secret123",
      email: "john@example.com",
      submissionDate: expectedDate,
    });
    
    expect(await screen.findByText("John")).toBeInTheDocument();
    expect(screen.getByText("Marston")).toBeInTheDocument();
    expect(screen.getByPlaceholderText("firstName")).toHaveValue("");
    expect(screen.getByPlaceholderText("lastName")).toHaveValue("");
    expect(screen.getByPlaceholderText("caseNumber")).toHaveValue("");
    expect(screen.getByPlaceholderText("status")).toHaveValue("");
    expect(screen.getByPlaceholderText("companyName")).toHaveValue("");
    expect(screen.getByPlaceholderText("realPassword")).toHaveValue("");
    expect(screen.getByPlaceholderText("email")).toHaveValue("");
  });
  test("logs error and does not append client when API fails (bad URL/network)", async () => {
    const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
    addActualClient.mockRejectedValue(new Error("Network Error"));
    render(<ActualClientsList />);
    
    fireEvent.change(screen.getByPlaceholderText("firstName"), { target: { value: "John" } });
    fireEvent.change(screen.getByPlaceholderText("lastName"), { target: { value: "Marston" } });
    fireEvent.change(screen.getByPlaceholderText("caseNumber"), { target: { value: "2356/2025" } });
    fireEvent.change(screen.getByPlaceholderText("status"), { target: { value: "Processing" } });
    fireEvent.change(screen.getByPlaceholderText("companyName"), { target: { value: "Girteka" } });
    fireEvent.change(screen.getByPlaceholderText("realPassword"), { target: { value: "secret123" } });
    fireEvent.change(screen.getByPlaceholderText("email"), { target: { value: "john@example.com" } });
    fireEvent.click(screen.getByRole("button", { name: "ADD" }));
    await waitFor(() => {
      expect(addActualClient).toHaveBeenCalledTimes(1);
      expect(consoleSpy).toHaveBeenCalled();
    });
    
    expect(screen.queryByText("John")).not.toBeInTheDocument();
    consoleSpy.mockRestore();
  });
});
describe("ActualClientsList - handleDelete, handleArchive ,handleUpdate, fetchClientDetails, updateClientNote", () => {
  beforeEach(() => {
    jest.resetAllMocks(); 
    localStorage.setItem("authHeader", "Bearer test-token");
    getActualClients.mockResolvedValue([{ id: 1, firstName: "John", lastName: "Marston" }]);
    archiveClient.mockResolvedValue(true);
    });
  afterEach(async () => {
  localStorage.clear();
  await act(async () => {
    await Promise.resolve();
  });
});
  test("deletes client and removes from list on success", async () => {
    deleteActualClient.mockResolvedValue(true);
    render(<ActualClientsList />);
    await screen.findByText("John");
    fireEvent.click(screen.getByRole("button", { name: "Delete" }));
    await waitFor(() => {
      expect(deleteActualClient).toHaveBeenCalledWith(1);
      expect(screen.queryByText("John")).not.toBeInTheDocument();
    });
  });
  test("Archiving client by deleting it from actual repository and adding it in completed client repository", async () => {
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
  archiveClient.mockResolvedValue(true);
  render(<ActualClientsList />);
  await screen.findByText("John");
  await act(async () => {
    fireEvent.click(screen.getByRole("button", { name: "Archive" }));
  });
  await waitFor(() => {
    expect(archiveClient).toHaveBeenCalledWith(1);
  });
  await waitFor(() => {
    expect(screen.queryByText("John")).not.toBeInTheDocument();
  });
  expect(alertSpy).toHaveBeenCalledWith("Client John Marston archived!");
  alertSpy.mockRestore();
  consoleSpy.mockRestore();
});

test("Checking failure scenario while archiving", async () => {
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
  archiveClient.mockRejectedValue(new Error("archive failed"));
  render(<ActualClientsList />);
  await screen.findByText("John");
  await act(async () => {
    fireEvent.click(screen.getByRole("button", { name: "Archive" }));
  });
  await waitFor(() => {
    expect(archiveClient).toHaveBeenCalledWith(1);
  });
  await waitFor(() => {
    expect(screen.queryByText("John")).toBeInTheDocument();
  });
  expect(alertSpy).toHaveBeenCalledWith("Archive error");
  expect(consoleSpy).toHaveBeenCalled();
  alertSpy.mockRestore();
  consoleSpy.mockRestore();
});
    test("updates client when all prompt fields are provided", async () =>{
      const initialClient = {
        id:1,
        firstName: "John",
        lastName: "Marston",
        caseNumber: "123/2025",
        status: "Processing",
        submissionDate: "2025-04-01",
        companyName: "Girteka",
        payed: "-500",
      };
      getActualClients.mockResolvedValue([initialClient]);

      const updatedClient = {
        ...initialClient,
        firstName: "Johny",
        lastName: "Marston Jr",
        caseNumber: "999/2025",
        status: "Finished",
        submissionDate: "2025-04-10",
        companyName: "VanDerLinde Gang",
        payed: "yes"
      };

      const promptSpy = jest.spyOn(window,"prompt");
      promptSpy
        .mockReturnValueOnce(updatedClient.firstName)
        .mockReturnValueOnce(updatedClient.lastName)
        .mockReturnValueOnce(updatedClient.caseNumber)
        .mockReturnValueOnce(updatedClient.status)
        .mockReturnValueOnce(updatedClient.submissionDate)
        .mockReturnValueOnce(updatedClient.companyName)
        .mockReturnValueOnce(updatedClient.payed);

        updateActualClient.mockResolvedValue(updatedClient);
        render(<ActualClientsList/>)
        await screen.findByText("John");
        fireEvent.click(screen.getByRole("button",{ name: "Update" }));
        await waitFor(() => {
          expect(updateActualClient).toHaveBeenCalledWith(1,updatedClient);
        });
        expect(await screen.findByText("Johny")).toBeInTheDocument();
        promptSpy.mockRestore();
    });
  test("updates client when not all prompt fields are provided", async () =>{
    const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
      const initialClient = {
        id:1,
        firstName: "John",
        lastName: "Marston",
        caseNumber: "123/2025",
        status: "Processing",
        submissionDate: "2025-04-01",
        companyName: "Girteka",
        payed: "-500",
      };
      getActualClients.mockResolvedValue([initialClient]);

      const updatedClient = {
        ...initialClient,
        firstName: "",
        lastName: "Marston Jr",
        caseNumber: "999/2025",
        status: "Finished",
        submissionDate: "2025-04-10",
        companyName: "VanDerLinde Gang",
        payed: "yes"
      };

      const promptSpy = jest.spyOn(window,"prompt");
      promptSpy
        .mockReturnValueOnce(updatedClient.firstName)
        .mockReturnValueOnce(updatedClient.lastName)
        .mockReturnValueOnce(updatedClient.caseNumber)
        .mockReturnValueOnce(updatedClient.status)
        .mockReturnValueOnce(updatedClient.submissionDate)
        .mockReturnValueOnce(updatedClient.companyName)
        .mockReturnValueOnce(updatedClient.payed);

        
        render(<ActualClientsList/>)
        await screen.findByText("John");
        fireEvent.click(screen.getByRole("button",{ name: "Update" }));
        await waitFor(() => {
          expect(updateActualClient).not.toHaveBeenCalled();
          expect(alertSpy).toHaveBeenCalledWith("All fields are required!");
        });
        expect(await screen.findByText("John")).toBeInTheDocument();
        promptSpy.mockRestore();
        alertSpy.mockRestore();
    });
    test("keeps old data when update API fails", async () => {
  const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  getActualClients.mockResolvedValue([initialClient]);
  const promptSpy = jest.spyOn(window, "prompt");
  promptSpy
    .mockReturnValueOnce("Johny")
    .mockReturnValueOnce("Marston Jr")
    .mockReturnValueOnce("999/2025")
    .mockReturnValueOnce("Finished")
    .mockReturnValueOnce("2025-04-10")
    .mockReturnValueOnce("NATADATA")
    .mockReturnValueOnce("yes");
  const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
  updateActualClient.mockRejectedValue(new Error("Update failed"));
  render(<ActualClientsList />);
  await screen.findByText("John");
  fireEvent.click(screen.getByRole("button", { name: "Update" }));
  await waitFor(() => {
    expect(updateActualClient).toHaveBeenCalledWith(1, {
      ...initialClient,
      firstName: "Johny",
      lastName: "Marston Jr",
      caseNumber: "999/2025",
      status: "Finished",
      submissionDate: "2025-04-10",
      companyName: "NATADATA",
      payed: "yes",
    });
    expect(consoleSpy).toHaveBeenCalled();
  });
  expect(screen.getByText("John")).toBeInTheDocument();
  expect(screen.queryByText("Johny")).not.toBeInTheDocument();
  promptSpy.mockRestore();
  consoleSpy.mockRestore();
});
  test("fetches and displays client details panel", async () => {
  const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  const details = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    note: "Important note",
  };
  getActualClients.mockResolvedValue([initialClient]);
  getDetails.mockResolvedValue(details);
  render(<ActualClientsList />);
  await screen.findByText("John");
  fireEvent.click(screen.getByRole("button", { name: "Details" }));
  await waitFor(() => {
    expect(getDetails).toHaveBeenCalledWith(1);
  });
  expect(await screen.findByText("Client Details")).toBeInTheDocument();
  expect(screen.getByText(/^Name:$/)).toBeInTheDocument();
  expect(screen.getByText(/Last Name:/)).toBeInTheDocument();
  expect(screen.getByText("Important note")).toBeInTheDocument();
});
  test("Updates client note correctly", async () => {
  const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  const details = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    note: "Important note",
  };
  getActualClients.mockResolvedValue([initialClient]);
  getDetails.mockResolvedValue(details);
  // Компонент читает updated.note (а не updated.data.note)
  updateDetails.mockResolvedValue({ note: "Updated note" });
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  render(<ActualClientsList />);
  await screen.findByText("John");
  fireEvent.click(screen.getByRole("button", { name: "Details" }));
  await waitFor(() => {
    expect(getDetails).toHaveBeenCalledWith(1);
  });
  expect(await screen.findByText("Client Details")).toBeInTheDocument();
  fireEvent.click(screen.getByRole("button", { name: "Edit" }));
  const noteInput = await screen.findByDisplayValue("Important note");
  fireEvent.change(noteInput, { target: { value: "Updated note" } });
  fireEvent.click(screen.getByRole("button", { name: "Save" }));
  await waitFor(() => {
    expect(updateDetails).toHaveBeenCalledWith(
      1,
      expect.objectContaining({ note: "Updated note" })
    );
  });
  expect(await screen.findByText("Updated note")).toBeInTheDocument();
  expect(alertSpy).toHaveBeenCalledWith("Note updated!");
  alertSpy.mockRestore();
});
  test("Searching clients at least using one field", async () => {
    const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  searchActualClients.mockResolvedValue([initialClient]);
  render(<ActualClientsList />);
  await screen.findByText("John");
  const parameterInput = await screen.findByPlaceholderText("Search by name, case...");
  fireEvent.change(parameterInput, { target: { value: "John" } });
  fireEvent.click(screen.getByRole("button", { name: "SEARCH" }));
  await waitFor(() => {
    expect(searchActualClients).toHaveBeenCalledWith({
      firstName: "John",
      lastName: "John",
      status: "John",
      caseNumber: "John",
      companyName: "John",
      submissionDate: "John",
      });
  });
  expect(await screen.findByText("John")).toBeInTheDocument();
  });
  test("Searching clients between certain dates", async () =>{
    const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
    const initialClient1 = {
      id: 2,
      firstName: "Arthur",
      lastName: "Morgan",
      caseNumber: "523432/2025",
      status: "Processing",
      submissionDate: "2025-04-03",
      companyName: "Girteka",
      payed: "yes"
    };
    searchClientsByDate.mockResolvedValue([initialClient,initialClient1]);
    
    const { container } = render(<ActualClientsList />);
    const dateInputs = container.querySelectorAll('input[type="date"]');
    const parameterInputFirstDate = dateInputs[0];
    const parameterInputSecondDate = dateInputs[1];
    fireEvent.change(parameterInputFirstDate, {target : { value: "2025-04-01"}});
    fireEvent.change(parameterInputSecondDate, {target : {value: "2025-04-03"}});
    fireEvent.click(screen.getByRole("button", { name : "SEARCH BETWEEN DATES"}));
    await waitFor(() => {
      expect(searchClientsByDate).toHaveBeenCalledWith("2025-04-01", "2025-04-03")
      })
    expect(await screen.findByText("John")).toBeInTheDocument();
    expect(await screen.findByText("Arthur")).toBeInTheDocument();
  });
  test("opens status page with encoded payload on success", async () => {
  const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  getActualClients.mockResolvedValue([initialClient]);
  getCasePassword.mockResolvedValue("secret123");
  const openSpy = jest.spyOn(window, "open").mockImplementation(() => null);
  render(<ActualClientsList />);
  await screen.findByText("John");
  fireEvent.click(screen.getByRole("button", { name: "Status" }));
  await waitFor(() => {
    expect(getCasePassword).toHaveBeenCalledWith(1);
    expect(openSpy).toHaveBeenCalledTimes(1);
  });
  const [calledUrl, target, features] = openSpy.mock.calls[0];
  expect(calledUrl).toContain("https://www.poznan.uw.gov.pl/cudzoziemcy-stan/?lang=pl#autofill=");
  expect(target).toBe("_blank");
  expect(features).toBe("noopener,noreferrer");
  const encodedPart = calledUrl.split("#autofill=")[1];
  const decodedPayload = JSON.parse(atob(decodeURIComponent(encodedPart)));
  expect(decodedPayload).toEqual({
    caseNumber: "123/2025",
    password: "secret123",
  });
  openSpy.mockRestore();
});
  test("shows alert when status check preparation fails", async () => {
  const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  getActualClients.mockResolvedValue([initialClient]);
  getCasePassword.mockRejectedValue(new Error("Vault down"));
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  const openSpy = jest.spyOn(window, "open").mockImplementation(() => null);
  render(<ActualClientsList />);
  await screen.findByText("John");
  fireEvent.click(screen.getByRole("button", { name: "Status" }));
  await waitFor(() => {
    expect(getCasePassword).toHaveBeenCalledWith(1);
    expect(openSpy).not.toHaveBeenCalled();
    expect(alertSpy).toHaveBeenCalledWith("Failed to prepare status check: Vault down");
  });
  alertSpy.mockRestore();
  openSpy.mockRestore();
});
  test("sends notification email for client", async () => {
    const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
    getActualClients.mockResolvedValue([initialClient]);
    sendNotification.mockResolvedValue("example@gmail.com");
    const alertSpy = jest.spyOn(window,"alert").mockImplementation(() => {});
    render(<ActualClientsList />);
    await screen.findByText("John");
    fireEvent.click(screen.getByRole("button", { name: "Notify Email"}));
    await waitFor(() => {
      expect(sendNotification).toHaveBeenCalledWith(1,"STATUS_CHANGED");
    });
      expect(alertSpy).toHaveBeenCalledWith("example@gmail.com");
  });
    test ("shows alert when sending notification fails", async () => {
      const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
    };
    getActualClients.mockResolvedValue([initialClient]);
    sendNotification.mockRejectedValue(new Error("Notification failed!"));
    const alertSpy = jest.spyOn(window,"alert").mockImplementation(() => {});
    render(<ActualClientsList />);
    await screen.findByText("John");
    fireEvent.click(screen.getByRole("button", { name: "Notify Email" }));
    await waitFor(() => {
      expect(sendNotification).toHaveBeenCalledWith(1, "STATUS_CHANGED");
      expect(alertSpy).toHaveBeenCalledWith("Failed to send notification: Notification failed!");
    });

  });
   test("Notifying client with Telegram bot", async () => {
      const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
    };
    getActualClients.mockResolvedValue([initialClient]);
    sendTelegramNotification.mockResolvedValue("Sent");
    const alertSpy = jest.spyOn(window,"alert").mockImplementation(() => {});
    render(<ActualClientsList />);
    await screen.findByText("John");
    fireEvent.click(screen.getByRole("button", { name: "Notify Telegram"}));
    await waitFor(() => {
      expect(sendTelegramNotification).toHaveBeenCalledWith(1);
      expect(alertSpy).toHaveBeenCalledWith("Sent");
    });
   });
   test("shows alert when sending Telegram notification", async () => {
      const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
    };
    getActualClients.mockResolvedValue([initialClient]);
    sendTelegramNotification.mockRejectedValue(new Error("Bot offline"));
    const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
    render(<ActualClientsList />);
    await screen.findByText("John");
    fireEvent.click(screen.getByRole("button", { name: "Notify Telegram"}));
    await waitFor(() => {
      expect(sendTelegramNotification).toHaveBeenCalledWith(1);
      expect(alertSpy).toHaveBeenCalledWith("Telegram notification failed: Bot offline");
    });
   });
   test("Telegram button shows loading state and resets in finally", async () => {
  const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  getActualClients.mockResolvedValue([initialClient]);
  
  let resolveTelegram;
  const telegramPromise = new Promise((resolve) => {
    resolveTelegram = resolve;
  });
  sendTelegramNotification.mockReturnValue(telegramPromise);
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  render(<ActualClientsList />);
  await screen.findByText("John");
  const telegramBtnInitial = screen.getByRole("button", { name: "Notify Telegram" });
  expect(telegramBtnInitial).toBeEnabled();
  fireEvent.click(telegramBtnInitial);
  expect(await screen.findByRole("button", { name: "Sending..." })).toBeDisabled();
  resolveTelegram("Sent!");
  await waitFor(() => {
    expect(screen.getByRole("button", { name: "Notify Telegram" })).toBeEnabled();
  });
  expect(alertSpy).toHaveBeenCalledWith("Sent!");
  alertSpy.mockRestore();
});
  test ("Getting secret password from HashiCorpVault", async () => {
    const initialClient = {
    id: 1,
    firstName: "John",
    lastName: "Marston",
    caseNumber: "123/2025",
    status: "Processing",
    submissionDate: "2025-04-01",
    companyName: "Girteka",
    payed: "no",
  };
  getActualClients.mockResolvedValue([initialClient]);
  getCasePassword.mockResolvedValue("secret-123");
  const alertSpy = jest.spyOn(window,"alert").mockImplementation(() => {});
  render(<ActualClientsList />);
  await screen.findByText("John");
  fireEvent.click(screen.getByRole("button", { name: "Get Password"}));
  await waitFor(() => {
    expect(getCasePassword).toHaveBeenCalledWith(1);
    expect(alertSpy).toHaveBeenCalledWith("secret-123");  
  });
  });
});