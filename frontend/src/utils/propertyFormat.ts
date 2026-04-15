export function formatArea(value: number) {
  return `${new Intl.NumberFormat('de-DE', {
    minimumFractionDigits: 0,
    maximumFractionDigits: 2
  }).format(value)} m²`;
}
