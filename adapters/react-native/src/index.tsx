import React, {createContext, useContext, useEffect, useState, type ReactNode} from 'react';
import {
  AccessibilityInfo, ActivityIndicator, Animated, BackHandler, KeyboardAvoidingView, Modal,
  I18nManager, Platform, Pressable, ScrollView, StyleSheet, Text, TextInput, View, useColorScheme, useWindowDimensions,
  type StyleProp, type ViewStyle, type TextInputProps,
} from 'react-native';
import {orchardSchemes, orchardTokens, type OrchardPalette, type OrchardColors} from './tokens';
export {orchardSchemes, orchardTokens};
export type {OrchardPalette, OrchardColors};

export type OrchardConfirmOptions = {title: string; message: string; confirmLabel: string; cancelLabel?: string; destructive?: boolean};
type Confirm = (options: OrchardConfirmOptions) => Promise<boolean>;
const missingConfirm: Confirm = async () => {throw new Error('Wrap the app in OrchardProvider before requesting a dialog.');};
type OrchardContextValue = {colors: OrchardColors; reducedMotion: boolean; dark: boolean; confirm: Confirm};
const OrchardContext = createContext<OrchardContextValue>({colors: orchardSchemes.blue.light, reducedMotion: false, dark: false, confirm: missingConfirm});
export const useOrchard = () => useContext(OrchardContext);
export const useOrchardConfirm = (): Confirm => useOrchard().confirm;

export function OrchardProvider({children, palette = 'blue', mode = 'system', reduceMotion}: {
  children: ReactNode; palette?: OrchardPalette; mode?: 'light' | 'dark' | 'system'; reduceMotion?: boolean;
}) {
  const [dialog, setDialog] = useState<OrchardConfirmOptions>();
  const pending = React.useRef<((value: boolean) => void) | undefined>(undefined);
  const confirm = React.useCallback<Confirm>(options => new Promise(resolve => {pending.current?.(false); pending.current = resolve; setDialog(options);}), []);
  const settle = (value: boolean) => {const resolve = pending.current; pending.current = undefined; setDialog(undefined); resolve?.(value);};
  useEffect(() => () => {pending.current?.(false); pending.current = undefined;}, []);
  const system = useColorScheme();
  const dark = mode === 'system' ? system === 'dark' : mode === 'dark';
  const [systemReduced, setSystemReduced] = useState(false);
  useEffect(() => {
    let alive = true;
    AccessibilityInfo.isReduceMotionEnabled().then(value => {if (alive) setSystemReduced(value);}).catch(() => {});
    const listener = AccessibilityInfo.addEventListener('reduceMotionChanged', setSystemReduced);
    return () => {alive = false; listener.remove();};
  }, []);
  return <OrchardContext.Provider value={{colors: orchardSchemes[palette][dark ? 'dark' : 'light'], dark, reducedMotion: systemReduced || reduceMotion === true, confirm}}>{children}{dialog ? <OrchardConfirmDialog {...dialog} onConfirm={() => settle(true)} onDismiss={() => settle(false)} /> : null}</OrchardContext.Provider>;
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
  const background = kind === 'soft' ? c.primaryContainer : kind === 'plain' ? 'transparent' : kind === 'destructive' ? c.danger : c.buttonFill;
  const foreground = kind === 'soft' ? c.onPrimaryContainer : kind === 'plain' ? c.primary : kind === 'destructive' ? c.onDanger : c.buttonText;
  return <Pressable accessibilityRole="button" accessibilityState={{disabled: inactive, busy: loading}} disabled={inactive} onPress={onPress}
    style={({pressed}) => [styles.button, {backgroundColor: background, opacity: inactive ? 0.5 : pressed ? 0.78 : 1}, style]}>
    <Text style={[styles.buttonText, {color: foreground}]}>{loading ? `${label} · ${loadingLabel}` : label}</Text>
  </Pressable>;
}
export function OrchardListRow({title, subtitle, value, onPress, destructive = false, leading}: {
 title: string; subtitle?: string; value?: string; onPress?: () => void; destructive?: boolean; leading?: ReactNode;
}) {
 const {colors:c}=useOrchard(); const large=useWindowDimensions().fontScale>1.5;
 const content=<>{leading}<View style={styles.rowContent}><Text style={[styles.body,{color:destructive?c.danger:c.text}]}>{title}</Text>
  {subtitle?<Text style={[styles.caption,{color:c.textSecondary}]}>{subtitle}</Text>:null}{large&&value?<Text style={[styles.label,{color:c.textSecondary}]}>{value}</Text>:null}</View>
  {!large&&value?<Text style={[styles.body,{color:c.textSecondary,maxWidth:'40%',textAlign:'right'}]}>{value}</Text>:null}
  {onPress?<Text importantForAccessibility="no" style={{fontSize:22,color:c.textSecondary,opacity:.6}}>{I18nManager.isRTL?'‹':'›'}</Text>:null}</>;
 return onPress?<Pressable accessibilityRole="button" onPress={onPress} style={({pressed})=>[styles.row,{opacity:pressed ? .55 : 1}]}>{content}</Pressable>:<View style={styles.row}>{content}</View>;
}

