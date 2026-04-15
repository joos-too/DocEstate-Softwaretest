import { type KeyboardEvent, type MouseEvent } from 'react';
import { FiEdit2, FiTrash2 } from 'react-icons/fi';
import type { Property } from '../types/property';
import { getPropertyTypeLabel } from '../utils/propertyType';

interface PropertyCardProps {
  property: Property;
  onDelete: (property: Property) => void;
  onEdit: (property: Property) => void;
  onOpen: (property: Property) => void;
}

export function PropertyCard({ property, onDelete, onEdit, onOpen }: PropertyCardProps) {
  function handleAction(event: MouseEvent<HTMLButtonElement>, action: (property: Property) => void) {
    event.stopPropagation();
    action(property);
  }

  function handleKeyDown(event: KeyboardEvent<HTMLElement>) {
    if (event.key === 'Enter' || event.key === ' ') {
      event.preventDefault();
      onOpen(property);
    }
  }

  return (
    <article
      role="button"
      tabIndex={0}
      onClick={() => onOpen(property)}
      onKeyDown={handleKeyDown}
      className="group flex cursor-pointer flex-col rounded-[1.75rem] border border-border bg-white p-6 shadow-panel transition duration-200 hover:-translate-y-1 hover:border-accentSoft hover:shadow-none focus:outline-none focus:ring-2 focus:ring-accent/40"
    >
      <div className="flex items-start justify-between gap-4">
        <div className="space-y-3">
          <h2 className="text-xl font-extrabold text-ink">{property.name}</h2>
          <div className="flex flex-wrap gap-2 text-sm text-stone-600">
            <span className="rounded-full bg-stone-100 px-3 py-1 font-semibold text-ink">
              {getPropertyTypeLabel(property.objectType)}
            </span>
            <span className="rounded-full bg-stone-100 px-3 py-1 font-semibold text-ink">
              Baujahr {property.constructionYear}
            </span>
          </div>
        </div>
        <div className="flex items-center gap-2">
          <button
            type="button"
            onClick={(event) => handleAction(event, onEdit)}
            className="rounded-full border border-border p-3 text-stone-500 transition hover:border-accent hover:text-accent"
            aria-label={`${property.name} bearbeiten`}
          >
            <FiEdit2 size={18} />
          </button>
          <button
            type="button"
            onClick={(event) => handleAction(event, onDelete)}
            className="rounded-full border border-border p-3 text-stone-500 transition hover:border-accent hover:text-accent"
            aria-label={`${property.name} löschen`}
          >
            <FiTrash2 size={18} />
          </button>
        </div>
      </div>
    </article>
  );
}
