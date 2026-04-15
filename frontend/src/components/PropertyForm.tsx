import { useEffect, useMemo, useState, type ChangeEvent, type FormEvent } from 'react';
import type { PropertyFormValues, PropertyPayload, PropertyType } from '../types/property';
import { PROPERTY_TYPE_OPTIONS } from '../utils/propertyType';
import { ErrorAlert } from './ErrorAlert';

type FormMode = 'create' | 'edit';
type FieldErrors = Partial<
  Record<
    | 'name'
    | 'objectType'
    | 'constructionYear'
    | 'lotSize'
    | 'livingSpace'
    | 'address.city'
    | 'address.postalCode'
    | 'address.street'
    | 'address.houseNumber',
    string
  >
>;
type TouchedFields = Partial<Record<keyof FieldErrors, boolean>>;

interface PropertyFormProps {
  defaultValues: PropertyFormValues;
  error?: string;
  isSubmitting: boolean;
  mode: FormMode;
  onCancel: () => void;
  onFieldChange?: (field: string) => void;
  onSubmit: (payload: PropertyPayload) => Promise<void>;
  serverFieldErrors?: Record<string, string>;
}

const POSTAL_CODE_PATTERN = /^\d{5}$/;
const DIGITS_ONLY_PATTERN = /^\d+$/;
const FLOAT_PATTERN = /^\d+(?:[.,]\d+)?$/;

function parseFloatValue(value: string) {
  return Number.parseFloat(value.replace(',', '.'));
}

function parseIntegerValue(value: string) {
  return Number.parseInt(value, 10);
}

function toRequestPayload(values: PropertyFormValues, mode: FormMode): PropertyPayload {
  if (mode === 'edit') {
    const payload: PropertyPayload = {};
    const address: PropertyPayload['address'] = {};

    if (values.name.trim()) {
      payload.name = values.name.trim();
    }

    if (values.objectType) {
      payload.objectType = values.objectType as PropertyType;
    }

    if (values.constructionYear.trim()) {
      payload.constructionYear = parseIntegerValue(values.constructionYear.trim());
    }

    if (values.lotSize.trim()) {
      payload.lotSize = parseFloatValue(values.lotSize.trim());
    }

    if (values.livingSpace.trim()) {
      payload.livingSpace = parseFloatValue(values.livingSpace.trim());
    }

    if (values.address.city.trim()) {
      address.city = values.address.city.trim();
    }

    if (values.address.postalCode.trim()) {
      address.postalCode = values.address.postalCode.trim();
    }

    if (values.address.street.trim()) {
      address.street = values.address.street.trim();
    }

    if (values.address.houseNumber.trim()) {
      address.houseNumber = values.address.houseNumber.trim();
    }

    if (Object.keys(address).length > 0) {
      payload.address = address;
    }

    return payload;
  }

  return {
    name: values.name.trim(),
    objectType: values.objectType as PropertyType,
    constructionYear: parseIntegerValue(values.constructionYear.trim()),
    lotSize: parseFloatValue(values.lotSize.trim()),
    livingSpace: parseFloatValue(values.livingSpace.trim()),
    address: {
      city: values.address.city.trim(),
      postalCode: values.address.postalCode.trim(),
      street: values.address.street.trim(),
      houseNumber: values.address.houseNumber.trim()
    }
  };
}

function validateValues(values: PropertyFormValues, mode: FormMode): FieldErrors {
  const errors: FieldErrors = {};

  if (mode === 'create' && !values.name.trim()) {
    errors.name = 'Bezeichnung ist erforderlich.';
  }

  if (mode === 'create' && !values.objectType) {
    errors.objectType = 'Objekttyp ist erforderlich.';
  }

  const constructionYear = values.constructionYear.trim();
  if (mode === 'create' && !constructionYear) {
    errors.constructionYear = 'Baujahr ist erforderlich.';
  } else if (constructionYear && !DIGITS_ONLY_PATTERN.test(constructionYear)) {
    errors.constructionYear = 'Baujahr darf nur aus Ziffern bestehen.';
  }

  const lotSize = values.lotSize.trim();
  if (mode === 'create' && !lotSize) {
    errors.lotSize = 'Grundstücksfläche ist erforderlich.';
  } else if (lotSize) {
    if (!FLOAT_PATTERN.test(lotSize)) {
      errors.lotSize = 'Grundstücksfläche muss eine Zahl sein.';
    } else if (parseFloatValue(lotSize) <= 0) {
      errors.lotSize = 'Grundstücksfläche muss größer als 0 sein.';
    }
  }

  const livingSpace = values.livingSpace.trim();
  if (mode === 'create' && !livingSpace) {
    errors.livingSpace = 'Wohnfläche ist erforderlich.';
  } else if (livingSpace) {
    if (!FLOAT_PATTERN.test(livingSpace)) {
      errors.livingSpace = 'Wohnfläche muss eine Zahl sein.';
    } else if (parseFloatValue(livingSpace) <= 0) {
      errors.livingSpace = 'Wohnfläche muss größer als 0 sein.';
    }
  }

  if (mode === 'create' && !values.address.city.trim()) {
    errors['address.city'] = 'Ort ist erforderlich.';
  }

  if (mode === 'create' && !values.address.street.trim()) {
    errors['address.street'] = 'Straße ist erforderlich.';
  }

  const postalCode = values.address.postalCode.trim();
  if (mode === 'create' && !postalCode) {
    errors['address.postalCode'] = 'Postleitzahl ist erforderlich.';
  } else if (postalCode && !POSTAL_CODE_PATTERN.test(postalCode)) {
    errors['address.postalCode'] = 'Postleitzahl muss genau 5 Ziffern haben.';
  }

  const houseNumber = values.address.houseNumber.trim();
  if (mode === 'create' && !houseNumber) {
    errors['address.houseNumber'] = 'Hausnummer ist erforderlich.';
  }

  return errors;
}

