import React, {createContext, useContext, useEffect, useState, type ReactNode} from 'react';
import {
  AccessibilityInfo, ActivityIndicator, Alert, BackHandler, KeyboardAvoidingView, Modal,
  I18nManager, Platform, Pressable, ScrollView, StyleSheet, Switch, Text, TextInput, View, useColorScheme,
  type StyleProp, type ViewStyle, type TextInputProps,
} from 'react-native';
import {orchardSchemes, orchardTokens, type OrchardPalette, type OrchardColors} from './tokens';
export {orchardSchemes, orchardTokens};
export type {OrchardPalette, OrchardColors};

type OrchardContextValue = {colors: OrchardColors; reducedMotion: boolean; dark: boolean};
const OrchardContext = createContext<OrchardContextValue>({colors: orchardSchemes.blue.light, reducedMotion: false, dark: false});
export const useOrchard = () => useContext(OrchardContext);

export function OrchardProvider({children, palette = 'blue', mode = 'system', reduceMotion}: {
  children: ReactNode; palette?: OrchardPalette; mode?: 'light' | 'dark' | 'system'; reduceMotion?: boolean;
}) {
  const system = useColorScheme();
  const dark = mode === 'system' ? system === 'dark' : mode === 'dark';
  const [systemReduced, setSystemReduced] = useState(false);
  useEffect(() => {
    let alive = true;
    AccessibilityInfo.isReduceMotionEnabled().then(value => {if (alive) setSystemReduced(value);}).catch(() => {});
    const listener = AccessibilityInfo.addEventListener('reduceMotionChanged', setSystemReduced);
    return () => {alive = false; listener.remove();};
  }, []);
  return <OrchardContext.Provider value={{colors: orchardSchemes[palette][dark ? 'dark' : 'light'], dark, reducedMotion: systemReduced || reduceMotion === true}}>{children}</OrchardContext.Provider>;
}

