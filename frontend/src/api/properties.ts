import type { ApiErrorPayload, Property, PropertyPayload } from '../types/property';

const API_BASE = '/api/properties';

const FIELD_ERROR_TRANSLATIONS: Record<string, string> = {
  'address.postalCode': 'Postleitzahl muss genau 5 Ziffern haben.',
  name: 'Bezeichnung ist ungültig.',
  'address.city': 'Ort ist ungültig.',
  'address.street': 'Straße ist ungültig.',
  'address.houseNumber': 'Hausnummer ist ungültig.',
  address: 'Adresse ist ungültig.'
};

function localizeFieldErrors(fieldErrors?: Record<string, string>) {
  if (!fieldErrors) {
    return undefined;
  }

  return Object.fromEntries(
    Object.entries(fieldErrors).map(([field, message]) => [
      field,
      FIELD_ERROR_TRANSLATIONS[field] ?? localizeMessage(message)
    ])
  );
}

function localizeMessage(message?: string) {
  if (!message) {
    return 'Die Anfrage konnte nicht verarbeitet werden.';
  }

  if (message.startsWith('Property with ID ') && message.endsWith(' was not found.')) {
    const id = message.replace('Property with ID ', '').replace(' was not found.', '');
    return `Immobilie mit ID ${id} wurde nicht gefunden.`;
  }

  return (
    {
      'Request is invalid.': 'Anfrage ist ungültig.',
      'Postal code must contain exactly 5 digits.': 'Postleitzahl muss genau 5 Ziffern haben.',
      'Name is invalid.': 'Bezeichnung ist ungültig.',
      'Address is invalid.': 'Adresse ist ungültig.',
      'City is invalid.': 'Ort ist ungültig.',
      'Postal code is invalid.': 'Postleitzahl ist ungültig.',
      'Street is invalid.': 'Straße ist ungültig.',
      'House number is invalid.': 'Hausnummer ist ungültig.'
    }[message] ?? message
  );
}

function localizePayload(payload: ApiErrorPayload | null): ApiErrorPayload | null {
  if (!payload) {
    return null;
  }

  const localizedFieldErrors = localizeFieldErrors(payload.fieldErrors);

  return {
    ...payload,
    message:
      localizedFieldErrors && Object.keys(localizedFieldErrors).length > 0
        ? Object.values(localizedFieldErrors)[0]
        : localizeMessage(payload.message),
    fieldErrors: localizedFieldErrors
  };
}

export class RequestError extends Error {
  status: number;
  payload: ApiErrorPayload | null;

  constructor(message: string, status: number, payload: ApiErrorPayload | null) {
    super(message);
    this.name = 'RequestError';
    this.status = status;
    this.payload = payload;
  }
}

async function request<T>(url: string, options: RequestInit = {}): Promise<T> {
  const response = await fetch(url, {
    headers: {
      'Content-Type': 'application/json',
      ...options.headers
    },
    ...options
  });

  if (response.status === 204) {
    return null as T;
  }

  const contentType = response.headers.get('content-type') ?? '';
  const payload = contentType.includes('application/json')
    ? ((await response.json()) as ApiErrorPayload)
    : null;

  if (!response.ok) {
    const localizedPayload = localizePayload(payload);
    const message =
      localizedPayload?.message ?? localizeMessage(payload?.message) ?? 'Die Anfrage konnte nicht verarbeitet werden.';
    throw new RequestError(message, response.status, localizedPayload);
  }

  return payload as T;
}

export function getProperties(): Promise<Property[]> {
  return request<Property[]>(API_BASE);
}

export function getProperty(id: string): Promise<Property> {
  return request<Property>(`${API_BASE}/${id}`);
}

export function createProperty(property: PropertyPayload): Promise<Property> {
  return request<Property>(API_BASE, {
    method: 'POST',
    body: JSON.stringify(property)
  });
}

export function updateProperty(id: string, property: PropertyPayload): Promise<Property> {
  return request<Property>(`${API_BASE}/${id}`, {
    method: 'PUT',
    body: JSON.stringify(property)
  });
}

export function deleteProperty(id: string): Promise<null> {
  return request<null>(`${API_BASE}/${id}`, {
    method: 'DELETE'
  });
}
