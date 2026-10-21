import 'package:flutter/material.dart';
import 'components.dart';

Future<bool> orchardConfirm(
  BuildContext context, {
  required String title,
  required String message,
  required String confirmLabel,
  String cancelLabel = '取消',
  bool destructive = false,
}) async =>
    await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(title),
        content: Text(message),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: Text(cancelLabel),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: Text(
              confirmLabel,
              style: destructive
                  ? TextStyle(color: Theme.of(context).colorScheme.error)
                  : null,
            ),
          ),
        ],
      ),
    ) ??
    false;

class OrchardSettingsScreen extends StatelessWidget {
  const OrchardSettingsScreen({
    super.key,
    this.title = '偏好设置',
    this.subtitle = '让它更合你的习惯',
    required this.children,
  });
  final String title;
  final String subtitle;
  final List<Widget> children;
  @override
  Widget build(BuildContext context) => Scaffold(
    body: SafeArea(
      child: ListView(
        padding: const EdgeInsets.all(20),
        children: [
          OrchardLargeTitle(title: title, subtitle: subtitle),
          const SizedBox(height: 24),
          for (final child in children) ...[child, const SizedBox(height: 24)],
        ],
      ),
    ),
  );
}

/// Text belongs to the caller. A failed save never clears the controller.
/// The host supplies persistence and must recreate dirty state after process death.
class OrchardEditorScreen extends StatefulWidget {
  const OrchardEditorScreen({
    super.key,
    required this.controller,
    required this.dirty,
    required this.onSave,
    required this.onClose,
    this.title = '随手记录',
    this.saveErrorMessage = '还没有保存，请重试。输入已保留。',
  });
  final TextEditingController controller;
  final bool dirty;
  final Future<void> Function() onSave;
  final VoidCallback onClose;
  final String title;
  final String saveErrorMessage;
  @override
  State<OrchardEditorScreen> createState() => _OrchardEditorScreenState();
}

class _OrchardEditorScreenState extends State<OrchardEditorScreen> {
  bool _saving = false;
  bool _discardApproved = false;
  bool _confirming = false;
  String? _error;
  Future<void> _close() async {
    if (_saving || _confirming) return;
    if (widget.dirty && !_discardApproved) {
      _confirming = true;
      final discard = await orchardConfirm(
        context,
        title: '放弃修改？',
        message: '本次修改尚未保存，确定放弃吗？',
        confirmLabel: '放弃修改',
        cancelLabel: '继续编辑',
        destructive: true,
      );
      _confirming = false;
      if (!mounted || !discard) return;
      setState(() => _discardApproved = true);
      await WidgetsBinding.instance.endOfFrame;
    }
    if (mounted) widget.onClose();
  }

  Future<void> _save() async {
    if (_saving || !widget.dirty) return;
    setState(() {
      _saving = true;
      _error = null;
    });
    try {
      await widget.onSave();
    } catch (_) {
      if (mounted) setState(() => _error = widget.saveErrorMessage);
    } finally {
      if (mounted) setState(() => _saving = false);
    }
  }

  @override
  Widget build(BuildContext context) => PopScope(
    canPop: !_saving && (!widget.dirty || _discardApproved),
    onPopInvokedWithResult: (didPop, _) {
      if (!didPop) _close();
    },
    child: Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
        leading: IconButton(
          tooltip: '返回',
          onPressed: _close,
          icon: const Icon(Icons.arrow_back),
        ),
      ),
      body: SafeArea(
        child: ListView(
          padding: const EdgeInsets.all(20),
          children: [
            OrchardTextField(
              controller: widget.controller,
              label: '正文',
              maxLines: null,
              enabled: !_saving,
            ),
            const SizedBox(height: 16),
            if (_error != null) ...[
              OrchardBanner(title: '还没有保存', message: _error!, error: true),
              const SizedBox(height: 16),
            ],
            OrchardButton(
              label: '保存',
              onPressed: widget.dirty ? _save : null,
              loading: _saving,
            ),
          ],
        ),
      ),
    ),
  );
}

class OrchardImportReview extends StatelessWidget {
  const OrchardImportReview({
    super.key,
    required this.source,
    required this.summary,
    required this.preview,
    required this.onImport,
    required this.onCancel,
    this.warnings = const [],
    this.canImport = true,
    this.importing = false,
  });
  final String source;
  final String summary;
  final List<String> warnings;
  final Widget preview;
  final VoidCallback onImport;
  final VoidCallback onCancel;
  final bool canImport;
  final bool importing;
  @override
  Widget build(BuildContext context) => Column(
    crossAxisAlignment: CrossAxisAlignment.stretch,
    children: [
      OrchardLargeTitle(title: '先看看，再收入', subtitle: source),
      Text(summary),
      const SizedBox(height: 16),
      for (final warning in warnings) ...[
        OrchardBanner(title: '需要留意', message: warning),
        const SizedBox(height: 12),
      ],
      preview,
      const SizedBox(height: 16),
      OrchardButton(
        label: '确认导入',
        onPressed: canImport ? onImport : null,
        loading: importing,
      ),
      OrchardButton(
        label: '取消',
        onPressed: importing ? null : onCancel,
        kind: OrchardButtonKind.plain,
      ),
    ],
  );
}