type BoxProps = {children: ReactNode; style?: StyleProp<ViewStyle>};
export function OrchardCard({children, style}: BoxProps) {
  const {colors: c} = useOrchard();
  return <View style={[styles.card, {backgroundColor: c.surface}, style]}>{children}</View>;
}
export function OrchardGroup({children, style}: BoxProps) {
  const {colors: c} = useOrchard();
  return <View style={[styles.group, {backgroundColor: c.surface}, style]}>{children}</View>;
}
export function OrchardSection({title, footnote, children}: BoxProps & {title: string; footnote?: string}) {
  const {colors: c} = useOrchard();
  return <View style={styles.section}>
    <Text accessibilityRole="header" style={[styles.caption, {color: c.textSecondary, paddingHorizontal: 12}]}>{title}</Text>
    <OrchardGroup>{children}</OrchardGroup>
    {footnote ? <Text style={[styles.caption, {color: c.textSecondary, paddingHorizontal: 12}]}>{footnote}</Text> : null}
  </View>;
}
export type OrchardButtonKind = 'filled' | 'soft' | 'plain' | 'destructive';
export function OrchardButton({label, onPress, kind = 'filled', disabled = false, loading = false, loadingLabel = '正在处理', style}: {
  label: string; onPress: () => void; kind?: OrchardButtonKind; disabled?: boolean; loading?: boolean; loadingLabel?: string; style?: StyleProp<ViewStyle>;
}) {
  const {colors: c} = useOrchard();
  const inactive = disabled || loading;
  const background = kind === 'soft' ? c.primaryContainer : kind === 'plain' ? 'transparent' : kind === 'destructive' ? c.danger : c.primary;
  const foreground = kind === 'soft' ? c.onPrimaryContainer : kind === 'plain' ? c.primary : kind === 'destructive' ? c.onDanger : c.onPrimary;
  return <Pressable accessibilityRole="button" accessibilityState={{disabled: inactive, busy: loading}} disabled={inactive} onPress={onPress}
    style={({pressed}) => [styles.button, {backgroundColor: background, opacity: inactive ? 0.5 : pressed ? 0.78 : 1}, style]}>
    <Text style={[styles.buttonText, {color: foreground}]}>{loading ? `${label} · ${loadingLabel}` : label}</Text>
  </Pressable>;
}
export function OrchardListRow({title, subtitle, value, onPress, destructive = false}: {
  title: string; subtitle?: string; value?: string; onPress?: () => void; destructive?: boolean;
}) {
  const {colors: c} = useOrchard();
  const content = <>
    <View style={styles.rowContent}>
      <Text style={[styles.body, {color: destructive ? c.danger : c.text}]}>{title}</Text>
      {subtitle ? <Text style={[styles.caption, {color: c.textSecondary}]}>{subtitle}</Text> : null}
      {value ? <Text style={[styles.label, {color: c.primary}]}>{value}</Text> : null}
    </View>
    {onPress ? <Text importantForAccessibility="no" style={{fontSize: 24, color: c.textSecondary, writingDirection: 'auto'}}>{I18nManager.isRTL ? '‹' : '›'}</Text> : null}
  </>;
  return onPress
    ? <Pressable accessibilityRole="button" onPress={onPress} style={({pressed}) => [styles.row, {opacity: pressed ? 0.65 : 1}]}>{content}</Pressable>
    : <View style={styles.row}>{content}</View>;
}
export function OrchardDivider() {
  const {colors: c} = useOrchard();
  return <View style={{height: StyleSheet.hairlineWidth, marginHorizontal: 16, backgroundColor: c.divider}} />;
}
export function OrchardSwitchRow({title, subtitle, value, onValueChange, disabled = false}: {
  title: string; subtitle?: string; value: boolean; onValueChange: (value: boolean) => void; disabled?: boolean;
}) {
  const {colors: c} = useOrchard();
  return <View style={styles.row}>
    <View style={styles.rowContent}><Text style={[styles.body, {color: c.text}]}>{title}</Text>{subtitle ? <Text style={[styles.caption, {color: c.textSecondary}]}>{subtitle}</Text> : null}</View>
    <Switch accessibilityLabel={title} value={value} onValueChange={onValueChange} disabled={disabled} trackColor={{false: c.outline, true: c.primary}} />
  </View>;
}
export function OrchardTextField({label, hint, error, style, ...props}: TextInputProps & {label: string; hint?: string; error?: string}) {
  const {colors: c} = useOrchard();
  return <View style={{gap: 6}}>
    <Text style={[styles.label, {color: c.text}]}>{label}</Text>
    <TextInput {...props} accessibilityLabel={label} placeholderTextColor={c.textSecondary} selectionColor={c.primary}
      style={[styles.field, {color: c.text, backgroundColor: c.surface, borderColor: error ? c.danger : c.outline}, props.multiline ? {minHeight: 144, textAlignVertical: 'top'} : null, style]} />
    {error || hint ? <Text accessibilityLiveRegion={error ? 'polite' : 'none'} style={[styles.caption, {color: error ? c.danger : c.textSecondary}]}>{error || hint}</Text> : null}
  </View>;
}
export function OrchardSearchField({value, onChangeText, label = '搜索'}: {value: string; onChangeText: (text: string) => void; label?: string}) {
  return <View style={{gap: 4}}><OrchardTextField label={label} value={value} onChangeText={onChangeText} returnKeyType="search" />
    {value ? <OrchardButton label="清除搜索" kind="plain" onPress={() => onChangeText('')} /> : null}</View>;
}
export function OrchardSegments<T extends string>({options, selected, label, onSelect}: {options: readonly T[]; selected: T; label: (value: T) => string; onSelect: (value: T) => void}) {
  const {colors: c} = useOrchard();
  return <View accessibilityRole="radiogroup" style={styles.wrap}>
    {options.map(option => <Pressable key={option} accessibilityRole="radio" accessibilityState={{checked: selected === option}} onPress={() => onSelect(option)}
      style={[styles.segment, {backgroundColor: selected === option ? c.primaryContainer : c.surfaceMuted}]}>
      <Text style={[styles.label, {color: selected === option ? c.onPrimaryContainer : c.textSecondary}]}>{label(option)}</Text>
    </Pressable>)}
  </View>;
}
export function OrchardLargeTitle({title, subtitle}: {title: string; subtitle?: string}) {
  const {colors: c} = useOrchard();
  return <View style={{gap: 8, paddingVertical: 12}}><Text accessibilityRole="header" style={[styles.largeTitle, {color: c.text}]}>{title}</Text>
    {subtitle ? <Text style={[styles.body, {color: c.textSecondary}]}>{subtitle}</Text> : null}</View>;
}
export function OrchardBadge({label}: {label: string}) {
  const {colors: c} = useOrchard();
  return <View style={{alignSelf: 'flex-start', backgroundColor: c.primaryContainer, paddingHorizontal: 10, paddingVertical: 5, borderRadius: 999}}>
    <Text style={[styles.caption, {color: c.onPrimaryContainer}]}>{label}</Text></View>;
}
export function OrchardBanner({title, message, error = false, action}: {title: string; message: string; error?: boolean; action?: ReactNode}) {
  const {colors: c} = useOrchard();
  const textColor = error ? c.onDangerContainer : c.onPrimaryContainer;
  return <View accessibilityLiveRegion="polite" style={[styles.banner, {backgroundColor: error ? c.dangerContainer : c.primaryContainer}]}>
    <Text style={[styles.title, {color: textColor}]}>{title}</Text><Text style={[styles.label, {color: textColor}]}>{message}</Text>{action}</View>;
}
export function OrchardMetric({label, value, detail}: {label: string; value: string; detail?: string}) {
  const {colors: c} = useOrchard();
  return <OrchardCard><Text style={[styles.caption, {color: c.textSecondary}]}>{label}</Text>
    <Text style={[styles.largeTitle, {color: c.text}]}>{value}</Text>{detail ? <Text style={[styles.caption, {color: c.textSecondary}]}>{detail}</Text> : null}</OrchardCard>;
}
export function OrchardEmptyState({title, message, action}: {title: string; message: string; action?: ReactNode}) {
  const {colors: c} = useOrchard();
  return <View style={{paddingVertical: 32, gap: 12, alignItems: 'center'}}><Text style={[styles.title, {color: c.text, textAlign: 'center'}]}>{title}</Text>
    <Text style={[styles.label, {color: c.textSecondary, textAlign: 'center'}]}>{message}</Text>{action}</View>;
}
export function OrchardLoadingState({label = '正在加载'}: {label?: string}) {
  const {colors: c} = useOrchard();
  return <View accessibilityLiveRegion="polite" style={{padding: 32, gap: 16, alignItems: 'center'}}><ActivityIndicator color={c.primary} /><Text style={[styles.label, {color: c.text}]}>{label}</Text></View>;
}
export function OrchardErrorState({message, onRetry}: {message: string; onRetry: () => void}) {
  return <OrchardBanner title="暂时没能完成" message={message} error action={<OrchardButton label="重试" kind="soft" onPress={onRetry} />} />;
}
export function OrchardReadingSurface({title, text, textSize = 19}: {title: string; text: string; textSize?: number}) {
  const {colors: c} = useOrchard();
  const size = Math.max(14, Math.min(36, textSize));
  return <View style={{width: '100%', maxWidth: orchardTokens.size.readingMax, alignSelf: 'center', padding: 24, gap: 24}}>
    <Text accessibilityRole="header" style={[styles.title, {color: c.text}]}>{title}</Text><Text selectable style={{color: c.text, fontSize: size, lineHeight: size * 1.7}}>{text}</Text></View>;
}
export function OrchardSheet({visible, title, onClose, canDismiss = true, onDismissBlocked, children}: {
  visible: boolean; title: string; onClose: () => void; canDismiss?: boolean; onDismissBlocked?: () => void; children: ReactNode;
}) {
  const {colors: c, reducedMotion} = useOrchard();
  const close = () => {if (canDismiss) onClose(); else onDismissBlocked?.();};
  return <Modal visible={visible} transparent animationType={reducedMotion ? 'none' : 'slide'} onRequestClose={close}>
    <KeyboardAvoidingView behavior={Platform.OS === 'ios' ? 'padding' : 'height'} style={styles.scrim}>
      <Pressable accessible={false} onPress={close} style={StyleSheet.absoluteFill} />
      <View accessibilityViewIsModal style={[styles.sheet, {backgroundColor: c.surface}]}>
        <ScrollView contentContainerStyle={{padding: 24, gap: 16}} keyboardShouldPersistTaps="handled">
          <Text accessibilityRole="header" style={[styles.title, {color: c.text}]}>{title}</Text>{children}
          <OrchardButton label="关闭" onPress={close} kind="plain" />
        </ScrollView>
      </View>
    </KeyboardAvoidingView>
  </Modal>;
}
export function orchardConfirm({title, message, confirmLabel, cancelLabel = '取消', destructive = false}: {
  title: string; message: string; confirmLabel: string; cancelLabel?: string; destructive?: boolean;
}): Promise<boolean> {
  return new Promise(resolve => Alert.alert(title, message, [
    {text: cancelLabel, style: 'cancel', onPress: () => resolve(false)},
    {text: confirmLabel, style: destructive ? 'destructive' : 'default', onPress: () => resolve(true)},
  ], {cancelable: true, onDismiss: () => resolve(false)}));
}

