import React, { useState } from "react";
import { Text, View } from "react-native";
import {
  OrchardAppPage,
  OrchardGroup,
  OrchardNoteRow,
  OrchardSearchDock,
  useOrchard,
} from "../src";

/** Mount under OrchardProvider. Host owns routes, safe-area insets, icons and persistence. */
export function NotesReference({
  onMore,
  onCompose,
  onOpen,
  insets,
}: {
  onMore: () => void;
  onCompose: () => void;
  onOpen: (title: string) => void;
  insets: { top: number; bottom: number };
}) {
  const { colors: c } = useOrchard();
  const [query, setQuery] = useState("");
  const notes = ["周末的小计划", "灵感随记", "想读的书"];
  return (
    <OrchardAppPage
      title="备忘录"
      subtitle={`${notes.length} 则备忘录`}
      onMore={onMore}
      insets={insets}
      bottomBar={
        <OrchardSearchDock
          value={query}
          onChangeText={setQuery}
          onCompose={onCompose}
          bottomInset={insets.bottom}
        />
      }
    >
      <View style={{ gap: 12 }}>
        <Text
          accessibilityRole="header"
          style={{ fontSize: 22, fontWeight: "600", color: c.text }}
        >
          今天
        </Text>
        <OrchardGroup>
          {notes
            .filter((title) => title.includes(query))
            .map((title) => (
              <OrchardNoteRow
                key={title}
                title={title}
                preview="留一点时间给自己。"
                time="09:41"
                onPress={() => onOpen(title)}
              />
            ))}
        </OrchardGroup>
      </View>
    </OrchardAppPage>
  );
}
