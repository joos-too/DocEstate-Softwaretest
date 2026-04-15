import type { PropertyType } from '../types/property';

export const PROPERTY_TYPE_OPTIONS: Array<{ value: PropertyType; label: string }> = [
  { value: 'EINFAMILIENHAUS', label: 'Einfamilienhaus' },
  { value: 'EIGENTUMSWOHNUNG', label: 'Eigentumswohnung' },
  { value: 'DOPPELHAUSHAELFTE', label: 'Doppelhaushälfte' },
  { value: 'MEHRFAMILIENHAUS', label: 'Mehrfamilienhaus' }
];

export function getPropertyTypeLabel(propertyType: PropertyType) {
  return PROPERTY_TYPE_OPTIONS.find((option) => option.value === propertyType)?.label ?? propertyType;
}