/** Owns no storage. The caller keeps drafts and marks a successful save clean. */
export function OrchardEditorScreen({title = '随手记录', value, onChangeText, dirty, onSave, onClose}: {
  title?: string; value: string; onChangeText: (text: string) => void; dirty: boolean; onSave: () => Promise<void>; onClose: () => void;
}) {
  const {colors: c} = useOrchard();
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState<string>();
  const savingRef = React.useRef(false);
  const confirmingRef = React.useRef(false);
  const mountedRef = React.useRef(true);
  useEffect(() => {mountedRef.current = true; return () => {mountedRef.current = false;};}, []);
  const close = React.useCallback(async () => {
    if (savingRef.current || confirmingRef.current) return;
    confirmingRef.current = true;
    try {
      const approved = !dirty || await orchardConfirm({title: '放弃修改？', message: '本次修改尚未保存，确定放弃吗？', confirmLabel: '放弃修改', cancelLabel: '继续编辑', destructive: true});
      if (mountedRef.current && approved) onClose();
    } finally {confirmingRef.current = false;}
  }, [dirty, onClose]);
  useEffect(() => {
    const sub = BackHandler.addEventListener('hardwareBackPress', () => {void close(); return true;});
    return () => sub.remove();
  }, [close]);
  const save = async () => {
    if (savingRef.current || !dirty) return;
    savingRef.current = true; setSaving(true); setError(undefined);
    try {await onSave();}
    catch {if (mountedRef.current) setError('还没有保存，请重试。输入已保留。');}
    finally {savingRef.current = false; if (mountedRef.current) setSaving(false);}
  };
  return <KeyboardAvoidingView style={{flex: 1, backgroundColor: c.background}} behavior={Platform.OS === 'ios' ? 'padding' : 'height'}>
    <ScrollView keyboardShouldPersistTaps="handled" contentContainerStyle={{padding: 20, gap: 16}}>
      <OrchardButton label="返回" kind="plain" onPress={() => {void close();}} disabled={saving} />
      <OrchardLargeTitle title={title} /><OrchardTextField label="正文" value={value} onChangeText={onChangeText} multiline editable={!saving} />
      {error ? <OrchardBanner title="还没有保存" message={error} error /> : null}
      <OrchardButton label="保存" onPress={() => {void save();}} disabled={!dirty} loading={saving} />
    </ScrollView>
  </KeyboardAvoidingView>;
}

