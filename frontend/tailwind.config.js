/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        surface: '#f4f8ff',
        ink: '#171717',
        accent: '#2563eb',
        accentSoft: '#dbeafe',
        border: '#d7e3f4'
      },
      boxShadow: {
        panel: '0 18px 40px rgba(37, 99, 235, 0.12)'
      },
      fontFamily: {
        sans: ['"Trebuchet MS"', '"Segoe UI"', 'sans-serif']
      }
    }
  },
  plugins: []
};
