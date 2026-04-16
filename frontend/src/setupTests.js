import '@testing-library/jest-dom';

// Suppress noisy React test warning about async updates not wrapped in act.
// Keep other console.error messages visible.
const originalConsoleError = console.error;
let consoleErrorSpy;
beforeAll(() => {
  consoleErrorSpy = jest.spyOn(console, "error").mockImplementation((...args) => {
    const message = String(args[0] ?? "");
    if (message.includes("not wrapped in act")) {
      return;
    }
    originalConsoleError(...args);
  });
});

afterAll(() => {
  if (consoleErrorSpy) {
    consoleErrorSpy.mockRestore();
  }
});
