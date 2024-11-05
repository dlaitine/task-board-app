import { StrictMode } from 'react'
import { createRoot } from 'react-dom/client'
import App from './App.tsx'
import { LoginProvider } from './context/LoginContext.tsx'
import { BrowserRouter } from 'react-router-dom'
import { createTheme, ThemeProvider } from '@mui/material'
import  '@fontsource/neucha';
import { StompSessionProvider } from 'react-stomp-hooks'
import { NotificationProvider } from './context/NotificationContext.tsx'
import { baseUrl } from './common/constants.ts'

const theme = createTheme({
  components: {
    MuiDialogContentText: {
      styleOverrides: {
        root: {
          color: '#0077B6',
        }
      }
    }
  },
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
