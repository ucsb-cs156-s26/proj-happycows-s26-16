import { render, screen } from "@testing-library/react";
import { MemoryRouter as Router } from "react-router";
import TestJobForm from "main/components/Jobs/TestJobForm";

describe("TestJobForm tests", () => {
  it("renders basic form content", () => {
    render(
      <Router>
        <TestJobForm />
      </Router>
    );

    // Check static text (what actually exists in your component)
    expect(screen.getByText(/Fail\?/i)).toBeInTheDocument();
    expect(screen.getByText(/Sleep \(milliseconds\)/i)).toBeInTheDocument();
    expect(screen.getByText(/Submit/i)).toBeInTheDocument();
  });
});
