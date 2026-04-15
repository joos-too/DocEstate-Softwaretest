import { useEffect, useState } from 'react';
import { FiArrowLeft } from 'react-icons/fi';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { RequestError, createProperty, getProperty, updateProperty } from '../api/properties';
import { ErrorAlert } from '../components/ErrorAlert';
import { LoadingState } from '../components/LoadingState';
import { PropertyForm } from '../components/PropertyForm';
import type { Property, PropertyFormValues, PropertyPayload } from '../types/property';

const EMPTY_FORM: PropertyFormValues = {
  name: '',
  objectType: '',
  constructionYear: '',
  lotSize: '',
  livingSpace: '',
  address: {
    city: '',
    postalCode: '',
    street: '',
    houseNumber: ''
  }
};

function mapPropertyToForm(property: Property): PropertyFormValues {
  return {
    name: property.name,
    objectType: property.objectType,
    constructionYear: property.constructionYear,
    lotSize: String(property.lotSize),
    livingSpace: String(property.livingSpace),
    address: {
      city: property.address.city,
      postalCode: property.address.postalCode,
      street: property.address.street,
      houseNumber: property.address.houseNumber
    }
  };
}

interface PropertyFormPageProps {
  mode: 'create' | 'edit';
}

export function PropertyFormPage({ mode }: PropertyFormPageProps) {
  const navigate = useNavigate();
  const { id = '' } = useParams();
  const [defaultValues, setDefaultValues] = useState<PropertyFormValues>(EMPTY_FORM);
  const [isLoading, setIsLoading] = useState(mode === 'edit');
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [error, setError] = useState('');
  const [serverFieldErrors, setServerFieldErrors] = useState<Record<string, string>>({});

  useEffect(() => {
    if (mode !== 'edit') {
      return undefined;
    }

    let active = true;

    async function loadProperty() {
      setIsLoading(true);
      setError('');
      setServerFieldErrors({});

      try {
        const property = await getProperty(id);
        if (active) {
          setDefaultValues(mapPropertyToForm(property));
        }
      } catch (requestError) {
        if (active) {
          const message =
            requestError instanceof Error ? requestError.message : 'Formulardaten konnten nicht geladen werden.';
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
  }, [id, mode]);

  async function handleSubmit(payload: PropertyPayload) {
    setIsSubmitting(true);
    setError('');
    setServerFieldErrors({});

    try {
      const property =
        mode === 'create' ? await createProperty(payload) : await updateProperty(id, payload);

      navigate(`/immobilien/${property.id}`);
    } catch (requestError) {
      if (requestError instanceof RequestError) {
        setServerFieldErrors(requestError.payload?.fieldErrors ?? {});
        setError(requestError.payload?.fieldErrors ? '' : requestError.message);
      } else {
        const message =
          requestError instanceof Error ? requestError.message : 'Die Immobilie konnte nicht gespeichert werden.';
        setError(message);
      }
    } finally {
      setIsSubmitting(false);
    }
  }

  function handleFieldChange(field: string) {
    setServerFieldErrors((current) => {
      if (!current[field]) {
        return current;
      }

      const nextErrors = { ...current };
      delete nextErrors[field];
      return nextErrors;
    });
  }

  if (isLoading) {
    return <LoadingState label="Formulardaten werden geladen..." />;
  }

  const hasBlockingError = mode === 'edit' && Boolean(error) && !defaultValues.name;

  return (
    <section className="space-y-5">
      <Link
        to={mode === 'edit' ? `/immobilien/${id}` : '/'}
        className="inline-flex items-center gap-2 text-sm font-semibold text-stone-600 transition hover:text-ink"
      >
        <FiArrowLeft size={16} />
        {mode === 'edit' ? 'Zur Detailansicht' : 'Zur Übersicht'}
      </Link>

      {hasBlockingError ? <ErrorAlert message={error} /> : null}

      {!hasBlockingError ? (
        <PropertyForm
          mode={mode}
          defaultValues={defaultValues}
          error={error}
          isSubmitting={isSubmitting}
          onCancel={() => navigate(mode === 'edit' ? `/immobilien/${id}` : '/')}
          onFieldChange={handleFieldChange}
          onSubmit={handleSubmit}
          serverFieldErrors={serverFieldErrors}
        />
      ) : null}
    </section>
  );
}
