import { useState } from 'react';
import Grid from '@mui/material/Grid2';
import { TextField, Button } from '@mui/material';

interface ChatMessageFormProps {
  sendMessage: (newMessage: string) => void;
}

export const ChatMessageForm = ({ sendMessage }: ChatMessageFormProps) => {
  const [newMessage, setNewMessage] = useState<string>('');

  const createNewMessage = () => {
    sendMessage(newMessage);
    setNewMessage('');
  };

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        createNewMessage();
      }}
    >
      <Grid container alignItems="center" style={{ marginTop: '16px' }}>
        <Grid>
          <TextField
            sx={{
              '.MuiOutlinedInput-root': {
                borderRadius: '10px 0px 0px 10px',
                width: '300px',
              },
            }}
            autoComplete="off"
            value={newMessage}
            onChange={(e) => setNewMessage(e.target.value)}
            label="Type message here"
            variant="outlined"
            fullWidth
          />
        </Grid>
        <Grid>
          <Button
            type="submit"
            variant="contained"
            color="primary"
            sx={{
              height: '56px',
              borderRadius: '0px 10px 10px 0px',
              background: '#0077B6',
            }}
          >
            Send
          </Button>
        </Grid>
      </Grid>
    </form>
  );
};
