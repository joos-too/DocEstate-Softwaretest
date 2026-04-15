export interface Address {
  city: string;
  postalCode: string;
  street: string;
  houseNumber: string;
}

export type PropertyType =
  | 'EINFAMILIENHAUS'
  | 'EIGENTUMSWOHNUNG'
  | 'DOPPELHAUSHAELFTE'
  | 'MEHRFAMILIENHAUS';

export interface Property {
  id: number;
  name: string;
  objectType: PropertyType;
  constructionYear: string;
  lotSize: number;
  livingSpace: number;
  address: Address;
}

export interface PropertyPayload {
  name?: string;
  objectType?: PropertyType;
  constructionYear?: string;
  lotSize?: number;
  livingSpace?: number;
  address?: Partial<Address>;
}

export interface PropertyFormValues {
  name: string;
  objectType: PropertyType | '';
  constructionYear: string;
  lotSize: string;
  livingSpace: string;
  address: Address;
}

export interface ApiErrorPayload {
  timestamp?: string;
  message?: string;
  fieldErrors?: Record<string, string>;
}
