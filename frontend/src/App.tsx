import { Navigate, Route, Routes } from 'react-router-dom';
import { Layout } from './components/Layout';
import { PropertyDetailPage } from './pages/PropertyDetailPage';
import { PropertyFormPage } from './pages/PropertyFormPage';
import { PropertyListPage } from './pages/PropertyListPage';

export default function App() {
  return (
    <Routes>
      <Route element={<Layout />}>
        <Route path="/" element={<PropertyListPage />} />
        <Route path="/neu" element={<PropertyFormPage mode="create" />} />
        <Route path="/immobilien/:id" element={<PropertyDetailPage />} />
        <Route path="/immobilien/:id/bearbeiten" element={<PropertyFormPage mode="edit" />} />
        <Route path="*" element={<Navigate to="/" replace />} />
      </Route>
    </Routes>
  );
}
