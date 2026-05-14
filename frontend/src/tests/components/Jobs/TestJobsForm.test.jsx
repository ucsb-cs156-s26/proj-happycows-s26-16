import { fireEvent, render, screen, waitFor } from "@testing-library/react";
import { MemoryRouter as Router } from "react-router";
import { vi } from "vitest";
import TestJobForm from "main/components/Jobs/TestJobForm";

describe("TestJobForm (mutation resistant) tests", () => {
  const renderForm = (submitAction = vi.fn()) => {
    render(
      <Router>
        <TestJobForm submitAction={submitAction} />
      </Router>
    );

    const sleepMs = screen.getByTestId("TestJobForm-sleepMs");
    const fail = screen.getByTestId("TestJobForm-fail");
    const submit = screen.getByTestId("TestJobForm-Submit-Button");
    return { sleepMs, fail, submit, submitAction };
  };

  it("applies default values (kills defaultValues {})", () => {
    const { sleepMs, fail } = renderForm();
    expect(sleepMs).toHaveValue(1000);
    expect(fail).not.toBeChecked();
  });

  it("validates sleepMs required + sets invalid styling (kills required string + isInvalid mutants)", async () => {
    const { sleepMs, submit, submitAction } = renderForm();

    fireEvent.change(sleepMs, { target: { value: "" } });
    fireEvent.click(submit);

    expect(
      await screen.findByText("sleepMs is required (0 is ok)")
    ).toBeInTheDocument();

    expect(submitAction).not.toHaveBeenCalled();
    expect(sleepMs).toHaveClass("is-invalid");
  });

  it("validates sleepMs >= 0 (kills min object + min message mutants)", async () => {
    const { sleepMs, submit, submitAction } = renderForm();

    // IMPORTANT: use a step-aligned negative value to reliably trigger RHF min validation in jsdom
    fireEvent.change(sleepMs, { target: { value: "-100" } });
    fireEvent.click(submit);

    expect(
      await screen.findByText("sleepMs must be positive")
    ).toBeInTheDocument();

    expect(submitAction).not.toHaveBeenCalled();
    expect(sleepMs).toHaveClass("is-invalid");
  });

  it("validates sleepMs <= 60000 (kills max object + max message mutants)", async () => {
    const { sleepMs, submit, submitAction } = renderForm();

    // step-aligned value above max
    fireEvent.change(sleepMs, { target: { value: "60100" } });
    fireEvent.click(submit);

    expect(
      await screen.findByText("sleepMs must be ≤ 60000")
    ).toBeInTheDocument();

    expect(submitAction).not.toHaveBeenCalled();
    expect(sleepMs).toHaveClass("is-invalid");
  });

  it("submits successfully when valid", async () => {
    const submitAction = vi.fn();
    const { sleepMs, submit } = renderForm(submitAction);

    fireEvent.change(sleepMs, { target: { value: "0" } });
    fireEvent.click(submit);

    await waitFor(() => expect(submitAction).toHaveBeenCalled());
  });
});