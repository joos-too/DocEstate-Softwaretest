import { useEffect, useState } from 'react';
import { FiArrowLeft, FiEdit2, FiMapPin, FiTrash2 } from 'react-icons/fi';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { deleteProperty, getProperty } from '../api/properties';
import { DeleteModal } from '../components/DeleteModal';
import { ErrorAlert } from '../components/ErrorAlert';
import { LoadingState } from '../components/LoadingState';
import type { Property } from '../types/property';

interface InfoRowProps {
  label: string;
  value: string | number;
}

function InfoRow({ label, value }: InfoRowProps) {
  return (
    <div className="rounded-2xl border border-border bg-stone-50 px-4 py-4">
      <p className="text-xs font-semibold uppercase tracking-[0.2em] text-stone-400">{label}</p>
      <p className="mt-2 text-base font-semibold text-ink">{value}</p>
    </div>
  );
}

export function PropertyDetailPage() {
  const { id = '' } = useParams();
  const navigate = useNavigate();
  const [property, setProperty] = useState<Property | null>(null);
  const [error, setError] = useState('');
  const [isLoading, setIsLoading] = useState(true);
  const [isDeleteOpen, setIsDeleteOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

  useEffect(() => {
    let active = true;

    async function loadProperty() {
      setIsLoading(true);
      setError('');

      try {
        const response = await getProperty(id);
        if (active) {
          setProperty(response);
        }
      } catch (requestError) {
        if (active) {
          const message =
            requestError instanceof Error ? requestError.message : 'Immobilie konnte nicht geladen werden.';
          setError(message);
        }
      } finally {
        if (active) {
          setIsLoading(false);
        }
      }
    }

    void loadProperty();

    return () => {
      active = false;
    };
  }, [id]);

  async function handleDelete() {
    setIsDeleting(true);
    setError('');

    try {
      await deleteProperty(id);
      navigate('/');
    } catch (requestError) {
      const message =
        requestError instanceof Error ? requestError.message : 'Immobilie konnte nicht gelöscht werden.';
      setError(message);
    } finally {
      setIsDeleting(false);
      setIsDeleteOpen(false);
    }
  }

  if (isLoading) {
    return <LoadingState label="Immobilie wird geladen..." />;
  }

  if (!property) {
    return (
      <div className="space-y-4">
        <Link
          to="/"
          className="inline-flex items-center gap-2 text-sm font-semibold text-stone-600 transition hover:text-ink"
        >
          <FiArrowLeft size={16} />
          Zur Übersicht
        </Link>
        <ErrorAlert message={error || 'Immobilie konnte nicht geladen werden.'} />
      </div>
    );
  }

  return (
    <>
      <section className="space-y-5">
        <Link
          to="/"
          className="inline-flex items-center gap-2 text-sm font-semibold text-stone-600 transition hover:text-ink"
        >
          <FiArrowLeft size={16} />
          Zur Übersicht
        </Link>

        <div className="rounded-[2rem] border border-border bg-white p-6 shadow-panel sm:p-8">
          <div className="flex flex-col gap-5 md:flex-row md:items-start md:justify-between">
            <div>
              <p className="text-sm font-semibold uppercase tracking-[0.24em] text-accent">
                Detailansicht
              </p>
              <h2 className="mt-3 text-3xl font-extrabold text-ink">{property.name}</h2>
              <div className="mt-5 inline-flex items-center gap-3 rounded-full bg-stone-50 px-4 py-3 text-sm text-stone-600">
                <span className="rounded-full bg-accentSoft p-2 text-accent">
                  <FiMapPin size={16} />
                </span>
                {property.address.street} {property.address.houseNumber}, {property.address.postalCode}{' '}
                {property.address.city}
              </div>
            </div>

            <div className="flex flex-wrap gap-3">
              <Link
                to={`/immobilien/${property.id}/bearbeiten`}
                className="inline-flex items-center gap-2 rounded-full border border-border px-5 py-3 text-sm font-semibold text-stone-700 transition hover:border-accent hover:text-accent"
              >
                <FiEdit2 size={16} />
                Bearbeiten
              </Link>
              <button
                type="button"
                onClick={() => setIsDeleteOpen(true)}
                className="inline-flex items-center gap-2 rounded-full bg-accent px-5 py-3 text-sm font-semibold text-white transition hover:bg-blue-700"
              >
                <FiTrash2 size={16} />
                Löschen
              </button>
            </div>
          </div>

          <div className="mt-6">
            <ErrorAlert message={error} />
          </div>

          <div className="mt-8 grid gap-4 sm:grid-cols-2">
            <InfoRow label="Bezeichnung" value={property.name} />
            <InfoRow label="Ort" value={property.address.city} />
            <InfoRow label="Postleitzahl" value={property.address.postalCode} />
            <InfoRow label="Straße" value={property.address.street} />
            <InfoRow label="Hausnummer" value={property.address.houseNumber} />
          </div>
        </div>
      </section>

      <DeleteModal
        isOpen={isDeleteOpen}
        isDeleting={isDeleting}
        propertyName={property.name}
        onCancel={() => setIsDeleteOpen(false)}
        onConfirm={handleDelete}
      />
    </>
  );
}
