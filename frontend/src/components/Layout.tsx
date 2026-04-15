import { Outlet } from 'react-router-dom';

export function Layout() {
  return (
    <div className="min-h-screen">
      <div className="mx-auto flex min-h-screen max-w-6xl flex-col px-4 py-8 sm:px-6 lg:px-8">
        <header className="rounded-[2rem] border border-border bg-white/80 px-6 py-6 shadow-panel backdrop-blur">
          <p className="text-sm font-semibold uppercase tracking-[0.28em] text-accent">DocEstate</p>
          <div className="mt-4 flex flex-col gap-3 md:flex-row md:items-end md:justify-between">
            <div>
              <h1 className="text-3xl font-extrabold tracking-tight text-ink sm:text-4xl">
                Immobilienverwaltung
              </h1>
              <p className="mt-2 max-w-2xl text-sm leading-6 text-stone-600 sm:text-base">
                Minimalistische Oberfläche für Anlegen, Anzeigen, Bearbeiten und Löschen von Immobilien.
              </p>
            </div>
          </div>
        </header>
        <main className="flex-1 py-8">
          <Outlet />
        </main>
      </div>
    </div>
  );
}