export function OrchardDivider() {
  const {colors: c} = useOrchard();
  return <View style={{height: StyleSheet.hairlineWidth, marginHorizontal: 16, backgroundColor: c.divider}} />;
}
export function OrchardSwitchRow({title,subtitle,value,onValueChange,disabled=false}: {
 title:string;subtitle?:string;value:boolean;onValueChange:(value:boolean)=>void;disabled?:boolean;
}) {
 const {colors:c,reducedMotion}=useOrchard();const offset=React.useRef(new Animated.Value(value?20:0)).current;
 useEffect(()=>{if(reducedMotion){offset.setValue(value?20:0);return;}const anim=Animated.spring(offset,{toValue:value?20:0,stiffness:550,damping:32,mass:1,useNativeDriver:true});anim.start();return()=>anim.stop();},[offset,value,reducedMotion]);
 return <Pressable accessibilityRole="switch" accessibilityLabel={title} accessibilityState={{checked:value,disabled}} disabled={disabled} onPress={()=>onValueChange(!value)} style={[styles.row,{opacity:disabled ? .4 : 1}]}>
  <View style={styles.rowContent}><Text style={[styles.body,{color:c.text}]}>{title}</Text>{subtitle?<Text style={[styles.caption,{color:c.textSecondary}]}>{subtitle}</Text>:null}</View>
  <View style={{width:51,height:31,borderRadius:16,backgroundColor:value?c.toggleOn:c.toggleOff}} pointerEvents="none">
   <Animated.View style={{position:'absolute',left:2,top:2,width:27,height:27,borderRadius:14,backgroundColor:'#FFF',shadowColor:'#000',shadowOpacity:.15,shadowRadius:2,shadowOffset:{width:0,height:1},elevation:2,transform:[{translateX:offset}]}} />
  </View></Pressable>;
}

export function OrchardTextField({label,hint,error,style,...props}: TextInputProps & {label:string;hint?:string;error?:string}) {
 const {colors:c}=useOrchard();const stacked=props.multiline||useWindowDimensions().fontScale>1.5;
 return <View style={{gap:6}}><View style={{flexDirection:stacked?'column':'row',alignItems:stacked?'stretch':'center',gap:stacked?12:16,paddingHorizontal:16,paddingVertical:stacked?16:2,borderRadius:14,backgroundColor:c.surface}}>
  <Text style={[stacked?styles.label:styles.body,{color:stacked?c.textSecondary:c.text}]}>{label}</Text>
  <TextInput {...props} accessibilityLabel={label} accessibilityHint={props.accessibilityHint??error??hint} underlineColorAndroid="transparent" placeholderTextColor={c.textSecondary} selectionColor={c.primary}
   style={[styles.field,{color:c.text,textAlign:stacked?'left':'right',minHeight:48},stacked?{minHeight:144,textAlignVertical:'top'}:{flex:1},style]} />
 </View>{error||hint?<Text accessibilityLiveRegion={error?'polite':'none'} style={[styles.caption,{color:error?c.danger:c.textSecondary,paddingHorizontal:16}]}>{error||hint}</Text>:null}</View>;
}

export function OrchardSearchField({value,onChangeText,label='搜索'}: {value:string;onChangeText:(text:string)=>void;label?:string}) {
 const {colors:c}=useOrchard();
 return <View style={{flexDirection:'row',alignItems:'center',backgroundColor:c.surfaceMuted,borderRadius:14,paddingLeft:14,gap:10,minHeight:48}}>
  <View style={{width:18,height:20}} importantForAccessibility="no-hide-descendants"><View style={{width:13,height:13,borderRadius:7,borderWidth:1.7,borderColor:c.textSecondary}} /><View style={{position:'absolute',width:8,height:1.7,left:10,top:13,backgroundColor:c.textSecondary,transform:[{rotate:'45deg'}]}} /></View>
  <TextInput accessibilityLabel={label} value={value} onChangeText={onChangeText} placeholder={label} placeholderTextColor={c.textSecondary} underlineColorAndroid="transparent" returnKeyType="search" style={[styles.field,{color:c.text,flex:1,paddingVertical:12}]} />
  {value?<Pressable accessibilityRole="button" accessibilityLabel="清除搜索" onPress={()=>onChangeText('')} style={{width:48,minHeight:48,alignItems:'center',justifyContent:'center'}}><Text style={{color:c.textSecondary,fontSize:22}}>×</Text></Pressable>:<View style={{width:12}} />}
 </View>;
}

