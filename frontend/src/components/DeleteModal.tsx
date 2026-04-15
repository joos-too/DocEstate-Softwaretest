interface DeleteModalProps {
  isOpen: boolean;
  isDeleting: boolean;
  propertyName?: string;
  onCancel: () => void;
  onConfirm: () => void | Promise<void>;
}

export function DeleteModal({
  isOpen,
  isDeleting,
  propertyName,
  onCancel,
  onConfirm
}: DeleteModalProps) {
  if (!isOpen) {
    return null;
  }

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-stone-950/45 px-4">
      <div className="w-full max-w-md rounded-3xl border border-border bg-white p-6 shadow-panel">
        <p className="text-sm font-semibold uppercase tracking-[0.24em] text-accent">Löschen</p>
        <h2 className="mt-3 text-2xl font-extrabold text-ink">Immobilie entfernen?</h2>
        <p className="mt-3 text-sm leading-6 text-stone-600">
          {propertyName
            ? `„${propertyName}“ wird dauerhaft gelöscht.`
            : 'Die Immobilie wird dauerhaft gelöscht.'}
        </p>
        <div className="mt-6 flex flex-col gap-3 sm:flex-row sm:justify-end">
          <button
            type="button"
            onClick={onCancel}
            disabled={isDeleting}
            className="rounded-full border border-border px-5 py-3 text-sm font-semibold text-stone-700 transition hover:border-stone-400 hover:text-ink disabled:cursor-not-allowed disabled:opacity-60"
          >
            Abbrechen
          </button>
          <button
            type="button"
            onClick={() => void onConfirm()}
            disabled={isDeleting}
            className="rounded-full bg-accent px-5 py-3 text-sm font-semibold text-white transition hover:bg-blue-700 disabled:cursor-not-allowed disabled:opacity-60"
          >
            {isDeleting ? 'Lösche...' : 'Endgültig löschen'}
          </button>
        </div>
      </div>
    </div>
  );
}
