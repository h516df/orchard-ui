import React, {useState} from 'react';
import {ScrollView, View} from 'react-native';
import {
  OrchardProvider, OrchardLargeTitle, OrchardSection, OrchardSwitchRow, OrchardListRow,
  OrchardMetric, OrchardReadingSurface, useOrchard, type OrchardPalette,
} from '../src';

/** Mount inside the application's existing SafeArea/Navigation shell. */
export default function Gallery() {
  const [palette, setPalette] = useState<OrchardPalette>('blue');
  return <OrchardProvider palette={palette}><GalleryContent palette={palette} setPalette={setPalette} /></OrchardProvider>;
}
function GalleryContent({palette, setPalette}: {palette: OrchardPalette; setPalette: (v: OrchardPalette) => void}) {
  const {colors: c} = useOrchard();
  const [haptics, setHaptics] = useState(true);
  const palettes: Array<[OrchardPalette, string]> = [['blue', '清透蓝'], ['sage', '纸感绿'], ['peach', '暖杏色'], ['ink', '墨色紫']];
  return <View style={{flex: 1, backgroundColor: c.background}}><ScrollView contentContainerStyle={{padding: 20, gap: 24}}>
    <OrchardLargeTitle title="你的风格" subtitle="同一份语义变量，不同技术栈" />
    <OrchardSection title="主题配色">{palettes.map(([key, label]) =>
      <OrchardListRow key={key} title={label} value={palette === key ? '已选用' : undefined} onPress={() => setPalette(key)} />)}</OrchardSection>
    <OrchardSection title="偏好"><OrchardSwitchRow title="触感反馈" subtitle="示例只改变偏好，不调用振动器" value={haptics} onValueChange={setHaptics} /></OrchardSection>
    <OrchardMetric label="留给自己的时间" value="2 小时" detail="合成示例数据" />
    <OrchardReadingSurface title="雨停之后" text={'窗外的树叶被洗得很亮。\n\n这是验证排版的原创合成文字。'} />
  </ScrollView></View>;
}
