import React from "react";
import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import ClientFiles from "../ClientFiles";
jest.mock("framer-motion", () => ({
  motion: {
    div: ({ children, ...props }) => <div {...props}>{children}</div>,
  },
}));
jest.mock(
  "../../api/clientFileService",
  () => ({
    __esModule: true,
    default: {
      uploadFile: jest.fn(),
      getClientFiles: jest.fn(),
      deleteFile: jest.fn(),
      downloadFile: jest.fn(),
    },
  })
);
import clientFileService from "../../api/clientFileService";
    const mockFile = {
    id: 42,
    originalName: "doc.pdf",
    size: 2048,
    contentType: "application/pdf",
    };


describe("ClientFiles - fetchFiles, handleUpload", () => {
  const clientUuid = "test-client-uuid";
  beforeEach(() => {
    jest.clearAllMocks();
    localStorage.setItem("authHeader", "Bearer test-token");
    clientFileService.getClientFiles.mockResolvedValue([]);
  });
  afterEach(() => {
    localStorage.clear();
  });
  test("shows all files returned for this client id", async () => {
    clientFileService.getClientFiles.mockResolvedValue([
      {
        id: 1,
        originalName: "doc.pdf",
        size: 2048,
        contentType: "application/pdf",
      },
    ]);
    render(<ClientFiles clientUuid={clientUuid} />);
    await waitFor(() => {
      expect(clientFileService.getClientFiles).toHaveBeenCalledWith(clientUuid);
    });
    expect(await screen.findByText("doc.pdf")).toBeInTheDocument();
  });
  test("showing empty clientFiles list" , async () => {
    
    clientFileService.getClientFiles.mockResolvedValue([]);
    render(<ClientFiles clientUuid={clientUuid} />);
    await waitFor(() => {
        expect(clientFileService.getClientFiles).toHaveBeenCalledWith(clientUuid);
    });
    expect(await screen.findByText("No files uploaded yet.")).toBeInTheDocument();
    });
    test("clicking Upload does not call upload when no file is selected", async () => {
    clientFileService.getClientFiles.mockResolvedValue([]);
    render(<ClientFiles clientUuid={clientUuid} />);
    await waitFor(() => {
        expect(clientFileService.getClientFiles).toHaveBeenCalledWith(clientUuid);
    });
    const uploadButton = screen.getByRole("button", { name: "Upload" });
    expect(uploadButton).toBeDisabled();
    fireEvent.click(uploadButton);
    expect(clientFileService.uploadFile).not.toHaveBeenCalled();
});
    test("uploads selected file for the given clientUuid", async () => {
        const file = new File(["hello"], "doc.pdf", { type: "application/pdf" });
        clientFileService.getClientFiles.mockResolvedValue([]);
        clientFileService.uploadFile.mockResolvedValue({ id: 1, originalName: "doc.pdf" });
        const { container } = render(<ClientFiles clientUuid={clientUuid} />);
        await waitFor(() => {
            expect(clientFileService.getClientFiles).toHaveBeenCalledWith(clientUuid);
        });
        const input = container.querySelector('input[type="file"]');
        fireEvent.change(input, { target: { files: [file] } });
        fireEvent.click(screen.getByRole("button", { name: "Upload" }));
         await waitFor(() => {
            expect(clientFileService.uploadFile).toHaveBeenCalledWith(clientUuid, file);
        });
        await waitFor(() => {
         expect(clientFileService.getClientFiles).toHaveBeenCalledTimes(2);
        });
    });
    test("shows alert when upload fails", async () => {
        const file = new File(["x"], "doc.pdf", { type: "application/pdf" });
        const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
        const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
        clientFileService.getClientFiles.mockResolvedValue([]);
        clientFileService.uploadFile.mockRejectedValue(new Error("Upload failed"));
        const { container } = render(<ClientFiles clientUuid={clientUuid} />);
        await waitFor(() => {
            expect(clientFileService.getClientFiles).toHaveBeenCalledWith(clientUuid);
        });
        const input = container.querySelector('input[type="file"]');
        fireEvent.change(input, { target: { files: [file] } });
        fireEvent.click(screen.getByRole("button", { name: "Upload" }));
        await waitFor(() => {
            expect(clientFileService.uploadFile).toHaveBeenCalledWith(clientUuid, file);
        });
        expect(alertSpy).toHaveBeenCalledWith("Upload error");
        expect(consoleSpy).toHaveBeenCalled();
        alertSpy.mockRestore();
        consoleSpy.mockRestore();
    });
    test("calls downloadFile with id and originalName on Download success", async () => {
  clientFileService.getClientFiles.mockResolvedValue([mockFile]);
  clientFileService.downloadFile.mockResolvedValue(undefined);
  const { container } = render(<ClientFiles clientUuid={clientUuid} />);
  await waitFor(() => {
    expect(clientFileService.getClientFiles).toHaveBeenCalledWith(clientUuid);
  });
  fireEvent.click(screen.getByRole("button", { name: "Download" }));
  await waitFor(() => {
    expect(clientFileService.downloadFile).toHaveBeenCalledWith(
      mockFile.id,
      mockFile.originalName
    );
  });
});
test('shows alert when download fails', async () => {
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
  clientFileService.getClientFiles.mockResolvedValue([mockFile]);
  clientFileService.downloadFile.mockRejectedValue(new Error("fail"));
  render(<ClientFiles clientUuid={clientUuid} />);
  await screen.findByText("doc.pdf");
  fireEvent.click(screen.getByRole("button", { name: "Download" }));
  await waitFor(() => {
    expect(alertSpy).toHaveBeenCalledWith("Download error");
  });
  expect(consoleSpy).toHaveBeenCalled();
  alertSpy.mockRestore();
  consoleSpy.mockRestore();
});
    test("does not call deleteFile when user cancels confirm", async () => {
  const confirmSpy = jest.spyOn(window, "confirm").mockReturnValue(false);
  clientFileService.getClientFiles.mockResolvedValue([mockFile]);
  render(<ClientFiles clientUuid={clientUuid} />);
  await screen.findByText("doc.pdf");
  fireEvent.click(screen.getByRole("button", { name: "Delete" }));
  expect(confirmSpy).toHaveBeenCalledWith(`Delete file ${mockFile.originalName}?`);
  expect(clientFileService.deleteFile).not.toHaveBeenCalled();
  confirmSpy.mockRestore();
});
    test("calls deleteFile and refetches list when user confirms", async () => {
  const confirmSpy = jest.spyOn(window, "confirm").mockReturnValue(true);
  clientFileService.getClientFiles.mockResolvedValue([mockFile]);
  clientFileService.deleteFile.mockResolvedValue(true);
  render(<ClientFiles clientUuid={clientUuid} />);
  await waitFor(() => {
    expect(clientFileService.getClientFiles).toHaveBeenCalledTimes(1);
  });
  await screen.findByRole("button", { name: "Delete" });
  fireEvent.click(screen.getByRole("button", { name: "Delete" }));
  await waitFor(() => {
    expect(clientFileService.deleteFile).toHaveBeenCalledWith(mockFile.id);
  });
  await waitFor(() => {
    expect(clientFileService.getClientFiles).toHaveBeenCalledTimes(2);
    expect(clientFileService.getClientFiles).toHaveBeenLastCalledWith(clientUuid);
  });
  confirmSpy.mockRestore();
});
    test('shows alert when delete fails after confirm', async () => {
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
  const confirmSpy = jest.spyOn(window, "confirm").mockReturnValue(true);
  clientFileService.getClientFiles.mockResolvedValue([mockFile]);
  clientFileService.deleteFile.mockRejectedValue(new Error("fail"));
  render(<ClientFiles clientUuid={clientUuid} />);
  await screen.findByText("doc.pdf");
  fireEvent.click(screen.getByRole("button", { name: "Delete" }));
  await waitFor(() => {
    expect(alertSpy).toHaveBeenCalledWith("Delete error");
  });
  expect(consoleSpy).toHaveBeenCalled();
  alertSpy.mockRestore();
  consoleSpy.mockRestore();
  confirmSpy.mockRestore();
});

test("opens preview overlay with Close after successful preview fetch", async () => {
  const createObjectURLMock = jest.fn(() => "blob:http://localhost/mock-preview");
  const revokeObjectURLMock = jest.fn();
  URL.createObjectURL = createObjectURLMock;
  URL.revokeObjectURL = revokeObjectURLMock;
  global.fetch = jest.fn(() =>
    Promise.resolve({
      ok: true,
      blob: () => Promise.resolve(new Blob(["x"], { type: "image/png" })),
    })
  );
  const previewFile = { ...mockFile, contentType: "image/png" };
  clientFileService.getClientFiles.mockResolvedValue([previewFile]);
  render(<ClientFiles clientUuid={clientUuid} />);
  await screen.findByText("doc.pdf");
  fireEvent.click(screen.getByRole("button", { name: "Preview" }));
  await waitFor(() => {
    expect(global.fetch).toHaveBeenCalled();
  });
  expect(createObjectURLMock).toHaveBeenCalled();
  await screen.findByRole("button", { name: "Close" });
  delete URL.createObjectURL;
  delete URL.revokeObjectURL;
});
test('shows alert when preview fetch fails', async () => {
  const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
  const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
  global.fetch = jest.fn().mockRejectedValue(new Error("network"));
  clientFileService.getClientFiles.mockResolvedValue([mockFile]);
  render(<ClientFiles clientUuid={clientUuid} />);
  await screen.findByText("doc.pdf");
  fireEvent.click(screen.getByRole("button", { name: "Preview" }));
  await waitFor(() => {
    expect(alertSpy).toHaveBeenCalledWith("Preview failed");
  });
  expect(consoleSpy).toHaveBeenCalled();
  alertSpy.mockRestore();
  consoleSpy.mockRestore();
  global.fetch.mockRestore?.();
});

  test("shows alert when preview response is not ok", async () => {
    const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
    const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
    global.fetch = jest.fn().mockResolvedValue({ ok: false, status: 500 });
    clientFileService.getClientFiles.mockResolvedValue([mockFile]);
    render(<ClientFiles clientUuid={clientUuid} />);
    await screen.findByText("doc.pdf");
    fireEvent.click(screen.getByRole("button", { name: "Preview" }));
    await waitFor(() => {
      expect(alertSpy).toHaveBeenCalledWith("Preview failed");
    });
    expect(consoleSpy).toHaveBeenCalled();
    alertSpy.mockRestore();
    consoleSpy.mockRestore();
    global.fetch.mockRestore?.();
  });
});
