// Wiring tests only: this mock does not execute UIKit or Android RuntimeShader.
jest.mock("@sbaiahmed1/react-native-blur", () => {
  const React = require("react");
  const { View } = require("react-native");
  return {
    LiquidGlassView: (props: unknown) => React.createElement(View, props),
  };
});
