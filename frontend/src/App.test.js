import React from "react";
import { render } from "@testing-library/react";
import App from "./App";

jest.mock(
  "react-router-dom",
  () => ({
    BrowserRouter: ({ children }) => <div>{children}</div>,
    Routes: ({ children }) => <div>{children}</div>,
    Route: () => null,
  }),
  { virtual: true }
);

test("renders app without crashing", () => {
  render(<App />);
});
