// Run inside an existing Flutter application. Demo content stays in memory.
import 'package:flutter/material.dart';
import 'package:orchard_ui/orchard_ui.dart';

class NotesReference extends StatefulWidget {
  const NotesReference({
    super.key,
    required this.onMore,
    required this.onOpen,
    required this.onCompose,
  });
  final VoidCallback onMore, onCompose;
  final ValueChanged<String> onOpen;
  @override
  State<NotesReference> createState() => _NotesReferenceState();
}

class _NotesReferenceState extends State<NotesReference> {
  final _query = TextEditingController();
  final _notes = ['周末的小计划', '灵感随记', '想读的书'];
  @override
  void dispose() {
    _query.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) => OrchardAppPage(
    title: '备忘录',
    subtitle: '${_notes.length} 则备忘录',
    onMore: widget.onMore,
    bottomBar: OrchardSearchDock(
      controller: _query,
      onChanged: (_) => setState(() {}),
      onCompose: widget.onCompose,
    ),
    children: [
      Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Text('今天', style: Theme.of(context).textTheme.titleLarge),
          const SizedBox(height: 12),
          OrchardGroup(
            child: Column(
              children: [
                for (final title in _notes.where(
                  (n) => n.contains(_query.text),
                ))
                  OrchardNoteRow(
                    title: title,
                    preview: '留一点时间给自己。',
                    time: '09:41',
                    onTap: () => widget.onOpen(title),
                  ),
              ],
            ),
          ),
        ],
      ),
    ],
  );
}
