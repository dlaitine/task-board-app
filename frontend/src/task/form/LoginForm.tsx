import { useContext, useState } from 'react';
import { Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, DialogContentText, } from '@mui/material';
import { LoginContext } from '../context/LoginContext';



export const LoginForm = () => {
  const { username: loggedInUsername, login } = useContext(LoginContext);

  // Inputs
  const [username, setUsername] = useState<string>('');

  // Errors
  const [usernameError, setUsernameError] = useState<string>('');

  const handleSubmit = () => {
    if (username === '') {
      setUsernameError('Required')
      return;
    }

    login(username);
  };

  return (
    <>
      <Dialog open={loggedInUsername == null}>
        <DialogTitle variant='h5'>Login</DialogTitle>
        <DialogContent>
          <DialogContentText>
            Welcome to Task App! Please login to continue. No passwords required at this time!
          </DialogContentText>
          <TextField
            label='Username'
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            margin='normal'
            fullWidth
            required
            error={usernameError !== ''}
            helperText={usernameError}
            slotProps={{
              htmlInput: { maxLength: 25 }
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button onClick={handleSubmit} color='primary' variant='contained'>
            Login
          </Button>
        </DialogActions>
      </Dialog>
    </>
  );
};