const styles = StyleSheet.create({
  card: {borderRadius: orchardTokens.radius.card, padding: 24, gap: 12},
  group: {borderRadius: orchardTokens.radius.card, overflow: 'hidden'},
  section: {gap: 8},
  button: {minHeight: orchardTokens.size.touch, minWidth: 48, borderRadius: orchardTokens.radius.control, paddingHorizontal: 20, paddingVertical: 14, alignItems: 'center', justifyContent: 'center'},
  buttonText: {fontSize: orchardTokens.type.label, fontWeight: '600', textAlign: 'center'},
  row: {flexDirection: 'row', alignItems: 'center', gap: 12, minHeight: 56, padding: 16},
  rowContent: {flex: 1, gap: 4, flexShrink: 1},
  body: {fontSize: orchardTokens.type.body}, label: {fontSize: orchardTokens.type.label}, caption: {fontSize: orchardTokens.type.caption},
  title: {fontSize: orchardTokens.type.title, fontWeight: '600'},
  largeTitle: {fontSize: orchardTokens.type.largeTitle, fontWeight: '700'},
  field: {borderWidth: 1, borderRadius: orchardTokens.radius.control, minHeight: 56, padding: 16, fontSize: orchardTokens.type.body},
  wrap: {flexDirection: 'row', flexWrap: 'wrap', gap: 8},
  segment: {minHeight: 48, paddingHorizontal: 16, paddingVertical: 12, borderRadius: 14, alignItems: 'center', justifyContent: 'center'},
  banner: {borderRadius: 16, padding: 16, gap: 8},
  scrim: {flex: 1, justifyContent: 'flex-end', backgroundColor: '#00000066'},
  sheet: {maxHeight: '90%', borderTopLeftRadius: 28, borderTopRightRadius: 28},
});
