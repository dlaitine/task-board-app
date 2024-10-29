import { Alert, Snackbar } from '@mui/material';
import { useState } from 'react';
import { useSubscription } from 'react-stomp-hooks';

export const SocketErrorPopUp = () => {
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  useSubscription('/user/topic/error', (error) => {
    setErrorMessage('An error occurred: ' + error.body);
    setShowError(true);
    setTimeout(() => {
      setShowError(false);
    }, 5000);
  });

  return (
    <Snackbar
      anchorOrigin={{ vertical: 'top', horizontal: 'right' }}
      open={showError}
      autoHideDuration={5000}
      message={errorMessage}
      color='red'
    >
      <Alert severity='error'>
        {errorMessage}
      </Alert>
    </Snackbar>
  );
};