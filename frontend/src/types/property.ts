export interface Address {
  city: string;
  postalCode: string;
  street: string;
  houseNumber: string;
}

export interface Property {
  id: number;
  name: string;
  address: Address;
}

export interface PropertyPayload {
  name?: string;
  address?: Partial<Address>;
}

export interface PropertyFormValues {
  name: string;
  address: Address;
}

export interface ApiErrorPayload {
  timestamp?: string;
  message?: string;
  fieldErrors?: Record<string, string>;
}