function getFormError(values: PropertyFormValues, mode: FormMode): string {
  if (mode === 'edit') {
    const hasAnyValue = [
      values.name,
      values.objectType,
      values.constructionYear,
      values.lotSize,
      values.livingSpace,
      values.address.city,
      values.address.postalCode,
      values.address.street,
      values.address.houseNumber
    ].some((field) => Boolean(field.trim()));

    if (!hasAnyValue) {
      return 'Bitte geben Sie mindestens ein Feld zur Aktualisierung an.';
    }
  }

  return '';
}

function hasVisibleError(
  field: keyof FieldErrors,
  touchedFields: TouchedFields,
  hasAttemptedSubmit: boolean
) {
  return Boolean(touchedFields[field] || hasAttemptedSubmit);
}

export function PropertyForm({
  defaultValues,
  error,
  isSubmitting,
  mode,
  onCancel,
  onFieldChange,
  onSubmit,
  serverFieldErrors = {}
}: PropertyFormProps) {
  const [values, setValues] = useState<PropertyFormValues>(defaultValues);
  const [touchedFields, setTouchedFields] = useState<TouchedFields>({});
  const [hasAttemptedSubmit, setHasAttemptedSubmit] = useState(false);

  useEffect(() => {
    setValues(defaultValues);
    setTouchedFields({});
    setHasAttemptedSubmit(false);
  }, [defaultValues]);

  const localFieldErrors = useMemo(() => validateValues(values, mode), [values, mode]);
  const formError = useMemo(() => getFormError(values, mode), [values, mode]);
  const isSubmitDisabled =
    isSubmitting || Object.keys(localFieldErrors).length > 0 || Boolean(formError);

  function updateField(path: string, nextValue: string) {
    if (path.startsWith('address.')) {
      const key = path.replace('address.', '') as keyof PropertyFormValues['address'];
      setValues((current) => ({
        ...current,
        address: {
          ...current.address,
          [key]: nextValue
        }
      }));
      return;
    }

    setValues((current) => ({
      ...current,
      [path]: nextValue
    }));
  }

  function handleInputChange(path: keyof FieldErrors | 'name') {
    return (event: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
      updateField(path, event.target.value);
      setTouchedFields((current) => ({
        ...current,
        [path]: true
      }));
      onFieldChange?.(path);
    };
  }

  function getFieldError(field: keyof FieldErrors): string {
    if (serverFieldErrors[field]) {
      return serverFieldErrors[field];
    }

    if (hasVisibleError(field, touchedFields, hasAttemptedSubmit)) {
      return localFieldErrors[field] ?? '';
    }

    return '';
  }

  function getInputClassName(field: keyof FieldErrors) {
    const hasError = Boolean(getFieldError(field));

    return `w-full rounded-2xl border bg-stone-50 px-4 py-3 text-sm text-ink outline-none transition focus:bg-white ${
      hasError
        ? 'border-rose-300 focus:border-rose-500'
        : 'border-border focus:border-accent'
    }`;
  }

  async function handleSubmit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    setHasAttemptedSubmit(true);

    if (Object.keys(localFieldErrors).length > 0 || formError) {
      return;
    }

    await onSubmit(toRequestPayload(values, mode));
  }

  return (
    <form
      onSubmit={(event) => void handleSubmit(event)}
      className="rounded-[2rem] border border-border bg-white p-6 shadow-panel sm:p-8"
      noValidate
    >
      <div className="flex flex-col gap-2">
        <p className="text-sm font-semibold uppercase tracking-[0.24em] text-accent">
          {mode === 'create' ? 'Neue Immobilie' : 'Immobilie bearbeiten'}
        </p>
        <h2 className="text-2xl font-extrabold text-ink">
          {mode === 'create' ? 'Daten anlegen' : 'Daten aktualisieren'}
        </h2>
      </div>

      <div className="mt-6 space-y-4">
        <ErrorAlert message={formError || error} />

        <label className="block">
          <span className="mb-2 block text-sm font-semibold text-stone-700">Bezeichnung</span>
          <input
            value={values.name}
            onChange={handleInputChange('name')}
            className={getInputClassName('name')}
            placeholder="z. B. Stadtwohnung"
          />
          {getFieldError('name') ? (
            <span className="mt-2 block text-sm font-medium text-rose-700">{getFieldError('name')}</span>
          ) : null}
        </label>

        <div className="grid gap-4 md:grid-cols-2">
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">Objekttyp</span>
            <select
              value={values.objectType}
              onChange={handleInputChange('objectType')}
              className={getInputClassName('objectType')}
            >
              <option value="">Bitte wählen</option>
              {PROPERTY_TYPE_OPTIONS.map((option) => (
                <option key={option.value} value={option.value}>
                  {option.label}
                </option>
              ))}
            </select>
            {getFieldError('objectType') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">
                {getFieldError('objectType')}
              </span>
            ) : null}
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">Baujahr</span>
            <input
              value={values.constructionYear}
              onChange={handleInputChange('constructionYear')}
              className={getInputClassName('constructionYear')}
              placeholder="1998"
              inputMode="numeric"
            />
            {getFieldError('constructionYear') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">
                {getFieldError('constructionYear')}
              </span>
            ) : null}
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">
              Grundstücksfläche (m²)
            </span>
            <input
              value={values.lotSize}
              onChange={handleInputChange('lotSize')}
              className={getInputClassName('lotSize')}
              placeholder="450.5"
              inputMode="decimal"
            />
            {getFieldError('lotSize') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">{getFieldError('lotSize')}</span>
            ) : null}
          </label>

          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">Wohnfläche (m²)</span>
            <input
              value={values.livingSpace}
              onChange={handleInputChange('livingSpace')}
              className={getInputClassName('livingSpace')}
              placeholder="132.75"
              inputMode="decimal"
            />
            {getFieldError('livingSpace') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">
                {getFieldError('livingSpace')}
              </span>
            ) : null}
          </label>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">Ort</span>
            <input
              value={values.address.city}
              onChange={handleInputChange('address.city')}
              className={getInputClassName('address.city')}
              placeholder="Bonn"
            />
            {getFieldError('address.city') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">
                {getFieldError('address.city')}
              </span>
            ) : null}
          </label>
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">Postleitzahl</span>
            <input
              value={values.address.postalCode}
              onChange={handleInputChange('address.postalCode')}
              className={getInputClassName('address.postalCode')}
              placeholder="53127"
              inputMode="numeric"
            />
            {getFieldError('address.postalCode') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">
                {getFieldError('address.postalCode')}
              </span>
            ) : null}
          </label>
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">Straße</span>
            <input
              value={values.address.street}
              onChange={handleInputChange('address.street')}
              className={getInputClassName('address.street')}
              placeholder="Zur Marterkapelle"
            />
            {getFieldError('address.street') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">
                {getFieldError('address.street')}
              </span>
            ) : null}
          </label>
          <label className="block">
            <span className="mb-2 block text-sm font-semibold text-stone-700">Hausnummer</span>
            <input
              value={values.address.houseNumber}
              onChange={handleInputChange('address.houseNumber')}
              className={getInputClassName('address.houseNumber')}
              placeholder="29"
            />
            {getFieldError('address.houseNumber') ? (
              <span className="mt-2 block text-sm font-medium text-rose-700">
                {getFieldError('address.houseNumber')}
              </span>
            ) : null}
          </label>
        </div>
      </div>

      <div className="mt-8 flex flex-col gap-3 sm:flex-row sm:justify-end">
        <button
          type="button"
          onClick={onCancel}
          className="rounded-full border border-border px-5 py-3 text-sm font-semibold text-stone-700 transition hover:border-stone-400 hover:text-ink"
        >
          Abbrechen
        </button>
        <button
          type="submit"
          disabled={isSubmitDisabled}
          className="rounded-full bg-ink px-5 py-3 text-sm font-semibold text-white transition hover:bg-stone-800 disabled:cursor-not-allowed disabled:opacity-60"
        >
          {isSubmitting
            ? mode === 'create'
              ? 'Speichere...'
              : 'Aktualisiere...'
            : mode === 'create'
              ? 'Immobilie anlegen'
              : 'Änderungen speichern'}
        </button>
      </div>
    </form>
  );
}
