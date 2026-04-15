import { useEffect, useState } from 'react';
import { FiPlus } from 'react-icons/fi';
import { Link, useNavigate } from 'react-router-dom';
import { deleteProperty, getProperties } from '../api/properties';
import { DeleteModal } from '../components/DeleteModal';
import { ErrorAlert } from '../components/ErrorAlert';
import { LoadingState } from '../components/LoadingState';
import { PropertyCard } from '../components/PropertyCard';
import type { Property } from '../types/property';

export function PropertyListPage() {
  const navigate = useNavigate();
  const [properties, setProperties] = useState<Property[]>([]);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [propertyToDelete, setPropertyToDelete] = useState<Property | null>(null);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    let active = true;

    async function loadProperties() {
      setIsLoading(true);
      setError('');

      try {
        const response = await getProperties();
        if (active) {
          setProperties(response);
        }
      } catch (requestError) {
        if (active) {
          const message =
            requestError instanceof Error ? requestError.message : 'Immobilien konnten nicht geladen werden.';
          setError(message);
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    }

    void loadProperties();

    return () => {
      active = false;
    };
  }, []);

  async function handleDeleteConfirm() {
    if (!propertyToDelete) {
      return;
    }

    setIsDeleting(true);
    setError('');

    try {
      await deleteProperty(String(propertyToDelete.id));
      setProperties((current) =>
        current.filter((property) => property.id !== propertyToDelete.id)
      );
      setPropertyToDelete(null);
    } catch (requestError) {
      const message =
        requestError instanceof Error ? requestError.message : 'Immobilie konnte nicht gelöscht werden.';
      setError(message);
    } finally {
      setIsDeleting(false);
    }
  }

  return (
    <>
      <section className="space-y-6">
        <div className="flex flex-col gap-4 rounded-[2rem] border border-border bg-white p-6 shadow-panel sm:flex-row sm:items-center sm:justify-between">
          <div>
            <p className="text-sm font-semibold uppercase tracking-[0.24em] text-accent">Bestand</p>
            <h2 className="mt-2 text-2xl font-extrabold text-ink">Alle Immobilien</h2>
            <p className="mt-2 text-sm text-stone-600">
              {properties.length} {properties.length === 1 ? 'Eintrag' : 'Einträge'} verfügbar
            </p>
          </div>
          <Link
            to="/neu"
            className="inline-flex items-center justify-center gap-2 rounded-full bg-ink px-5 py-3 text-sm font-semibold text-white transition hover:bg-stone-800"
          >
            <FiPlus size={18} />
            Immobilie erstellen
          </Link>
        </div>

        <ErrorAlert message={error} />

        {isLoading ? <LoadingState /> : null}

        {!isLoading && !error && properties.length === 0 ? (
          <div className="rounded-[2rem] border border-dashed border-border bg-white/70 px-6 py-14 text-center shadow-panel">
            <p className="text-lg font-semibold text-ink">Noch keine Immobilien vorhanden.</p>
            <p className="mt-2 text-sm text-stone-500">Legen Sie den ersten Eintrag direkt über den Button an.</p>
          </div>
        ) : null}

        {!isLoading && properties.length > 0 ? (
          <div className="grid gap-5 lg:grid-cols-2">
            {properties.map((property) => (
              <PropertyCard
                key={property.id}
                property={property}
                onOpen={(selectedProperty) => navigate(`/immobilien/${selectedProperty.id}`)}
                onEdit={(selectedProperty) => navigate(`/immobilien/${selectedProperty.id}/bearbeiten`)}
                onDelete={(selectedProperty) => setPropertyToDelete(selectedProperty)}
              />
            ))}
          </div>
        ) : null}
      </section>

      <DeleteModal
        isOpen={Boolean(propertyToDelete)}
        isDeleting={isDeleting}
        propertyName={propertyToDelete?.name}
        onCancel={() => {
          if (!isDeleting) {
            setPropertyToDelete(null);
          }
        }}
        onConfirm={handleDeleteConfirm}
      />
    </>
  );
}
