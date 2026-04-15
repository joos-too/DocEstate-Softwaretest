interface LoadingStateProps {
  label?: string;
}

export function LoadingState({ label = 'Lade Daten...' }: LoadingStateProps) {
  return (
    <div className="rounded-[2rem] border border-dashed border-border bg-white/70 px-6 py-12 text-center text-sm font-medium text-stone-500">
      {label}
    </div>
  );
}