export function OrchardSegments<T extends string>({options,selected,label,onSelect}: {options:readonly T[];selected:T;label:(value:T)=>string;onSelect:(value:T)=>void}) {
 const {colors:c,reducedMotion}=useOrchard();const large=useWindowDimensions().fontScale>1.5;const [width,setWidth]=useState(0);const offset=React.useRef(new Animated.Value(0)).current;
 const cell=options.length?(width-8)/options.length:0;const index=Math.max(0,options.indexOf(selected));const position=(I18nManager.isRTL?options.length-1-index:index)*cell;
 useEffect(()=>{if(reducedMotion){offset.setValue(position);return;}const anim=Animated.spring(offset,{toValue:position,stiffness:500,damping:30,mass:1,useNativeDriver:true});anim.start();return()=>anim.stop();},[position,offset,reducedMotion]);
 return <View accessibilityRole="radiogroup" onLayout={e=>setWidth(e.nativeEvent.layout.width)} style={{flexDirection:large?'column':'row',paddingHorizontal:4,paddingVertical:large?4:0,minHeight:48}}>
  <View pointerEvents="none" style={{position:'absolute',top:large?0:6,bottom:large?0:6,left:0,right:0,borderRadius:large?14:999,backgroundColor:c.segmentTrack}} />
  {!large&&width>0?<Animated.View pointerEvents="none" style={{position:'absolute',left:4,top:9,bottom:9,width:cell,borderRadius:999,backgroundColor:c.segmentThumb,shadowColor:'#000',shadowOpacity:.12,shadowRadius:2,shadowOffset:{width:0,height:1},elevation:1,transform:[{translateX:offset}]}} />:null}
  {options.map(option=><Pressable key={option} accessibilityRole="radio" accessibilityState={{checked:selected===option}} onPress={()=>onSelect(option)} style={[styles.segment,large?{backgroundColor:option===selected?c.segmentThumb:'transparent'}:{flex:1}]}>
   <Text style={{fontSize:14,fontWeight:option===selected?'600':'500',color:c.text,textAlign:'center'}}>{label(option)}</Text></Pressable>)}
 </View>;
}

