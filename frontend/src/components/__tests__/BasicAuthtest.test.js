import React from "react";
import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import "@testing-library/jest-dom";
import { Login } from "../BasicAuth";

describe ("BasicAuth - handleLogin", () => {
    let setActiveTabMock;
    beforeEach(() => {
        jest.clearAllMocks();
        localStorage.clear();
        setActiveTabMock = jest.fn();
        global.fetch = jest.fn();
    });
    
    afterEach(() => {
        localStorage.clear();
        jest.restoreAllMocks();
    });

    test("stores auth data and switches tab on successful login ", async () => {
        global.fetch.mockResolvedValue({
            ok: true,
            status: 200,
        
    });

    render(<Login setActiveTab={setActiveTabMock} />);

    fireEvent.change(screen.getByPlaceholderText("Username"), {
        target: { value: "testuser" },
    });
    fireEvent.change(screen.getByPlaceholderText("Password"), {
        target: { value: "secret" },
    });
    fireEvent.click(screen.getByRole("button", { name: "Login"}));

    await waitFor(() => {
        expect(global.fetch).toHaveBeenCalledTimes(1);
    });

    expect(localStorage.getItem("loggedIn")).toBe("true");
    expect(localStorage.getItem("authHeader")).toBe("Basic dGVzdHVzZXI6c2VjcmV0");
    expect(setActiveTabMock).toHaveBeenCalledWith("actual");
});

test("Show alert for invalid credentials (401)" , async () => {
    const alertSpy = jest.spyOn(window, "alert").mockImplementation(() => {});
    global.fetch.mockResolvedValue({
        ok: false,
        status: 401,
    });
    render(<Login setActiveTab={setActiveTabMock} />);
    fireEvent.click(screen.getByRole("button", { name: "Login" }));
    
    await waitFor (() => {
        expect(alertSpy).toHaveBeenCalledWith("Неверный логин или пароль");
    });
    expect(setActiveTabMock).not.toHaveBeenCalled();
});
test("shows network error alert when request fails", async () => {
    const alertSpy = jest.spyOn(window,"alert").mockImplementation(() => {});
    const consoleSpy = jest.spyOn(console, "error").mockImplementation(() => {});
    global.fetch.mockRejectedValue(new Error("Network error"));
    render(<Login setActiveTab={setActiveTabMock} />);
    fireEvent.click(screen.getByRole("button", { name: "Login" }));
    await waitFor(() => {
        expect(alertSpy).toHaveBeenCalledWith("Ошибка сети или сервера");
    });
    expect(consoleSpy).toHaveBeenCalled();
    expect(setActiveTabMock).not.toHaveBeenCalled();
});
});


