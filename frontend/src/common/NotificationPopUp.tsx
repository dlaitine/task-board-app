import { Alert, Snackbar } from '@mui/material';
import { useContext, useEffect, useState } from 'react';
import { NotificationContext } from '../context/NotificationContext';
import { useSubscription } from 'react-stomp-hooks';

export const ErrorPopUp = () => {
  const [showError, setShowError] = useState(false);

  const { message, setMessage, severity, setSeverity } =
    useContext(NotificationContext);

  // Subscribe to STOMP errors
  useSubscription('/user/topic/error', (error) => {
    setMessage(error.body);
    setSeverity('error');
  });

  useEffect(() => {
    if (message !== '') {
      setShowError(true);
      setTimeout(() => {
        setShowError(false);
        setMessage('');
      }, 5000);
    }
  }, [message, setMessage]);

  return (
    <Snackbar
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      open={showError}
      message={message}
    >
      <Alert sx={{ width: '100%' }} severity={severity}>
        {message}
      </Alert>
    </Snackbar>
  );
};
