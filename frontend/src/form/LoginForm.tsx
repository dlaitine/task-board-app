import { useContext, useState } from 'react';
import { Button, Dialog, DialogTitle, DialogContent, DialogActions, TextField, DialogContentText, Typography, } from '@mui/material';
import { LoginContext } from '../context/LoginContext';



export const LoginForm = () => {
  const { username: loggedInUsername, login } = useContext(LoginContext);

  // Inputs
  const [username, setUsername] = useState<string>('');

  // Errors
  const [usernameError, setUsernameError] = useState<string>('');

  const handleSubmit = () => {
    if (username.trim() === '') {
      setUsernameError('Required')
      return;
    }

    login(username);
  };

  return (
    <>
      <Dialog open={loggedInUsername == null}>
      <form onSubmit={(e) => {
        e.preventDefault();
        handleSubmit();
        }}>
        <DialogTitle variant='h5'>Login</DialogTitle>
        <DialogContent>
          <DialogContentText>
            <Typography>
              Welcome to Task Board App! Please login to continue. No passwords required at this time!
            </Typography>
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
              htmlInput: { maxLength: 50 }
            }}
          />
        </DialogContent>
        <DialogActions>
          <Button type='submit' color='primary' variant='contained'>
            Login
          </Button>
        </DialogActions>
        </form>
      </Dialog>
    </>
  );
};