import React from "react";
import { BackHandler, TextInput } from "react-native";
import { act, create, type ReactTestRenderer } from "react-test-renderer";
import {
  OrchardAppPage,
  OrchardSearchDock,
  OrchardNoteRow,
  OrchardMiniPlayer,
  OrchardButton,
  OrchardConfirmDialog,
  OrchardEditorScreen,
  OrchardProvider,
  OrchardSegments,
  OrchardSwitchRow,
  orchardSchemes,
  useOrchard,
  useOrchardConfirm,
} from "../src";

(
  globalThis as { IS_REACT_ACT_ENVIRONMENT?: boolean }
).IS_REACT_ACT_ENVIRONMENT = true;

async function render(element: React.ReactElement): Promise<ReactTestRenderer> {
  let tree!: ReactTestRenderer;
  await act(async () => {
    tree = create(<OrchardProvider>{element}</OrchardProvider>);
  });
  return tree;
}
const cleanup: ReactTestRenderer[] = [];
afterEach(async () => {
  await act(async () => {
    cleanup.splice(0).forEach((tree) => tree.unmount());
  });
  jest.restoreAllMocks();
});

test("unmounting a dialog provider cancels its pending confirmation", async () => {
  let confirm!: ReturnType<typeof useOrchardConfirm>;
  function Probe() {
    confirm = useOrchardConfirm();
    return null;
  }
  const tree = await render(<Probe />);
  let result!: Promise<boolean>;
  await act(async () => {
    result = confirm({
      title: "离开？",
      message: "合成草稿",
      confirmLabel: "放弃",
    });
  });
  expect(tree.root.findByType(OrchardConfirmDialog).props.title).toBe("离开？");
  await act(async () => {
    tree.unmount();
  });
  await expect(result).resolves.toBe(false);
});