export function OrchardLargeTitle({title, subtitle}: {title: string; subtitle?: string}) {
  const {colors: c} = useOrchard();
  return <View style={{gap: 5, paddingTop: 8, paddingBottom: 6}}><Text accessibilityRole="header" style={[styles.largeTitle, {color: c.text}]}>{title}</Text>
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
/** Supply a native blur view through renderBackdrop; the default is an opaque, legible fallback. */
export function OrchardGlassSurface({children, renderBackdrop, style}: BoxProps & {renderBackdrop?: () => ReactNode}) {
  const {colors: c} = useOrchard();
  return <View style={[{shadowColor:'#000',shadowOpacity:.1,shadowRadius:12,shadowOffset:{width:0,height:4},elevation:5},style]}>
    <View style={{borderRadius:999,overflow:'hidden',borderWidth:1,borderColor:c.divider}}>
      {renderBackdrop ? <View pointerEvents="none" style={StyleSheet.absoluteFill}>{renderBackdrop()}</View> : null}
      <View style={{backgroundColor:renderBackdrop?`${c.surface}DF`:c.surface}}>{children}</View>
    </View>
  </View>;
}
export type OrchardDestination = {key: string; label: string; icon: (color: string) => ReactNode};
export function OrchardTabBar({destinations,selected,onSelect,bottomInset=0,renderBackdrop}: {
  destinations: readonly OrchardDestination[]; selected: string; onSelect: (key:string)=>void; bottomInset?: number; renderBackdrop?: ()=>ReactNode;
}) {
  const {colors:c}=useOrchard();
  return <View style={{paddingHorizontal:20,paddingBottom:bottomInset+10}}><OrchardGlassSurface renderBackdrop={renderBackdrop}>
    <View style={{flexDirection:'row',padding:5}}>{destinations.map(destination=>{
      const active=destination.key===selected;const color=active?c.primary:c.textSecondary;
      return <Pressable key={destination.key} accessibilityRole="tab" accessibilityState={{selected:active}} onPress={()=>onSelect(destination.key)}
        style={{flex:1,minHeight:58,alignItems:'center',justifyContent:'center',padding:8,borderRadius:999,backgroundColor:active?c.surfaceMuted:'transparent'}}>
        {destination.icon(color)}<Text style={{fontSize:11,fontWeight:'600',color}}>{destination.label}</Text>
      </Pressable>;
    })}</View>
  </OrchardGlassSurface></View>;
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
export function OrchardConfirmDialog({title,message,confirmLabel,cancelLabel='取消',destructive=false,onConfirm,onDismiss}: OrchardConfirmOptions & {onConfirm:()=>void;onDismiss:()=>void}) {
 const {colors:c,reducedMotion}=useOrchard();const large=useWindowDimensions().fontScale>1.5;
 return <Modal transparent animationType={reducedMotion?'none':'fade'} visible onRequestClose={onDismiss}><View style={{flex:1,alignItems:'center',justifyContent:'center',backgroundColor:'#00000055',padding:24}}>
  <Pressable accessible={false} style={StyleSheet.absoluteFill} onPress={onDismiss} />
  <View accessibilityViewIsModal style={{width:'100%',maxWidth:300,borderRadius:28,backgroundColor:c.surface,overflow:'hidden'}}>
   <View style={{padding:22,gap:6}}><Text accessibilityRole="header" style={{fontSize:17,fontWeight:'600',color:c.text,textAlign:'center'}}>{title}</Text><Text style={{fontSize:13,color:c.text,textAlign:'center'}}>{message}</Text></View>
   <View style={{flexDirection:large?'column':'row',borderTopWidth:StyleSheet.hairlineWidth,borderTopColor:c.divider}}>
    <Pressable accessibilityRole="button" onPress={onDismiss} style={{flex:large?undefined:1,padding:16,alignItems:'center'}}><Text style={{fontSize:17,fontWeight:'600',color:c.primary}}>{cancelLabel}</Text></Pressable>
    <View style={large?{height:StyleSheet.hairlineWidth,backgroundColor:c.divider}:{width:StyleSheet.hairlineWidth,backgroundColor:c.divider}} />
    <Pressable accessibilityRole="button" onPress={onConfirm} style={{flex:large?undefined:1,padding:16,alignItems:'center'}}><Text style={{fontSize:17,color:destructive?c.danger:c.primary}}>{confirmLabel}</Text></Pressable>
   </View></View></View></Modal>;
}

/** Owns no storage. The caller keeps drafts and marks a successful save clean. */
export function OrchardEditorScreen({title = '随手记录', value, onChangeText, dirty, onSave, onClose}: {
  title?: string; value: string; onChangeText: (text: string) => void; dirty: boolean; onSave: () => Promise<void>; onClose: () => void;
}) {
  const {colors: c, confirm} = useOrchard();
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
      const approved = !dirty || await confirm({title: '放弃修改？', message: '本次修改尚未保存，确定放弃吗？', confirmLabel: '放弃修改', cancelLabel: '继续编辑', destructive: true});
      if (mountedRef.current && approved) onClose();
    } finally {confirmingRef.current = false;}
  }, [dirty, onClose, confirm]);
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
  card: {borderRadius: orchardTokens.radius.card, padding: 20, gap: 12},
  group: {borderRadius: orchardTokens.radius.card, overflow: 'hidden'},
  section: {gap: 8},
  button: {minHeight: orchardTokens.size.touch, minWidth: 48, borderRadius: 999, paddingHorizontal: 20, paddingVertical: 14, alignItems: 'center', justifyContent: 'center'},
  buttonText: {fontSize: orchardTokens.type.body, fontWeight: '600', textAlign: 'center'},
  row: {flexDirection: 'row', alignItems: 'center', gap: 12, minHeight: 52, paddingHorizontal: 16, paddingVertical: 11},
  rowContent: {flex: 1, gap: 4, flexShrink: 1},
  body: {fontSize: orchardTokens.type.body}, label: {fontSize: orchardTokens.type.label}, caption: {fontSize: orchardTokens.type.caption},
  title: {fontSize: orchardTokens.type.title, fontWeight: '600'},
  largeTitle: {fontSize: orchardTokens.type.largeTitle, letterSpacing: -.8, fontWeight: '700'},
  field: {borderWidth: 1, borderRadius: orchardTokens.radius.control, minHeight: 52, paddingHorizontal: 16, paddingVertical: 11, fontSize: orchardTokens.type.body},
  wrap: {flexDirection: 'row', flexWrap: 'wrap', gap: 8},
  segment: {minHeight: 48, paddingHorizontal: 8, paddingVertical: 12, borderRadius: 10, alignItems: 'center', justifyContent: 'center'},
  banner: {borderRadius: 16, padding: 16, gap: 8},
  scrim: {flex: 1, justifyContent: 'flex-end', backgroundColor: '#00000066'},
  sheet: {maxHeight: '90%', borderTopLeftRadius: 28, borderTopRightRadius: 28},
});
