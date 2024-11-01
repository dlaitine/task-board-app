import { Alert, Snackbar } from '@mui/material';
import { useContext, useEffect, useState } from 'react';
import { NotificationContext } from '../context/NotificationContext';

export const ErrorPopUp = () => {
  const [showError, setShowError] = useState(false);

  const { message, setMessage, severity, } = useContext(NotificationContext);

  useEffect(() => {
    if (message !== '') {
      setShowError(true);
      setTimeout(() => {
        setShowError(false);
        setMessage('');
      }, 5000);

    }
  }, [ message, setMessage, ]);

  return (
    <Snackbar
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      open={showError}
      message={message}
    >
      <Alert severity={severity}>
        {message}
      </Alert>
    </Snackbar>
  );
};