test("busy button has native disabled semantics and cannot submit twice", async () => {
  const onPress = jest.fn();
  const tree = await render(
    <OrchardButton label="保存" onPress={onPress} loading />,
  );
  cleanup.push(tree);
  const button = tree.root.findAll(
    (node) =>
      node.props?.accessibilityRole === "button" &&
      typeof node.props.onPress === "function",
    { deep: false },
  )[0];
  expect(button.props.disabled).toBe(true);
  expect(button.props.accessibilityState).toEqual({
    disabled: true,
    busy: true,
  });
  await act(async () => {
    tree.update(<OrchardButton label="保存" onPress={onPress} />);
  });
  expect(
    tree.root.findAll(
      (node) =>
        node.props?.accessibilityRole === "button" &&
        typeof node.props.onPress === "function",
      { deep: false },
    )[0].props.disabled,
  ).toBe(false);
  await act(async () => {
    tree.root
      .findAll(
        (node) =>
          node.props?.accessibilityRole === "button" &&
          typeof node.props.onPress === "function",
        { deep: false },
      )[0]
      .props.onPress();
  });
  expect(onPress).toHaveBeenCalledTimes(1);
});
test("segments expose radio state and switch delegates exactly one change", async () => {
  const onSelect = jest.fn();
  const onValueChange = jest.fn();
  const tree = await render(
    <>
      <OrchardSegments
        options={["全部", "收藏"]}
        selected="全部"
        label={(v) => v}
        onSelect={onSelect}
      />
      <OrchardSwitchRow
        title="反馈"
        value={false}
        onValueChange={onValueChange}
      />
    </>,
  );
  cleanup.push(tree);
  const radios = tree.root.findAll(
    (node) =>
      node.props?.accessibilityRole === "radio" &&
      typeof node.props.onPress === "function",
    { deep: false },
  );
  expect(radios[0].props.accessibilityState.checked).toBe(true);
  await act(async () => {
    radios[1].props.onPress();
    tree.root
      .findAll(
        (node) =>
          node.props?.accessibilityRole === "switch" &&
          typeof node.props.onPress === "function",
        { deep: false },
      )[0]
      .props.onPress();
  });
  expect(onSelect).toHaveBeenCalledWith("收藏");
  expect(onValueChange).toHaveBeenCalledTimes(1);
});
test("provider selects the shared dark palette", async () => {
  let primary: string | undefined;
  function Probe() {
    primary = useOrchard().colors.primary;
    return null;
  }
  const tree = await render(
    <OrchardProvider palette="sage" mode="dark">
      <Probe />
    </OrchardProvider>,
  );
  cleanup.push(tree);
  expect(primary).toBe(orchardSchemes.sage.dark.primary);
});
test("failed editor save keeps controlled text and a second save can succeed", async () => {
  const onSave = jest
    .fn()
    .mockRejectedValueOnce(new Error("synthetic"))
    .mockResolvedValue(undefined);
  const tree = await render(
    <OrchardEditorScreen
      value="草稿不会丢失"
      onChangeText={() => {}}
      dirty
      onSave={onSave}
      onClose={() => {}}
    />,
  );
  cleanup.push(tree);
  const saveButton = () =>
    tree.root
      .findAllByType(OrchardButton)
      .find((b) => b.props.label === "保存")!;
  await act(async () => {
    saveButton().props.onPress();
  });
  expect(tree.root.findByType(TextInput).props.value).toBe("草稿不会丢失");
  expect(JSON.stringify(tree.toJSON())).toContain("还没有保存");
  await act(async () => {
    saveButton().props.onPress();
  });
  expect(onSave).toHaveBeenCalledTimes(2);
});
test("android back asks before discarding and cancel keeps draft", async () => {
  const listeners: Array<() => boolean> = [];
  jest
    .spyOn(BackHandler, "addEventListener")
    .mockImplementation((_event, listener) => {
      listeners.push(listener as () => boolean);
      return { remove: jest.fn() };
    });
  const onClose = jest.fn();
  const tree = await render(
    <OrchardEditorScreen
      value="草稿"
      onChangeText={() => {}}
      dirty
      onSave={async () => {}}
      onClose={onClose}
    />,
  );
  cleanup.push(tree);
  await act(async () => {
    expect(listeners[0]()).toBe(true);
  });
  expect(tree.root.findByType(OrchardConfirmDialog).props.title).toBe(
    "放弃修改？",
  );
  await act(async () => {
    tree.root.findByType(OrchardConfirmDialog).props.onDismiss();
  });
  expect(onClose).not.toHaveBeenCalled();
  await act(async () => {
    listeners[0]();
  });
  await act(async () => {
    tree.root.findByType(OrchardConfirmDialog).props.onConfirm();
  });
  expect(onClose).toHaveBeenCalledTimes(1);
});

test("app reference patterns delegate search, opening, compose and playback to host", async () => {
  const onQuery = jest.fn(),
    onOpen = jest.fn(),
    onCompose = jest.fn(),
    onPlay = jest.fn(),
    onNext = jest.fn();
  const tree = await render(
    <OrchardAppPage
      title="备忘录"
      onMore={() => {}}
      bottomBar={
        <OrchardSearchDock
          value=""
          onChangeText={onQuery}
          onCompose={onCompose}
        />
      }
    >
      <OrchardNoteRow
        title="周末的小计划"
        time="09:41"
        preview="散步"
        onPress={onOpen}
      />
      <OrchardMiniPlayer
        title="微风"
        playing={false}
        onPlayPause={onPlay}
        onNext={onNext}
      />
    </OrchardAppPage>,
  );
  cleanup.push(tree);
  await act(async () => {
    tree.root.findByType(TextInput).props.onChangeText("散步");
    tree.root.findByType(OrchardNoteRow).props.onPress();
    for (const label of ["新建备忘录", "播放", "下一首"]) {
      tree.root
        .findAll(
          (node) =>
            node.props?.accessibilityLabel === label &&
            typeof node.props.onPress === "function",
          { deep: false },
        )[0]
        .props.onPress();
    }
  });
  expect(onQuery).toHaveBeenCalledWith("散步");
  for (const action of [onOpen, onCompose, onPlay, onNext])
    expect(action).toHaveBeenCalledTimes(1);
});
