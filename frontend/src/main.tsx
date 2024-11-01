import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { LoginProvider } from './task/context/LoginContext.tsx'
import { BrowserRouter } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material'
import  '@fontsource/neucha';
import { StompSessionProvider } from 'react-stomp-hooks'
import { NotificationProvider } from './task/context/NotificationContext.tsx'

const theme = createTheme({
  typography: {
    fontFamily: 'Neucha',
    allVariants: {
      color: '#0077B6',
      wordWrap: 'break-word',
      wordBreak: 'break-word',
      overflowWrap: 'break-word',
    }
  },
});

const baseUrl = import.meta.env.VITE_TASK_BACKEND_BASE_URL;

createRoot(document.getElementById('root')!).render(
  <StrictMode>
    <ThemeProvider theme={theme}>
      <BrowserRouter>
        <LoginProvider>
          <StompSessionProvider url={`${baseUrl}/ws`}>
            <NotificationProvider>
              <App />
            </NotificationProvider>
          </StompSessionProvider>
        </LoginProvider>
      </BrowserRouter>
    </ThemeProvider>
  </StrictMode>
)
